#include <jni.h>
#include <vector>
#include "liquid.h"

class NativeBandPassFilter {
public:
    NativeBandPassFilter(float lowCutoff, float highCutoff, float sampleRate) {
        float bw = (highCutoff - lowCutoff) / sampleRate;
        float f0 = (highCutoff + lowCutoff) / (2.0f * sampleRate);

        // Using Butterworth filter, 4th order, SOS format for stability
        filter = iirfilt_rrrf_create_prototype(
            LIQUID_IIRDES_BUTTER,
            LIQUID_IIRDES_BANDPASS,
            LIQUID_IIRDES_SOS,
            4,      // order
            bw,     // bandwidth
            f0,     // center frequency
            0.1f,   // passband ripple (ignored for Butterworth)
            60.0f   // stopband attenuation (ignored for Butterworth)
        );
    }

    ~NativeBandPassFilter() {
        if (filter) {
            iirfilt_rrrf_destroy(filter);
        }
    }

    void process(const float* input, int numSamples, float* output) {
        if (filter) {
            iirfilt_rrrf_execute_block(filter, const_cast<float*>(input), numSamples, output);
        } else {
            for (int i = 0; i < numSamples; ++i) {
                output[i] = input[i];
            }
        }
    }

private:
    iirfilt_rrrf filter = nullptr;
};

extern "C"
JNIEXPORT jlong JNICALL
Java_com_whisper_dsp_filter_AndroidBandPassFilter_nativeCreate(JNIEnv *env, jobject thiz, jfloat low_cutoff, jfloat high_cutoff, jfloat sample_rate) {
    return reinterpret_cast<jlong>(new NativeBandPassFilter(low_cutoff, high_cutoff, sample_rate));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_whisper_dsp_filter_AndroidBandPassFilter_nativeProcess(JNIEnv *env, jobject thiz, jlong ptr,
                                                               jfloatArray samples, jfloatArray filtered_samples) {
    NativeBandPassFilter* filter = reinterpret_cast<NativeBandPassFilter*>(ptr);

    jsize numSamples = env->GetArrayLength(samples);
    jfloat* samplesPtr = env->GetFloatArrayElements(samples, nullptr);
    jfloat* filteredSamplesPtr = env->GetFloatArrayElements(filtered_samples, nullptr);

    filter->process(samplesPtr, numSamples, filteredSamplesPtr);

    env->ReleaseFloatArrayElements(samples, samplesPtr, JNI_ABORT);
    env->ReleaseFloatArrayElements(filtered_samples, filteredSamplesPtr, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_whisper_dsp_filter_AndroidBandPassFilter_nativeDestroy(JNIEnv *env, jobject thiz, jlong ptr) {
    delete reinterpret_cast<NativeBandPassFilter*>(ptr);
}
