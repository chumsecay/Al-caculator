package com.aicalculator.calc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicalculator.calc.ai.AiThinkingState
import com.aicalculator.calc.ai.FakeLogEngine
import com.aicalculator.calc.ai.LoadingConfig
import com.aicalculator.calc.calculator.CalculatorAction
import com.aicalculator.calc.calculator.CalculatorEngine
import com.aicalculator.calc.calculator.CalculatorState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val engine: CalculatorEngine = CalculatorEngine(),
    private val loadingConfig: LoadingConfig = LoadingConfig.Default,
    private val fakeLogEngine: FakeLogEngine = FakeLogEngine(),
) : ViewModel() {

    private val _state = MutableStateFlow(engine.snapshot())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    private val _thinking = MutableStateFlow(AiThinkingState())
    val thinking: StateFlow<AiThinkingState> = _thinking.asStateFlow()

    private var thinkingJob: Job? = null

    fun onAction(action: CalculatorAction) {
        if (action is CalculatorAction.Equals) {
            onEqualsPressed()
            return
        }
        if (_thinking.value.isActive) return
        dispatch(action)
    }

    private fun onEqualsPressed() {
        if (_thinking.value.isActive) return

        val hint = buildExpressionHint()
        engine.calculate()

        val logs = fakeLogEngine.nextSession()
        thinkingJob?.cancel()
        _thinking.value = AiThinkingState(
            isActive = true,
            logs = logs,
            expressionHint = hint,
        )

        thinkingJob = viewModelScope.launch {
            delay(loadingConfig.loadingDurationMs)
            _state.update { engine.snapshot() }
            _thinking.value = AiThinkingState()
        }
    }

    private fun buildExpressionHint(): String {
        val expr = engine.expression.trim()
        val display = engine.displayValue
        return when {
            expr.isNotEmpty() && !expr.endsWith("=") -> "$expr $display"
            expr.isNotEmpty() -> expr
            else -> display
        }
    }

    private fun dispatch(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Digit -> engine.inputDigit(action.digit)
            CalculatorAction.Decimal -> engine.inputDecimal()
            CalculatorAction.Equals -> engine.calculate()
            CalculatorAction.Clear -> engine.clear()
            CalculatorAction.ClearEntry -> engine.clearEntry()
            CalculatorAction.Backspace -> engine.backspace()
            CalculatorAction.ToggleSign -> engine.toggleSign()
            CalculatorAction.Percent -> engine.percent()
            is CalculatorAction.Operation -> engine.setOperation(action.op)
        }
        _state.update { engine.snapshot() }
    }
}
