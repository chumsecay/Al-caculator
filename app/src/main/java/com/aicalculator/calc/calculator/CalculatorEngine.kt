package com.aicalculator.calc.calculator

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Pure immediate-execution calculator (stock phone style).
 * No Android deps — unit-testable.
 */
class CalculatorEngine {

    private var accumulator: BigDecimal? = null
    private var currentInput: String = "0"
    private var pendingOp: CalculatorAction.Op? = null
    private var expressionLine: String = ""
    private var isResult: Boolean = false
    private var isError: Boolean = false
    private var overwriteInput: Boolean = false

    val displayValue: String
        get() = if (isError) ERROR_TEXT else formatDisplay(currentInput)

    val expression: String
        get() = if (isError) "" else expressionLine

    val error: Boolean
        get() = isError

    val resultMode: Boolean
        get() = isResult

    val canClearEntry: Boolean
        get() {
            if (isError || isResult || overwriteInput) return false
            if (currentInput == "0" || currentInput == "-0") return false
            return true
        }

    fun snapshot(): CalculatorState = CalculatorState(
        displayValue = displayValue,
        expression = expression,
        isError = isError,
        isResult = isResult,
        canClearEntry = canClearEntry,
    )

    fun inputDigit(digit: Int) {
        require(digit in 0..9)
        if (isError) return

        if (isResult || overwriteInput) {
            currentInput = digit.toString()
            isResult = false
            overwriteInput = false
            if (pendingOp == null) {
                expressionLine = ""
                accumulator = null
            }
            return
        }

        if (currentInput == "0") {
            currentInput = digit.toString()
        } else if (currentInput == "-0") {
            currentInput = "-$digit"
        } else {
            val significant = currentInput.replace(".", "").removePrefix("-")
            if (significant.length >= MAX_DIGITS) return
            currentInput += digit
        }
    }

    fun inputDecimal() {
        if (isError) return

        if (isResult || overwriteInput) {
            currentInput = "0."
            isResult = false
            overwriteInput = false
            if (pendingOp == null) {
                expressionLine = ""
                accumulator = null
            }
            return
        }

        if (!currentInput.contains('.')) {
            currentInput += if (currentInput == "-" || currentInput.isEmpty()) "0." else "."
        }
    }

    fun setOperation(op: CalculatorAction.Op) {
        if (isError) return

        if (pendingOp != null && !overwriteInput && !isResult) {
            val right = parseInput(currentInput) ?: return setError()
            val left = accumulator ?: BigDecimal.ZERO
            val result = applyOp(left, pendingOp!!, right) ?: return setError()
            accumulator = result
            currentInput = formatRaw(result)
            expressionLine = "${formatRaw(result)} ${op.symbol}"
        } else {
            val value = parseInput(currentInput) ?: return setError()
            accumulator = value
            expressionLine = "${formatRaw(value)} ${op.symbol}"
        }

        pendingOp = op
        isResult = false
        overwriteInput = true
    }

    fun calculate() {
        if (isError) return
        if (pendingOp == null) {
            expressionLine = if (currentInput.isNotEmpty()) {
                "${formatDisplay(currentInput)} ="
            } else {
                expressionLine
            }
            isResult = true
            overwriteInput = true
            return
        }

        val right = parseInput(currentInput) ?: return setError()
        val left = accumulator ?: BigDecimal.ZERO
        val op = pendingOp!!
        val result = applyOp(left, op, right) ?: return setError()

        expressionLine = "${formatRaw(left)} ${op.symbol} ${formatRaw(right)} ="
        currentInput = formatRaw(result)
        accumulator = result
        pendingOp = null
        isResult = true
        overwriteInput = true
    }

    fun clear() {
        accumulator = null
        currentInput = "0"
        pendingOp = null
        expressionLine = ""
        isResult = false
        isError = false
        overwriteInput = false
    }

    fun clearEntry() {
        if (isError) return
        currentInput = "0"
        isResult = false
        overwriteInput = false
    }

    fun backspace() {
        if (isError) return

        if (isResult || overwriteInput) {
            currentInput = "0"
            isResult = false
            overwriteInput = false
            if (pendingOp == null) {
                expressionLine = ""
                accumulator = null
            }
            return
        }

        if (currentInput.length <= 1 || currentInput == "-0" ||
            (currentInput.startsWith("-") && currentInput.length == 2)
        ) {
            currentInput = "0"
            return
        }
        currentInput = currentInput.dropLast(1)
        if (currentInput == "-" || currentInput.isEmpty()) {
            currentInput = "0"
        }
    }

    fun toggleSign() {
        if (isError) return

        if (isResult) {
            isResult = false
            overwriteInput = false
        }

        if (overwriteInput && pendingOp != null) {
            overwriteInput = false
        }

        val value = parseInput(currentInput) ?: return
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            currentInput = "0"
            return
        }

        currentInput = if (currentInput.startsWith("-")) {
            currentInput.removePrefix("-")
        } else {
            "-$currentInput"
        }
    }

    fun percent() {
        if (isError) return

        val n = parseInput(currentInput) ?: return setError()

        val result = if (pendingOp != null && accumulator != null) {
            accumulator!!.multiply(n, MATH_CTX)
                .divide(BigDecimal(100), MATH_CTX)
        } else {
            n.divide(BigDecimal(100), MATH_CTX)
        }

        currentInput = formatRaw(result)
        isResult = false
        overwriteInput = true
    }

    private fun applyOp(left: BigDecimal, op: CalculatorAction.Op, right: BigDecimal): BigDecimal? {
        return try {
            when (op) {
                CalculatorAction.Op.Add -> left.add(right, MATH_CTX)
                CalculatorAction.Op.Subtract -> left.subtract(right, MATH_CTX)
                CalculatorAction.Op.Multiply -> left.multiply(right, MATH_CTX)
                CalculatorAction.Op.Divide -> {
                    if (right.compareTo(BigDecimal.ZERO) == 0) return null
                    left.divide(right, MATH_CTX)
                }
            }
        } catch (_: ArithmeticException) {
            null
        }
    }

    private fun setError() {
        isError = true
        currentInput = ERROR_TEXT
        expressionLine = ""
        pendingOp = null
        accumulator = null
        isResult = false
        overwriteInput = false
    }

    private fun parseInput(s: String): BigDecimal? {
        if (s.isEmpty() || s == "-" || s == "." || s == "-.") return BigDecimal.ZERO
        return try {
            BigDecimal(s, MATH_CTX)
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun formatRaw(value: BigDecimal): String {
        var v = value.round(MATH_CTX).stripTrailingZeros()
        if (v.abs() < NEAR_ZERO && v.compareTo(BigDecimal.ZERO) != 0) {
            v = BigDecimal.ZERO
        }
        if (v.compareTo(BigDecimal.ZERO) == 0) return "0"

        val plain = v.toPlainString()
        if (plain.replace("-", "").replace(".", "").length > MAX_DISPLAY_DIGITS ||
            v.abs() >= SCI_THRESHOLD || (v.abs() < SCI_SMALL && v.compareTo(BigDecimal.ZERO) != 0)
        ) {
            return formatScientific(v)
        }
        return plain
    }

    private fun formatDisplay(raw: String): String {
        if (raw == ERROR_TEXT) return ERROR_TEXT
        if (raw.endsWith(".") || raw == "-0" || raw == "0." || raw == "-0.") return raw
        return try {
            formatRaw(BigDecimal(raw, MATH_CTX))
        } catch (_: NumberFormatException) {
            raw
        }
    }

    private fun formatScientific(v: BigDecimal): String {
        val d = v.toDouble()
        if (d.isInfinite() || d.isNaN()) return ERROR_TEXT
        return String.format(java.util.Locale.US, "%.8g", d)
            .replace('E', 'e')
    }

    companion object {
        const val ERROR_TEXT = "Error"
        private const val MAX_DIGITS = 15
        private const val MAX_DISPLAY_DIGITS = 12
        private val MATH_CTX = MathContext(16, RoundingMode.HALF_UP)
        private val NEAR_ZERO = BigDecimal("1e-15")
        private val SCI_THRESHOLD = BigDecimal("1e12")
        private val SCI_SMALL = BigDecimal("1e-9")
    }
}
