package com.aicalculator.calc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aicalculator.calc.ui.CalculatorScreen
import com.aicalculator.calc.ui.EmptyMod
import com.aicalculator.calc.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Surface(
                    modifier = EmptyMod.fillMaxSize(),
                    color = Color.Transparent,
                ) {
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val thinking by viewModel.thinking.collectAsStateWithLifecycle()
                    CalculatorScreen(
                        state = state,
                        thinking = thinking,
                        onAction = viewModel::onAction,
                    )
                }
            }
        }
    }
}
