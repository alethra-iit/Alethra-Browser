package com.alethra.browser

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong
import javax.net.ssl.HttpsURLConnection

/**
 * Proxy HTTP/CONNECT yang hanya mendengarkan di 127.0.0.1. WebView diarahkan ke sini lewat
 * ProxyController, lalu setiap nama situs di-resolve memakai DNS-over-HTTPS (RFC 8484).
 * Lalu lintas HTTPS situs tetap terenkripsi ujung ke ujung: proxy hanya menyambungkan soket.
 *
 * Jawaban 0.0.0.0 / :: / REFUSED dari penyedia (AdGuard, Cloudflare Keamanan, Quad9) dianggap
 * "diblokir" sehingga pelacak dan situs berbahaya tidak pernah tersambung.
 */
class DohProxy(
    private val endpoint: () -> String,
    private val nxBlocks: () -> Boolean
) {
    class Blocked : IOException("diblokir oleh DNS")

    class Ans(val addrs: List<InetAddress>, val rcode: Int, val ttl: Int)
    private class Entry(val addrs: List<InetAddress>, val exp: Long)

    val blocked = AtomicLong(0)
    private val cache = ConcurrentHashMap<String, Entry>()
    private val pool = Executors.newCachedThreadPool(ThreadFactory { r -> Thread(r, "doh").apply { isDaemon = true } })
    @Volatile private var server: ServerSocket? = null

    /* ---------------- siklus hidup ---------------- */

    @Synchronized
    fun start(): Int {
        server?.let { if (!it.isClosed) return it.localPort }
        val s = ServerSocket(0, 128, InetAddress.getByName("127.0.0.1"))
        server = s
        Thread({
            while (!s.isClosed) {
                try {
                    val c = s.accept()
                    pool.execute { handle(c) }
                } catch (e: IOException) {
                    break
                } catch (e: Exception) {
                }
            }
        }, "doh-accept").apply { isDaemon = true; start() }
        return s.localPort
    }

    @Synchronized
    fun stop() {
        try { server?.close() } catch (e: Exception) {}
        server = null
    }

    fun flush() = cache.clear()

    /* ---------------- resolusi DNS ---------------- */

    fun resolve(host: String): List<InetAddress> {
        val h = host.trim().trimEnd('.').lowercase()
        if (h.isEmpty()) throw IOException("nama kosong")
        if (isIp(h)) return listOf(InetAddress.getByName(h))
        val now = System.currentTimeMillis()
        cache[h]?.let {
            if (it.exp > now) {
                if (it.addrs.isEmpty()) throw Blocked()
                return it.addrs
            }
        }
        val ep = endpoint()
        val res: Ans = try {
            var a = query(ep, h, 1)
            if (a.addrs.isEmpty() && a.rcode == 0) {
                val b = query(ep, h, 28)
                if (b.addrs.isNotEmpty()) a = b
            }
            a
        } catch (e: Exception) {
            // Penyedia DoH tidak terjangkau: lanjut dengan DNS sistem agar browsing tidak putus
            return InetAddress.getAllByName(h).toList()
        }
        val real = res.addrs.filter { !it.isAnyLocalAddress }
        val nullRouted = res.addrs.isNotEmpty() && real.isEmpty()
        if (nullRouted || res.rcode == 5 || (res.rcode == 3 && nxBlocks())) {
            blocked.incrementAndGet()
            cache[h] = Entry(emptyList(), now + 60_000L)
            throw Blocked()
        }
        if (real.isEmpty()) throw UnknownHostException(h)
        cache[h] = Entry(real, now + res.ttl.coerceIn(30, 600) * 1000L)
        return real
    }

    private fun query(ep: String, host: String, type: Int): Ans {
        val q = buildQuery(host, type)
        val c = URL(ep).openConnection() as HttpsURLConnection
        c.requestMethod = "POST"
        c.doOutput = true
        c.useCaches = false
        c.connectTimeout = 5000
        c.readTimeout = 6000
        c.setRequestProperty("Content-Type", "application/dns-message")
        c.setRequestProperty("Accept", "application/dns-message")
        c.setFixedLengthStreamingMode(q.size)
        c.outputStream.use { it.write(q) }
        if (c.responseCode != 200) throw IOException("DoH HTTP " + c.responseCode)
        val body = c.inputStream.use { it.readBytes() }
        return parse(body)
    }

    /* ---------------- format paket DNS ---------------- */

    private fun isIp(h: String) = h.contains(':') || Regex("^\\d{1,3}(\\.\\d{1,3}){3}$").matches(h)

    private fun buildQuery(host: String, type: Int): ByteArray {
        val o = ByteArrayOutputStream()
        val d = DataOutputStream(o)
        d.writeShort(0)        // ID 0 (disarankan RFC 8484 agar mudah di-cache)
        d.writeShort(0x0100)   // RD
        d.writeShort(1); d.writeShort(0); d.writeShort(0); d.writeShort(0)
        for (l in java.net.IDN.toASCII(host).split('.')) {
            if (l.isEmpty() || l.length > 63) throw IOException("nama tidak valid")
            d.writeByte(l.length)
            d.write(l.toByteArray(Charsets.US_ASCII))
        }
        d.writeByte(0)
        d.writeShort(type)
        d.writeShort(1)        // kelas IN
        return o.toByteArray()
    }

    private fun u16(b: ByteArray, p: Int) = ((b[p].toInt() and 0xFF) shl 8) or (b[p + 1].toInt() and 0xFF)
    private fun u32(b: ByteArray, p: Int): Long =
        ((u16(b, p).toLong()) shl 16) or u16(b, p + 2).toLong()

    private fun skipName(b: ByteArray, start: Int): Int {
        var p = start
        while (true) {
            val len = b[p].toInt() and 0xFF
            if (len == 0) return p + 1
            if ((len and 0xC0) == 0xC0) return p + 2
            p += len + 1
        }
    }

    fun parse(b: ByteArray): Ans {
        try {
            if (b.size < 12) throw IOException("jawaban terlalu pendek")
            val rcode = b[3].toInt() and 0x0F
            val qd = u16(b, 4)
            val an = u16(b, 6)
            var p = 12
            repeat(qd) { p = skipName(b, p) + 4 }
            val list = ArrayList<InetAddress>()
            var ttl = Long.MAX_VALUE
            repeat(an) {
                p = skipName(b, p)
                val type = u16(b, p)
                val t = u32(b, p + 4)
                val len = u16(b, p + 8)
                val data = p + 10
                if ((type == 1 && len == 4) || (type == 28 && len == 16)) {
                    list.add(InetAddress.getByAddress(b.copyOfRange(data, data + len)))
                    if (t < ttl) ttl = t
                }
                p = data + len
            }
            return Ans(list, rcode, if (ttl == Long.MAX_VALUE) 60 else ttl.coerceAtMost(86400L).toInt())
        } catch (e: IndexOutOfBoundsException) {
            throw IOException("jawaban DNS rusak")
        }
    }

    /* ---------------- proxy ---------------- */

    private fun handle(c: Socket) {
        var up: Socket? = null
        try {
            c.tcpNoDelay = true
            c.soTimeout = 20000
            val cin = BufferedInputStream(c.getInputStream())
            val head = readHead(cin)
            if (head == null) { quiet(c); return }
            c.soTimeout = 0
            val lines = String(head, Charsets.ISO_8859_1).split("\r\n")
            val first = lines[0].split(" ")
            if (first.size < 3) { replyAndClose(c, "400 Bad Request"); return }
            val method = first[0]
            val target = first[1]
            if (method.equals("CONNECT", true)) {
                val (host, port) = splitHostPort(target, 443)
                up = connect(host, port)
                c.getOutputStream().write("HTTP/1.1 200 Connection Established\r\n\r\n".toByteArray())
                startPipes(c, cin, up)
            } else {
                val u = try { URL(target) } catch (e: Exception) { null }
                if (u == null) { replyAndClose(c, "400 Bad Request"); return }
                up = connect(u.host, if (u.port > 0) u.port else 80)
                val path = if (u.file.isNullOrEmpty()) "/" else u.file
                val sb = StringBuilder()
                sb.append(method).append(' ').append(path).append(' ').append(first[2]).append("\r\n")
                for (i in 1 until lines.size) {
                    val l = lines[i]
                    if (l.isEmpty()) continue
                    val lo = l.lowercase()
                    if (lo.startsWith("proxy-connection:") || lo.startsWith("connection:") || lo.startsWith("proxy-authorization:")) continue
                    sb.append(l).append("\r\n")
                }
                sb.append("Connection: close\r\n\r\n")
                up.getOutputStream().write(sb.toString().toByteArray(Charsets.ISO_8859_1))
                startPipes(c, cin, up)
            }
        } catch (e: Blocked) {
            quiet(up); replyAndClose(c, "403 Forbidden")
        } catch (e: Exception) {
            quiet(up); replyAndClose(c, "502 Bad Gateway")
        }
    }

    private fun readHead(i: InputStream): ByteArray? {
        val o = ByteArrayOutputStream()
        var m = 0
        while (o.size() < 16384) {
            val b = i.read()
            if (b < 0) return null
            o.write(b)
            m = when {
                (m == 0 || m == 2) && b == 13 -> m + 1
                (m == 1 || m == 3) && b == 10 -> m + 1
                b == 13 -> 1
                else -> 0
            }
            if (m == 4) return o.toByteArray()
        }
        return null
    }

    fun splitHostPort(t: String, def: Int): Pair<String, Int> {
        if (t.startsWith("[")) {
            val e = t.indexOf(']')
            if (e > 0) {
                val port = if (t.length > e + 2 && t[e + 1] == ':') t.substring(e + 2).toIntOrNull() ?: def else def
                return Pair(t.substring(1, e), port)
            }
        }
        val i = t.lastIndexOf(':')
        if (i > 0 && t.indexOf(':') == i) return Pair(t.substring(0, i), t.substring(i + 1).toIntOrNull() ?: def)
        return Pair(t, def)
    }

    private fun connect(host: String, port: Int): Socket {
        var last: IOException? = null
        for (a in resolve(host).take(4)) {
            val s = Socket()
            try {
                s.tcpNoDelay = true
                s.connect(InetSocketAddress(a, port), 8000)
                return s
            } catch (e: IOException) {
                last = e
                quiet(s)
            }
        }
        throw last ?: IOException("tidak ada alamat")
    }

    private fun startPipes(c: Socket, cin: InputStream, up: Socket) {
        pool.execute { pipe(cin, up.getOutputStream(), c, up) }
        pool.execute { pipe(up.getInputStream(), c.getOutputStream(), c, up) }
    }

    private fun pipe(i: InputStream, o: OutputStream, a: Socket, b: Socket) {
        try {
            val buf = ByteArray(16384)
            while (true) {
                val n = i.read(buf)
                if (n < 0) break
                o.write(buf, 0, n)
            }
        } catch (e: IOException) {
        } finally {
            quiet(a); quiet(b)
        }
    }

    private fun replyAndClose(c: Socket, status: String) {
        try { c.getOutputStream().write(("HTTP/1.1 $status\r\nContent-Length: 0\r\nConnection: close\r\n\r\n").toByteArray()) } catch (e: Exception) {}
        quiet(c)
    }

    private fun quiet(s: Socket?) { try { s?.close() } catch (e: Exception) {} }
}
