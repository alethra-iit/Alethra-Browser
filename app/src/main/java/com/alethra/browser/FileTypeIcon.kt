package com.alethra.browser

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable

/**
 * Icon berkas bergaya lembar dengan sudut terlipat: badan pastel, label ekstensi di bawah,
 * dan simbol khas tiap jenis (PDF, DOCX, XLSX, ZIP, PNG, MP3, MP4, …). Digambar lewat Canvas,
 * jadi tajam di ukuran apa pun dan tidak butuh satu berkas gambar per jenis.
 */
class FileTypeIcon(ext: String) : Drawable() {

    private val kind: String
    private val label: String
    private val body: Int
    private val bar: Int
    private val glyph: Int
    private var alphaAll = 255

    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val rect = RectF()

    init {
        val e = ext.lowercase().trim().removePrefix(".")
        kind = KIND[e] ?: "file"
        val c = COLORS[kind] ?: COLORS.getValue("file")
        body = c.first
        bar = c.second
        glyph = darken(c.second, 0.9f)
        label = when (e) {
            "jpeg", "jfif" -> "JPG"
            "tiff" -> "TIF"
            "htm", "xhtml" -> "HTML"
            else -> e.uppercase().take(4)
        }
    }

    override fun draw(c: Canvas) {
        val b = bounds
        if (b.width() <= 0 || b.height() <= 0) return
        val h = minOf(b.height().toFloat(), b.width() / 0.85f)
        val w = h * 0.85f
        val x0 = b.exactCenterX() - w / 2f
        val y0 = b.exactCenterY() - h / 2f
        val r = w * 0.10f
        val fold = w * 0.30f
        val barH = h * 0.20f

        // badan lembar
        path.reset()
        path.moveTo(x0 + r, y0)
        path.lineTo(x0 + w - fold, y0)
        path.lineTo(x0 + w, y0 + fold)
        path.lineTo(x0 + w, y0 + h - r)
        path.quadTo(x0 + w, y0 + h, x0 + w - r, y0 + h)
        path.lineTo(x0 + r, y0 + h)
        path.quadTo(x0, y0 + h, x0, y0 + h - r)
        path.lineTo(x0, y0 + r)
        path.quadTo(x0, y0, x0 + r, y0)
        path.close()
        fill(body)
        c.drawPath(path, p)

        // label ekstensi di bawah, dipotong mengikuti bentuk lembar
        c.save()
        c.clipPath(path)
        fill(bar)
        c.drawRect(x0, y0 + h - barH, x0 + w, y0 + h, p)
        c.restore()

        // sudut terlipat
        path.reset()
        path.moveTo(x0 + w - fold, y0)
        path.lineTo(x0 + w, y0 + fold)
        path.lineTo(x0 + w - fold, y0 + fold)
        path.close()
        fill(blend(body, bar, 0.35f))
        c.drawPath(path, p)

        if (label.isNotEmpty()) {
            fill(Color.WHITE)
            p.typeface = Typeface.DEFAULT_BOLD
            p.textAlign = Paint.Align.CENTER
            p.textSize = barH * 0.64f
            val tw = p.measureText(label)
            val maxW = w * 0.88f
            if (tw > maxW) p.textSize = p.textSize * maxW / tw
            val fm = p.fontMetrics
            c.drawText(label, x0 + w / 2f, y0 + h - barH / 2f - (fm.ascent + fm.descent) / 2f, p)
        }

        val gs = w * 0.46f
        val cx = x0 + w * 0.47f
        val cy = y0 + (h - barH) * 0.56f
        drawGlyph(c, cx - gs / 2f, cy - gs / 2f, gs)
    }

    // ---------- simbol ----------

    private fun drawGlyph(c: Canvas, gx: Float, gy: Float, gs: Float) {
        fun u(x: Float) = gx + x * gs
        fun v(y: Float) = gy + y * gs
        val col = glyph
        when (kind) {
            "pdf" -> {
                stroke(col, gs * 0.11f)
                path.reset()
                path.moveTo(u(.5f), v(.05f))
                path.cubicTo(u(.66f), v(.45f), u(.75f), v(.65f), u(.93f), v(.85f))
                path.cubicTo(u(.6f), v(.62f), u(.3f), v(.72f), u(.08f), v(.9f))
                path.cubicTo(u(.34f), v(.7f), u(.44f), v(.4f), u(.5f), v(.05f))
                path.close()
                c.drawPath(path, p)
            }
            "doc", "docx" -> {
                val sw = if (kind == "docx") gs * 0.8f else gs
                fill(col); rr(c, gx, gy, gx + sw, gy + gs, gs * 0.18f)
                text(c, "W", gx + sw / 2f, gy + gs / 2f, gs * 0.72f, Color.WHITE)
                if (kind == "docx") {
                    fill(col)
                    for (i in 0..2) rr(c, u(.86f), v(.26f + i * .22f), u(1f), v(.33f + i * .22f), gs * 0.035f)
                }
            }
            "xls", "xlsx", "csv" -> {
                fill(col); rr(c, gx, gy, gx + gs, gy + gs, gs * 0.16f)
                fill(body)
                val s = gs * 0.17f
                val gap = gs * 0.07f
                val start = gs / 2f - (s * 3f + gap * 2f) / 2f
                for (i in 0..2) for (j in 0..2)
                    rr(c, gx + start + j * (s + gap), gy + start + i * (s + gap), gx + start + j * (s + gap) + s, gy + start + i * (s + gap) + s, s * 0.2f)
            }
            "ppt", "pptx" -> {
                fill(col); rr(c, gx, gy, gx + gs, gy + gs, gs * 0.18f)
                text(c, "P", gx + gs * 0.46f, gy + gs / 2f, gs * 0.72f, Color.WHITE)
            }
            "txt", "file" -> {
                fill(col)
                rr(c, u(0f), v(.18f), u(1f), v(.28f), gs * 0.05f)
                rr(c, u(0f), v(.45f), u(1f), v(.55f), gs * 0.05f)
                rr(c, u(0f), v(.72f), u(.6f), v(.82f), gs * 0.05f)
            }
            "log" -> {
                stroke(col, gs * 0.09f)
                rr(c, u(.12f), v(0f), u(.88f), v(1f), gs * 0.12f)
                fill(col)
                rr(c, u(.28f), v(.28f), u(.72f), v(.36f), gs * 0.03f)
                rr(c, u(.28f), v(.48f), u(.72f), v(.56f), gs * 0.03f)
                rr(c, u(.28f), v(.68f), u(.55f), v(.76f), gs * 0.03f)
            }
            "rar" -> {
                for (i in 2 downTo 0) {
                    val y = v(.3f + .2f * i)
                    path.reset()
                    path.moveTo(u(0f), y)
                    path.lineTo(u(.5f), y - gs * 0.22f)
                    path.lineTo(u(1f), y)
                    path.lineTo(u(.5f), y + gs * 0.22f)
                    path.close()
                    fill(col); c.drawPath(path, p)
                    stroke(body, gs * 0.05f); c.drawPath(path, p)
                }
            }
            "zip", "gz", "bz2" -> {
                fill(col)
                val zw = gs * 0.24f
                val zh = gs * 0.09f
                for (i in 0..5) {
                    val xx = if (i % 2 == 0) u(.5f) - zw else u(.5f)
                    rr(c, xx, v(i * 0.12f), xx + zw, v(i * 0.12f) + zh, zh * 0.3f)
                }
                stroke(col, gs * 0.07f)
                c.drawCircle(u(.5f), v(.86f), gs * 0.13f, p)
            }
            "7z" -> text(c, "7z", gx + gs / 2f, gy + gs / 2f, gs * 0.95f, col)
            "tar" -> {
                path.reset()
                for (i in 0..5) {
                    val ang = Math.toRadians((-90 + 60 * i).toDouble())
                    val px = (gx + gs / 2f + Math.cos(ang) * gs * 0.52).toFloat()
                    val py = (gy + gs / 2f + Math.sin(ang) * gs * 0.52).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                fill(col); c.drawPath(path, p)
                stroke(body, gs * 0.06f)
                for (i in 0..2) {
                    val ang = Math.toRadians((30 + 120 * i).toDouble())
                    c.drawLine(gx + gs / 2f, gy + gs / 2f,
                        (gx + gs / 2f + Math.cos(ang) * gs * 0.52).toFloat(), (gy + gs / 2f + Math.sin(ang) * gs * 0.52).toFloat(), p)
                }
            }
            "json" -> text(c, "{ }", gx + gs / 2f, gy + gs / 2f, gs * 0.92f, col)
            "xml", "html" -> text(c, "</>", gx + gs / 2f, gy + gs / 2f, gs * 0.8f, col)
            "css" -> {
                path.reset()
                path.moveTo(u(.1f), v(.04f)); path.lineTo(u(.9f), v(.04f)); path.lineTo(u(.82f), v(.74f))
                path.lineTo(u(.5f), v(.98f)); path.lineTo(u(.18f), v(.74f)); path.close()
                fill(col); c.drawPath(path, p)
                text(c, "3", u(.5f), v(.47f), gs * 0.6f, Color.WHITE)
            }
            "js" -> text(c, "JS", gx + gs / 2f, gy + gs / 2f, gs * 1.0f, col)
            "ai" -> text(c, "Ai", gx + gs / 2f, gy + gs / 2f, gs * 1.0f, col)
            "psd" -> text(c, "Ps", gx + gs / 2f, gy + gs / 2f, gs * 1.0f, col)
            "indd" -> text(c, "Id", gx + gs / 2f, gy + gs / 2f, gs * 1.0f, col)
            "png", "jpg", "webp", "tif" -> {
                fill(col); rr(c, gx, v(.08f), gx + gs, v(.92f), gs * 0.12f)
                path.reset()
                path.moveTo(u(.1f), v(.82f)); path.lineTo(u(.38f), v(.45f)); path.lineTo(u(.58f), v(.7f))
                path.lineTo(u(.7f), v(.58f)); path.lineTo(u(.9f), v(.82f)); path.close()
                fill(body); c.drawPath(path, p)
                c.drawCircle(u(.72f), v(.33f), gs * 0.08f, p)
            }
            "gif", "mp4" -> {
                path.reset()
                path.moveTo(u(.22f), v(.08f)); path.lineTo(u(.92f), v(.5f)); path.lineTo(u(.22f), v(.92f)); path.close()
                fill(col); c.drawPath(path, p)
                stroke(col, gs * 0.1f); p.strokeJoin = Paint.Join.ROUND; c.drawPath(path, p)
            }
            "mov" -> {
                stroke(col, gs * 0.14f)
                c.drawCircle(u(.5f), v(.5f), gs * 0.38f, p)
                path.reset()
                path.moveTo(u(.42f), v(.34f)); path.lineTo(u(.68f), v(.5f)); path.lineTo(u(.42f), v(.66f)); path.close()
                fill(col); c.drawPath(path, p)
            }
            "avi", "mkv" -> {
                fill(col); rr(c, gx, gy, gx + gs, gy + gs, gs * 0.12f)
                fill(body)
                for (i in 0..3) {
                    rr(c, u(.08f), v(.1f + i * .21f), u(.2f), v(.2f + i * .21f), gs * 0.02f)
                    rr(c, u(.8f), v(.1f + i * .21f), u(.92f), v(.2f + i * .21f), gs * 0.02f)
                }
                rr(c, u(.3f), v(.12f), u(.7f), v(.46f), gs * 0.03f)
                rr(c, u(.3f), v(.54f), u(.7f), v(.88f), gs * 0.03f)
            }
            "mp3" -> {
                fill(col)
                rect.set(u(.1f), v(.66f), u(.46f), v(.94f)); c.drawOval(rect, p)
                rect.set(u(.58f), v(.54f), u(.94f), v(.82f)); c.drawOval(rect, p)
                rr(c, u(.4f), v(.2f), u(.48f), v(.8f), gs * 0.02f)
                rr(c, u(.88f), v(.08f), u(.96f), v(.68f), gs * 0.02f)
                path.reset()
                path.moveTo(u(.4f), v(.2f)); path.lineTo(u(.96f), v(.06f)); path.lineTo(u(.96f), v(.26f)); path.lineTo(u(.4f), v(.4f)); path.close()
                c.drawPath(path, p)
            }
            "wav", "ogg", "flac" -> {
                fill(col)
                val hs = floatArrayOf(.25f, .5f, .8f, 1f, .7f, .45f, .25f)
                val bw = gs * 0.08f
                for (i in hs.indices) {
                    val xx = gx + (i + 0.5f) * gs / hs.size - bw / 2f
                    val hh = hs[i] * gs
                    rr(c, xx, gy + gs / 2f - hh / 2f, xx + bw, gy + gs / 2f + hh / 2f, bw / 2f)
                }
            }
            "svg", "eps" -> {
                stroke(col, gs * 0.07f)
                path.reset()
                path.moveTo(u(.12f), v(.8f)); path.cubicTo(u(.12f), v(.05f), u(.88f), v(.05f), u(.88f), v(.8f))
                c.drawPath(path, p)
                fill(col)
                rr(c, u(.02f), v(.72f), u(.22f), v(.92f), gs * 0.03f)
                rr(c, u(.78f), v(.72f), u(.98f), v(.92f), gs * 0.03f)
            }
            "exe" -> {
                fill(col)
                for (i in 0..7) {
                    c.save()
                    c.rotate(i * 45f, u(.5f), v(.5f))
                    rr(c, u(.5f) - gs * 0.08f, v(0f), u(.5f) + gs * 0.08f, v(.24f), gs * 0.03f)
                    c.restore()
                }
                c.drawCircle(u(.5f), v(.5f), gs * 0.32f, p)
                fill(body); c.drawCircle(u(.5f), v(.5f), gs * 0.13f, p)
            }
            else -> {
                fill(col)
                rr(c, u(0f), v(.3f), u(1f), v(.4f), gs * 0.05f)
                rr(c, u(0f), v(.6f), u(.6f), v(.7f), gs * 0.05f)
            }
        }
    }

    // ---------- alat gambar ----------

    private fun fill(color: Int) {
        p.reset()
        p.isAntiAlias = true
        p.style = Paint.Style.FILL
        p.color = color
        p.alpha = Color.alpha(color) * alphaAll / 255
    }

    private fun stroke(color: Int, width: Float) {
        p.reset()
        p.isAntiAlias = true
        p.style = Paint.Style.STROKE
        p.strokeWidth = width
        p.strokeCap = Paint.Cap.ROUND
        p.strokeJoin = Paint.Join.ROUND
        p.color = color
        p.alpha = Color.alpha(color) * alphaAll / 255
    }

    private fun rr(c: Canvas, l: Float, t: Float, r: Float, b: Float, rad: Float) {
        rect.set(l, t, r, b)
        c.drawRoundRect(rect, rad, rad, p)
    }

    private fun text(c: Canvas, s: String, cx: Float, cy: Float, size: Float, color: Int) {
        fill(color)
        p.typeface = Typeface.DEFAULT_BOLD
        p.textAlign = Paint.Align.CENTER
        p.textSize = size
        val fm = p.fontMetrics
        c.drawText(s, cx, cy - (fm.ascent + fm.descent) / 2f, p)
    }

    override fun setAlpha(alpha: Int) { alphaAll = alpha; invalidateSelf() }
    override fun setColorFilter(cf: ColorFilter?) {}
    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    companion object {
        private fun darken(c: Int, f: Float) = Color.rgb(
            (Color.red(c) * f).toInt(), (Color.green(c) * f).toInt(), (Color.blue(c) * f).toInt())

        private fun blend(a: Int, b: Int, t: Float) = Color.rgb(
            (Color.red(a) + (Color.red(b) - Color.red(a)) * t).toInt(),
            (Color.green(a) + (Color.green(b) - Color.green(a)) * t).toInt(),
            (Color.blue(a) + (Color.blue(b) - Color.blue(a)) * t).toInt())

        private fun col(body: String, bar: String) = Pair(Color.parseColor(body), Color.parseColor(bar))

        // warna (badan, label) diambil dari gambar acuan
        private val COLORS: Map<String, Pair<Int, Int>> = mapOf(
            "pdf" to col("#FEE6E6", "#E5726E"), "doc" to col("#D2E5FE", "#65A0EE"), "docx" to col("#C7DBFD", "#538CE6"),
            "xls" to col("#D8ECD5", "#8DC68C"), "xlsx" to col("#BAE4DB", "#54B29E"), "ppt" to col("#FEDFC4", "#F9A15E"),
            "pptx" to col("#FED5C6", "#FC8664"), "txt" to col("#D7DDE7", "#949FB4"), "rar" to col("#D5CBF3", "#9983CA"),
            "zip" to col("#FEE7AC", "#F1C863"), "7z" to col("#C5CCD9", "#808BA2"), "tar" to col("#E2C7B2", "#BB8F6D"),
            "gz" to col("#B7E6E2", "#53B5A8"), "bz2" to col("#D4C2EF", "#957AC4"), "json" to col("#C6EBFA", "#67BCD6"),
            "xml" to col("#FED7BF", "#F4A57C"), "html" to col("#FDCDB9", "#FD9673"), "css" to col("#C5E4FD", "#65ADEF"),
            "js" to col("#FEEEBD", "#F3CD6A"), "png" to col("#D9CFF5", "#A592D7"), "jpg" to col("#C2E5C6", "#7BBF81"),
            "gif" to col("#FDCCDF", "#E586A9"), "svg" to col("#D7CDF4", "#A394D5"), "webp" to col("#C0E7EB", "#6CBAC3"),
            "mp3" to col("#FBCAD7", "#E6839C"), "wav" to col("#CCE7FD", "#6DB3F1"), "ogg" to col("#DCD3F6", "#AA9AD8"),
            "flac" to col("#C6E6C6", "#7DC081"), "mp4" to col("#BFD4FC", "#729CDE"), "avi" to col("#D3C9F4", "#9F8CD2"),
            "mov" to col("#C4ECFD", "#6ABEDF"), "mkv" to col("#C6CDDC", "#8995AA"), "eps" to col("#D3C1EE", "#977CC7"),
            "ai" to col("#FED6B6", "#F8A665"), "psd" to col("#BFD3FC", "#789DE4"), "indd" to col("#FCB8D5", "#E56A99"),
            "tif" to col("#B6E8EE", "#64BEC6"), "csv" to col("#C5E6C5", "#88C387"), "log" to col("#C8CEDC", "#939EB3"),
            "exe" to col("#C5DFFD", "#74ABEA"), "file" to col("#E1E5EC", "#9AA4B8")
        )

        // ekstensi -> jenis dasar
        private val KIND: Map<String, String> = HashMap<String, String>().apply {
            fun m(kind: String, vararg exts: String) { exts.forEach { put(it, kind) } }
            m("pdf", "pdf")
            m("doc", "doc", "dot", "rtf", "odt", "pages")
            m("docx", "docx", "docm", "dotx")
            m("xls", "xls", "ods")
            m("xlsx", "xlsx", "xlsm", "xltx")
            m("csv", "csv", "tsv")
            m("ppt", "ppt", "pps", "odp", "key")
            m("pptx", "pptx", "pptm", "ppsx")
            m("txt", "txt", "text", "md", "srt", "ini", "conf", "vtt")
            m("log", "log")
            m("rar", "rar")
            m("zip", "zip", "jar", "zipx")
            m("7z", "7z")
            m("tar", "tar")
            m("gz", "gz", "tgz", "gzip")
            m("bz2", "bz2", "xz", "zst", "lz", "lzma")
            m("json", "json", "jsonl")
            m("xml", "xml", "xsl", "rss")
            m("html", "html", "htm", "xhtml")
            m("css", "css", "scss", "sass")
            m("js", "js", "mjs", "ts", "jsx", "tsx")
            m("png", "png", "bmp", "ico")
            m("jpg", "jpg", "jpeg", "jfif")
            m("gif", "gif")
            m("svg", "svg")
            m("webp", "webp", "avif", "heic", "heif")
            m("tif", "tif", "tiff")
            m("mp3", "mp3", "m4a", "aac", "wma", "amr", "mid", "midi")
            m("wav", "wav")
            m("ogg", "ogg", "opus", "oga")
            m("flac", "flac")
            m("mp4", "mp4", "m4v", "webm", "3gp", "mpeg", "mpg")
            m("avi", "avi", "wmv", "flv")
            m("mov", "mov")
            m("mkv", "mkv")
            m("eps", "eps")
            m("ai", "ai")
            m("psd", "psd")
            m("indd", "indd")
            m("exe", "exe", "msi", "apk", "apks", "xapk", "bat", "sh", "bin", "dmg", "iso")
        }
    }
}
