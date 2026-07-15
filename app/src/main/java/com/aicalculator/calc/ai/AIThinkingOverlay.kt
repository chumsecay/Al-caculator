package com.aicalculator.calc.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicalculator.calc.ui.ComposeMod
import com.aicalculator.calc.ui.EmptyMod
import kotlinx.coroutines.delay

private val TerminalPanel = Color(0xF00A1018)
private val Cyan = Color(0xFF67E8F9)
private val CyanDim = Color(0xFF22D3EE)
private val GreenDone = Color(0xFF86EFAC)
private val Border = Color(0x5567E8F9)

@Composable
fun AIThinkingOverlay(
    visible: Boolean,
    logs: List<String>,
    config: LoadingConfig = LoadingConfig.Default,
    expressionHint: String = "",
) {
    AnimatedVisibility(
        visible = visible && logs.isNotEmpty(),
        enter = fadeIn(tween(config.fadeInMs)),
        exit = fadeOut(tween(config.fadeOutMs)),
    ) {
        Box(
            EmptyMod
                .fillMaxSize()
                .background(TerminalPanel)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Column(
                EmptyMod.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Header(expressionHint)
                LogStream(
                    logs = logs,
                    config = config,
                    modifier = EmptyMod
                        .fillMaxWidth()
                        .weight(1f),
                )
                Footer()
            }
        }
    }
}

@Composable
private fun Header(expressionHint: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "AI THINKING",
            color = Cyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
        Text(
            text = if (expressionHint.isBlank()) "> evaluating..." else "> $expressionHint",
            color = CyanDim.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
        )
        Text(
            text = "────────────────────────",
            color = Border,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun LogStream(
    logs: List<String>,
    config: LoadingConfig,
    modifier: ComposeMod = EmptyMod,
) {
    val listState = rememberLazyListState()
    var revealed by remember(logs) { mutableIntStateOf(1) }

    LaunchedEffect(logs, config.lineDelayMs) {
        if (logs.isEmpty()) return@LaunchedEffect
        revealed = 1
        for (i in 2..logs.size) {
            delay(config.lineDelayMs)
            revealed = i
        }
    }

    LaunchedEffect(revealed, logs) {
        if (logs.isEmpty()) return@LaunchedEffect
        listState.animateScrollToItem((revealed - 1).coerceIn(0, logs.lastIndex))
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        itemsIndexed(
            items = logs.take(revealed),
            key = { i, line -> "$i:$line" },
        ) { index, line ->
            val active = index == revealed - 1
            FullLine(line = line, active = active)
        }
    }
}

@Composable
private fun FullLine(line: String, active: Boolean) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = if (active) "$ " else "✓ ",
            color = if (active) CyanDim else GreenDone.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
        )
        Text(
            text = line,
            color = if (active) Cyan else GreenDone.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun Footer() {
    Text(
        text = "// rendering result…",
        color = CyanDim.copy(alpha = 0.5f),
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
    )
}
