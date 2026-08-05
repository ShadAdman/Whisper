package com.whisper.core.util

actual object WLogger {
    actual fun d(tag: String, message: String) { println("D/$tag: $message") }
    actual fun i(tag: String, message: String) { println("I/$tag: $message") }
    actual fun e(tag: String, message: String) { println("E/$tag: $message") }
}
