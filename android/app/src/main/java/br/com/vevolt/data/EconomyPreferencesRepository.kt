package br.com.vevolt.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.economyDataStore by preferencesDataStore(name = "economy_preferences")

data class EconomyPreferences(
    val fuelPricePerLiter: Double = 0.0,
    val gasolineKmPerLiter: Double = DEFAULT_GASOLINE_KM_PER_LITER
)

class EconomyPreferencesRepository(context: Context) {
    private val dataStore = context.applicationContext.economyDataStore

    val preferences: Flow<EconomyPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            EconomyPreferences(
                fuelPricePerLiter = preferences[Keys.FUEL_PRICE_PER_LITER] ?: 0.0,
                gasolineKmPerLiter = preferences[Keys.GASOLINE_KM_PER_LITER]
                    ?: DEFAULT_GASOLINE_KM_PER_LITER
            )
        }

    suspend fun save(preferences: EconomyPreferences) {
        dataStore.edit { stored ->
            stored[Keys.FUEL_PRICE_PER_LITER] = preferences.fuelPricePerLiter.coerceAtLeast(0.0)
            stored[Keys.GASOLINE_KM_PER_LITER] = preferences.gasolineKmPerLiter.coerceAtLeast(1.0)
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    private object Keys {
        val FUEL_PRICE_PER_LITER = doublePreferencesKey("fuel_price_per_liter")
        val GASOLINE_KM_PER_LITER = doublePreferencesKey("gasoline_km_per_liter")
    }
}

const val DEFAULT_GASOLINE_KM_PER_LITER = 10.0
