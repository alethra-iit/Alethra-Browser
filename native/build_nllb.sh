#!/usr/bin/env bash
# Membangun libctranslate2.so + libalethra_nllb.so (terjemahan NLLB-200 offline) untuk arm64-v8a
# dengan Android NDK, lalu menaruhnya di app/src/main/jniLibs/arm64-v8a/.
# Bila gagal, APK tetap bisa dibangun: aplikasi menandai opsi NLLB "tidak tersedia di build ini".
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
NDK="${ANDROID_NDK_HOME:-${ANDROID_NDK_LATEST_HOME:-${ANDROID_NDK_ROOT:-}}}"
[ -n "$NDK" ] && [ -d "$NDK" ] || { echo "Android NDK tidak ditemukan"; exit 1; }
CT2_TAG="${CT2_TAG:-v4.5.0}"
SP_TAG="${SP_TAG:-v0.2.0}"
ABI=arm64-v8a
OUT="$ROOT/app/src/main/jniLibs/$ABI"
WORK="$(mktemp -d)"
BIN="$(echo "$NDK"/toolchains/llvm/prebuilt/*/bin)"
echo "NDK: $NDK"; echo "Kerja: $WORK"

git clone -q --depth 1 --branch "$CT2_TAG" --recursive --shallow-submodules https://github.com/OpenNMT/CTranslate2.git "$WORK/ct2"
git clone -q --depth 1 --branch "$SP_TAG" https://github.com/google/sentencepiece.git "$WORK/sp"

COMMON=(-G Ninja -DCMAKE_TOOLCHAIN_FILE="$NDK/build/cmake/android.toolchain.cmake"
  -DANDROID_ABI=$ABI -DANDROID_PLATFORM=android-24 -DANDROID_STL=c++_shared
  -DCMAKE_BUILD_TYPE=Release -DCMAKE_POLICY_VERSION_MINIMUM=3.5)

ok=0
# Percobaan 1: OpenMP statis (multi-thread). Percobaan 2: tanpa OpenMP (lebih lambat tapi lebih mungkin berhasil).
for OMP in COMP NONE; do
  echo "=== CTranslate2, OPENMP_RUNTIME=$OMP ==="
  rm -rf "$WORK/ct2b" "$WORK/ct2i"
  EXTRA=()
  [ "$OMP" = COMP ] && EXTRA=(-DCMAKE_SHARED_LINKER_FLAGS=-static-openmp)
  if cmake -S "$WORK/ct2" -B "$WORK/ct2b" "${COMMON[@]}" "${EXTRA[@]}" \
       -DBUILD_CLI=OFF -DBUILD_TESTS=OFF -DWITH_MKL=OFF -DWITH_DNNL=OFF -DWITH_RUY=ON \
       -DOPENMP_RUNTIME=$OMP -DCMAKE_INSTALL_PREFIX="$WORK/ct2i" \
     && cmake --build "$WORK/ct2b" -j"$(nproc)" && cmake --install "$WORK/ct2b"; then
    ok=1; break
  fi
  echo "Percobaan OPENMP_RUNTIME=$OMP gagal"
done
[ "$ok" = 1 ] || { echo "CTranslate2 gagal dibangun"; exit 1; }

echo "=== JNI + sentencepiece ==="
cmake -S "$ROOT/native/nllb" -B "$WORK/jnib" "${COMMON[@]}" -DCT2_PREFIX="$WORK/ct2i" -DSP_SOURCE_DIR="$WORK/sp"
cmake --build "$WORK/jnib" -j"$(nproc)"

mkdir -p "$OUT"
rm -f "$OUT"/libctranslate2.so "$OUT"/libalethra_nllb.so
cp "$WORK/ct2i/lib/libctranslate2.so" "$OUT/"
cp "$WORK/jnib/libalethra_nllb.so" "$OUT/"
# pustaka pendamping yang ikut dibutuhkan (libc++_shared selalu; libomp hanya bila ikut terhubung dinamis)
NEEDED="$("$BIN/llvm-readelf" -d "$OUT/libctranslate2.so" "$OUT/libalethra_nllb.so" | grep NEEDED || true)"
echo "$NEEDED"
CXXSO="$(find "$NDK/toolchains/llvm/prebuilt" -path '*aarch64-linux-android/libc++_shared.so' | head -1)"
[ -n "$CXXSO" ] && cp "$CXXSO" "$OUT/"
if echo "$NEEDED" | grep -q 'libomp.so'; then
  OMPSO="$(find "$NDK/toolchains/llvm/prebuilt" -path '*linux/aarch64/libomp.so' | head -1)"
  [ -n "$OMPSO" ] && cp "$OMPSO" "$OUT/"
fi
"$BIN/llvm-strip" --strip-unneeded "$OUT"/*.so
ls -lh "$OUT"
echo "Selesai: terjemahan NLLB tersedia di build ini"
