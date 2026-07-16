package br.com.vevolt.data

import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.PaymentStatus
import br.com.vevolt.model.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EconomyAnalyticsTest {
    @Test
    fun filtersFinishedSessionsBySelectedPeriod() {
        val now = 2_000_000_000_000L
        val recent = session("recent", now - 10L * DAY_MILLIS)
        val old = session("old", now - 120L * DAY_MILLIS)
        val active = session("active", now, ChargingSessionStatus.ACTIVE)

        assertEquals(listOf(recent), filterFinishedSessions(listOf(recent, old, active), EconomyPeriod.DAYS_30, now))
        assertEquals(listOf(recent, old), filterFinishedSessions(listOf(recent, old, active), EconomyPeriod.ALL, now))
    }

    @Test
    fun usesConfiguredVehicleEfficiencyAndFuelComparison() {
        val summary = buildEconomySummary(
            sessions = listOf(session("one", 1L, energyKwh = 10.0, amount = 20.0)),
            vehicle = Vehicle(rangeKm = 300, batteryKwh = 50.0),
            preferences = EconomyPreferences(fuelPricePerLiter = 6.0, gasolineKmPerLiter = 12.0)
        )

        assertEquals(60, summary.estimatedKm)
        assertEquals(30.0, summary.gasolineCost, 0.001)
        assertEquals(10.0, summary.savings, 0.001)
        assertTrue(summary.vehicleEfficiencyUsed)
        assertTrue(summary.comparisonAvailable)
    }

    @Test
    fun comparisonRequiresUserFuelPrice() {
        val summary = buildEconomySummary(
            sessions = listOf(session("one", 1L, energyKwh = 10.0, amount = 20.0)),
            vehicle = Vehicle(rangeKm = 300, batteryKwh = 50.0),
            preferences = EconomyPreferences()
        )

        assertFalse(summary.comparisonAvailable)
        assertEquals(0.0, summary.gasolineCost, 0.001)
    }

    @Test
    fun groupsStationMetrics() {
        val summaries = buildStationEconomySummaries(
            listOf(
                session("a", 1L, charger = "Estacao A", energyKwh = 10.0, amount = 20.0),
                session("b", 2L, charger = "Estacao A", energyKwh = 5.0, amount = 8.0),
                session("c", 3L, charger = "Estacao B", energyKwh = 7.0, amount = 14.0)
            )
        )

        assertEquals("Estacao A", summaries.first().name)
        assertEquals(2, summaries.first().sessions)
        assertEquals(15.0, summaries.first().kwh, 0.001)
        assertEquals(28.0, summaries.first().cost, 0.001)
    }

    @Test
    fun buildsChargingTimeAndPowerInsights() {
        val sessions = listOf(
            session("a", 3_600_000L, charger = "A", energyKwh = 10.0, amount = 20.0),
            session("b", 7_200_000L, charger = "A", energyKwh = 20.0, amount = 0.0)
        ).map { it.copy(startedAtMillis = 0L) }

        val insights = buildChargingInsights(sessions)

        assertEquals(2, insights.measuredSessions)
        assertEquals(180L, insights.totalDurationMinutes)
        assertEquals(90L, insights.averageDurationMinutes)
        assertEquals(10.0, insights.averageRecordedPowerKw, 0.001)
        assertEquals(1, insights.freeSessions)
        assertEquals("A", insights.mostUsedStation)
    }

    private fun session(
        id: String,
        endedAt: Long,
        status: ChargingSessionStatus = ChargingSessionStatus.FINISHED,
        charger: String = "Estacao",
        energyKwh: Double = 1.0,
        amount: Double = 2.0
    ) = ChargingSession(
        id = id,
        chargerId = 1,
        chargerName = charger,
        connector = ConnectorType.CCS2,
        startedAtMillis = endedAt - 1_000L,
        endedAtMillis = if (status == ChargingSessionStatus.FINISHED) endedAt else null,
        energyKwh = energyKwh,
        amount = amount,
        status = status,
        paymentStatus = PaymentStatus.RECORDED_LOCAL
    )

    private companion object {
        const val DAY_MILLIS = 24L * 60L * 60L * 1_000L
    }
}
