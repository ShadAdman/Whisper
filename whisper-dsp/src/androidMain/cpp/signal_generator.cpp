#ifndef _USE_MATH_DEFINES
#define _USE_MATH_DEFINES
#endif
#include <jni.h>
#include <vector>
#include <cmath>
#include <complex>
#include "liquid.h"

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_whisper_dsp_generator_AndroidSignalGenerator_nativeGenerateTone(
    JNIEnv *env,
    jobject thiz,
    jfloat frequency,
    jint durationMs
) {
    const float sampleRate = 44100.0f;
    int numSamples = static_cast<int>(sampleRate * durationMs / 1000.0f);

    std::vector<float> result(numSamples);

    // Create NCO object
    nco_crcf nco = nco_crcf_create(LIQUID_NCO);
    nco_crcf_set_frequency(nco, 2.0f * M_PI * frequency / sampleRate);

    for (int i = 0; i < numSamples; ++i) {
        result[i] = nco_crcf_cos(nco);
        nco_crcf_step(nco);
    }

    nco_crcf_destroy(nco);

    jfloatArray jResult = env->NewFloatArray(numSamples);
    env->SetFloatArrayRegion(jResult, 0, numSamples, result.data());

    return jResult;
}
