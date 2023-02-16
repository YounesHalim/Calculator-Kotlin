package com.calculator.model
import java.time.LocalDateTime
data class HistoricalData(val mathematicalExpression: String, val result: String, val dateOfExecution: String = LocalDateTime.now().toString())