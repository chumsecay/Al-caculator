package com.aicalculator.calc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicalculator.calc.ai.AIThinkingOverlay
import com.aicalculator.calc.ai.AiThinkingState
import com.aicalculator.calc.calculator.CalculatorAction
import com.aicalculator.calc.calculator.CalculatorState
import com.aicalculator.calc.ui.components.CalcButton
import com.aicalculator.calc.ui.components.CalcButtonStyle
import com.aicalculator.calc.ui.theme.CalcBgBottom
import com.aicalculator.calc.ui.theme.CalcBgMid
import com.aicalculator.calc.ui.theme.CalcBgTop
import com.aicalculator.calc.ui.theme.CalcGlass
import com.aicalculator.calc.ui.theme.CalcGlassBorder
import com.aicalculator.calc.ui.theme.CalcGlassStrong
import com.aicalculator.calc.ui.theme.CalcOrbBlue
import com.aicalculator.calc.ui.theme.CalcOrbCyan
import com.aicalculator.calc.ui.theme.CalcOrbPink
import com.aicalculator.calc.ui.theme.CalcOrbPurple
import com.aicalculator.calc.ui.theme.CalcText
import com.aicalculator.calc.ui.theme.CalcTextMuted
import com.aicalculator.calc.ui.theme.CalculatorTheme

@Composable
fun CalculatorScreen(
    state: CalculatorState,
    thinking: AiThinkingState = AiThinkingState(),
    onAction: (CalculatorAction) -> Unit,
) {
    Box(EmptyMod.fillMaxSize()) {
        BlurBackground()
        Column(
            EmptyMod
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Display(state = state, thinking = thinking)
            Pad(state, onAction)
        }
    }
}

@Composable
private fun BlurBackground() {
    Box(EmptyMod.fillMaxSize()) {
        Box(
            EmptyMod
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(CalcBgTop, CalcBgMid, CalcBgBottom),
                    ),
                ),
        )
        Box(
            EmptyMod
                .size(280.dp)
                .align(Alignment.TopStart)
                .offset((-60).dp, (-40).dp)
                .blur(80.dp)
                .background(CalcOrbPurple.copy(alpha = 0.55f), CircleShape),
        )
        Box(
            EmptyMod
                .size(220.dp)
                .align(Alignment.TopEnd)
                .offset(40.dp, 80.dp)
                .blur(70.dp)
                .background(CalcOrbPink.copy(alpha = 0.40f), CircleShape),
        )
        Box(
            EmptyMod
                .size(300.dp)
                .align(Alignment.BottomCenter)
                .offset((-20).dp, 40.dp)
                .blur(90.dp)
                .background(CalcOrbBlue.copy(alpha = 0.35f), CircleShape),
        )
        Box(
            EmptyMod
                .size(160.dp)
                .align(Alignment.BottomEnd)
                .offset((-30).dp, (-120).dp)
                .blur(60.dp)
                .background(CalcOrbCyan.copy(alpha = 0.25f), CircleShape),
        )
    }
}

@Composable
private fun ColumnScope.Display(
    state: CalculatorState,
    thinking: AiThinkingState,
) {
    val glassShape = RoundedCornerShape(28.dp)
    Box(
        EmptyMod
            .fillMaxWidth()
            .weight(1f)
            .clip(glassShape)
            .background(CalcGlass)
            .border(1.dp, CalcGlassBorder.copy(alpha = 0.40f), glassShape),
        contentAlignment = Alignment.BottomEnd,
    ) {
        if (!thinking.isActive) {
            Box(
                EmptyMod
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 20.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Box(
                    EmptyMod
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.10f), Color.Transparent),
                            ),
                        ),
                )
                Column(
                    EmptyMod.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Bottom),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = state.expression.ifEmpty { " " },
                        color = CalcTextMuted,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = EmptyMod.fillMaxWidth(),
                    )
                    Text(
                        text = state.displayValue,
                        color = CalcText,
                        fontSize = sizeFor(state.displayValue.length),
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = EmptyMod.fillMaxWidth(),
                    )
                }
            }
        }

        AIThinkingOverlay(
            visible = thinking.isActive,
            logs = thinking.logs,
            expressionHint = thinking.expressionHint,
        )
    }
}

private fun sizeFor(length: Int): TextUnit {
    return when {
        length > 12 -> 36.sp
        length > 9 -> 44.sp
        else -> 58.sp
    }
}

@Composable
private fun Pad(state: CalculatorState, onAction: (CalculatorAction) -> Unit) {
    val gap = 12.dp
    val padShape = RoundedCornerShape(28.dp)
    val clearLabel = if (state.canClearEntry) "C" else "AC"
    val clearAction =
        if (state.canClearEntry) CalculatorAction.ClearEntry else CalculatorAction.Clear
    Column(
        EmptyMod
            .fillMaxWidth()
            .clip(padShape)
            .background(CalcGlassStrong.copy(alpha = 0.12f))
            .border(1.dp, CalcGlassBorder.copy(alpha = 0.22f), padShape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        ButtonRow(gap) {
            FnKey(clearLabel, onAction) { clearAction }
            Key("±", CalcButtonStyle.Function) { onAction(CalculatorAction.ToggleSign) }
            FnKey("%", onAction) { CalculatorAction.Percent }
            OpKey("÷", CalculatorAction.Op.Divide, onAction)
        }
        ButtonRow(gap) {
            NumKey(7, onAction)
            NumKey(8, onAction)
            NumKey(9, onAction)
            OpKey("×", CalculatorAction.Op.Multiply, onAction)
        }
        ButtonRow(gap) {
            NumKey(4, onAction)
            NumKey(5, onAction)
            NumKey(6, onAction)
            OpKey("−", CalculatorAction.Op.Subtract, onAction)
        }
        ButtonRow(gap) {
            NumKey(1, onAction)
            NumKey(2, onAction)
            NumKey(3, onAction)
            OpKey("+", CalculatorAction.Op.Add, onAction)
        }
        ButtonRow(gap) {
            CalcButton(
                text = "0",
                style = CalcButtonStyle.Number,
                onClick = { onAction(CalculatorAction.Digit(0)) },
                circle = false,
                modifier = EmptyMod
                    .weight(2f)
                    .aspectRatio(2f),
            )
            Key(".", CalcButtonStyle.Number) { onAction(CalculatorAction.Decimal) }
            Key("=", CalcButtonStyle.Equals) { onAction(CalculatorAction.Equals) }
        }
    }
}

@Composable
private fun ButtonRow(gap: Dp, content: @Composable RowScope.() -> Unit) {
    Row(
        EmptyMod.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun RowScope.Key(
    label: String,
    style: CalcButtonStyle,
    onClick: () -> Unit,
) {
    CalcButton(
        text = label,
        style = style,
        onClick = onClick,
        modifier = EmptyMod
            .weight(1f)
            .aspectRatio(1f),
    )
}

@Composable
private fun RowScope.NumKey(d: Int, onAction: (CalculatorAction) -> Unit) {
    Key(d.toString(), CalcButtonStyle.Number) {
        onAction(CalculatorAction.Digit(d))
    }
}

@Composable
private fun RowScope.OpKey(
    label: String,
    op: CalculatorAction.Op,
    onAction: (CalculatorAction) -> Unit,
) {
    Key(label, CalcButtonStyle.Operator) {
        onAction(CalculatorAction.Operation(op))
    }
}

@Composable
private fun RowScope.FnKey(
    label: String,
    onAction: (CalculatorAction) -> Unit,
    make: () -> CalculatorAction,
) {
    Key(label, CalcButtonStyle.Function) {
        onAction(make())
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0A1F)
@Composable
private fun CalculatorScreenPreview() {
    CalculatorTheme {
        CalculatorScreen(
            state = CalculatorState(displayValue = "1234.5", expression = "56 x 22"),
            thinking = AiThinkingState(),
            onAction = {},
        )
    }
}
