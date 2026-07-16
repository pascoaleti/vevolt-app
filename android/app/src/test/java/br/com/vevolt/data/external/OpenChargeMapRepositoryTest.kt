package br.com.vevolt.data.external

import br.com.vevolt.model.ChargerStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenChargeMapRepositoryTest {
    @Test
    fun filtersProviderTestAndDemoLocations() {
        assertTrue("Teste OCM".isNonProductionChargerName())
        assertTrue("Demo charging point".isNonProductionChargerName())
        assertTrue("Estacao de homologacao".isNonProductionChargerName())
        assertTrue("Sample EVSE".isNonProductionChargerName())
    }

    @Test
    fun keepsLegitimateLocationNames() {
        assertFalse("Shopping Cidade Sao Paulo".isNonProductionChargerName())
        assertFalse("Volvo - Faberge Mogi das Cruzes".isNonProductionChargerName())
        assertFalse("Teston Road Charging Hub".isNonProductionChargerName())
    }

    @Test
    fun mapsCompactOpenChargeMapStatuses() {
        assertEquals(
            ChargerStatus.OPERATIONAL,
            mapOpenChargeMapStatus(isOperational = null, statusTypeId = 50)
        )
        assertEquals(
            ChargerStatus.UNKNOWN,
            mapOpenChargeMapStatus(isOperational = null, statusTypeId = 100)
        )
        assertEquals(
            ChargerStatus.UNKNOWN,
            mapOpenChargeMapStatus(isOperational = null, statusTypeId = 0)
        )
    }

    @Test
    fun unverifiedOperatorStatusDoesNotClaimTheChargerIsOffline() {
        assertEquals(
            ChargerStatus.UNKNOWN,
            mapOpenChargeMapStatus(isOperational = false, statusTypeId = 50)
        )
    }

    @Test
    fun removesMissingExternalTextValues() {
        assertNull(normalizeExternalText(null))
        assertNull(normalizeExternalText("  "))
        assertNull(normalizeExternalText("null"))
        assertEquals("R$ 1,97/kWh", normalizeExternalText("  R$ 1,97/kWh  "))
    }
}
