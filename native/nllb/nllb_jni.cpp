// Jembatan JNI: terjemahan NLLB-200 distilled 600M (CTranslate2, int8) + tokenizer sentencepiece.
// Kotlin: object NllbNative { load(dir, threads), translate(textUtf8, src, tgt): ByteArray, release() }
#include <jni.h>

#include <fstream>
#include <map>
#include <memory>
#include <mutex>
#include <string>
#include <vector>

#include <ctranslate2/translator.h>
#include <sentencepiece_processor.h>

namespace {

struct Engine {
  std::unique_ptr<ctranslate2::Translator> translator;
  sentencepiece::SentencePieceProcessor sp;
};

std::mutex g_mutex;
std::shared_ptr<Engine> g_engine;

std::string to_std(JNIEnv* env, jstring s) {
  if (!s) return {};
  const char* c = env->GetStringUTFChars(s, nullptr);
  std::string r(c ? c : "");
  if (c) env->ReleaseStringUTFChars(s, c);
  return r;
}

std::string bytes_to_std(JNIEnv* env, jbyteArray a) {
  if (!a) return {};
  const jsize n = env->GetArrayLength(a);
  std::string r(static_cast<size_t>(n), '\0');
  if (n > 0) env->GetByteArrayRegion(a, 0, n, reinterpret_cast<jbyte*>(&r[0]));
  return r;
}

jbyteArray std_to_bytes(JNIEnv* env, const std::string& s) {
  jbyteArray a = env->NewByteArray(static_cast<jsize>(s.size()));
  if (a && !s.empty()) env->SetByteArrayRegion(a, 0, static_cast<jsize>(s.size()), reinterpret_cast<const jbyte*>(s.data()));
  return a;
}

void throw_java(JNIEnv* env, const std::string& msg) {
  jclass cls = env->FindClass("java/lang/RuntimeException");
  if (cls) env->ThrowNew(cls, msg.c_str());
}


// ---------------------------------------------------------------------------------------------
// Mesin CTranslate2 serbaguna untuk M2M100, Opus-MT (Marian), dan Argos Translate.
// Beberapa model bisa dimuat sekaligus (satu per id). Kotlin: object MtNative.
//   kind 1 = M2M100  : satu sentencepiece, token bahasa "__ja__" di awal sumber dan awalan target
//   kind 2 = Opus-MT : source.spm + target.spm, tanpa token bahasa, "</s>" di akhir sumber
//   kind 3 = Argos   : <dir>/model/ (CTranslate2) + <dir>/sentencepiece.model, tanpa token tambahan
// ---------------------------------------------------------------------------------------------
struct MtEngine {
  int kind = 0;
  std::unique_ptr<ctranslate2::Translator> translator;
  sentencepiece::SentencePieceProcessor sp_src;
  sentencepiece::SentencePieceProcessor sp_tgt;
  bool same_sp = true;  // true: sp_src juga dipakai untuk decode
};

std::mutex g_mt_mutex;
std::map<std::string, std::shared_ptr<MtEngine>> g_mt;

bool file_exists(const std::string& path) {
  std::ifstream f(path, std::ios::binary);
  return f.good();
}

std::string first_existing(const std::string& dir, const std::vector<std::string>& names) {
  for (const auto& n : names) {
    const std::string p = dir + "/" + n;
    if (file_exists(p)) return p;
  }
  return {};
}

}  // namespace

extern "C" {

// Memuat model dari folder (model.bin, shared_vocabulary.txt, config.json, sentencepiece.bpe.model).
JNIEXPORT void JNICALL Java_com_alethra_browser_NllbNative_load(JNIEnv* env, jobject, jstring jdir, jint threads) {
  try {
    const std::string dir = to_std(env, jdir);
    auto engine = std::make_shared<Engine>();

    const auto status = engine->sp.Load(dir + "/sentencepiece.bpe.model");
    if (!status.ok()) {
      throw_java(env, "sentencepiece: " + status.ToString());
      return;
    }

    ctranslate2::models::ModelLoader loader(dir);
    loader.device = ctranslate2::Device::CPU;
    loader.compute_type = ctranslate2::ComputeType::DEFAULT;  // ikut kuantisasi model (int8)
    ctranslate2::ReplicaPoolConfig config;
    config.num_threads_per_replica = threads > 0 ? static_cast<size_t>(threads) : 0;
    engine->translator = std::make_unique<ctranslate2::Translator>(loader, config);

    std::lock_guard<std::mutex> lock(g_mutex);
    g_engine = engine;
  } catch (const std::exception& e) {
    throw_java(env, e.what());
  } catch (...) {
    throw_java(env, "gagal memuat model NLLB");
  }
}

// [src] / [tgt] = kode FLORES-200, mis. "jpn_Jpan", "ind_Latn".
JNIEXPORT jbyteArray JNICALL Java_com_alethra_browser_NllbNative_translate(JNIEnv* env, jobject, jbyteArray jtext, jstring jsrc, jstring jtgt) {
  std::shared_ptr<Engine> engine;
  {
    std::lock_guard<std::mutex> lock(g_mutex);
    engine = g_engine;
  }
  if (!engine) {
    throw_java(env, "Model NLLB belum dimuat");
    return nullptr;
  }
  try {
    const std::string text = bytes_to_std(env, jtext);  // UTF-8 murni
    const std::string src = to_std(env, jsrc);
    const std::string tgt = to_std(env, jtgt);

    std::vector<std::string> pieces;
    const auto st = engine->sp.Encode(text, &pieces);
    if (!st.ok()) {
      throw_java(env, "sentencepiece: " + st.ToString());
      return nullptr;
    }
    std::vector<std::string> source;
    source.reserve(pieces.size() + 2);
    source.push_back(src);
    source.insert(source.end(), pieces.begin(), pieces.end());
    source.push_back("</s>");

    ctranslate2::TranslationOptions options;
    options.beam_size = 2;
    options.max_decoding_length = 256;

    const std::vector<std::vector<std::string>> batch{source};
    const std::vector<std::vector<std::string>> prefix{{tgt}};
    const auto results = engine->translator->translate_batch(batch, prefix, options);
    if (results.empty() || results[0].hypotheses.empty()) return std_to_bytes(env, "");

    std::vector<std::string> out = results[0].hypotheses[0];
    if (!out.empty() && out.front() == tgt) out.erase(out.begin());  // buang token bahasa tujuan

    std::string text_out;
    const auto ds = engine->sp.Decode(out, &text_out);
    if (!ds.ok()) {
      throw_java(env, "sentencepiece: " + ds.ToString());
      return nullptr;
    }
    return std_to_bytes(env, text_out);
  } catch (const std::exception& e) {
    throw_java(env, e.what());
    return nullptr;
  } catch (...) {
    throw_java(env, "terjemahan NLLB gagal");
    return nullptr;
  }
}

JNIEXPORT void JNICALL Java_com_alethra_browser_NllbNative_release(JNIEnv*, jobject) {
  std::shared_ptr<Engine> old;
  {
    std::lock_guard<std::mutex> lock(g_mutex);
    old = std::move(g_engine);
  }
  // old dilepas di luar kunci
}

JNIEXPORT void JNICALL Java_com_alethra_browser_MtNative_load(JNIEnv* env, jobject, jstring jid, jstring jdir, jint threads, jint kind) {
  try {
    const std::string id = to_std(env, jid);
    const std::string dir = to_std(env, jdir);
    {
      std::lock_guard<std::mutex> lock(g_mt_mutex);
      if (g_mt.count(id)) return;  // sudah dimuat
    }
    auto engine = std::make_shared<MtEngine>();
    engine->kind = kind;

    std::string model_dir = dir;
    if (kind == 1) {
      const std::string sp = first_existing(dir, {"sentencepiece.bpe.model", "sentencepiece.model", "spiece.model"});
      if (sp.empty()) { throw_java(env, "berkas sentencepiece tidak ada"); return; }
      const auto st = engine->sp_src.Load(sp);
      if (!st.ok()) { throw_java(env, "sentencepiece: " + st.ToString()); return; }
    } else if (kind == 2) {
      const std::string a = first_existing(dir, {"source.spm"});
      const std::string b = first_existing(dir, {"target.spm"});
      if (a.empty() || b.empty()) { throw_java(env, "source.spm/target.spm tidak ada"); return; }
      auto st = engine->sp_src.Load(a);
      if (!st.ok()) { throw_java(env, "sentencepiece: " + st.ToString()); return; }
      st = engine->sp_tgt.Load(b);
      if (!st.ok()) { throw_java(env, "sentencepiece: " + st.ToString()); return; }
      engine->same_sp = false;
    } else if (kind == 3) {
      const std::string sp = first_existing(dir, {"sentencepiece.model"});
      if (sp.empty()) { throw_java(env, "sentencepiece.model tidak ada"); return; }
      const auto st = engine->sp_src.Load(sp);
      if (!st.ok()) { throw_java(env, "sentencepiece: " + st.ToString()); return; }
      model_dir = dir + "/model";
    } else {
      throw_java(env, "jenis model tidak dikenal");
      return;
    }

    ctranslate2::models::ModelLoader loader(model_dir);
    loader.device = ctranslate2::Device::CPU;
    // M2M100 sudah int8; model Marian/Argos dikuantisasi saat dimuat supaya hemat RAM
    loader.compute_type = (kind == 1) ? ctranslate2::ComputeType::DEFAULT : ctranslate2::ComputeType::INT8;
    ctranslate2::ReplicaPoolConfig config;
    config.num_threads_per_replica = threads > 0 ? static_cast<size_t>(threads) : 0;
    engine->translator = std::make_unique<ctranslate2::Translator>(loader, config);

    std::lock_guard<std::mutex> lock(g_mt_mutex);
    g_mt[id] = engine;
  } catch (const std::exception& e) {
    throw_java(env, e.what());
  } catch (...) {
    throw_java(env, "gagal memuat model terjemahan");
  }
}

// [src] / [tgt] hanya dipakai kind 1 (mis. "__ja__", "__id__"); kind 2 dan 3 mengabaikannya.
JNIEXPORT jbyteArray JNICALL Java_com_alethra_browser_MtNative_translate(JNIEnv* env, jobject, jstring jid, jbyteArray jtext, jstring jsrc, jstring jtgt) {
  std::shared_ptr<MtEngine> engine;
  {
    std::lock_guard<std::mutex> lock(g_mt_mutex);
    auto it = g_mt.find(to_std(env, jid));
    if (it != g_mt.end()) engine = it->second;
  }
  if (!engine) {
    throw_java(env, "Model terjemahan belum dimuat");
    return nullptr;
  }
  try {
    const std::string text = bytes_to_std(env, jtext);
    const std::string src = to_std(env, jsrc);
    const std::string tgt = to_std(env, jtgt);

    std::vector<std::string> pieces;
    const auto st = engine->sp_src.Encode(text, &pieces);
    if (!st.ok()) { throw_java(env, "sentencepiece: " + st.ToString()); return nullptr; }

    std::vector<std::string> source;
    source.reserve(pieces.size() + 2);
    if (engine->kind == 1) source.push_back(src);
    source.insert(source.end(), pieces.begin(), pieces.end());
    if (engine->kind == 1 || engine->kind == 2) source.push_back("</s>");

    ctranslate2::TranslationOptions options;
    options.beam_size = 2;
    options.max_decoding_length = 256;

    const std::vector<std::vector<std::string>> batch{source};
    std::vector<ctranslate2::TranslationResult> results;
    if (engine->kind == 1) {
      const std::vector<std::vector<std::string>> prefix{{tgt}};
      results = engine->translator->translate_batch(batch, prefix, options);
    } else {
      results = engine->translator->translate_batch(batch, options);
    }
    if (results.empty() || results[0].hypotheses.empty()) return std_to_bytes(env, "");

    std::vector<std::string> out = results[0].hypotheses[0];
    if (engine->kind == 1 && !out.empty() && out.front() == tgt) out.erase(out.begin());

    std::string text_out;
    auto& dec = engine->same_sp ? engine->sp_src : engine->sp_tgt;
    const auto ds = dec.Decode(out, &text_out);
    if (!ds.ok()) { throw_java(env, "sentencepiece: " + ds.ToString()); return nullptr; }
    return std_to_bytes(env, text_out);
  } catch (const std::exception& e) {
    throw_java(env, e.what());
    return nullptr;
  } catch (...) {
    throw_java(env, "terjemahan gagal");
    return nullptr;
  }
}

JNIEXPORT void JNICALL Java_com_alethra_browser_MtNative_release(JNIEnv* env, jobject, jstring jid) {
  std::shared_ptr<MtEngine> old;
  {
    std::lock_guard<std::mutex> lock(g_mt_mutex);
    auto it = g_mt.find(to_std(env, jid));
    if (it != g_mt.end()) { old = std::move(it->second); g_mt.erase(it); }
  }
}

JNIEXPORT void JNICALL Java_com_alethra_browser_MtNative_releaseAll(JNIEnv*, jobject) {
  std::map<std::string, std::shared_ptr<MtEngine>> old;
  {
    std::lock_guard<std::mutex> lock(g_mt_mutex);
    old.swap(g_mt);
  }
}

}  // extern "C"
