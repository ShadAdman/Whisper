<p align="center">
  <img src="whisper-logo.png" width="150" alt="Whisper Logo">
</p>

<p align="center">Whisper</p>

<p align="center">
  <img src="whisper.gif" width="600" alt="Whisper Animation">
</p>

Whisper is a communication protocol designed for decentralized and secure data exchange using near-ultrasound acoustic waves. It enables devices to communicate in close proximity without relying on traditional wireless technologies such as Wi-Fi, Bluetooth, or cellular networks.

## Core Concepts

To effectively use Whisper, it is helpful to understand the underlying principles that make acoustic data transfer possible.

### Acoustic Communication
Acoustic communication uses sound waves to transmit information. Whisper operates in the near-ultrasound frequency range, typically between 18 kHz and 22 kHz. These frequencies are generally inaudible to most adult humans but can be captured by standard device microphones and reproduced by speakers.

### FSK Modulation
Whisper uses Frequency Shift Keying (FSK) to represent data. In FSK, different frequencies are assigned to represent specific bit values. For example, one frequency might represent a binary 0, while another represents a binary 1. By switching between these frequencies over time, the protocol can encode a stream of data into a sound signal.

### DSP Pipeline
Digital Signal Processing (DSP) is used to clean and prepare the audio signal before it is analyzed. Whisper employs a pipeline that includes:
- **Windowing**: Breaking the continuous audio stream into manageable segments for analysis.
- **Bandpass Filtering**: Removing noise from frequencies outside the communication range (e.g., background speech or music).
- **Gain Control**: Normalizing the volume of the signal to ensure consistent detection.

### Forward Error Correction (FEC)
Sound is an unstable medium for data transfer due to background noise and physical obstructions. Whisper includes Forward Error Correction to improve reliability. By sending redundant information along with the actual data, the receiving device can reconstruct the original message even if some parts of the acoustic signal were corrupted or lost.

## API Usage

The Whisper API is built using Kotlin Coroutines and Flows, providing a reactive way to handle data transmission and reception.

### Initialization and Configuration

The `Whisper` object serves as the primary entry point for the library. You can customize its behavior using `WhisperConfig`.

```kotlin
val config = WhisperConfig(
    sampleRate = 48000,
    carrierFrequency = 19000f,
    fecConfig = FecConfig(enabled = true, redundancy = 2)
)

Whisper.configure(config)
```

The `WhisperConfig` class accepts the following parameters:

- **sampleRate**: The audio sampling rate in Hz. A value of 48000 Hz is recommended for most modern devices to ensure high-fidelity signal processing.
- **carrierFrequency**: The central frequency used for the acoustic signal, measured in Hz. By default, this is set to 19000 Hz, which resides in the near-ultrasound spectrum. This allows for communication that is typically inaudible to humans but recognizable by standard microphones.
- **fecConfig**: This parameter handles the Forward Error Correction settings through the `FecConfig` class:
    - **enabled**: A boolean that determines whether error correction logic should be applied to the transmitted data.
    - **redundancy**: An integer that defines the level of data duplication. A higher value improves the chances of successful data recovery in environments with significant background noise, though it will increase the total time required for transmission.

### Receiving Data

To start listening for incoming signals, call `startListening()`. This initializes the audio engine and begins processing the microphone input. You can then collect data from the `receivedData` flow.

```kotlin
// Start the microphone and detection engine
Whisper.startListening()

// Observe incoming data
scope.launch {
    Whisper.receivedData.collect { data ->
        val message = data.decodeToString()
        println("Received: $message")
    }
}

// Stop listening when finished
Whisper.stopListening()
```

### Transmitting Data

Transmission is handled by the `transmit` function. It takes a byte array, encodes it into an acoustic signal, and plays it through the device speaker.

```kotlin
val message = "Hello from Whisper".encodeToByteArray()

scope.launch {
    Whisper.transmit(message)
}
```

### Testing and Debugging

You can use the `playTestTone()` function to verify that the audio hardware is working correctly. This will emit a 2-second tone at the configured carrier frequency.

```kotlin
scope.launch {
    Whisper.playTestTone()
}
```

### Monitoring Signal Events

If you need lower-level information about the signal state, such as when a carrier frequency is detected, you can observe the `carrierEvents` flow.

```kotlin
scope.launch {
    Whisper.carrierEvents.collect { event ->
        when (event) {
            is CarrierDetected -> println("Signal detected")
            is CarrierLost -> println("Signal lost")
        }
    }
}
```

## Sample Application

The project includes a comprehensive sample application located in the `sample` directory. This application demonstrates the core capabilities of the Whisper library using Compose Multiplatform.

Features of the sample app include:
- **Tone Detection**: Real-time visualization of detected frequencies and their magnitudes.
- **Data Transmission**: A simple interface to input text and transmit it as an acoustic signal.
- **Nearby Device Discovery**: Automatically lists the IDs of nearby devices that are currently transmitting.
- **Configurable FEC**: Allows users to toggle Forward Error Correction and adjust redundancy levels dynamically to see their impact on communication reliability.
- **Live Bit Stream**: Displays the raw bit stream being decoded from the acoustic signal in real-time.

You can run the sample app on Android, iOS, or Desktop to test the protocol between multiple devices.

## Module Structure

Whisper is organized into several modules to maintain a clear separation of concerns:

- **whisper**: The high-level API for application developers.
- **whisper-core**: Core data models and packet definitions.
- **whisper-dsp**: Digital signal processing logic, including filters and modems.
- **whisper-audio**: Platform-specific audio recording and playback implementations.
- **doc**: Documentation and landing page project.

## Requirements

- **Android**: API Level 29 or higher.
- **iOS**: iOS 14.0 or higher.
- **Desktop**: (On the way) support for Linux, macOS, and Windows.
- **Web**: (On the way) support via Kotlin/Wasm.

## Security Note

Whisper is designed for local, proximity-based communication. While it is decentralized by nature, users should implement their own encryption layers if they are transmitting sensitive information, as acoustic signals can be recorded by any nearby device with a microphone.

Support for manual encryption within the protocol is currently on the way, which will provide built-in hooks for securing the data payload before transmission.
