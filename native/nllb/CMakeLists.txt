cmake_minimum_required(VERSION 3.18)
project(alethra_nllb CXX)

# JNI untuk terjemahan offline NLLB-200 (CTranslate2 + sentencepiece).
# Dipanggil oleh native/build_nllb.sh dengan -DCT2_PREFIX=<folder install CTranslate2>
# dan (opsional) -DSP_SOURCE_DIR=<folder sumber sentencepiece>.
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

if(NOT CT2_PREFIX)
  message(FATAL_ERROR "CT2_PREFIX belum diisi")
endif()

set(SPM_ENABLE_SHARED OFF CACHE BOOL "" FORCE)
set(SPM_BUILD_TEST OFF CACHE BOOL "" FORCE)
set(SPM_ENABLE_TCMALLOC OFF CACHE BOOL "" FORCE)
if(SP_SOURCE_DIR)
  add_subdirectory(${SP_SOURCE_DIR} ${CMAKE_BINARY_DIR}/sentencepiece EXCLUDE_FROM_ALL)
else()
  include(FetchContent)
  FetchContent_Declare(sentencepiece GIT_REPOSITORY https://github.com/google/sentencepiece.git GIT_TAG v0.2.0 GIT_SHALLOW TRUE)
  FetchContent_MakeAvailable(sentencepiece)
  set(SP_SOURCE_DIR ${sentencepiece_SOURCE_DIR})
endif()

find_library(CT2_LIB NAMES ctranslate2 PATHS ${CT2_PREFIX}/lib NO_DEFAULT_PATH REQUIRED)

add_library(alethra_nllb SHARED nllb_jni.cpp)
target_include_directories(alethra_nllb PRIVATE ${CT2_PREFIX}/include ${SP_SOURCE_DIR}/src)
target_link_libraries(alethra_nllb PRIVATE ${CT2_LIB} sentencepiece-static)
if(ANDROID)
  target_link_libraries(alethra_nllb PRIVATE log)
endif()
set_target_properties(alethra_nllb PROPERTIES CXX_VISIBILITY_PRESET hidden)
