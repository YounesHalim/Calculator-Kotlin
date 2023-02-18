package com.calculator.controller
import java.time.LocalDateTime
object TimeController {
    fun getCurrentTime(): String {
        return LocalDateTime.now().toString()
    }
}