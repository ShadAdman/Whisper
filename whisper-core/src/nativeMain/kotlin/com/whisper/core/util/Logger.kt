package com.whisper.core.util

actual object WLogger {
    actual fun d(tag: String, message: String) {
        println("DEBUG: [$tag] $message")
    }

    actual fun i(tag: String, message: String) {
        println("INFO: [$tag] $message")
    }

    actual fun e(tag: String, message: String) {
        println("ERROR: [$tag] $message")
    }
}
