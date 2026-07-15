package com.aicalculator.calc.ai

import org.junit.Assert.assertEquals
import org.junit.Test

class FakeLogEngineTest {

    @Test
    fun nextSession_returnsFullListInOrder() {
        assertEquals(LogRepository.lines, FakeLogEngine().nextSession())
    }

    @Test
    fun nextSession_startsAndEndsAsConfigured() {
        val session = FakeLogEngine().nextSession()
        assertEquals("Initializing neural calculator...", session.first())
        assertEquals("Finalizing response...", session.last())
    }
}
