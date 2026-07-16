package br.com.vevolt

import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.vevolt.data.EconomyPreferences
import br.com.vevolt.data.PremiumBackup
import br.com.vevolt.data.decodePremiumBackup
import br.com.vevolt.data.encodePremiumBackup
import br.com.vevolt.model.ChargerReservation
import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.PaymentStatus
import br.com.vevolt.model.ReservationStatus
import br.com.vevolt.model.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PremiumBackupCodecTest {
    @Test
    fun roundTripsGarageAndLocalHistory() {
        val vehicle = Vehicle(
            id = "primary",
            brand = "BYD",
            model = "Dolphin",
            year = "2024",
            rangeKm = 218,
            batteryKwh = 44.9
        )
        val backup = PremiumBackup(
            vehicles = listOf(vehicle),
            activeVehicleId = vehicle.id,
            reservations = listOf(
                ChargerReservation(
                    id = "saved-1",
                    chargerId = 10,
                    chargerName = "Station",
                    connector = ConnectorType.CCS2,
                    createdAtMillis = 100L,
                    status = ReservationStatus.ACTIVE
                )
            ),
            chargingSessions = listOf(
                ChargingSession(
                    id = "session-1",
                    chargerId = 10,
                    chargerName = "Station",
                    connector = ConnectorType.CCS2,
                    startedAtMillis = 100L,
                    endedAtMillis = 200L,
                    energyKwh = 20.0,
                    amount = 35.0,
                    status = ChargingSessionStatus.FINISHED,
                    paymentStatus = PaymentStatus.RECORDED_LOCAL,
                    vehicleId = vehicle.id
                )
            ),
            economyPreferences = EconomyPreferences(6.2, 12.0)
        )

        val restored = decodePremiumBackup(encodePremiumBackup(backup))

        assertEquals(backup.copy(), restored)
    }
}
