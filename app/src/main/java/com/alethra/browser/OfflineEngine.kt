package com.alethra.browser

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.k2fsa.sherpa.onnx.FeatureConfig
import com.k2fsa.sherpa.onnx.OfflineModelConfig
import com.k2fsa.sherpa.onnx.OfflineRecognizer
import com.k2fsa.sherpa.onnx.OfflineRecognizerConfig
import com.k2fsa.sherpa.onnx.OfflineSenseVoiceModelConfig
import com.k2fsa.sherpa.onnx.OfflineNemoEncDecCtcModelConfig
import com.k2fsa.sherpa.onnx.OfflineMoonshineModelConfig
import com.k2fsa.sherpa.onnx.OfflineQwen3AsrModelConfig
import com.k2fsa.sherpa.onnx.OfflineFunAsrNanoModelConfig
import com.k2fsa.sherpa.onnx.OfflineTransducerModelConfig
import com.k2fsa.sherpa.onnx.OfflineZipformerCtcModelConfig
import com.k2fsa.sherpa.onnx.OfflineWhisperModelConfig
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.json.JSONObject

/* =====================================================================
 * Mesin offline untuk Suara ke teks:
 *  - Suara -> teks : sherpa-onnx (SenseVoice / Whisper), model diunduh sekali ke penyimpanan aplikasi
 *  - Terjemahan    : ML Kit On-device Translation (model per bahasa, memakai Inggris sebagai perantara)
 * Tidak ada yang di sini menyentuh UI; MainActivity memanggilnya dan menampilkan hasilnya.
 * ===================================================================== */

/** Satu model suara ke teks. [files] = nama berkas di repo Hugging Face + perkiraan ukuran (byte). */
class AsrModel(
    val id: String,
    val title: String,
    val note: String,
    val kind: String,            // "sense" atau "whisper"
    val repo: String,
    val files: List<Pair<String, Long>>,
    val prefix: String = "",     // awalan nama berkas Whisper (tiny, base.en, ...)
    val host: String = "https://huggingface.co",
    /** true = daftar berkas dibaca dari API Hugging Face saat diunduh; [files] hanya perkiraan ukuran untuk tampilan. */
    val dynamic: Boolean = false,
    /** Bahasa tetap model (mis. "ja" untuk model khusus Jepang); dipakai bila model tidak melaporkan bahasa. */
    val fixedLang: String = ""
) {
    val totalBytes: Long get() = files.sumOf { it.second }
    fun url(file: String) = "$host/$repo/resolve/main/$file"
}

object OfflineModels {
    private fun whisper(id: String, title: String, note: String, name: String, repo: String, enc: Long, dec: Long, tok: Long) =
        AsrModel(id, title, note, "whisper", repo,
            listOf("$name-encoder.int8.onnx" to enc, "$name-decoder.int8.onnx" to dec, "$name-tokens.txt" to tok), name)

    val asr: List<AsrModel> = listOf(
        AsrModel("zipformer-ja", "Zipformer Jepang (ReazonSpeech)",
            "Khusus bahasa Jepang, tidak mengenali bahasa lain. Cepat dan akurat untuk ucapan Jepang sehari-hari. Ukuran ±170 MB.", "transducer",
            "reazon-research/reazonspeech-k2-v2",
            listOf("(otomatis)" to 170_000_000L), dynamic = true, fixedLang = "ja"),
        dyn("parakeet-ja", "Parakeet TDT-CTC Jepang (NeMo)",
            "Khusus bahasa Jepang (NVIDIA Parakeet 0.6B, dilatih ±35.000 jam, jenis CTC, int8). Akurat dan memberi tanda baca, tapi berat: ±650 MB dan lebih lambat di HP biasa.",
            "ctc-nemo", "csukuangfj/sherpa-onnx-nemo-parakeet-tdt_ctc-0.6b-ja-35000-int8", 656_000_000L, "ja"),
        dyn("moonshine-ja-tiny", "Moonshine Tiny Jepang",
            "Khusus bahasa Jepang. Sangat ringan dan cepat, cocok untuk HP biasa; akurasi di bawah model besar.",
            "moonshine", "csukuangfj/sherpa-onnx-moonshine-tiny-ja-quantized-2026-02-27", 40_000_000L, "ja"),
        dyn("moonshine-ja-base", "Moonshine Base Jepang",
            "Khusus bahasa Jepang. Lebih akurat dari Moonshine Tiny, masih ringan.",
            "moonshine", "csukuangfj/sherpa-onnx-moonshine-base-ja-quantized-2026-02-27", 130_000_000L, "ja"),
        dyn("funasr-nano", "FunASR Nano (Fun-ASR-Nano)",
            "Model suara berbasis LLM (Mandarin, Inggris, Jepang) dengan tanda baca. Akurat tapi berat: ±1 GB, memakai banyak RAM dan lebih lambat di HP biasa.",
            "funasr-nano", "csukuangfj/sherpa-onnx-funasr-nano-int8-2025-12-30", 900_000_000L, "ja"),
        dyn("qwen3-asr", "Qwen3-ASR 0.6B",
            "Model suara berbasis LLM (Qwen3) multibahasa, mengenali bahasa otomatis, memberi tanda baca. Akurat tapi berat: ±1 GB, memakai banyak RAM dan lebih lambat di HP biasa.",
            "qwen3-asr", "csukuangfj/sherpa-onnx-qwen3-asr-0.6B-int8-2026-03-25", 950_000_000L, "ja"),
        AsrModel("sensevoice", "SenseVoice",
            "Mandarin, Kanton, Jepang, Korea, Inggris. Cepat dan akurat, mendeteksi bahasa otomatis. Disarankan.", "sense",
            "csukuangfj/sherpa-onnx-sense-voice-zh-en-ja-ko-yue-int8-2025-09-09",
            listOf("model.int8.onnx" to 237_000_000L, "tokens.txt" to 316_000L)),
        whisper("whisper-tiny", "Whisper tiny", "Semua bahasa (±99). Paling ringan, akurasi dasar.", "tiny",
            "csukuangfj/sherpa-onnx-whisper-tiny", 12_900_000L, 89_900_000L, 817_000L),
        whisper("whisper-base", "Whisper base", "Semua bahasa (±99). Seimbang antara kecepatan dan akurasi.", "base",
            "csukuangfj/sherpa-onnx-whisper-base", 29_100_000L, 131_000_000L, 817_000L),
        whisper("whisper-small", "Whisper small", "Semua bahasa (±99). Paling akurat, tapi berat dan lambat di HP biasa (tunda beberapa detik).", "small",
            "csukuangfj/sherpa-onnx-whisper-small", 112_000_000L, 262_000_000L, 817_000L),
        whisper("whisper-base-en", "Whisper base · Inggris", "Khusus bahasa Inggris. Lebih akurat dari base biasa untuk video Inggris.", "base.en",
            "csukuangfj/sherpa-onnx-whisper-base.en", 29_100_000L, 131_000_000L, 836_000L),
        // ---- Model satu bahasa (daftar berkas dibaca dari Hugging Face saat diunduh; ukuran hanya perkiraan) ----
        dyn("zipformer-ko", "Zipformer Korea", "Khusus bahasa Korea. Ringan dan cepat.", "transducer", "k2-fsa/sherpa-onnx-zipformer-korean-2024-06-24", 85_000_000L, "ko"),
        dyn("zipformer-th", "Zipformer Thai", "Khusus bahasa Thai.", "transducer", "k2-fsa/sherpa-onnx-zipformer-thai-2024-06-20", 100_000_000L, "th"),
        dyn("zipformer-ru", "Zipformer Rusia", "Khusus bahasa Rusia.", "transducer", "k2-fsa/sherpa-onnx-zipformer-ru-2024-09-18", 100_000_000L, "ru"),
        dyn("zipformer-ru-small", "Zipformer Rusia (kecil)", "Khusus bahasa Rusia. Versi lebih kecil dan cepat, akurasi sedikit di bawah versi biasa.", "transducer", "k2-fsa/sherpa-onnx-small-zipformer-ru-2024-09-18", 40_000_000L, "ru"),
        dyn("nemo-giga-ru", "NeMo GigaAM Rusia (transducer)", "Khusus bahasa Rusia (NeMo GigaAM).", "nemo-transducer", "k2-fsa/sherpa-onnx-nemo-transducer-giga-am-russian-2024-10-24", 130_000_000L, "ru"),
        dyn("nemo-giga-ru-ctc", "NeMo GigaAM Rusia (CTC)", "Khusus bahasa Rusia (NeMo GigaAM, jenis CTC).", "ctc-nemo", "k2-fsa/sherpa-onnx-nemo-ctc-giga-am-russian-2024-10-24", 100_000_000L, "ru"),
        dyn("zipformer-vi-30m", "Zipformer Vietnam (kecil)", "Khusus bahasa Vietnam. Sangat ringan (30 juta parameter).", "transducer", "k2-fsa/sherpa-onnx-zipformer-vi-30M-int8-2026-02-09", 40_000_000L, "vi"),
        dyn("zipformer-vi", "Zipformer Vietnam", "Khusus bahasa Vietnam (versi int8).", "transducer", "k2-fsa/sherpa-onnx-zipformer-vi-int8-2025-04-20", 80_000_000L, "vi"),
        dyn("zipformer-yue", "Zipformer Kanton", "Khusus bahasa Kanton.", "transducer", "k2-fsa/sherpa-onnx-zipformer-cantonese-2024-03-13", 80_000_000L, "yue"),
        dyn("zipformer-ctc-zh", "Zipformer CTC Mandarin", "Khusus bahasa Mandarin (jenis CTC, int8).", "ctc-zipformer", "k2-fsa/sherpa-onnx-zipformer-ctc-zh-int8-2025-07-03", 150_000_000L, "zh"),
        dyn("telespeech-zh", "TeleSpeech Mandarin", "Khusus Mandarin, mendukung banyak dialek Tionghoa. Lebih berat.", "ctc-tele", "k2-fsa/sherpa-onnx-telespeech-ctc-int8-zh-2024-06-04", 340_000_000L, "zh"),
        dyn("zipformer-en-giga", "Zipformer Inggris (GigaSpeech)", "Khusus bahasa Inggris.", "transducer", "k2-fsa/sherpa-onnx-zipformer-gigaspeech-2023-12-12", 90_000_000L, "en"),
        dyn("zipformer-en", "Zipformer Inggris", "Khusus bahasa Inggris.", "transducer", "k2-fsa/sherpa-onnx-zipformer-en-2023-06-26", 90_000_000L, "en"),
        dyn("parakeet-en", "Parakeet TDT 0.6b v2 · Inggris", "Khusus bahasa Inggris. Akurat tapi cukup berat.", "nemo-transducer", "k2-fsa/sherpa-onnx-nemo-parakeet-tdt-0.6b-v2-int8", 180_000_000L, "en"),
        dyn("whisper-tiny-en", "Whisper tiny · Inggris", "Khusus bahasa Inggris. Paling ringan.", "whisper", "csukuangfj/sherpa-onnx-whisper-tiny.en", 100_000_000L, "en", "tiny.en")
    )

    private fun dyn(id: String, title: String, note: String, kind: String, repo: String, est: Long, lang: String, prefix: String = "") =
        AsrModel(id, title, "$note Ukuran perkiraan, angka pasti muncul saat mengunduh.", kind, repo, listOf("(otomatis)" to est), prefix, dynamic = true, fixedLang = lang)

    /** Model yang sudah dihapus dari daftar: berkasnya di perangkat dibersihkan supaya tidak jadi sampah tak terlihat. */
    private val removedIds = listOf("whisper-small-en", "whisper-turbo", "whisper-medium-en")
    fun cleanupRemoved(ctx: Context) {
        for (id in removedIds) { try { if (dir(ctx, id).exists()) delete(ctx, id) } catch (e: Exception) { } }
    }

    fun find(id: String): AsrModel? = asr.firstOrNull { it.id == id } ?: OfflineNllb.model.takeIf { it.id == id }
        ?: OfflineMt.allModels.firstOrNull { it.id == id }

    /** Pengelompokan per bahasa untuk daftar model. */
    val langOrder = listOf("ja", "multi", "ko", "zh", "yue", "th", "vi", "ru", "en")
    private val langNames = mapOf("ja" to "Jepang", "multi" to "Multibahasa", "ko" to "Korea", "zh" to "Mandarin", "yue" to "Kanton",
        "th" to "Thai", "vi" to "Vietnam", "ru" to "Rusia", "en" to "Inggris")
    fun group(m: AsrModel): String = when {
        m.fixedLang.isNotEmpty() -> m.fixedLang
        m.prefix.endsWith(".en") -> "en"
        else -> "multi"
    }
    fun groupName(g: String): String = langNames[g] ?: g
    fun groupRank(g: String): Int = langOrder.indexOf(g).let { if (it < 0) 999 else it }

    /** Pilih berkas yang perlu diunduh dari daftar isi repo (hanya berkas di tingkat teratas). Melempar bila tidak lengkap. */
    fun select(m: AsrModel, all: List<Pair<String, Long>>): List<Pair<String, Long>> {
        val top = all.filter { '/' !in it.first }
        fun first(ok: (String) -> Boolean) = top.firstOrNull { ok(it.first) }
        val out = ArrayList<Pair<String, Long>>()
        if (m.kind == "ct2") {
            // model CTranslate2: model.bin + vocabulary + sentencepiece (tanpa README, pytorch, dll.)
            val mod = first { it == "model.bin" } ?: throw RuntimeException("model.bin tidak ditemukan di repo")
            out += mod
            top.filter { it.first != "model.bin" && (it.first == "config.json" || it.first.startsWith("shared_vocabulary") ||
                it.first.startsWith("source_vocabulary") || it.first.startsWith("target_vocabulary") || it.first.startsWith("vocabulary") ||
                it.first.endsWith(".spm") || it.first == "sentencepiece.bpe.model" || it.first == "sentencepiece.model" || it.first == "spiece.model") }
                .forEach { out += it }
            if (out.none { it.first.startsWith("shared_vocabulary") || it.first.startsWith("source_vocabulary") || it.first.startsWith("vocabulary") })
                throw RuntimeException("Berkas kosakata tidak ditemukan di repo")
        } else if (m.kind == "moonshine") {
            val enc = first { it == "encoder_model.ort" } ?: first { it == "encoder_model.onnx" }
                ?: first { it.startsWith("encoder_model") && (it.endsWith(".ort") || it.endsWith(".onnx")) }
            val dec = first { it == "decoder_model_merged.ort" } ?: first { it == "decoder_model_merged.onnx" }
                ?: first { it.startsWith("decoder_model_merged") && (it.endsWith(".ort") || it.endsWith(".onnx")) }
            val tok = first { it == "tokens.txt" }
            if (enc == null || dec == null || tok == null) throw RuntimeException("Berkas model tidak ditemukan di repo")
            out += listOf(enc, dec, tok)
        } else if (m.kind == "funasr-nano" || m.kind == "qwen3-asr") {
            // model LLM: beberapa berkas onnx (int8 bila ada), bobot pendamping (.data), dan satu folder tokenizer
            val names = if (m.kind == "funasr-nano") listOf("encoder_adaptor", "llm", "embedding") else listOf("conv_frontend", "encoder", "decoder")
            val tokDir = if (m.kind == "funasr-nano") "Qwen3-0.6B/" else "tokenizer/"
            for (n in names) {
                val mod = first { it == "$n.int8.onnx" } ?: first { it == "$n.onnx" }
                    ?: throw RuntimeException("Berkas model tidak ditemukan di repo")
                out += mod
                top.filter { it.first.startsWith(mod.first + ".") || (it.first.startsWith(mod.first.removeSuffix(".onnx")) && it.first.endsWith(".data")) }
                    .forEach { if (out.none { o -> o.first == it.first }) out += it }
            }
            val tk = all.filter { it.first.startsWith(tokDir) && !it.first.endsWith("/") }
            if (tk.isEmpty()) throw RuntimeException("Berkas tokenizer tidak ditemukan di repo")
            out += tk
        } else if (m.kind.endsWith("ctc") || m.kind.startsWith("ctc")) {
            val mod = first { it == "model.int8.onnx" } ?: first { it == "model.onnx" }
                ?: first { it.startsWith("model") && it.endsWith(".onnx") }
            val tok = first { it == "tokens.txt" }
            if (mod == null || tok == null) throw RuntimeException("Berkas model tidak ditemukan di repo")
            out += listOf(mod, tok)
        } else if (m.kind == "transducer" || m.kind == "nemo-transducer") {
            val enc = first { it.startsWith("encoder") && it.endsWith(".onnx") && ".int8" in it }
                ?: first { it.startsWith("encoder") && it.endsWith(".onnx") }
            val joi = first { it.startsWith("joiner") && it.endsWith(".onnx") && ".int8" in it }
                ?: first { it.startsWith("joiner") && it.endsWith(".onnx") }
            // decoder kecil, lebih baik versi biasa (int8 pada decoder menurunkan akurasi)
            val dec = first { it.startsWith("decoder") && it.endsWith(".onnx") && "int8" !in it }
                ?: first { it.startsWith("decoder") && it.endsWith(".onnx") }
            val tok = first { it == "tokens.txt" }
            if (enc == null || joi == null || dec == null || tok == null) throw RuntimeException("Berkas model tidak ditemukan di repo")
            out += listOf(enc, dec, joi, tok)
        } else {
            // Whisper: encoder/decoder int8 beserta berkas bobot pendampingnya (nama diawali sama), dan tokens
            val pre = m.prefix
            top.filter { it.first.startsWith("$pre-encoder.int8") || it.first.startsWith("$pre-decoder.int8") }.forEach { out += it }
            first { it.endsWith("tokens.txt") && it.startsWith(pre) }?.let { out += it }
            if (out.none { it.first.startsWith("$pre-encoder.int8") && it.first.endsWith(".onnx") } ||
                out.none { it.first.startsWith("$pre-decoder.int8") && it.first.endsWith(".onnx") } ||
                out.none { it.first.endsWith("tokens.txt") }) throw RuntimeException("Berkas model tidak ditemukan di repo")
        }
        return out
    }

    fun root(ctx: Context) = File(ctx.filesDir, "offline_models")
    fun dir(ctx: Context, id: String) = File(root(ctx), id)
    fun installed(ctx: Context, m: AsrModel) = File(dir(ctx, m.id), ".ok").exists()

    fun sizeOnDisk(ctx: Context, id: String): Long =
        dir(ctx, id).walkTopDown().filter { it.isFile }.sumOf { it.length() }

    fun totalOnDisk(ctx: Context): Long = asr.sumOf { sizeOnDisk(ctx, it.id) }

    fun delete(ctx: Context, id: String) {
        ModelDownloader.cancel(id)
        dir(ctx, id).deleteRecursively()
    }
}

/** Unduhan model satu per satu, bisa dilanjutkan (Range) dan dibatalkan. Berjalan di thread sendiri. */
object ModelDownloader {
    class Job(val id: String, total: Long) {
        @Volatile var total = total
        @Volatile var done = 0L
        @Volatile var cancel = false
        @Volatile var finished = false
        @Volatile var error: String? = null
    }

    private val pool = Executors.newSingleThreadExecutor()
    private val jobs = ConcurrentHashMap<String, Job>()

    /** Dipanggil dari thread unduhan (dibatasi ±4 kali per detik); UI harus pindah ke thread utama sendiri. */
    @Volatile var listener: ((String) -> Unit)? = null

    fun job(id: String): Job? = jobs[id]
    fun running(id: String): Boolean = jobs[id]?.let { !it.finished } ?: false

    fun cancel(id: String) { jobs[id]?.cancel = true }

    /** Baca daftar berkas repo (beserta ukuran) dari API Hugging Face lalu pilih yang perlu. */
    private fun resolveOne(m: AsrModel, repo: String): List<Pair<String, Long>> {
        val c = URL("${m.host}/api/models/$repo?blobs=true").openConnection() as HttpURLConnection
        c.connectTimeout = 15000
        c.readTimeout = 30000
        c.setRequestProperty("User-Agent", "Alethra")
        try {
            val code = c.responseCode
            if (code != 200) throw RuntimeException("Daftar berkas: server menjawab $code")
            val js = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
            val sib = js.getJSONArray("siblings")
            val all = (0 until sib.length()).map { val o = sib.getJSONObject(it); o.getString("rfilename") to o.optLong("size", 0L) }
            return OfflineModels.select(m, all)
        } finally { c.disconnect() }
    }

    /** Model sherpa-onnx pindah-pindah antara organisasi k2-fsa dan csukuangfj di Hugging Face; coba keduanya. */
    private fun resolve(m: AsrModel): Pair<String, List<Pair<String, Long>>> {
        val name = m.repo.substringAfter('/')
        // Model lama ada di k2-fsa/csukuangfj; model rilis 2026 (Moonshine, Qwen3-ASR, ...) diunggah ke csukuangfj2.
        // Hugging Face menjawab 401 untuk repo yang tidak ada, jadi coba semua organisasi.
        val org = m.repo.substringBefore('/')
        val cands = listOf(m.repo) + listOf("csukuangfj", "csukuangfj2", "k2-fsa").filter { it != org }.map { "$it/$name" }
        var err = ""
        for (r in cands) {
            try { return r to resolveOne(m, r) } catch (e: Exception) { if (err.isEmpty()) err = e.message ?: "Gagal" }
        }
        throw RuntimeException(err)
    }


    /** Paket Argos Translate (.argosmodel = zip): tautan dicari di indeks resmi, lalu hanya folder model/ dan sentencepiece.model diekstrak. */
    private fun argosFetch(dir: File, m: AsrModel, job: Job) {
        val ft = m.repo.split("-")
        val from = ft[0]; val to = ft[1]
        var link = ""
        val ic = URL("https://raw.githubusercontent.com/argosopentech/argospm-index/main/index.json").openConnection() as HttpURLConnection
        ic.connectTimeout = 15000; ic.readTimeout = 30000; ic.setRequestProperty("User-Agent", "Alethra")
        try {
            if (ic.responseCode != 200) throw RuntimeException("Indeks Argos: server menjawab ${ic.responseCode}")
            val arr = org.json.JSONArray(ic.inputStream.bufferedReader().use { it.readText() })
            loop@ for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                if (o.optString("from_code") == from && o.optString("to_code") == to) {
                    val l = o.optJSONArray("links") ?: continue
                    for (j in 0 until l.length()) if (l.getString(j).startsWith("http")) { link = l.getString(j); break@loop }
                }
            }
        } finally { ic.disconnect() }
        if (link.isEmpty()) throw RuntimeException("Paket Argos $from-$to tidak ditemukan")

        val zip = File(dir, "pkg.argosmodel")
        val part = File(dir, "pkg.argosmodel.part")
        if (!zip.exists()) {
            var have = if (part.exists()) part.length() else 0L
            val c = URL(link).openConnection() as HttpURLConnection
            c.connectTimeout = 15000; c.readTimeout = 30000; c.instanceFollowRedirects = true
            c.setRequestProperty("User-Agent", "Alethra")
            if (have > 0) c.setRequestProperty("Range", "bytes=$have-")
            try {
                val code = c.responseCode
                if (code == 416 && have > 0) { part.renameTo(zip) }
                else {
                    if (code != 200 && code != 206) throw RuntimeException("Server menjawab $code")
                    val append = code == 206 && have > 0
                    if (!append) have = 0L
                    val len = c.contentLengthLong
                    if (len > 0) job.total = len + have
                    var last = 0L
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
                                val now = System.currentTimeMillis()
                                if (now - last > 250) { last = now; try { listener?.invoke(m.id) } catch (e: Exception) { } }
                            }
                        }
                    }
                    if (job.cancel) return
                    if (part.length() < 1_000_000L) throw RuntimeException("Paket Argos tidak lengkap")
                    if (!part.renameTo(zip)) throw RuntimeException("Gagal menyimpan paket")
                }
            } finally { c.disconnect() }
        }
        // ekstrak: <paket>/model/* -> model/*, <paket>/sentencepiece.model -> sentencepiece.model (stanza dilewati)
        java.util.zip.ZipInputStream(java.io.BufferedInputStream(java.io.FileInputStream(zip))).use { zin ->
            while (true) {
                val e = zin.nextEntry ?: break
                if (e.isDirectory) continue
                val name = e.name.replace('\\', '/')
                if (name.contains("stanza")) continue
                val base = name.substringAfterLast('/')
                val parent = name.substringBeforeLast('/', "").substringAfterLast('/')
                val target = when {
                    parent == "model" -> File(File(dir, "model").also { it.mkdirs() }, base)
                    base == "sentencepiece.model" -> File(dir, "sentencepiece.model")
                    else -> null
                } ?: continue
                FileOutputStream(target).use { o -> zin.copyTo(o) }
            }
        }
        if (!File(dir, "model/model.bin").exists() || !File(dir, "sentencepiece.model").exists())
            throw RuntimeException("Isi paket Argos tidak lengkap")
        zip.delete()
        File(dir, ".ok").createNewFile()
    }

    fun start(ctx: Context, m: AsrModel) {
        if (running(m.id)) return
        val job = Job(m.id, m.totalBytes)
        jobs[m.id] = job
        val dir = OfflineModels.dir(ctx.applicationContext, m.id)
        pool.execute {
            try {
                dir.mkdirs()
                File(dir, ".ok").delete()
                if (m.kind == "argos") { argosFetch(dir, m, job); return@execute }
                var repo = m.repo
                val list: List<Pair<String, Long>> = if (m.dynamic) resolve(m).let { repo = it.first; it.second } else m.files
                if (m.dynamic) {
                    val sum = list.sumOf { it.second }
                    if (sum > 0) job.total = sum
                }
                var base = 0L
                var lastTick = 0L
                fun tick(force: Boolean) {
                    val n = System.currentTimeMillis()
                    if (force || n - lastTick > 250) { lastTick = n; try { listener?.invoke(m.id) } catch (e: Exception) { } }
                }
                for ((name, size) in list) {
                    if (job.cancel) break
                    val target = File(dir, name)
                    target.parentFile?.mkdirs()   // berkas di subfolder (mis. tokenizer/)
                    if (target.exists() && target.length() > 0) { base += size; job.done = base; continue }
                    val part = File(dir, "$name.part")
                    var have = if (part.exists()) part.length() else 0L
                    val c = URL("${m.host}/$repo/resolve/main/$name").openConnection() as HttpURLConnection
                    c.connectTimeout = 15000
                    c.readTimeout = 30000
                    c.instanceFollowRedirects = true
                    c.setRequestProperty("User-Agent", "Alethra")
                    if (have > 0) c.setRequestProperty("Range", "bytes=$have-")
                    try {
                        val code = c.responseCode
                        if (code == 416 && have > 0) {
                            // berkas sebagian ternyata sudah lengkap
                            part.renameTo(target); base += size; job.done = base; continue
                        }
                        if (code != 200 && code != 206) throw RuntimeException("Server menjawab $code")
                        val append = code == 206 && have > 0
                        if (!append) have = 0L
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
                                    job.done = base + minOf(got, size + size / 4)
                                    tick(false)
                                }
                            }
                        }
                        if (job.cancel) break
                        if (part.length() < minOf(size / 2, 100_000L)) throw RuntimeException("Berkas $name tidak lengkap")
                        if (!part.renameTo(target)) throw RuntimeException("Gagal menyimpan $name")
                        base += size
                        job.done = base
                    } finally { c.disconnect() }
                }
                if (!job.cancel) {
                    if (list.all { File(dir, it.first).exists() }) File(dir, ".ok").createNewFile()
                    else throw RuntimeException("Unduhan belum lengkap")
                }
            } catch (e: Exception) {
                job.error = (e.message ?: "Gagal mengunduh").take(80)
            } finally {
                job.finished = true
                try { listener?.invoke(m.id) } catch (e: Exception) { }
            }
        }
    }
}

/** Suara -> teks di perangkat memakai sherpa-onnx. Aman dipanggil dari satu thread kerja; release() dari thread mana pun. */
object OfflineAsr {
    class Out(val text: String, val lang: String)

    private val lock = Any()
    private var rec: OfflineRecognizer? = null
    private var key = ""
    private var fixed = ""
    /** Mode klasik (v78): paksa greedy_search untuk Zipformer. Diset sebelum [ensure]; ikut menentukan kunci muat ulang. */
    @Volatile var greedy = false

    private fun threads(): Int = (Runtime.getRuntime().availableProcessors() / 2).coerceIn(2, 4)

    /** Muat model bila belum (atau bila model/bahasa berubah). Melempar exception bila gagal. */
    fun ensure(ctx: Context, m: AsrModel, lang: String) {
        synchronized(lock) {
            val k = m.id + "|" + lang + "|" + (if (greedy) "g" else "b")
            if (rec != null && key == k) return
            releaseLocked()
            val dir = OfflineModels.dir(ctx, m.id)
            if (!OfflineModels.installed(ctx, m)) throw IllegalStateException("Model belum diunduh")
            fun p(n: String) = File(dir, n).absolutePath
            // cari berkas di folder model berdasarkan pola nama (dipakai model yang daftar berkasnya dinamis)
            fun f(vararg tries: (String) -> Boolean): String {
                val names = dir.listFiles()?.filter { it.isFile && !it.name.endsWith(".part") }?.map { it.name } ?: emptyList()
                for (t in tries) names.firstOrNull(t)?.let { return p(it) }
                throw IllegalStateException("Berkas model tidak lengkap, hapus lalu unduh ulang")
            }
            val mc = if (m.kind == "moonshine") {
                OfflineModelConfig(
                    moonshine = OfflineMoonshineModelConfig(
                        encoder = f({ it == "encoder_model.ort" }, { it == "encoder_model.onnx" }, { it.startsWith("encoder_model") && (it.endsWith(".ort") || it.endsWith(".onnx")) }),
                        mergedDecoder = f({ it == "decoder_model_merged.ort" }, { it == "decoder_model_merged.onnx" }, { it.startsWith("decoder_model_merged") && (it.endsWith(".ort") || it.endsWith(".onnx")) })),
                    tokens = p("tokens.txt"), numThreads = threads(), provider = "cpu")
            } else if (m.kind == "funasr-nano") {
                OfflineModelConfig(
                    funasrNano = OfflineFunAsrNanoModelConfig(
                        encoderAdaptor = f({ it == "encoder_adaptor.int8.onnx" }, { it == "encoder_adaptor.onnx" }),
                        llm = f({ it == "llm.int8.onnx" }, { it == "llm.onnx" }),
                        embedding = f({ it == "embedding.int8.onnx" }, { it == "embedding.onnx" }),
                        tokenizer = p("Qwen3-0.6B")),
                    tokens = "", numThreads = threads(), provider = "cpu")
            } else if (m.kind == "qwen3-asr") {
                OfflineModelConfig(
                    qwen3Asr = OfflineQwen3AsrModelConfig(
                        convFrontend = f({ it == "conv_frontend.onnx" }, { it.startsWith("conv_frontend") && it.endsWith(".onnx") }),
                        encoder = f({ it == "encoder.int8.onnx" }, { it == "encoder.onnx" }),
                        decoder = f({ it == "decoder.int8.onnx" }, { it == "decoder.onnx" }),
                        tokenizer = p("tokenizer")),
                    tokens = "", numThreads = threads(), provider = "cpu")
            } else if (m.kind == "ctc-zipformer") {
                OfflineModelConfig(
                    zipformerCtc = OfflineZipformerCtcModelConfig(
                        model = f({ it == "model.int8.onnx" }, { it == "model.onnx" }, { it.startsWith("model") && it.endsWith(".onnx") })),
                    tokens = p("tokens.txt"), numThreads = threads(), provider = "cpu")
            } else if (m.kind == "ctc-nemo") {
                OfflineModelConfig(
                    nemo = OfflineNemoEncDecCtcModelConfig(
                        model = f({ it == "model.int8.onnx" }, { it == "model.onnx" }, { it.startsWith("model") && it.endsWith(".onnx") })),
                    tokens = p("tokens.txt"), numThreads = threads(), provider = "cpu")
            } else if (m.kind == "ctc-tele") {
                OfflineModelConfig(
                    teleSpeech = f({ it == "model.int8.onnx" }, { it == "model.onnx" }, { it.startsWith("model") && it.endsWith(".onnx") }),
                    tokens = p("tokens.txt"), numThreads = threads(), provider = "cpu", modelType = "telespeech_ctc")
            } else if (m.kind == "transducer" || m.kind == "nemo-transducer") {
                OfflineModelConfig(
                    transducer = OfflineTransducerModelConfig(
                        encoder = f({ it.startsWith("encoder") && it.endsWith(".onnx") && ".int8" in it }, { it.startsWith("encoder") && it.endsWith(".onnx") }),
                        decoder = f({ it.startsWith("decoder") && it.endsWith(".onnx") && "int8" !in it }, { it.startsWith("decoder") && it.endsWith(".onnx") }),
                        joiner = f({ it.startsWith("joiner") && it.endsWith(".onnx") && ".int8" in it }, { it.startsWith("joiner") && it.endsWith(".onnx") })),
                    tokens = p("tokens.txt"), numThreads = threads(), provider = "cpu",
                    modelType = if (m.kind == "nemo-transducer") "nemo_transducer" else "transducer")
            } else if (m.kind == "sense") {
                OfflineModelConfig(
                    senseVoice = OfflineSenseVoiceModelConfig(
                        model = p("model.int8.onnx"),
                        language = if (lang in setOf("zh", "en", "ja", "ko", "yue")) lang else "auto",
                        useInverseTextNormalization = true),
                    tokens = p("tokens.txt"), numThreads = threads(), provider = "cpu")
            } else {
                val en = m.prefix.endsWith(".en")
                OfflineModelConfig(
                    whisper = OfflineWhisperModelConfig(
                        encoder = if (m.dynamic) f({ it == m.prefix + "-encoder.int8.onnx" }) else p(m.prefix + "-encoder.int8.onnx"),
                        decoder = if (m.dynamic) f({ it == m.prefix + "-decoder.int8.onnx" }) else p(m.prefix + "-decoder.int8.onnx"),
                        language = if (en) "en" else lang,
                        task = "transcribe"),
                    tokens = if (m.dynamic) f({ it.endsWith("tokens.txt") }) else p(m.prefix + "-tokens.txt"), numThreads = threads(), provider = "cpu", modelType = "whisper")
            }
            rec = OfflineRecognizer(null, OfflineRecognizerConfig(
                featConfig = FeatureConfig(sampleRate = 16000, featureDim = 80),
                // Zipformer/transducer: beam search lebih akurat daripada greedy (sedikit lebih lambat)
                modelConfig = mc, decodingMethod = if (m.kind == "transducer" && !greedy) "modified_beam_search" else "greedy_search", maxActivePaths = 4))
            key = k
            fixed = m.fixedLang
        }
    }

    // Decode final dan decode sementara (teks langsung) tidak boleh berjalan bersamaan pada pengenal yang sama.
    // Final menunggu giliran; sementara hanya jalan bila pengenal menganggur (tryLock), jadi final tidak pernah tertunda lama.
    private val decLock = java.util.concurrent.locks.ReentrantLock()

    /** Hasil sementara untuk teks langsung. null bila model belum dimuat atau pengenal sedang sibuk. */
    fun transcribeLive(wav: ByteArray): Out? {
        if (!decLock.tryLock()) return null
        try {
            synchronized(lock) { if (rec == null) return null }
            return transcribeLocked(wav)
        } finally { decLock.unlock() }
    }

    /** [wav] = WAV PCM 16-bit mono 16 kHz dari stt.js (header 44 byte). */
    fun transcribe(wav: ByteArray): Out {
        decLock.lock()
        try { return transcribeLocked(wav) } finally { decLock.unlock() }
    }

    private fun transcribeLocked(wav: ByteArray): Out {
        val r: OfflineRecognizer = synchronized(lock) { rec } ?: throw IllegalStateException("Model belum dimuat")
        val n = (wav.size - 44) / 2
        if (n < 1600) return Out("", "")
        val f = FloatArray(n)
        var i = 0
        while (i < n) {
            val lo = wav[44 + i * 2].toInt() and 0xFF
            val hi = wav[45 + i * 2].toInt()
            f[i] = ((hi shl 8) or lo).toShort() / 32768f
            i++
        }
        val s = r.createStream()
        try {
            s.acceptWaveform(f, 16000)
            r.decode(s)
            val res = r.getResult(s)
            val lang = res.lang.replace("<|", "").replace("|>", "").trim().lowercase()
            return Out(res.text.trim(), lang.ifBlank { synchronized(lock) { fixed } })
        } finally { s.release() }
    }

    fun release() { synchronized(lock) { releaseLocked() } }

    private fun releaseLocked() {
        try { rec?.release() } catch (e: Throwable) { }
        rec = null
        key = ""
        fixed = ""
    }
}

/** Terjemahan di perangkat memakai ML Kit. Semua fungsi memblokir: panggil dari thread latar. */
object OfflineTr {
    private val cache = LinkedHashMap<String, Translator>()
    @Volatile private var dlCache: Set<String>? = null
    @Volatile private var dlAt = 0L

    private val langId by lazy { LanguageIdentification.getClient() }

    /** Kode bahasa Alethra (id, zh-CN, ...) -> kode ML Kit, atau null bila tidak didukung. */
    fun tag(code: String): String? {
        if (code.isBlank()) return null
        val c = when (code) { "zh-CN", "zh-TW", "zh", "yue" -> "zh"; else -> code.substringBefore('-').lowercase() }
        return try { TranslateLanguage.fromLanguageTag(c) } catch (e: Throwable) { null }
    }

    fun all(): List<String> = try { TranslateLanguage.getAllLanguages() } catch (e: Throwable) { emptyList() }

    fun name(tag: String): String {
        val n = Locale(tag).getDisplayLanguage(Locale("id", "ID"))
        return if (n.isBlank() || n.equals(tag, true)) tag else n.replaceFirstChar { it.uppercase() }
    }

    /** Bahasa yang modelnya sudah terpasang. */
    fun downloaded(force: Boolean = false): Set<String> {
        val now = System.currentTimeMillis()
        val c = dlCache
        if (!force && c != null && now - dlAt < 5000) return c
        val s = try {
            Tasks.await(RemoteModelManager.getInstance().getDownloadedModels(TranslateRemoteModel::class.java), 15, TimeUnit.SECONDS)
                .map { it.language }.toSet()
        } catch (e: Exception) { c ?: emptySet() }
        dlCache = s; dlAt = now
        return s
    }

    /** Daftar bahasa yang masih harus diunduh agar [src] -> [tgt] bisa jalan offline (Inggris ikut, sebagai perantara). */
    fun missing(src: String, tgt: String): List<String> {
        val d = downloaded()
        val need = if (src == tgt) listOf(src) else listOf(src, tgt, TranslateLanguage.ENGLISH).distinct()
        return need.filter { it !in d }
    }

    fun download(tag: String) {
        Tasks.await(RemoteModelManager.getInstance().download(TranslateRemoteModel.Builder(tag).build(), DownloadConditions.Builder().build()), 10, TimeUnit.MINUTES)
        dlCache = null
    }

    fun delete(tag: String) {
        synchronized(cache) {
            val it = cache.entries.iterator()
            while (it.hasNext()) { val e = it.next(); if (e.key.startsWith("$tag>") || e.key.endsWith(">$tag")) { try { e.value.close() } catch (x: Exception) { }; it.remove() } }
        }
        Tasks.await(RemoteModelManager.getInstance().deleteDownloadedModel(TranslateRemoteModel.Builder(tag).build()), 30, TimeUnit.SECONDS)
        dlCache = null
    }

    /** Tebak bahasa teks; null bila tidak yakin. */
    fun detect(text: String): String? = try {
        val l = Tasks.await(langId.identifyLanguage(text), 5, TimeUnit.SECONDS)
        if (l == "und") null else tag(l)
    } catch (e: Exception) { null }

    fun translate(text: String, src: String, tgt: String): String {
        if (src == tgt) return text
        val k = "$src>$tgt"
        val t = synchronized(cache) {
            cache.getOrPut(k) {
                Translation.getClient(TranslatorOptions.Builder().setSourceLanguage(src).setTargetLanguage(tgt).build())
            }.also {
                while (cache.size > 3) {
                    val e = cache.entries.first()
                    if (e.key == k) break
                    try { e.value.close() } catch (x: Exception) { }
                    cache.remove(e.key)
                }
            }
        }
        return Tasks.await(t.translate(text), 20, TimeUnit.SECONDS)
    }

    fun close() {
        synchronized(cache) { cache.values.forEach { try { it.close() } catch (e: Exception) { } }; cache.clear() }
    }
}

/** Jembatan JNI ke libalethra_nllb.so (dibangun oleh native/build_nllb.sh). Bila pustaka tidak ada, [available] = false. */
object NllbNative {
    val available: Boolean by lazy {
        try { System.loadLibrary("ctranslate2"); System.loadLibrary("alethra_nllb"); true } catch (e: Throwable) { false }
    }
    external fun load(dir: String, threads: Int)
    external fun translate(text: ByteArray, src: String, tgt: String): ByteArray
    external fun release()
}

/**
 * Terjemahan offline NLLB-200 distilled 600M (CTranslate2, int8). Langsung dari bahasa sumber ke bahasa tujuan
 * (tanpa perantara Inggris seperti ML Kit). Model diunduh dengan [ModelDownloader] yang sama dengan model suara,
 * tetapi tidak masuk daftar [OfflineModels.asr]. Lisensi model: CC-BY-NC 4.0 (non-komersial).
 */
object OfflineNllb {
    val model = AsrModel("nllb-600m", "NLLB-200 distilled 600M",
        "Terjemahan offline lebih akurat daripada ML Kit, langsung dari bahasa sumber ke tujuan (±200 bahasa). Berat: memakai ±1 GB RAM saat dipakai, terjemahan butuh 1-3 detik per kalimat. Lisensi model CC-BY-NC 4.0 (non-komersial).",
        "nllb", "JustFrederik/nllb-200-distilled-600M-ct2-int8",
        listOf("model.bin" to 623_000_000L, "sentencepiece.bpe.model" to 4_850_000L, "shared_vocabulary.txt" to 2_570_000L, "config.json" to 159L))

    private val lock = Any()
    @Volatile private var loaded = false

    /** Pustaka native ikut terpasang di build ini. */
    fun available(): Boolean = NllbNative.available
    fun installed(ctx: Context): Boolean = OfflineModels.installed(ctx, model)
    fun ready(ctx: Context): Boolean = available() && installed(ctx)

    fun ensure(ctx: Context) {
        synchronized(lock) {
            if (loaded) return
            if (!available()) throw IllegalStateException("NLLB tidak tersedia di build ini")
            if (!installed(ctx)) throw IllegalStateException("Model NLLB belum diunduh")
            val threads = (Runtime.getRuntime().availableProcessors() / 2).coerceIn(2, 4)
            NllbNative.load(OfflineModels.dir(ctx, model.id).absolutePath, threads)
            loaded = true
        }
    }

    /** [src] dan [tgt] = kode FLORES-200 dari [code]. Memblokir: panggil dari thread latar. */
    fun translate(text: String, src: String, tgt: String): String {
        if (!loaded) throw IllegalStateException("Model NLLB belum dimuat")
        return String(NllbNative.translate(text.toByteArray(Charsets.UTF_8), src, tgt), Charsets.UTF_8).trim()
    }

    fun release() {
        synchronized(lock) {
            if (loaded) { try { NllbNative.release() } catch (e: Throwable) { } }
            loaded = false
        }
    }

    private val codes = mapOf(
        "id" to "ind_Latn", "en" to "eng_Latn", "ja" to "jpn_Jpan", "ko" to "kor_Hang", "zh" to "zho_Hans", "zh-CN" to "zho_Hans",
        "zh-TW" to "zho_Hant", "yue" to "yue_Hant", "es" to "spa_Latn", "ar" to "arb_Arab", "hi" to "hin_Deva", "fr" to "fra_Latn",
        "de" to "deu_Latn", "pt" to "por_Latn", "ru" to "rus_Cyrl", "it" to "ita_Latn", "nl" to "nld_Latn", "tr" to "tur_Latn",
        "th" to "tha_Thai", "vi" to "vie_Latn", "ms" to "zsm_Latn", "jv" to "jav_Latn", "su" to "sun_Latn", "tl" to "tgl_Latn",
        "bn" to "ben_Beng", "ur" to "urd_Arab", "ta" to "tam_Taml", "fa" to "pes_Arab", "he" to "heb_Hebr", "iw" to "heb_Hebr",
        "pl" to "pol_Latn", "uk" to "ukr_Cyrl", "el" to "ell_Grek", "sv" to "swe_Latn", "ro" to "ron_Latn", "cs" to "ces_Latn",
        "hu" to "hun_Latn", "da" to "dan_Latn", "fi" to "fin_Latn", "no" to "nob_Latn", "sw" to "swh_Latn")

    /** Kode bahasa Alethra / ML Kit (id, zh-CN, ja, ...) -> kode FLORES-200, atau null bila tidak didukung. */
    fun code(tag: String): String? {
        val t = tag.trim()
        if (t.isEmpty()) return null
        return codes[t] ?: codes[t.substringBefore('-').lowercase()]
    }
}
