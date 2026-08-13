package com.whisper.dsp.native

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference

interface LiquidLibrary : Library {
    companion object {
        init {
            // Add search paths for development
            val userDir = System.getProperty("user.dir")
            NativeLibrary.addSearchPath("liquid", "$userDir/whisper-dsp/prebuilt/desktop/linux/lib")
            NativeLibrary.addSearchPath("liquid", "$userDir/whisper-dsp/prebuilt/desktop/macos/lib")
        }

        val INSTANCE: LiquidLibrary = Native.load("liquid", LiquidLibrary::class.java)
        
        const val LIQUID_IIRDES_BUTTER = 0
        const val LIQUID_IIRDES_BANDPASS = 2
        const val LIQUID_IIRDES_SOS = 0
        
        const val LIQUID_FFT_FORWARD = 1
        const val LIQUID_NCO = 0
    }

    // FFT
    fun fft_create_plan(n: Int, input: Pointer, output: Pointer, dir: Int, flags: Int): Pointer
    fun fft_execute(plan: Pointer)
    fun fft_destroy_plan(plan: Pointer)

    // IIR Filter
    fun iirfilt_rrrf_create_prototype(
        ftype: Int,
        btype: Int,
        format: Int,
        order: Int,
        fc: Float,
        f0: Float,
        ap: Float,
        as_: Float
    ): Pointer
    fun iirfilt_rrrf_destroy(filter: Pointer)
    fun iirfilt_rrrf_execute_block(filter: Pointer, x: FloatArray, n: Int, y: FloatArray)

    // NCO
    fun nco_crcf_create(type: Int): Pointer
    fun nco_crcf_set_frequency(nco: Pointer, freq: Float)
    fun nco_crcf_cos(nco: Pointer): Float
    fun nco_crcf_step(nco: Pointer)
    fun nco_crcf_destroy(nco: Pointer)
}
