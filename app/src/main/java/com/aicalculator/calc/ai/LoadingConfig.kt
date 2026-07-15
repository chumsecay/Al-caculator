package com.aicalculator.calc.ai

data class LoadingConfig(
    val loadingDurationMs: Long = 3000L,
    val lineDelayMs: Long = 220L,
    val fadeInMs: Int = 220,
    val fadeOutMs: Int = 280,
) {
    init {
        require(lineDelayMs > 0)
        require(loadingDurationMs > 0)
    }

    companion object {
        val Default = LoadingConfig()
    }
}
