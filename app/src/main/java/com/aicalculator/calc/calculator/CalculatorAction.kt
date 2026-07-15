package com.aicalculator.calc.calculator

sealed interface CalculatorAction {
    data class Digit(val digit: Int) : CalculatorAction {
        init {
            require(digit in 0..9) { "digit must be 0..9" }
        }
    }

    data object Decimal : CalculatorAction
    data object Equals : CalculatorAction
    data object Clear : CalculatorAction
    data object ClearEntry : CalculatorAction
    data object Backspace : CalculatorAction
    data object ToggleSign : CalculatorAction
    data object Percent : CalculatorAction

    data class Operation(val op: Op) : CalculatorAction

    enum class Op(val symbol: String) {
        Add("+"),
        Subtract("−"),
        Multiply("×"),
        Divide("÷");
    }
}
