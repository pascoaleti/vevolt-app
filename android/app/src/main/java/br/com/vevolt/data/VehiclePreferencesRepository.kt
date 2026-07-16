package br.com.vevolt.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.Vehicle
import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.vehicleDataStore by preferencesDataStore(name = "vehicle_preferences")

class VehiclePreferencesRepository(context: Context) {
    private val dataStore = context.applicationContext.vehicleDataStore

    val garage: Flow<VehicleGarage> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map(::readGarage)

    val onboardingSeen: Flow<Boolean> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> preferences[Keys.ONBOARDING_SEEN] ?: false }

    val vehicleConfigured: Flow<Boolean> = garage.map { it.vehicles.isNotEmpty() }

    val vehicle: Flow<Vehicle> = garage.map { it.activeVehicle }

    suspend fun saveVehicle(vehicle: Vehicle): Vehicle {
        var savedVehicle = vehicle
        dataStore.edit { preferences ->
            val current = readGarage(preferences)
            savedVehicle = vehicle.copy(id = vehicle.id.ifBlank { UUID.randomUUID().toString() })
            val vehicles = if (current.vehicles.any { it.id == savedVehicle.id }) {
                current.vehicles.map { existing ->
                    if (existing.id == savedVehicle.id) savedVehicle else existing
                }
            } else {
                current.vehicles.plus(savedVehicle).take(MAX_GARAGE_VEHICLES)
            }
            writeGarage(preferences, vehicles, savedVehicle.id)
        }
        return savedVehicle
    }

    suspend fun selectVehicle(vehicleId: String) {
        dataStore.edit { preferences ->
            val current = readGarage(preferences)
            val active = current.vehicles.firstOrNull { it.id == vehicleId } ?: return@edit
            writeGarage(preferences, current.vehicles, active.id)
        }
    }

    suspend fun removeVehicle(vehicleId: String) {
        dataStore.edit { preferences ->
            val current = readGarage(preferences)
            if (current.vehicles.size <= 1) return@edit
            val vehicles = current.vehicles.filterNot { it.id == vehicleId }
            val activeId = current.activeVehicleId
                .takeIf { id -> vehicles.any { it.id == id } }
                ?: vehicles.first().id
            writeGarage(preferences, vehicles, activeId)
        }
    }

    suspend fun replaceGarage(vehicles: List<Vehicle>, activeVehicleId: String) {
        val normalized = vehicles
            .take(MAX_GARAGE_VEHICLES)
            .map { vehicle -> vehicle.copy(id = vehicle.id.ifBlank { UUID.randomUUID().toString() }) }
        require(normalized.isNotEmpty())
        val activeId = activeVehicleId.takeIf { id -> normalized.any { it.id == id } }
            ?: normalized.first().id
        dataStore.edit { preferences -> writeGarage(preferences, normalized, activeId) }
    }

    suspend fun markOnboardingSeen() {
        dataStore.edit { preferences ->
            preferences[Keys.ONBOARDING_SEEN] = true
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    private fun String.toConnectorType(): ConnectorType =
        runCatching { ConnectorType.valueOf(this) }.getOrDefault(Vehicle().connector)

    private fun readGarage(preferences: androidx.datastore.preferences.core.Preferences): VehicleGarage {
        val stored = preferences[Keys.GARAGE_JSON]
            ?.let(::decodeVehicles)
            .orEmpty()
        val vehicles = stored.ifEmpty {
            if (preferences[Keys.VEHICLE_CONFIGURED] == true) {
                listOf(readLegacyVehicle(preferences))
            } else {
                emptyList()
            }
        }
        val activeId = preferences[Keys.ACTIVE_VEHICLE_ID]
            .takeIf { id -> vehicles.any { it.id == id } }
            ?: vehicles.firstOrNull()?.id.orEmpty()
        return VehicleGarage(vehicles = vehicles, activeVehicleId = activeId)
    }

    private fun readLegacyVehicle(preferences: androidx.datastore.preferences.core.Preferences): Vehicle {
        val defaultVehicle = Vehicle()
        return Vehicle(
            id = LEGACY_VEHICLE_ID,
            type = preferences[Keys.TYPE] ?: defaultVehicle.type,
            brand = preferences[Keys.BRAND] ?: defaultVehicle.brand,
            model = preferences[Keys.MODEL] ?: defaultVehicle.model,
            year = preferences[Keys.YEAR] ?: defaultVehicle.year,
            rangeKm = preferences[Keys.RANGE_KM] ?: defaultVehicle.rangeKm,
            batteryKwh = preferences[Keys.BATTERY_KWH] ?: defaultVehicle.batteryKwh,
            connector = preferences[Keys.CONNECTOR]?.toConnectorType() ?: defaultVehicle.connector,
            currentBatteryPercent = preferences[Keys.CURRENT_BATTERY_PERCENT]
                ?: defaultVehicle.currentBatteryPercent
        )
    }

    private fun writeGarage(
        preferences: androidx.datastore.preferences.core.MutablePreferences,
        vehicles: List<Vehicle>,
        activeVehicleId: String
    ) {
        val active = vehicles.first { it.id == activeVehicleId }
        preferences[Keys.GARAGE_JSON] = encodeVehicles(vehicles)
        preferences[Keys.ACTIVE_VEHICLE_ID] = activeVehicleId
        preferences[Keys.VEHICLE_CONFIGURED] = true
        preferences[Keys.TYPE] = active.type
        preferences[Keys.BRAND] = active.brand
        preferences[Keys.MODEL] = active.model
        preferences[Keys.YEAR] = active.year
        preferences[Keys.RANGE_KM] = active.rangeKm
        preferences[Keys.BATTERY_KWH] = active.batteryKwh
        preferences[Keys.CONNECTOR] = active.connector.name
        preferences[Keys.CURRENT_BATTERY_PERCENT] = active.currentBatteryPercent
    }

    private fun encodeVehicles(vehicles: List<Vehicle>): String {
        val array = JSONArray()
        vehicles.forEach { vehicle ->
            array.put(
                JSONObject()
                    .put("id", vehicle.id)
                    .put("type", vehicle.type)
                    .put("brand", vehicle.brand)
                    .put("model", vehicle.model)
                    .put("year", vehicle.year)
                    .put("rangeKm", vehicle.rangeKm)
                    .put("batteryKwh", vehicle.batteryKwh)
                    .put("connector", vehicle.connector.name)
                    .put("batteryPercent", vehicle.currentBatteryPercent)
            )
        }
        return array.toString()
    }

    private fun decodeVehicles(raw: String): List<Vehicle> = runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until minOf(array.length(), MAX_GARAGE_VEHICLES)) {
                val item = array.getJSONObject(index)
                val id = item.optString("id").takeIf { it.isNotBlank() } ?: continue
                add(
                    Vehicle(
                        id = id,
                        type = item.optString("type", Vehicle().type),
                        brand = item.optString("brand"),
                        model = item.optString("model"),
                        year = item.optString("year"),
                        rangeKm = item.optInt("rangeKm").coerceAtLeast(0),
                        batteryKwh = item.optDouble("batteryKwh").coerceAtLeast(0.0),
                        connector = item.optString("connector").toConnectorType(),
                        currentBatteryPercent = item.optInt("batteryPercent", 50).coerceIn(0, 100)
                    )
                )
            }
        }
    }.getOrDefault(emptyList())

    private object Keys {
        val TYPE = stringPreferencesKey("vehicle_type")
        val BRAND = stringPreferencesKey("vehicle_brand")
        val MODEL = stringPreferencesKey("vehicle_model")
        val YEAR = stringPreferencesKey("vehicle_year")
        val RANGE_KM = intPreferencesKey("vehicle_range_km")
        val BATTERY_KWH = doublePreferencesKey("vehicle_battery_kwh")
        val CONNECTOR = stringPreferencesKey("vehicle_connector")
        val CURRENT_BATTERY_PERCENT = intPreferencesKey("vehicle_current_battery_percent")
        val ONBOARDING_SEEN = booleanPreferencesKey("onboarding_seen")
        val VEHICLE_CONFIGURED = booleanPreferencesKey("vehicle_configured")
        val GARAGE_JSON = stringPreferencesKey("vehicle_garage_json")
        val ACTIVE_VEHICLE_ID = stringPreferencesKey("active_vehicle_id")
    }
}

data class VehicleGarage(
    val vehicles: List<Vehicle> = emptyList(),
    val activeVehicleId: String = ""
) {
    val activeVehicle: Vehicle
        get() = vehicles.firstOrNull { it.id == activeVehicleId } ?: vehicles.firstOrNull() ?: Vehicle()
}

const val MAX_GARAGE_VEHICLES = 5
private const val LEGACY_VEHICLE_ID = "primary"
