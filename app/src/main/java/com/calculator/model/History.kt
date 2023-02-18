package com.calculator.model
import com.calculator.controller.TimeController
data class History(val mathematicalExpression: String, val result: String, val dateOfExecution: String = TimeController.getCurrentTime())