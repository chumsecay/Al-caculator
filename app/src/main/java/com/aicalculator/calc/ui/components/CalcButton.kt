package com.aicalculator.calc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicalculator.calc.ui.ComposeMod
import com.aicalculator.calc.ui.EmptyMod
import com.aicalculator.calc.ui.theme.CalcEqBtn
import com.aicalculator.calc.ui.theme.CalcFnBtn
import com.aicalculator.calc.ui.theme.CalcGlassBorder
import com.aicalculator.calc.ui.theme.CalcNumBtn
import com.aicalculator.calc.ui.theme.CalcOpBtn
import com.aicalculator.calc.ui.theme.CalcText

enum class CalcButtonStyle {
    Number,
    Operator,
    Function,
    Equals,
}

@Composable
fun CalcButton(
    text: String,
    style: CalcButtonStyle,
    onClick: () -> Unit,
    modifier: ComposeMod = EmptyMod,
    circle: Boolean = true,
) {
    val shape: Shape = if (circle) CircleShape else RoundedCornerShape(20.dp)
    val labelSize = if (text.length > 1 && text != "CE" && text != "AC") 20.sp else 26.sp

    val bgBrush: Brush? = when (style) {
        CalcButtonStyle.Operator -> Brush.linearGradient(
            listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA)),
        )
        CalcButtonStyle.Equals -> Brush.linearGradient(
            listOf(Color(0xFFEC4899), Color(0xFFF472B6)),
        )
        else -> null
    }
    val flatColor = when (style) {
        CalcButtonStyle.Number -> CalcNumBtn
        CalcButtonStyle.Function -> CalcFnBtn
        CalcButtonStyle.Operator -> CalcOpBtn
        CalcButtonStyle.Equals -> CalcEqBtn
    }

    val interaction = remember { MutableInteractionSource() }
    val glow = when (style) {
        CalcButtonStyle.Equals -> CalcEqBtn.copy(alpha = 0.50f)
        CalcButtonStyle.Operator -> CalcOpBtn.copy(alpha = 0.45f)
        else -> Color.Transparent
    }
    val elev = if (style == CalcButtonStyle.Number || style == CalcButtonStyle.Function) {
        0.dp
    } else {
        10.dp
    }

    val painted = if (bgBrush != null) {
        modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .shadow(elev, shape, ambientColor = glow, spotColor = glow)
            .clip(shape)
            .background(bgBrush, shape)
    } else {
        modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .shadow(elev, shape, ambientColor = glow, spotColor = glow)
            .clip(shape)
            .background(flatColor, shape)
            .border(1.dp, CalcGlassBorder.copy(alpha = 0.35f), shape)
    }

    Box(
        painted
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .semantics { contentDescription = text },
        contentAlignment = Alignment.Center,
    ) {
        if (bgBrush == null) {
            Box(
                EmptyMod
                    .fillMaxSize()
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.14f), Color.Transparent),
                        ),
                    ),
            )
        }
        Text(
            text = text,
            color = CalcText,
            fontSize = labelSize,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = EmptyMod.padding(4.dp),
        )
    }
}
