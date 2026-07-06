#include <jni.h>
#include <vector>
#include <complex>
#include <cstdlib>
#include "liquid.h"

// Liquid-DSP uses float complex in C, which is binary compatible with std::complex<float> in C++
// We define a helper to handle the cast if float complex is not available as a keyword.
typedef std::complex<float> complex_float;

class NativeFFTProcessor {
public:
    NativeFFTProcessor(int fftSize) : fftSize(fftSize) {
        input = new complex_float[fftSize];
        output = new complex_float[fftSize];
        // Liquid expects float complex*, we provide std::complex<float>* and cast
        plan = fft_create_plan(fftSize, (liquid_float_complex*)input, (liquid_float_complex*)output, LIQUID_FFT_FORWARD, 0);
    }

    ~NativeFFTProcessor() {
        fft_destroy_plan(plan);
        delete[] input;
        delete[] output;
    }

    void process(const float* samples, int numSamples, float* magnitudes) {
        for (int i = 0; i < fftSize; ++i) {
            if (i < numSamples) {
                input[i] = complex_float(samples[i], 0.0f);
            } else {
                input[i] = complex_float(0.0f, 0.0f);
            }
        }

        fft_execute(plan);

        for (int i = 0; i <= fftSize / 2; ++i) {
            magnitudes[i] = std::abs(output[i]);
        }
    }

private:
    int fftSize;
    complex_float* input;
    complex_float* output;
    fftplan plan;
};

extern "C"
JNIEXPORT jlong JNICALL
Java_com_whisper_dsp_fft_AndroidFFTProcessor_nativeCreate(JNIEnv *env, jobject thiz, jint fft_size) {
    return reinterpret_cast<jlong>(new NativeFFTProcessor(fft_size));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_whisper_dsp_fft_AndroidFFTProcessor_nativeProcess(JNIEnv *env, jobject thiz, jlong ptr,
                                                          jfloatArray samples, jfloatArray magnitudes) {
    NativeFFTProcessor* processor = reinterpret_cast<NativeFFTProcessor*>(ptr);

    jsize numSamples = env->GetArrayLength(samples);
    jfloat* samplesPtr = env->GetFloatArrayElements(samples, nullptr);
    jfloat* magnitudesPtr = env->GetFloatArrayElements(magnitudes, nullptr);

    processor->process(samplesPtr, numSamples, magnitudesPtr);

    env->ReleaseFloatArrayElements(samples, samplesPtr, JNI_ABORT);
    env->ReleaseFloatArrayElements(magnitudes, magnitudesPtr, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_whisper_dsp_fft_AndroidFFTProcessor_nativeDestroy(JNIEnv *env, jobject thiz, jlong ptr) {
    delete reinterpret_cast<NativeFFTProcessor*>(ptr);
}
