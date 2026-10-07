package com.fcc.calculadoracientifica

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class CalculatorEngineTest {
    @Test fun evaluatesBasicAndScientificExpressions() {
        assertEquals(14.0, CalculatorEngine.evaluate("2+3×4"), 1e-10)
        assertEquals(9.0, CalculatorEngine.evaluate("(1+2)^2"), 1e-10)
        assertEquals(1.0, CalculatorEngine.evaluate("sin(90)"), 1e-10)
        assertEquals(30.0, CalculatorEngine.evaluate("asin(0.5)"), 1e-10)
        assertEquals(120.0, CalculatorEngine.evaluate("5!"), 1e-10)
        assertEquals(0.25, CalculatorEngine.evaluate("25%"), 1e-10)
    }

    @Test fun rejectsInvalidAndOutOfDomainInput() {
        for (input in listOf("", "2+", "1÷0", "sqrt(-1)", "log(0)", "ln(-2)", "asin(2)", "2.3!")) {
            try {
                CalculatorEngine.evaluate(input)
                fail("Expected an error for $input")
            } catch (_: IllegalArgumentException) {
                // La Activity presenta estas validaciones como "Error".
            }
        }
    }
}
