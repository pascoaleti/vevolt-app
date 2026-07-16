package br.com.vevolt

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.vevolt.data.VehiclePreferencesRepository
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.Vehicle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleRepositoryTest {
    @Test
    fun savesAndRestoresConfiguredVehicle() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = VehiclePreferencesRepository(context)
        val expected = Vehicle(
            brand = "BYD",
            model = "Dolphin",
            year = "2024",
            rangeKm = 218,
            batteryKwh = 44.9,
            connector = ConnectorType.CCS2,
            currentBatteryPercent = 62
        )

        repository.clearAll()
        repository.markOnboardingSeen()
        val saved = repository.saveVehicle(expected)

        assertTrue(repository.onboardingSeen.first())
        assertTrue(repository.vehicleConfigured.first())
        assertTrue(saved.id.isNotBlank())
        assertEquals(saved, repository.vehicle.first())
    }
}
