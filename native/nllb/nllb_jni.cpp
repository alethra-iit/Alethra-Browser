// Jembatan JNI: terjemahan NLLB-200 distilled 600M (CTranslate2, int8) + tokenizer sentencepiece.
// Kotlin: object NllbNative { load(dir, threads), translate(textUtf8, src, tgt): ByteArray, release() }
#include <jni.h>

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

}  // extern "C"
