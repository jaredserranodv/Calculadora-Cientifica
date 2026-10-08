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
    private lateinit var visor: TextView
    private var expresion = ""
    private var resultadoMostrado = false

    // Ciclo de vida: Android crea la Activity de nuevo al rotar y carga el XML adecuado.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        visor = findViewById(R.id.display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bordes = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val relleno = (8 * resources.displayMetrics.density).toInt()
            view.setPadding(relleno + bordes.left, relleno + bordes.top,
                relleno + bordes.right, relleno + bordes.bottom)
            insets
        }
    }

    // Guardado de estado: la expresión contiene los operandos y operadores pendientes.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("expresion", expresion)
        outState.putString("visor", visor.text.toString())
        outState.putBoolean("resultadoMostrado", resultadoMostrado)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        expresion = savedInstanceState.getString("expresion", "")
        visor.text = savedInstanceState.getString("visor", "0")
        resultadoMostrado = savedInstanceState.getBoolean("resultadoMostrado")
    }

    // Vistas: los botones de ambos ConstraintLayout llaman al mismo método.
    fun onButtonClick(view: View) {
        val tecla = (view as Button).tag.toString()
        when (tecla) {
            "C" -> limpiar()
            "DEL" -> {
                if (visor.text == "Error") limpiar()
                else {
                    expresion = expresion.dropLast(1)
                    visor.text = expresion.ifEmpty { "0" }
                    resultadoMostrado = false
                }
            }
            "=" -> {
                try {
                    val valor = CalculatorEngine.evaluate(expresion)
                    expresion = BigDecimal.valueOf(valor).stripTrailingZeros().toPlainString()
                    visor.text = expresion
                } catch (_: IllegalArgumentException) {
                    expresion = ""
                    visor.text = "Error"
                }
                resultadoMostrado = true
            }
            "±" -> cambiarSigno()
            else -> {
                val iniciaValor = tecla.first().isDigit() || tecla in listOf(".", "(", "π", "e") || tecla.endsWith("(")
                if (visor.text == "Error" || (resultadoMostrado && iniciaValor)) expresion = ""
                resultadoMostrado = false
                expresion += tecla
                visor.text = expresion
            }
        }
    }

    private fun cambiarSigno() {
        if (visor.text == "Error") limpiar()
        val numero = Regex("(?:\\d+(?:\\.\\d*)?|\\.\\d+)$").find(expresion)
        if (numero == null) {
            expresion += "-"
        } else {
            val inicio = numero.range.first
            expresion = if (inicio > 0 && expresion[inicio - 1] == '-' &&
                (inicio == 1 || expresion[inicio - 2] in "+-×÷^(")) {
                expresion.removeRange(inicio - 1, inicio)
            } else expresion.substring(0, inicio) + "-" + expresion.substring(inicio)
        }
        resultadoMostrado = false
        visor.text = expresion
    }

    private fun limpiar() {
        expresion = ""
        visor.text = "0"
        resultadoMostrado = false
    }
}
