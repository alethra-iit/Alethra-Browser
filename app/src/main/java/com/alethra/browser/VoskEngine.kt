package com.alethra.browser

import android.content.Context
import org.json.JSONObject
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.zip.ZipInputStream

/**
 * Model Vosk (Kaldi). [id] = nama berkas zip di alphacephei.com/vosk/models sekaligus nama folder di dalam zip.
 * [big] = model besar untuk server: butuh RAM sangat besar, hanya cocok untuk HP kelas atas.
 */
class VoskModel(
    val id: String,
    val title: String,
    val note: String,
    val lang: String,
    val zipBytes: Long,
    val big: Boolean = false
) {
    val url: String get() = "https://alphacephei.com/vosk/models/$id.zip"
}

object VoskModels {
    private const val MB = 1_000_000L
    private const val GB = 1_000_000_000L

    val list: List<VoskModel> = listOf(
        // Jepang
        VoskModel("vosk-model-small-ja-0.22", "Jepang kecil", "Ringan dan cepat, cocok untuk HP biasa. Tingkat kesalahan karakter ±9,5% (set csj) dan ±17% (set ted10k).", "ja", 48 * MB),
        VoskModel("vosk-model-ja-0.22", "Jepang besar", "Lebih akurat dari yang kecil (±8,4% dan ±13,9%), tapi model server: ±1 GB unduhan dan butuh RAM sangat besar. Hanya untuk HP kelas atas.", "ja", 1 * GB, true),
        // Inggris
        VoskModel("vosk-model-small-en-us-0.15", "Inggris (AS) kecil", "Ringan dan cepat. Cocok untuk HP biasa.", "en", 40 * MB),
        VoskModel("vosk-model-en-us-0.22-lgraph", "Inggris (AS) sedang", "Lebih akurat dari yang kecil dan masih layak di HP (±128 MB).", "en", 128 * MB),
        VoskModel("vosk-model-small-en-in-0.4", "Inggris (India) kecil", "Logat Inggris India, ringan.", "en", 36 * MB),
        VoskModel("vosk-model-en-us-0.22", "Inggris (AS) besar", "Paling akurat untuk Inggris AS, tapi model server: ±1,8 GB dan butuh RAM sangat besar.", "en", (1.8 * GB).toLong(), true),
        VoskModel("vosk-model-en-us-0.42-gigaspeech", "Inggris (AS) Gigaspeech", "Besar, cocok untuk podcast. Model server: ±2,3 GB dan butuh RAM sangat besar.", "en", (2.3 * GB).toLong(), true),
        // Lainnya (model kecil untuk HP)
        VoskModel("vosk-model-small-cn-0.22", "Mandarin kecil", "Ringan.", "zh", 42 * MB),
        VoskModel("vosk-model-small-ko-0.22", "Korea kecil", "Ringan.", "ko", 82 * MB),
        VoskModel("vosk-model-small-ru-0.22", "Rusia kecil", "Ringan.", "ru", 45 * MB),
        VoskModel("vosk-model-small-fr-0.22", "Prancis kecil", "Ringan.", "fr", 41 * MB),
        VoskModel("vosk-model-small-de-0.15", "Jerman kecil", "Ringan.", "de", 45 * MB),
        VoskModel("vosk-model-small-es-0.42", "Spanyol kecil", "Ringan.", "es", 39 * MB),
        VoskModel("vosk-model-small-pt-0.3", "Portugis kecil", "Ringan.", "pt", 31 * MB),
        VoskModel("vosk-model-small-it-0.22", "Italia kecil", "Ringan.", "it", 48 * MB),
        VoskModel("vosk-model-small-nl-0.22", "Belanda kecil", "Ringan.", "nl", 39 * MB),
        VoskModel("vosk-model-small-tr-0.3", "Turki kecil", "Ringan.", "tr", 35 * MB),
        VoskModel("vosk-model-small-vn-0.4", "Vietnam kecil", "Ringan.", "vi", 32 * MB),
        VoskModel("vosk-model-small-hi-0.22", "Hindi kecil", "Ringan.", "hi", 42 * MB),
        VoskModel("vosk-model-small-fa-0.42", "Persia kecil", "Ringan.", "fa", 53 * MB),
        VoskModel("vosk-model-small-pl-0.22", "Polandia kecil", "Ringan.", "pl", 50 * MB),
        VoskModel("vosk-model-small-cs-0.4-rhasspy", "Ceko kecil", "Ringan.", "cs", 44 * MB),
        VoskModel("vosk-model-small-ca-0.4", "Katalan kecil", "Ringan.", "ca", 42 * MB),
        VoskModel("vosk-model-small-uk-v3-nano", "Ukraina nano", "Ringan.", "uk", 73 * MB),
        VoskModel("vosk-model-small-kz-0.42", "Kazakh kecil", "Ringan.", "kk", 58 * MB),
        VoskModel("vosk-model-small-uz-0.22", "Uzbek kecil", "Ringan.", "uz", 49 * MB),
        VoskModel("vosk-model-ar-mgb2-0.4", "Arab (MGB2)", "Ukuran sedang (±318 MB).", "ar", 318 * MB)
    )

    fun find(id: String): VoskModel? = list.firstOrNull { it.id == id }

    fun langName(l: String) = when (l) {
        "ja" -> "Jepang"; "en" -> "Inggris"; "zh" -> "Mandarin"; "ko" -> "Korea"; "ru" -> "Rusia"; "fr" -> "Prancis"
        "de" -> "Jerman"; "es" -> "Spanyol"; "pt" -> "Portugis"; "it" -> "Italia"; "nl" -> "Belanda"; "tr" -> "Turki"
        "vi" -> "Vietnam"; "hi" -> "Hindi"; "fa" -> "Persia"; "pl" -> "Polandia"; "cs" -> "Ceko"; "ca" -> "Katalan"
        "uk" -> "Ukraina"; "kk" -> "Kazakh"; "uz" -> "Uzbek"; "ar" -> "Arab"; else -> l
    }

    fun root(ctx: Context) = File(ctx.applicationContext.filesDir, "vosk_models")
    fun dir(ctx: Context, id: String) = File(root(ctx), id)
    fun modelDir(ctx: Context, m: VoskModel) = File(dir(ctx, m.id), "model")
    fun installed(ctx: Context, m: VoskModel) = File(dir(ctx, m.id), ".ok").exists()

    fun sizeOnDisk(ctx: Context, id: String): Long {
        fun walk(f: File): Long = if (f.isFile) f.length() else (f.listFiles()?.sumOf { walk(it) } ?: 0L)
        return walk(dir(ctx, id))
    }

    fun totalOnDisk(ctx: Context): Long = list.sumOf { sizeOnDisk(ctx, it.id) }

    fun delete(ctx: Context, id: String) {
        dir(ctx, id).deleteRecursively()
    }

    /* ---- Unduhan ---- */

    class Job(val id: String, total: Long) {
        @Volatile var total = total
        @Volatile var done = 0L
        @Volatile var cancel = false
        @Volatile var paused = false   // berhenti karena dijeda (bukan dibatalkan): berkas sebagian tetap disimpan
        @Volatile var finished = false
        @Volatile var extracting = false
        @Volatile var error: String? = null
    }

    private val pool = Executors.newSingleThreadExecutor()
    private val jobs = ConcurrentHashMap<String, Job>()

    /** Dipanggil dari thread unduhan (dibatasi ±4 kali per detik); UI harus pindah ke thread utama sendiri. */
    @Volatile var listener: ((String) -> Unit)? = null

    fun job(id: String): Job? = jobs[id]
    fun running(id: String): Boolean = jobs[id]?.let { !it.finished } ?: false
    fun cancel(id: String) { jobs[id]?.cancel = true }

    /** Jeda: hentikan unduhan dan simpan berkas sebagian (pkg.zip.part). [start] melanjutkan dari titik yang sama lewat header Range. */
    fun pause(id: String) { jobs[id]?.let { it.paused = true; it.cancel = true } }

    /** Byte sisa unduhan di disk untuk model yang belum terpasang (0 bila sudah terpasang atau belum pernah diunduh). */
    fun partial(ctx: Context, id: String): Long {
        val d = dir(ctx, id)
        if (!d.exists() || File(d, ".ok").exists()) return 0L
        return sizeOnDisk(ctx, id)
    }

    fun start(ctx: Context, m: VoskModel) {
        if (running(m.id)) return
        val job = Job(m.id, m.zipBytes)
        job.done = partial(ctx, m.id)   // lanjutan: progres langsung mulai dari yang sudah terunduh
        jobs[m.id] = job
        val dir = dir(ctx, m.id)
        pool.execute {
            try {
                if (job.cancel) return@execute   // dijeda saat masih antre
                dir.mkdirs()
                File(dir, ".ok").delete()
                val zip = File(dir, "pkg.zip")
                val part = File(dir, "pkg.zip.part")
                var lastTick = 0L
                fun tick(force: Boolean) {
                    val n = System.currentTimeMillis()
                    if (force || n - lastTick > 250) { lastTick = n; try { listener?.invoke(m.id) } catch (e: Exception) { } }
                }
                if (!zip.exists()) {
                    var have = if (part.exists()) part.length() else 0L
                    val c = URL(m.url).openConnection() as HttpURLConnection
                    c.connectTimeout = 15000
                    c.readTimeout = 30000
                    c.instanceFollowRedirects = true
                    c.setRequestProperty("User-Agent", "Alethra")
                    if (have > 0) c.setRequestProperty("Range", "bytes=$have-")
                    try {
                        val code = c.responseCode
                        if (code == 416 && have > 0) {
                            part.renameTo(zip)
                        } else {
                            if (code != 200 && code != 206) throw RuntimeException("Server menjawab $code")
                            val append = code == 206 && have > 0
                            if (!append) have = 0L
                            val len = c.contentLengthLong
                            if (len > 0) job.total = len + have
                            FileOutputStream(part, append).use { out ->
                                c.inputStream.use { inp ->
                                    val buf = ByteArray(64 * 1024)
                                    var got = have
                                    while (true) {
                                        if (job.cancel) break
                                        val n = inp.read(buf)
                                        if (n < 0) break
                                        out.write(buf, 0, n)
                                        got += n
                                        job.done = got
                                        tick(false)
                                    }
                                }
                            }
                            if (job.cancel) return@execute
                            if (job.total > 0 && part.length() < job.total) throw RuntimeException("Unduhan terputus, coba lagi untuk melanjutkan")
                            if (!part.renameTo(zip)) throw RuntimeException("Gagal menyimpan berkas")
                        }
                    } finally { c.disconnect() }
                }
                // Ekstrak: <nama-model>/* menjadi model/* (komponen pertama dibuang)
                job.extracting = true
                tick(true)
                val tmp = File(dir, "model.tmp")
                tmp.deleteRecursively()
                tmp.mkdirs()
                val tmpPath = tmp.canonicalPath + File.separator
                ZipInputStream(BufferedInputStream(FileInputStream(zip))).use { zin ->
                    while (true) {
                        if (job.cancel) return@execute
                        val e = zin.nextEntry ?: break
                        if (e.isDirectory) continue
                        val rel = e.name.replace('\\', '/').substringAfter('/', "")
                        if (rel.isEmpty()) continue
                        val out = File(tmp, rel)
                        // cegah path traversal (zip slip)
                        if (!out.canonicalPath.startsWith(tmpPath)) continue
                        out.parentFile?.mkdirs()
                        FileOutputStream(out).use { o -> zin.copyTo(o) }
                    }
                }
                if (!File(tmp, "am/final.mdl").exists() && !File(tmp, "conf/model.conf").exists())
                    throw RuntimeException("Isi model tidak lengkap, hapus lalu unduh ulang")
                val fin = File(dir, "model")
                fin.deleteRecursively()
                if (!tmp.renameTo(fin)) throw RuntimeException("Gagal menyimpan model")
                zip.delete()
                File(dir, ".ok").createNewFile()
            } catch (e: Throwable) {
                job.error = (e.message ?: "Gagal mengunduh").take(80)
            } finally {
                job.extracting = false
                job.finished = true
                try { listener?.invoke(m.id) } catch (e: Exception) { }
            }
        }
    }
}

/**
 * Suara -> teks streaming di perangkat memakai Vosk. Audio PCM 16-bit mono 16 kHz diumpan terus-menerus;
 * hasil sementara (kata per kata) keluar di setiap potongan, hasil final keluar saat Vosk mendeteksi jeda bicara.
 */
object VoskAsr {
    class Ev(val isFinal: Boolean, val text: String)

    private val lock = Any()
    private var model: Model? = null
    private var rec: Recognizer? = null
    private var key = ""
    private var spaceless = false

    private val cjk = "\\u3040-\\u30FF\\u3400-\\u9FFF\\uFF00-\\uFFEF"
    private val cjkGap = Regex("(?<=[$cjk]) +(?=[$cjk])")

    fun ensure(ctx: Context, m: VoskModel) {
        synchronized(lock) {
            val r0 = rec
            if (r0 != null && key == m.id) { r0.reset(); return }
            releaseLocked()
            if (!VoskModels.installed(ctx, m)) throw IllegalStateException("Model belum diunduh")
            try { LibVosk.setLogLevel(LogLevel.WARNINGS) } catch (e: Throwable) { }
            val md = Model(VoskModels.modelDir(ctx, m).absolutePath)
            val r = try { Recognizer(md, 16000f) } catch (e: Throwable) { try { md.close() } catch (e2: Throwable) { }; throw e }
            model = md
            rec = r
            key = m.id
            spaceless = m.lang == "ja" || m.lang == "zh"
        }
    }

    // Vosk memberi spasi antar kata; bahasa Jepang dan Mandarin tidak memakainya
    private fun clean(t: String): String {
        var s = t.replace(Regex("\\s+"), " ").trim()
        if (spaceless) s = s.replace(cjkGap, "")
        return s
    }

    /** [pcm] = PCM 16-bit little-endian mono 16 kHz. null bila model belum dimuat. */
    fun feed(pcm: ByteArray): Ev? {
        synchronized(lock) {
            val r = rec ?: return null
            val fin = r.acceptWaveForm(pcm, pcm.size)
            val raw = if (fin) r.getResult() else r.getPartialResult()
            val t = try { JSONObject(raw).optString(if (fin) "text" else "partial") } catch (e: Exception) { "" }
            return Ev(fin, clean(t))
        }
    }

    /** Paksa kalimat yang sedang berjalan selesai (video dijeda, atau ucapan terlalu panjang tanpa jeda), lalu mulai bersih. */
    fun finish(): String {
        synchronized(lock) {
            val r = rec ?: return ""
            val t = try { JSONObject(r.getFinalResult()).optString("text") } catch (e: Exception) { "" }
            try { r.reset() } catch (e: Throwable) { }
            return clean(t)
        }
    }

    fun release() { synchronized(lock) { releaseLocked() } }

    private fun releaseLocked() {
        try { rec?.close() } catch (e: Throwable) { }
        try { model?.close() } catch (e: Throwable) { }
        rec = null
        model = null
        key = ""
    }
}
