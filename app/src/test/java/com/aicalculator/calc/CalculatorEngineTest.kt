package com.aicalculator.calc

import com.aicalculator.calc.calculator.CalculatorAction
import com.aicalculator.calc.calculator.CalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setUp() {
        engine = CalculatorEngine()
    }

    private fun digits(vararg d: Int) = d.forEach { engine.inputDigit(it) }
    private fun op(o: CalculatorAction.Op) = engine.setOperation(o)

    @Test
    fun digitEntry_buildsNumber() {
        digits(1, 2, 3)
        assertEquals("123", engine.displayValue)
    }

    @Test
    fun add_twoNumbers() {
        digits(2)
        op(CalculatorAction.Op.Add)
        digits(3)
        engine.calculate()
        assertEquals("5", engine.displayValue)
    }

    @Test
    fun chain_immediateExecution() {
        digits(2)
        op(CalculatorAction.Op.Add)
        digits(3)
        op(CalculatorAction.Op.Multiply)
        assertEquals("5", engine.displayValue)
        digits(4)
        engine.calculate()
        assertEquals("20", engine.displayValue)
    }

    @Test
    fun divideByZero_setsError() {
        digits(8)
        op(CalculatorAction.Op.Divide)
        digits(0)
        engine.calculate()
        assertEquals("Error", engine.displayValue)
        assertTrue(engine.error)
        engine.clear()
        assertEquals("0", engine.displayValue)
    }

    @Test
    fun canClearEntry_togglesWithInput() {
        assertFalse(engine.canClearEntry)
        digits(1, 2)
        assertTrue(engine.canClearEntry)
        engine.clearEntry()
        assertFalse(engine.canClearEntry)
    }

    @Test
    fun percent_ofLeftOperand() {
        digits(2, 0, 0)
        op(CalculatorAction.Op.Add)
        digits(1, 0)
        engine.percent()
        assertEquals("20", engine.displayValue)
        engine.calculate()
        assertEquals("220", engine.displayValue)
    }
}
