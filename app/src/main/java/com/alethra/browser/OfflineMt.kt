package com.alethra.browser

import android.content.Context

/* =====================================================================
 *  Mesin terjemahan offline tambahan (semua lewat CTranslate2, pustaka native yang sama dengan NLLB):
 *   - M2M100 418M : satu model untuk ±100 bahasa, langsung sumber -> tujuan
 *   - Opus-MT     : model Marian kecil per pasangan bahasa (±75 MB)
 *   - Argos       : model Argos Translate per pasangan bahasa (paket .argosmodel)
 *  Model pasangan (Opus-MT, Argos) hanya satu arah; bila pasangan langsung tidak terpasang, teks
 *  diterjemahkan lewat Inggris (sumber -> Inggris -> tujuan) bila kedua arahnya terpasang.
 *  Model diunduh dengan [ModelDownloader] yang sama dengan model suara.
 * ===================================================================== */

/** Jembatan JNI ke libalethra_nllb.so (fungsi MtNative_* di native/nllb/nllb_jni.cpp). */
object MtNative {
    val available: Boolean get() = NllbNative.available
    /** kind: 1 = M2M100, 2 = Opus-MT (Marian), 3 = Argos. */
    external fun load(id: String, dir: String, threads: Int, kind: Int)
    external fun translate(id: String, text: ByteArray, src: String, tgt: String): ByteArray
    external fun release(id: String)
    external fun releaseAll()
}

object OfflineMt {
    /** Satu model pasangan bahasa (sumber -> tujuan). */
    class MtPair(val engine: String, val src: String, val tgt: String, val model: AsrModel)

    /** Tidak ada model yang cocok terpasang; pesan siap tampil. */
    class Missing(msg: String) : Exception(msg)

    const val M2M = "m2m"
    const val OPUS = "opus"
    const val ARGOS = "argos"
    val engines = listOf(M2M, OPUS, ARGOS)

    private val langNames = mapOf("ja" to "Jepang", "en" to "Inggris", "id" to "Indonesia", "zh" to "Mandarin", "ko" to "Korea")
    private fun nm(c: String) = langNames[c] ?: c

    val m2m = AsrModel("m2m100-418m", "M2M100 418M",
        "Satu model untuk ±100 bahasa, langsung dari bahasa sumber ke tujuan tanpa perantara Inggris. Kualitas di bawah NLLB-200 tetapi lebih ringan (±490 MB, ±600 MB RAM). Lisensi MIT.",
        "ct2", "jncraton/m2m100_418M-ct2-int8",
        listOf("(otomatis)" to 491_000_000L), dynamic = true)

    // Opus-MT (Helsinki-NLP) yang dikonversi ke CTranslate2 int8. "jap" = kode Jepang di nama repo en->ja.
    private fun opus(src: String, tgt: String, repoCode: String, mb: Long) = MtPair(OPUS, src, tgt,
        AsrModel("opus-$src-$tgt", "Opus-MT ${nm(src)} → ${nm(tgt)}",
            "Model Marian kecil khusus ${nm(src)} ke ${nm(tgt)} (CTranslate2 int8). Ringan dan cepat; kualitas sedang. Lisensi CC-BY 4.0.",
            "ct2", "manancode/opus-mt-$repoCode-ctranslate2-android", listOf("(otomatis)" to mb * 1_000_000L), dynamic = true))

    private fun argos(src: String, tgt: String, mb: Long) = MtPair(ARGOS, src, tgt,
        AsrModel("argos-$src-$tgt", "Argos ${nm(src)} → ${nm(tgt)}",
            "Paket Argos Translate untuk ${nm(src)} ke ${nm(tgt)} (CTranslate2). Ringan; kualitas sedang. Lisensi paket MIT/CC0 sesuai Argos Translate.",
            "argos", "$src-$tgt", listOf("(otomatis)" to mb * 1_000_000L), dynamic = true))

    val opusPairs: List<MtPair> = listOf(
        opus("ja", "en", "ja-en", 78), opus("en", "ja", "en-jap", 72),
        opus("en", "id", "en-id", 75), opus("id", "en", "id-en", 75),
        opus("zh", "en", "zh-en", 80), opus("en", "zh", "en-zh", 80))

    val argosPairs: List<MtPair> = listOf(
        argos("ja", "en", 100), argos("en", "ja", 100),
        argos("en", "id", 100), argos("id", "en", 100),
        argos("zh", "en", 100), argos("en", "zh", 100),
        argos("ko", "en", 100), argos("en", "ko", 100))

    /** Semua model terjemahan tambahan (dipakai untuk mencari model berdasarkan id). */
    val allModels: List<AsrModel> get() = listOf(m2m) + opusPairs.map { it.model } + argosPairs.map { it.model }

    fun pairs(engine: String): List<MtPair> = when (engine) { OPUS -> opusPairs; ARGOS -> argosPairs; else -> emptyList() }

    fun engineTitle(engine: String) = when (engine) { M2M -> "M2M100"; OPUS -> "Opus-MT"; ARGOS -> "Argos"; else -> engine }

    fun installed(ctx: Context, m: AsrModel) = OfflineModels.installed(ctx, m)

    /** Siap dipakai: pustaka native ada dan setidaknya satu model mesin ini terpasang. */
    fun ready(ctx: Context, engine: String): Boolean {
        if (!MtNative.available) return false
        return when (engine) {
            M2M -> installed(ctx, m2m)
            else -> pairs(engine).any { installed(ctx, it.model) }
        }
    }

    private val lock = Any()
    private val loaded = HashSet<String>()
    private fun threads(): Int = (Runtime.getRuntime().availableProcessors() / 2).coerceIn(2, 4)

    private fun kindOf(engine: String) = when (engine) { M2M -> 1; OPUS -> 2; else -> 3 }

    /** Muat model bila belum. Hanya beberapa model dijaga di memori sekaligus. */
    private fun ensure(ctx: Context, engine: String, m: AsrModel) {
        synchronized(lock) {
            if (m.id in loaded) return
            if (!MtNative.available) throw IllegalStateException("Mesin terjemahan tidak tersedia di build ini")
            if (!installed(ctx, m)) throw IllegalStateException("Model belum diunduh")
            if (loaded.size >= 3) { try { MtNative.releaseAll() } catch (e: Throwable) { }; loaded.clear() }
            MtNative.load(m.id, OfflineModels.dir(ctx, m.id).absolutePath, threads(), kindOf(engine))
            loaded.add(m.id)
        }
    }

    /** Muat M2M100 lebih awal (dipanggil saat suara ke teks dinyalakan). */
    fun preloadM2m(ctx: Context) = ensure(ctx, M2M, m2m)

    fun releaseAll() {
        synchronized(lock) {
            if (loaded.isNotEmpty()) { try { MtNative.releaseAll() } catch (e: Throwable) { } }
            loaded.clear()
        }
    }

    /** Lepas satu model (mis. sebelum dihapus dari penyimpanan). */
    fun release(id: String) {
        synchronized(lock) {
            if (loaded.remove(id)) { try { MtNative.release(id) } catch (e: Throwable) { } }
        }
    }

    /** Kode bahasa Alethra / ML Kit (id, zh-CN, ja, ...) -> kode dua huruf yang dipakai model pasangan. */
    fun norm(tag: String): String {
        val t = tag.trim()
        return when (t) { "zh-CN", "zh-TW", "zh", "yue" -> "zh"; "iw" -> "he"; else -> t.substringBefore('-').lowercase() }
    }

    // Bahasa M2M100 (token "__xx__")
    private val m2mLangs = ("af am ar ast az ba be bg bn br bs ca ceb cs cy da de el en es et fa ff fi fr fy ga gd gl gu ha he hi hr ht hu hy id ig ilo is it ja jv ka kk km kn ko lb lg ln lo lt lv mg mk ml mn mr ms my ne nl no ns oc or pa pl ps pt ro ru sd si sk sl so sq sr ss su sv sw ta th tl tn tr uk ur uz vi wo xh yi yo zh zu").split(' ').toSet()

    fun m2mCode(tag: String): String? {
        val c = when (norm(tag)) { "fil" -> "tl"; else -> norm(tag) }
        return if (c in m2mLangs) "__${c}__" else null
    }

    /** Urutan model yang dipakai untuk src -> tgt, atau null bila tidak ada (langsung, atau lewat Inggris). */
    private fun route(ctx: Context, engine: String, s: String, t: String): List<MtPair>? {
        val ps = pairs(engine).filter { installed(ctx, it.model) }
        ps.firstOrNull { it.src == s && it.tgt == t }?.let { return listOf(it) }
        val a = ps.firstOrNull { it.src == s && it.tgt == "en" }
        val b = ps.firstOrNull { it.src == "en" && it.tgt == t }
        return if (a != null && b != null) listOf(a, b) else null
    }

    /** [src] dan [tgt] = kode bahasa Alethra. Memblokir: panggil dari thread latar. Melempar [Missing] bila model tidak ada. */
    fun translate(ctx: Context, engine: String, text: String, src: String, tgt: String): String {
        val s = norm(src); val t = norm(tgt)
        if (s == t) return text
        if (engine == M2M) {
            val sc = m2mCode(s); val tc = m2mCode(t)
            if (sc == null || tc == null) throw Missing("Bahasa tidak didukung M2M100, pakai cadangan online")
            ensure(ctx, M2M, m2m)
            return run(m2m.id, text, sc, tc)
        }
        val steps = route(ctx, engine, s, t)
            ?: throw Missing("Model " + engineTitle(engine) + " " + nm(s) + " → " + nm(t) + " belum terpasang, pakai cadangan online")
        var cur = text
        for (p in steps) {
            ensure(ctx, engine, p.model)
            cur = run(p.model.id, cur, "", "")
            if (cur.isBlank()) break
        }
        return cur
    }

    private fun run(id: String, text: String, src: String, tgt: String): String =
        String(MtNative.translate(id, text.toByteArray(Charsets.UTF_8), src, tgt), Charsets.UTF_8).trim()
}
