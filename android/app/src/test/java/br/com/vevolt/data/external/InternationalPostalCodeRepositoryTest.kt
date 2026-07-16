package br.com.vevolt.data.external

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InternationalPostalCodeRepositoryTest {
    @Test
    fun normalizesInternationalPostalCodeRequestWithoutForcingBrazilianDigits() {
        val result = normalizePostalCodeSearchRequest(
            PostalCodeSearchRequest("ca", " k1a 0b1 ", "en-CA")
        )

        assertEquals(
            PostalCodeSearchRequest("CA", "K1A 0B1", "en-CA"),
            result
        )
    }

    @Test
    fun rejectsInvalidCountryOrUnsafeLength() {
        assertEquals(
            null,
            normalizePostalCodeSearchRequest(PostalCodeSearchRequest("USA", "10001", "en-US"))
        )
        assertEquals(
            null,
            normalizePostalCodeSearchRequest(PostalCodeSearchRequest("US", "1", "en-US"))
        )
    }

    @Test
    fun buildsCoordinatesAndReadableAddress() {
        val result = buildPostalCodeLookupResult(
            countryCode = "US",
            postalCode = "10001",
            latitude = 40.7506,
            longitude = -73.9972,
            label = "10001, Manhattan, New York, United States"
        )

        assertTrue(result is PostalCodeLookupResult.Success)
        result as PostalCodeLookupResult.Success
        assertEquals(40.7506, result.latitude, 0.000001)
        assertEquals(-73.9972, result.longitude, 0.000001)
        assertEquals("US", result.countryCode)
    }

    @Test
    fun reportsMissingCoordinatesWithoutInventingLocation() {
        val result = buildPostalCodeLookupResult(
            countryCode = "GB",
            postalCode = "SW1A 1AA",
            latitude = null,
            longitude = null,
            label = "London"
        )

        assertEquals(
            PostalCodeLookupResult.Failure(PostalCodeLookupError.COORDINATES_UNAVAILABLE),
            result
        )
    }
}
