package com.fcc.calculadoracientifica

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {
    private lateinit var display: TextView
    private var expression = ""
    private var resultShown = false

    // Ciclo de vida: Android crea la Activity de nuevo al rotar y carga el XML adecuado.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        display = findViewById(R.id.display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val padding = (8 * resources.displayMetrics.density).toInt()
            view.setPadding(padding + bars.left, padding + bars.top,
                padding + bars.right, padding + bars.bottom)
            insets
        }
    }

    // Guardado de estado: la expresión contiene los operandos y operadores pendientes.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("expression", expression)
        outState.putString("display", display.text.toString())
        outState.putBoolean("resultShown", resultShown)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        expression = savedInstanceState.getString("expression", "")
        display.text = savedInstanceState.getString("display", "0")
        resultShown = savedInstanceState.getBoolean("resultShown")
    }

    // Vistas: los botones de ambos ConstraintLayout llaman al mismo método.
    fun onButtonClick(view: View) {
        val key = (view as Button).tag.toString()
        when (key) {
            "C" -> clear()
            "DEL" -> {
                if (display.text == "Error") clear()
                else {
                    expression = expression.dropLast(1)
                    display.text = expression.ifEmpty { "0" }
                    resultShown = false
                }
            }
            "=" -> {
                try {
                    val value = CalculatorEngine.evaluate(expression)
                    expression = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
                    display.text = expression
                } catch (_: IllegalArgumentException) {
                    expression = ""
                    display.text = "Error"
                }
                resultShown = true
            }
            "±" -> toggleSign()
            else -> {
                val startsValue = key.first().isDigit() || key in listOf(".", "(", "π", "e") || key.endsWith("(")
                if (display.text == "Error" || (resultShown && startsValue)) expression = ""
                resultShown = false
                expression += key
                display.text = expression
            }
        }
    }

    private fun toggleSign() {
        if (display.text == "Error") clear()
        val number = Regex("(?:\\d+(?:\\.\\d*)?|\\.\\d+)$").find(expression)
        if (number == null) {
            expression += "-"
        } else {
            val start = number.range.first
            expression = if (start > 0 && expression[start - 1] == '-' &&
                (start == 1 || expression[start - 2] in "+-×÷^(")) {
                expression.removeRange(start - 1, start)
            } else expression.substring(0, start) + "-" + expression.substring(start)
        }
        resultShown = false
        display.text = expression
    }

    private fun clear() {
        expression = ""
        display.text = "0"
        resultShown = false
    }
}
