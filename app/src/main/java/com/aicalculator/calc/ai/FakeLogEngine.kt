package com.aicalculator.calc.ai

/** Returns thinking logs in fixed repository order. */
class FakeLogEngine(
    private val repository: LogRepository = LogRepository,
) {
    fun nextSession(): List<String> = repository.lines
}
