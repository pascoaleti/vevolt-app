package br.com.vevolt.domain

import br.com.vevolt.model.Charger
import br.com.vevolt.model.ChargerStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RouteEstimatorTest {
    private val vehicle = Vehicle(rangeKm = 200, currentBatteryPercent = 50)

    @Test
    fun estimatesRemainingRangeWithoutClaimingRouteDistance() {
        val estimate = estimateRoute(vehicle, charger(distanceKm = 80.0))

        assertEquals(80.0, estimate.distanceKm, 0.0)
        assertEquals(120.0, estimate.arrivalRangeKm, 0.0)
        assertEquals(30, estimate.arrivalBatteryPercent)
        assertTrue(estimate.hasEstimatedRange)
    }

    @Test
    fun marksEstimateInsufficientWhenSafetyReserveIsMissing() {
        val estimate = estimateRoute(vehicle, charger(distanceKm = 180.0))

        assertEquals(20.0, estimate.arrivalRangeKm, 0.0)
        assertEquals(5, estimate.arrivalBatteryPercent)
        assertFalse(estimate.hasEstimatedRange)
    }

    @Test
    fun clampsInvalidNegativeDistanceToZero() {
        val estimate = estimateRoute(vehicle, charger(distanceKm = -5.0))

        assertEquals(0.0, estimate.distanceKm, 0.0)
        assertEquals(200.0, estimate.arrivalRangeKm, 0.0)
    }

    private fun charger(distanceKm: Double) = Charger(
        id = 1,
        name = "Ponto teste",
        distanceKm = distanceKm,
        address = "Rua teste",
        city = "Sao Paulo, SP",
        status = ChargerStatus.OPERATIONAL,
        powerKw = 50,
        connector = ConnectorType.CCS2,
        pricePerKwh = null,
        rating = null,
        reviews = 0,
        parkingInfo = "Nao informado",
        safetyNote = "Operador teste",
        comments = emptyList()
    )
}
