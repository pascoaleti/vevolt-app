package br.com.vevolt.domain

import br.com.vevolt.model.Charger
import br.com.vevolt.model.ChargerStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChargerRecommendationTest {
    @Test
    fun prioritizesCompatibleAvailableCharger() {
        val incompatibleNearby = charger(1, ConnectorType.TYPE_2, ChargerStatus.AVAILABLE, 0.2)
        val compatible = charger(2, ConnectorType.CCS2, ChargerStatus.OPERATIONAL, 3.0)

        val ranked = rankChargersForVehicle(
            listOf(incompatibleNearby, compatible),
            Vehicle(connector = ConnectorType.CCS2)
        )

        assertEquals(2, ranked.first().charger.id)
        assertTrue(ranked.first().connectorCompatible)
    }

    @Test
    fun excludesOfflineChargers() {
        val ranked = rankChargersForVehicle(
            listOf(charger(1, ConnectorType.CCS2, ChargerStatus.OFFLINE, 0.1)),
            Vehicle(connector = ConnectorType.CCS2)
        )

        assertTrue(ranked.isEmpty())
    }

    private fun charger(
        id: Int,
        connector: ConnectorType,
        status: ChargerStatus,
        distanceKm: Double
    ) = Charger(
        id = id,
        name = "Station $id",
        distanceKm = distanceKm,
        address = "Address",
        city = "City",
        status = status,
        powerKw = 50,
        connector = connector,
        pricePerKwh = null,
        rating = null,
        reviews = 0,
        parkingInfo = "",
        safetyNote = "",
        comments = emptyList()
    )
}
