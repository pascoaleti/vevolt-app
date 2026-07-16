package br.com.vevolt.billing

import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.PaymentStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumHistoryExporterTest {
    @Test
    fun exportsOnlyFinishedSessionsAndEscapesSpreadsheetContent() {
        val csv = buildChargingHistoryCsv(
            listOf(
                session("finished", "=malicious, charger", ChargingSessionStatus.FINISHED),
                session("active", "Active charger", ChargingSessionStatus.ACTIVE)
            )
        )
        assertTrue(csv.contains("\"'=malicious, charger\""))
        assertTrue(csv.contains("12.345,20.50"))
        assertFalse(csv.contains("Active charger"))
    }

    private fun session(id: String, name: String, status: ChargingSessionStatus) = ChargingSession(
        id = id,
        chargerId = 1,
        chargerName = name,
        connector = ConnectorType.CCS2,
        startedAtMillis = 1_700_000_000_000,
        endedAtMillis = if (status == ChargingSessionStatus.FINISHED) 1_700_000_100_000 else null,
        energyKwh = 12.345,
        amount = 20.5,
        status = status,
        paymentStatus = if (status == ChargingSessionStatus.FINISHED) PaymentStatus.RECORDED_LOCAL else PaymentStatus.PENDING
    )
}
