package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatorapp.ui.theme.CalculatorAppTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorAppTheme {
                CalculatorApp()
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CalculatorApp() {
    val displayValue = remember { mutableStateOf("0") } // Estado do display
    val currentValue = remember { mutableStateOf("0") } // Valor atual inserido
    val operator = remember { mutableStateOf("") } // Operador atual
    val result = remember { mutableStateOf(0.0) } // Resultado acumulado
    val newInput = remember { mutableStateOf(true) } // Controla se a entrada é nova

    Scaffold(
        topBar = { StyledTopAppBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Display(displayValue.value)
            CalculatorButtons { label -> handleButtonClick(label, displayValue, currentValue, operator, result, newInput) }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun StyledTopAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Calculadora",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp
                ),
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

fun handleButtonClick(
    label: String,
    displayValue: MutableState<String>,
    currentValue: MutableState<String>,
    operator: MutableState<String>,
    result: MutableState<Double>,
    newInput: MutableState<Boolean>
) {
    when (label) {
        "C" -> {
            displayValue.value = "0"
            currentValue.value = "0"
            operator.value = ""
            result.value = 0.0
            newInput.value = true
        }
        "=" -> {
            if (operator.value.isNotEmpty() && currentValue.value.isNotEmpty()) {
                try {
                    val secondOperand = currentValue.value.toDouble()
                    result.value = performCalculation(result.value, operator.value, secondOperand)
                    displayValue.value = formatResult(result.value)
                    currentValue.value = result.value.toString()
                    operator.value = ""
                    newInput.value = true
                } catch (e: Exception) {
                    displayValue.value = "Erro"
                }
            }
        }
        "+", "-", "x", "÷" -> {
            if (operator.value.isNotEmpty()) {
                result.value = performCalculation(result.value, operator.value, currentValue.value.toDouble())
            } else {
                result.value = currentValue.value.toDouble()
            }
            operator.value = label
            displayValue.value = formatResult(result.value) + " " + label
            newInput.value = true
        }
        else -> {
            if (newInput.value || displayValue.value in listOf("+", "-", "x", "÷")) {
                displayValue.value = label
                currentValue.value = label
                newInput.value = false
            } else {
                displayValue.value += label
                currentValue.value += label
            }
        }
    }
}

fun performCalculation(firstOperand: Double, operator: String, secondOperand: Double): Double {
    return try {
        when (operator) {
            "+" -> firstOperand + secondOperand
            "-" -> firstOperand - secondOperand
            "x" -> firstOperand * secondOperand
            "÷" -> if (secondOperand != 0.0) firstOperand / secondOperand else Double.NaN
            else -> firstOperand
        }
    } catch (e: Exception) {
        Double.NaN
    }
}

fun formatResult(result: Double): String {
    return if (result % 1.0 == 0.0) result.toInt().toString() else result.toString()
}

@Composable
fun Display(value: String) {
    Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        maxLines = 1
    )
}

@Composable
fun CalculatorButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun CalculatorButtons(onButtonClick: (String) -> Unit) {
    val buttons = listOf(
        "7", "8", "9", "÷",
        "4", "5", "6", "x",
        "1", "2", "3", "-",
        "C", "0", "=", "+"
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(buttons) { label ->
            CalculatorButton(label = label, onClick = { onButtonClick(label) })
        }
    }
}

@ExperimentalMaterial3Api
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculatorAppTheme {
        CalculatorApp()
    }
}
