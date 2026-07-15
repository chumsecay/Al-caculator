package com.aicalculator.calc.ai

data class AiThinkingState(
    val isActive: Boolean = false,
    val logs: List<String> = emptyList(),
    val expressionHint: String = "",
)
