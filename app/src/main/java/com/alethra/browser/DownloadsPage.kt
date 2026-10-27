package com.alethra.browser

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.Formatter
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.alethra.browser.MainActivity.Companion.MP
import com.alethra.browser.MainActivity.Companion.WC
import com.alethra.browser.MainActivity.Dl
import org.json.JSONArray

/*
 * Halaman Unduhan, dipisah dari MainActivity.kt.
 * Semua fungsi adalah extension atas MainActivity supaya isi dan perilakunya
 * tidak berubah; anggota MainActivity yang dipakai diubah dari private ke internal.
 */

internal fun MainActivity.queryDownloads(): List<Dl> {
    val a = getList("dl")
    if (a.length() == 0) return emptyList()
    val ids = LongArray(a.length()) { a.getJSONObject(it).getLong("id") }
    val out = mutableListOf<Dl>()
    try {
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.query(DownloadManager.Query().setFilterById(*ids))?.use { c ->
            while (c.moveToNext()) {
                out.add(Dl(
                    c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_ID)),
                    c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE)) ?: "Berkas",
                    c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)),
                    c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)),
                    c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)),
                    c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE)),
                    c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)),
                    c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_URI))))
            }
        }
    } catch (e: Exception) { }
    // Unduhan kustom yang sudah disalin: baris DownloadManager bisa hilang setelah berkas sementara dihapus
    val have = out.map { it.id }.toSet()
    for (i in 0 until a.length()) {
        val o = a.getJSONObject(i)
        if (o.optLong("id") !in have && o.optString("doc").isNotEmpty())
            out.add(Dl(o.getLong("id"), o.optString("t", "Berkas"), DownloadManager.STATUS_SUCCESSFUL, o.optLong("size"), o.optLong("size"), o.optString("mime").ifEmpty { null }, 0, null))
    }
    return out.sortedByDescending { it.id }
}

internal fun MainActivity.isRunning(s: Int) = s == DownloadManager.STATUS_RUNNING || s == DownloadManager.STATUS_PENDING || s == DownloadManager.STATUS_PAUSED

internal fun MainActivity.pageDownloads(): View {
    val dlSettings = actBtn(R.drawable.ic_settings, "Pengaturan unduhan") { openPage("dlset") }
    val (page, body) = shell("Unduhan", true, listOf(dlSettings))
    val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
    var tick = 0
    fun fill() {
        val my = ++tick
        list.removeAllViews()
        val all = queryDownloads()
        val shown = all.filter { when (dlFilter) { 1 -> isRunning(it.status); 2 -> it.status == DownloadManager.STATUS_SUCCESSFUL; 3 -> it.status == DownloadManager.STATUS_FAILED; else -> true } }
        if (shown.isEmpty()) list.addView(emptyState("Belum ada unduhan"))
        shown.forEach { d ->
            val size = if (d.total > 0) Formatter.formatShortFileSize(this, d.total) else ""
            val pct = if (d.total > 0) "${(d.sofar * 100 / d.total).toInt()}% · " else ""
            val ent = dlEntry(d.id)
            val copyState = if (ent != null && ent.optString("tree").isNotEmpty() && ent.optString("doc").isEmpty()
                && d.status == DownloadManager.STATUS_SUCCESSFUL) { if (ent.optBoolean("err")) 2 else 1 } else 0
            val status = when {
                copyState == 1 -> "Menyalin ke folder pilihan…"
                copyState == 2 -> "Gagal menyalin ke folder pilihan"
                d.status == DownloadManager.STATUS_SUCCESSFUL -> "Selesai"
                d.status == DownloadManager.STATUS_FAILED -> "Gagal · " + dlErrorText(d.reason) + " · ketuk untuk ulangi"
                d.status == DownloadManager.STATUS_PAUSED -> pct + when (d.reason) {
                    1 -> "Menunggu untuk mencoba lagi"
                    2 -> "Menunggu jaringan"
                    3 -> "Menunggu Wi-Fi"
                    else -> "Dijeda"
                }
                d.status == DownloadManager.STATUS_PENDING -> "Mengantre"
                else -> pct + "Berlangsung"
            }
            val sub = if (size.isNotEmpty() && d.status == DownloadManager.STATUS_SUCCESSFUL) "$size · $status" else status
            val acts = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            fun act(res: Int, onTap: () -> Unit) = acts.addView(iconBtn(res) { _ -> onTap() }, lp(dp(40), dp(40)))
            when (d.status) {
                DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING ->
                    act(R.drawable.ic_pause) { dlControl(d.id, true) { fill() } }
                DownloadManager.STATUS_PAUSED ->
                    act(R.drawable.ic_play) { dlControl(d.id, false) { fill() } }
                DownloadManager.STATUS_FAILED ->
                    act(R.drawable.ic_rotate) { retryDownload(d); handler.postDelayed({ fill() }, 500) }
            }
            act(R.drawable.ic_trash) { removeDownload(d.id); fill() }
            val row = listRow(iconTile(R.drawable.ic_file), d.name, sub, acts,
                { when (d.status) {
                    DownloadManager.STATUS_SUCCESSFUL -> openDownload(d)
                    DownloadManager.STATUS_FAILED -> { retryDownload(d); handler.postDelayed({ fill() }, 500) }
                    else -> {}
                } })
            if (isRunning(d.status)) {
                // Bar progres di bawah baris, sejajar dengan teks judul (16 + 40 + 14 dp dari kiri)
                val unknown = d.total <= 0
                val bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                    max = 1000
                    progress = if (unknown) 0 else (d.sofar * 1000 / d.total).toInt().coerceIn(0, 1000)
                    progressDrawable = android.graphics.drawable.LayerDrawable(arrayOf(
                        shape(cLine, 2),
                        android.graphics.drawable.ClipDrawable(shape(cAcc, 2), Gravity.START, android.graphics.drawable.ClipDrawable.HORIZONTAL)
                    )).also { it.setId(0, android.R.id.background); it.setId(1, android.R.id.progress) }
                    if (unknown && d.status == DownloadManager.STATUS_RUNNING) {
                        isIndeterminate = true
                        indeterminateTintList = android.content.res.ColorStateList.valueOf(cAcc)
                    }
                }
                val wrap = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
                wrap.addView(row, lp(MP, WC))
                wrap.addView(bar, lp(MP, dp(4)).apply { leftMargin = dp(70); rightMargin = dp(16); bottomMargin = dp(8) })
                list.addView(wrap, lp(MP, WC))
            } else list.addView(row, lp(MP, WC))
        }
        finalizeCustomDownloads()
        if (all.any { isRunning(it.status) || dlEntry(it.id)?.let { e -> e.optString("tree").isNotEmpty() && e.optString("doc").isEmpty() && !e.optBoolean("err") } == true }) {
            handler.postDelayed({ if (my == tick && pageView === page && pages.lastOrNull() == "downloads") fill() }, 1500)
        }
    }
    body.addView(chips(listOf("Semua", "Berlangsung", "Selesai", "Gagal"), dlFilter) { dlFilter = it; renderPage() })
    body.addView(list, lp(MP, WC))
    fill()
    return page
}

internal fun MainActivity.dlErrorText(r: Int): String = when {
    r == DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Penyimpanan penuh"
    r == DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Penyimpanan tidak ditemukan"
    r == DownloadManager.ERROR_CANNOT_RESUME -> "Tidak bisa dilanjutkan"
    r == DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "Berkas sudah ada"
    r == DownloadManager.ERROR_FILE_ERROR -> "Kesalahan berkas"
    r == DownloadManager.ERROR_HTTP_DATA_ERROR -> "Koneksi terputus"
    r == DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Terlalu banyak pengalihan"
    r in 400..599 -> "Server menolak (HTTP $r)"
    else -> "Kesalahan tidak diketahui"
}

/** Jeda atau lanjutkan unduhan. DownloadManager tidak menyediakan API publik untuk ini,
 *  jadi dicoba lewat jalur internal; jika perangkat menolak, pengguna diberi tahu. */
internal fun MainActivity.dlControl(id: Long, pause: Boolean, after: () -> Unit) {
    var ok = false
    try {
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val m = DownloadManager::class.java.getMethod(if (pause) "pauseDownload" else "resumeDownload", LongArray::class.java)
        ok = ((m.invoke(dm, longArrayOf(id)) as? Int) ?: 0) > 0
    } catch (e: Throwable) {}
    if (!ok) try {
        val cv = android.content.ContentValues().apply { put("control", if (pause) 1 else 0) }
        ok = contentResolver.update(Uri.parse("content://downloads/my_downloads/$id"), cv, null, null) > 0
    } catch (e: Throwable) {}
    if (!ok) toast(if (pause) "Perangkat ini tidak mengizinkan jeda unduhan" else "Perangkat ini tidak mengizinkan melanjutkan unduhan")
    handler.postDelayed({ after() }, 500)
}

/** Ulangi unduhan yang gagal: buat permintaan baru dari alamat yang sama dan hapus entri lama. */
internal fun MainActivity.retryDownload(d: Dl) {
    val u = d.uri
    if (u.isNullOrEmpty() || !u.startsWith("http")) { toast("Alamat unduhan tidak ditemukan"); return }
    removeDownload(d.id)
    startDownload(u, null, d.mime, d.name)
}

internal fun MainActivity.removeDownload(id: Long) {
    try { (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).remove(id) } catch (e: Exception) { }
    val a = getList("dl"); val n = JSONArray()
    for (i in 0 until a.length()) { val o = a.getJSONObject(i); if (o.getLong("id") != id) n.put(o) }
    prefs.edit().putString("dl", n.toString()).apply()
}

internal fun MainActivity.openDownload(d: Dl) {
    try {
        val doc = dlEntry(d.id)?.optString("doc").orEmpty()
        val ent = dlEntry(d.id)
        if (ent != null && ent.optString("tree").isNotEmpty() && doc.isEmpty()) {
            if (ent.optBoolean("err")) { dlUpdate(d.id) { it.remove("err") }; finalizeCustomDownloads(); toast("Mencoba menyalin ulang…") }
            else toast("Berkas sedang disalin ke folder pilihan")
            return
        }
        val uri = if (doc.isNotEmpty()) Uri.parse(doc)
            else (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).getUriForDownloadedFile(d.id)
        val i = Intent(Intent.ACTION_VIEW).setDataAndType(uri, d.mime ?: "*/*")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    } catch (e: Exception) { toast("Tidak bisa membuka berkas ini") }
}
