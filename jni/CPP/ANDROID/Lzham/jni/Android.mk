# 
# build Lzham for armeabi and armeabi-v7a CPU
#
# WARNING : file generated by generate.py
#


LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := Lzham

LOCAL_CFLAGS := -DANDROID_NDK  -fexceptions \
	-DNDEBUG -D_REENTRANT -DENV_UNIX \
	-DEXTERNAL_CODECS \
	-DBREAK_HANDLER \
	-DUNICODE -D_UNICODE -DUNIX_USE_WIN_FILE \
	-I../../../Windows \
	-I../../../Common \
	-I../../../../C \
-I../../../myWindows \
-I../../../ \
-I../../../include_windows \
-I../../../../CPP/7zip/Compress/Lzham/include \
-I../../../../CPP/7zip/Compress/Lzham/lzhamcomp \
-I../../../../CPP/7zip/Compress/Lzham/lzhamdecomp

LOCAL_SRC_FILES := \
  ../../../../CPP/7zip/Common/StreamUtils.cpp \
  ../../../../CPP/7zip/Compress/CodecExports.cpp \
  ../../../../CPP/7zip/Compress/DllExportsCompress.cpp \
  ../../../../CPP/7zip/Compress/Lzham/LzhamRegister.cpp \
  ../../../../CPP/Common/MyWindows.cpp \
  ../../../../CPP/Windows/System.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamcomp/lzham_lzbase.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamcomp/lzham_lzcomp.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamcomp/lzham_lzcomp_internal.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamcomp/lzham_lzcomp_state.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamcomp/lzham_match_accel.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamcomp/lzham_pthreads_threading.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_assert.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_checksum.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_huffman_codes.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_lzdecomp.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_lzdecompbase.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_mem.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_platform.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_prefix_coding.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_symbol_codec.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_timer.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamdecomp/lzham_vector.cpp \
  ../../../../CPP/7zip/Compress/Lzham/lzhamlib/lzham_lib.cpp \
  ../../../../C/Alloc.c \

include $(BUILD_SHARED_LIBRARY)

