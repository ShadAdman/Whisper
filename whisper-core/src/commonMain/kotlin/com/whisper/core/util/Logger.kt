package com.whisper.core.util

expect object WLogger {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun e(tag: String, message: String)
}
