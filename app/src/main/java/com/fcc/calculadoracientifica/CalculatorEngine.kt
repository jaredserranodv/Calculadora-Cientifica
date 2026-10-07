package com.fcc.calculadoracientifica

import kotlin.math.*

/** Evalúa la misma expresión en portrait y landscape, sin depender de vistas Android. */
object CalculatorEngine {
    fun evaluate(expression: String): Double {
        val parser = Parser(expression)
        val result = parser.parse()
        require(result.isFinite())
        return result
    }

    private class Parser(private val input: String) {
        private var index = 0

        fun parse(): Double {
            require(input.isNotBlank())
            val value = expression()
            require(index == input.length)
            return value
        }

        private fun expression(): Double {
            var value = term()
            while (true) {
                value = when {
                    take('+') -> value + term()
                    take('−') || take('-') -> value - term()
                    else -> return value
                }
            }
        }

        private fun term(): Double {
            var value = unary()
            while (true) {
                value = when {
                    take('×') -> value * unary()
                    take('÷') -> {
                        val divisor = unary()
                        require(divisor != 0.0)
                        value / divisor
                    }
                    else -> return value
                }
            }
        }

        private fun unary(): Double = when {
            take('+') -> unary()
            take('−') || take('-') -> -unary()
            else -> power()
        }

        private fun power(): Double {
            val base = postfix()
            return if (take('^')) base.pow(unary()) else base
        }

        private fun postfix(): Double {
            var value = primary()
            while (true) {
                value = when {
                    take('%') -> value / 100.0
                    take('!') -> factorial(value)
                    else -> return value
                }
            }
        }

        private fun primary(): Double {
            if (take('(')) {
                val value = expression()
                require(take(')'))
                return value
            }
            if (take('π')) return PI
            if (take('e')) return E
            val start = index
            while (index < input.length && input[index].isLetter()) index++
            if (index > start) {
                val function = input.substring(start, index)
                require(take('('))
                val argument = expression()
                require(take(')'))
                return when (function) {
                    "sin" -> sin(Math.toRadians(argument))
                    "cos" -> cos(Math.toRadians(argument))
                    "tan" -> tan(Math.toRadians(argument))
                    "asin" -> Math.toDegrees(asin(argument))
                    "acos" -> Math.toDegrees(acos(argument))
                    "atan" -> Math.toDegrees(atan(argument))
                    "sqrt" -> { require(argument >= 0); sqrt(argument) }
                    "log" -> { require(argument > 0); log10(argument) }
                    "ln" -> { require(argument > 0); ln(argument) }
                    else -> throw IllegalArgumentException("Unknown function")
                }
            }
            while (index < input.length && (input[index].isDigit() || input[index] == '.')) index++
            require(index > start)
            return input.substring(start, index).toDoubleOrNull()
                ?: throw IllegalArgumentException("Invalid number")
        }

        private fun take(char: Char): Boolean {
            if (index < input.length && input[index] == char) {
                index++
                return true
            }
            return false
        }

        private fun factorial(value: Double): Double {
            require(value >= 0 && value <= 170 && value == floor(value))
            var result = 1.0
            for (number in 2..value.toInt()) result *= number
            return result
        }
    }
}
