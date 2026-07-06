cmake_minimum_required(VERSION 3.22.1)

project("whisper-dsp-jni")

# Set the path to the prebuilt liquid-dsp library
set(LIQUID_PREBUILT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../prebuilt/android")

add_library(liquid SHARED IMPORTED)
set_target_properties(liquid PROPERTIES IMPORTED_LOCATION
    "${LIQUID_PREBUILT_DIR}/lib/${ANDROID_ABI}/libliquid.so"
)

add_library(whisper_dsp_jni SHARED
    signal_generator.cpp
    fft_processor.cpp
)

target_include_directories(whisper_dsp_jni PRIVATE
    "${LIQUID_PREBUILT_DIR}/include"
)

find_library(log-lib log)

target_link_libraries(whisper_dsp_jni
    liquid
    ${log-lib}
)
