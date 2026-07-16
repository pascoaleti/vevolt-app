package br.com.vevolt.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InternationalPostalCodeFormattingTest {
    @Test
    fun formatsKnownPostalCodeTypesWithoutReorderingCharacters() {
        assertEquals("01310-930", typeIncrementally("BR", "01310930"))
        assertEquals("90210-1234", typeIncrementally("US", "902101234"))
        assertEquals("K1A 0B1", typeIncrementally("CA", "K1A0B1"))
        assertEquals("SW1A 1AA", typeIncrementally("GB", "SW1A1AA"))
        assertEquals("1000-001", typeIncrementally("PT", "1000001"))
        assertEquals("111 22", typeIncrementally("SE", "11122"))
        assertEquals("D02 X285", typeIncrementally("IE", "D02X285"))
        assertEquals("110001", typeIncrementally("IN", "110001"))
    }

    @Test
    fun choosesKeyboardAndExampleForTheSelectedCountry() {
        assertTrue(postalCodeUsesNumericKeyboard("US"))
        assertTrue(postalCodeUsesNumericKeyboard("ES"))
        assertFalse(postalCodeUsesNumericKeyboard("CA"))
        assertFalse(postalCodeUsesNumericKeyboard("IE"))
        assertEquals("A1A 1A1", postalCodeExample("CA"))
        assertEquals("SW1A 1AA", postalCodeExample("GB"))
        assertEquals("12345 / A1A 1A1", postalCodeExample("ZZ"))
    }

    private fun typeIncrementally(countryCode: String, raw: String): String {
        var value = ""
        raw.forEach { character ->
            value = formatInternationalPostalCodeInput(countryCode, value + character)
        }
        return value
    }
}
