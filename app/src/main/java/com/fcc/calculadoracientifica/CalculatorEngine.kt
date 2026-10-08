package com.fcc.calculadoracientifica

import kotlin.math.*

/** Evalúa la misma expresión en portrait y landscape, sin depender de vistas Android. */
object CalculatorEngine {
    fun evaluate(expresion: String): Double {
        val analizador = Analizador(expresion)
        val resultado = analizador.analizar()
        require(resultado.isFinite())
        return resultado
    }

    private class Analizador(private val entrada: String) {
        private var posicion = 0

        fun analizar(): Double {
            require(entrada.isNotBlank())
            val valor = expresion()
            require(posicion == entrada.length)
            return valor
        }

        private fun expresion(): Double {
            var valor = termino()
            while (true) {
                valor = when {
                    tomar('+') -> valor + termino()
                    tomar('−') || tomar('-') -> valor - termino()
                    else -> return valor
                }
            }
        }

        private fun termino(): Double {
            var valor = unario()
            while (true) {
                valor = when {
                    tomar('×') -> valor * unario()
                    tomar('÷') -> {
                        val divisor = unario()
                        require(divisor != 0.0)
                        valor / divisor
                    }
                    else -> return valor
                }
            }
        }

        private fun unario(): Double = when {
            tomar('+') -> unario()
            tomar('−') || tomar('-') -> -unario()
            else -> potencia()
        }

        private fun potencia(): Double {
            val base = sufijo()
            return if (tomar('^')) base.pow(unario()) else base
        }

        private fun sufijo(): Double {
            var valor = primario()
            while (true) {
                valor = when {
                    tomar('%') -> valor / 100.0
                    tomar('!') -> factorial(valor)
                    else -> return valor
                }
            }
        }

        private fun primario(): Double {
            if (tomar('(')) {
                val valor = expresion()
                require(tomar(')'))
                return valor
            }
            if (tomar('π')) return PI
            if (tomar('e')) return E
            val inicio = posicion
            while (posicion < entrada.length && entrada[posicion].isLetter()) posicion++
            if (posicion > inicio) {
                val funcion = entrada.substring(inicio, posicion)
                require(tomar('('))
                val argumento = expresion()
                require(tomar(')'))
                return when (funcion) {
                    "sin" -> sin(Math.toRadians(argumento))
                    "cos" -> cos(Math.toRadians(argumento))
                    "tan" -> tan(Math.toRadians(argumento))
                    "asin" -> Math.toDegrees(asin(argumento))
                    "acos" -> Math.toDegrees(acos(argumento))
                    "atan" -> Math.toDegrees(atan(argumento))
                    "sqrt" -> { require(argumento >= 0); sqrt(argumento) }
                    "log" -> { require(argumento > 0); log10(argumento) }
                    "ln" -> { require(argumento > 0); ln(argumento) }
                    else -> throw IllegalArgumentException("Función desconocida")
                }
            }
            while (posicion < entrada.length && (entrada[posicion].isDigit() || entrada[posicion] == '.')) posicion++
            require(posicion > inicio)
            return entrada.substring(inicio, posicion).toDoubleOrNull()
                ?: throw IllegalArgumentException("Número inválido")
        }

        private fun tomar(caracter: Char): Boolean {
            if (posicion < entrada.length && entrada[posicion] == caracter) {
                posicion++
                return true
            }
            return false
        }

        private fun factorial(valor: Double): Double {
            require(valor >= 0 && valor <= 170 && valor == floor(valor))
            var resultado = 1.0
            for (numero in 2..valor.toInt()) resultado *= numero
            return resultado
        }
    }
}
