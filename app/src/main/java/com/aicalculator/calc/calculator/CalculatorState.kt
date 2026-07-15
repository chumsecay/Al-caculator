package com.aicalculator.calc.calculator

data class CalculatorState(
    val displayValue: String = "0",
    val expression: String = "",
    val isError: Boolean = false,
    val isResult: Boolean = false,
    /** true → label "C"; false → label "AC" */
    val canClearEntry: Boolean = false,
)
