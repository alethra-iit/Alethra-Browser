package com.alethra.browser

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.alethra.browser.MainActivity.Companion.MP
import com.alethra.browser.MainActivity.Companion.WC
import com.alethra.browser.MainActivity.Tab

/*
 * Halaman Tab: grid kartu tab, daftar tab tertutup, thumbnail, dan geser-untuk-tutup.
 * Dipisah dari MainActivity.kt sebagai extension atas MainActivity; isi dan
 * perilakunya tidak berubah. Logika tab (newTab, closeTab, selectTab, dst.) masih
 * di MainActivity dan akan dipindah di tahap tersendiri.
 */

internal fun MainActivity.pageTabs(): View {
    val add = actBtn(R.drawable.ic_plus, "Tab baru") { autoMini(); pages.clear(); newTab(home()) }
    val inc = actBtn(R.drawable.ic_incognito, if (incognito) "Penyamaran aktif" else "Mode penyamaran", if (incognito) cAcc else cFg) {
        setIncognito(!incognito)
    }
    val reopen = actBtn(R.drawable.ic_reset, "Buka lagi tab tertutup") {
        if (closed.isEmpty()) toast("Belum ada tab yang ditutup")
        else { val c = closed.removeAt(0); pages.clear(); newTab(c.url) }
    }
    val share = actBtn(R.drawable.ic_share, "Bagikan semua tab") {
        val urls = tabs.map { it.url }.filter { it.startsWith("http") }
        if (urls.isEmpty()) toast("Tidak ada tab untuk dibagikan")
        else try {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, urls.joinToString("\n")), "Bagikan semua tab"))
        } catch (e: Exception) { toast("Tidak bisa membagikan") }
    }
    val closeAll = actBtn(R.drawable.ic_trash, "Tutup semua tab") {
        dlg().setTitle("Tutup semua tab?").setMessage("Ada ${tabs.size} tab yang akan ditutup.")
            .setPositiveButton("Tutup semua") { _, _ -> closeAllTabs() }
            .setNegativeButton("Batal", null).show()
    }
    val (page, body) = shell("Tab", false, listOf(add, inc, reopen, share, closeAll))
    body.addView(chips(listOf("Semua", "Aktif (${tabs.size})", "Tertutup"), tabFilter) { tabFilter = it; renderPage() })
    val cardLp = { lp(MP, WC).apply { setMargins(dp(16), dp(5), dp(16), dp(5)) } }
    if (tabFilter != 2) {
        tabs.chunked(2).forEachIndexed { ri, pair ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(10), dp(6), dp(10), dp(6)) }
            pair.forEachIndexed { ci, t ->
                row.addView(tabCard(ri * 2 + ci, t), lp(0, WC, 1f).apply { setMargins(dp(6), 0, dp(6), 0) })
            }
            if (pair.size == 1) row.addView(View(this), lp(0, 1, 1f).apply { setMargins(dp(6), 0, dp(6), 0) })
            body.addView(row, lp(MP, WC))
        }
    }
    if (tabFilter != 1 && closed.isNotEmpty()) {
        val hdr = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        hdr.addView(sectionLabel("Baru saja ditutup"), lp(0, WC, 1f))
        hdr.addView(tv("Hapus semua", 13f, cAcc, true).apply {
            setPadding(dp(16), dp(18), dp(20), dp(6)); isClickable = true
            setOnClickListener {
                dlg().setTitle("Hapus daftar tab tertutup?").setMessage("${closed.size} tab tertutup akan dihapus dari daftar.")
                    .setPositiveButton("Hapus") { _, _ -> closed.clear(); renderPage() }
                    .setNegativeButton("Batal", null).show()
            }
        }, lp(WC, WC))
        body.addView(hdr, lp(MP, WC))
        closed.toList().forEach { c ->
            val del = FrameLayout(this).apply {
                isClickable = true; contentDescription = "Hapus dari daftar"
                setOnClickListener { closed.remove(c); renderPage() }
                addView(icon(R.drawable.ic_close, 16, cSub), FrameLayout.LayoutParams(dp(16), dp(16), Gravity.CENTER))
            }
            val row = listRow(favTile(c.title, c.url, null, cLine), c.title.ifEmpty { c.url }, prettyUrl(c.url), del,
                onClick = { closed.remove(c); pages.clear(); newTab(c.url) },
                onLong = { closed.remove(c); renderPage() })
            row.background = shape(cSurf, 16)
            body.addView(row, cardLp())
        }
    } else if (tabFilter == 2) {
        body.addView(emptyState("Belum ada tab yang ditutup"))
    }
    return page
}

internal fun MainActivity.captureThumb(t: Tab) {
    try {
        val w = t.web
        if (w.width <= 0 || w.height <= 0 || !w.isAttachedToWindow) return
        val sc = 360f / w.width
        val bmp = Bitmap.createBitmap((w.width * sc).toInt().coerceAtLeast(1), (w.height * sc).toInt().coerceAtLeast(1), Bitmap.Config.RGB_565)
        val c = android.graphics.Canvas(bmp)
        c.scale(sc, sc)
        w.draw(c)
        t.thumb = bmp
    } catch (e: Throwable) {}
}

internal fun MainActivity.tabCard(i: Int, t: Tab): View {
    val card = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = if (i == cur) shape(cSurf, 16, cFg, 2) else shape(cSurf, 16)
        isClickable = true
        setPadding(dp(6), dp(6), dp(6), dp(6))
    }
    val head = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
    head.addView(if (t.incog) icon(R.drawable.ic_incognito, 20, cAcc) else favTile(t.title, t.url, t.icon, cLine), lp(dp(26), dp(26)).apply { rightMargin = dp(6) })
    head.addView(tv(t.title.ifEmpty { prettyUrl(t.url) }, 12f, cFg, true).apply { maxLines = 1; ellipsize = TextUtils.TruncateAt.END }, lp(0, WC, 1f))
    val x = FrameLayout(this).apply { isClickable = true; setOnClickListener { closeTab(i, true) } }
    x.addView(icon(R.drawable.ic_close, 16, cSub), FrameLayout.LayoutParams(dp(16), dp(16), Gravity.CENTER))
    head.addView(x, lp(dp(30), dp(30)))
    card.addView(head, lp(MP, WC))
    val holder = FrameLayout(this).apply { background = shape(cBg, 12); clipToOutline = true }
    val bm = t.thumb
    if (bm != null) {
        val img = ImageView(this).apply { scaleType = ImageView.ScaleType.MATRIX; setImageBitmap(bm) }
        img.addOnLayoutChangeListener { v, l, _, r, _, _, _, _, _ ->
            if (r - l > 0) {
                val m = android.graphics.Matrix()
                val sc = (r - l).toFloat() / bm.width
                m.setScale(sc, sc)
                (v as ImageView).imageMatrix = m
            }
        }
        holder.addView(img, FrameLayout.LayoutParams(MP, MP))
    } else {
        holder.addView(favTile(t.title, t.url, t.icon, cLine), FrameLayout.LayoutParams(dp(44), dp(44), Gravity.CENTER))
    }
    card.addView(holder, lp(MP, dp(170)).apply { topMargin = dp(6) })
    card.setOnClickListener { if (i != cur) autoMini(); selectTab(i) }
    swipeToClose(card) { closeTab(i, true) }
    return card
}

@SuppressLint("ClickableViewAccessibility")
internal fun MainActivity.swipeToClose(card: View, onClose: () -> Unit) {
    var x0 = 0f; var y0 = 0f; var drag = false
    card.setOnTouchListener { v, e ->
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> { x0 = e.rawX; y0 = e.rawY; drag = false; true }
            MotionEvent.ACTION_MOVE -> {
                val dx = e.rawX - x0
                val dy = e.rawY - y0
                if (!drag && Math.abs(dx) > dp(12) && Math.abs(dx) > Math.abs(dy)) {
                    drag = true
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                }
                if (drag) { v.translationX = dx; v.alpha = (1f - Math.abs(dx) / v.width).coerceIn(0.2f, 1f) }
                true
            }
            MotionEvent.ACTION_UP -> {
                if (drag) {
                    if (Math.abs(v.translationX) > v.width * 0.4f) {
                        v.animate().translationX(if (v.translationX > 0) v.width.toFloat() else -v.width.toFloat())
                            .alpha(0f).setDuration(140).withEndAction { onClose() }.start()
                    } else v.animate().translationX(0f).alpha(1f).setDuration(120).start()
                } else if (Math.abs(e.rawX - x0) < dp(10) && Math.abs(e.rawY - y0) < dp(10)) v.performClick()
                true
            }
            MotionEvent.ACTION_CANCEL -> { v.animate().translationX(0f).alpha(1f).setDuration(120).start(); true }
            else -> true
        }
    }
}
