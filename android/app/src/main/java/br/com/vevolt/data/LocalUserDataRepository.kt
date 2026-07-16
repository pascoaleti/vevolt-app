package br.com.vevolt.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import br.com.vevolt.model.Charger
import br.com.vevolt.model.ChargerReservation
import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.PaymentStatus
import br.com.vevolt.model.ReservationStatus
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore by preferencesDataStore(name = "local_user_data")

class LocalUserDataRepository(context: Context) {
    private val dataStore = context.applicationContext.userDataStore

    val reservations: Flow<List<ChargerReservation>> = dataStore.data.map { preferences ->
        preferences[Keys.RESERVATIONS].orEmpty().decodeReservations()
    }

    val chargingSessions: Flow<List<ChargingSession>> = dataStore.data.map { preferences ->
        preferences[Keys.SESSIONS].orEmpty().decodeSessions()
    }

    suspend fun reserve(charger: Charger) {
        dataStore.edit { preferences ->
            val reservations = preferences[Keys.RESERVATIONS].orEmpty().decodeReservations()
            val updated = reservations
                .filterNot { it.chargerId == charger.id && it.status == ReservationStatus.ACTIVE }
                .plus(
                    ChargerReservation(
                        id = UUID.randomUUID().toString(),
                        chargerId = charger.id,
                        chargerName = charger.name,
                        connector = charger.connector,
                        createdAtMillis = System.currentTimeMillis(),
                        status = ReservationStatus.ACTIVE
                    )
                )
            preferences[Keys.RESERVATIONS] = updated.encodeReservations()
        }
    }

    suspend fun removeSavedCharger(chargerId: Int) {
        dataStore.edit { preferences ->
            val updated = preferences[Keys.RESERVATIONS]
                .orEmpty()
                .decodeReservations()
                .filterNot { it.chargerId == chargerId }
            preferences[Keys.RESERVATIONS] = updated.encodeReservations()
        }
    }

    suspend fun startCharging(charger: Charger, vehicleId: String) {
        dataStore.edit { preferences ->
            val sessions = preferences[Keys.SESSIONS].orEmpty().decodeSessions()
            val updated = sessions
                .filterNot { it.status == ChargingSessionStatus.ACTIVE }
                .plus(
                    ChargingSession(
                        id = UUID.randomUUID().toString(),
                        chargerId = charger.id,
                        chargerName = charger.name,
                        connector = charger.connector,
                        startedAtMillis = System.currentTimeMillis(),
                        endedAtMillis = null,
                        energyKwh = 0.0,
                        amount = 0.0,
                        status = ChargingSessionStatus.ACTIVE,
                        paymentStatus = PaymentStatus.PENDING,
                        vehicleId = vehicleId
                    )
                )
            preferences[Keys.SESSIONS] = updated.encodeSessions()
        }
    }

    suspend fun finishActiveCharging(energyKwh: Double, amount: Double) {
        dataStore.edit { preferences ->
            val now = System.currentTimeMillis()
            val updated = preferences[Keys.SESSIONS].orEmpty().decodeSessions().map { session ->
                if (session.status != ChargingSessionStatus.ACTIVE) {
                    session
                } else {
                    session.copy(
                        endedAtMillis = now,
                        energyKwh = energyKwh.coerceAtLeast(0.0),
                        amount = amount.coerceAtLeast(0.0),
                        status = ChargingSessionStatus.FINISHED,
                        paymentStatus = PaymentStatus.RECORDED_LOCAL
                    )
                }
            }
            preferences[Keys.SESSIONS] = updated.encodeSessions()
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    suspend fun replaceAll(
        reservations: List<ChargerReservation>,
        chargingSessions: List<ChargingSession>
    ) {
        dataStore.edit { preferences ->
            preferences[Keys.RESERVATIONS] = reservations.encodeReservations()
            preferences[Keys.SESSIONS] = chargingSessions.encodeSessions()
        }
    }

    suspend fun removeVehicleData(vehicleId: String) {
        dataStore.edit { preferences ->
            val sessions = preferences[Keys.SESSIONS]
                .orEmpty()
                .decodeSessions()
                .filterNot { it.vehicleId == vehicleId }
            preferences[Keys.SESSIONS] = sessions.encodeSessions()
        }
    }

    private object Keys {
        val RESERVATIONS = stringPreferencesKey("reservations")
        val SESSIONS = stringPreferencesKey("charging_sessions")
    }
}

private const val RECORD_SEPARATOR = "\u001E"
private const val FIELD_SEPARATOR = "\u001F"

private fun List<ChargerReservation>.encodeReservations(): String =
    joinToString(RECORD_SEPARATOR) {
        listOf(
            it.id,
            it.chargerId,
            it.chargerName,
            it.connector.name,
            it.createdAtMillis,
            it.status.name
        ).joinToString(FIELD_SEPARATOR)
    }

private fun String.decodeReservations(): List<ChargerReservation> =
    split(RECORD_SEPARATOR)
        .filter { it.isNotBlank() }
        .mapNotNull { record ->
            val fields = record.split(FIELD_SEPARATOR)
            runCatching {
                ChargerReservation(
                    id = fields[0],
                    chargerId = fields[1].toInt(),
                    chargerName = fields[2],
                    connector = ConnectorType.valueOf(fields[3]),
                    createdAtMillis = fields[4].toLong(),
                    status = ReservationStatus.valueOf(fields[5])
                )
            }.getOrNull()
        }

private fun List<ChargingSession>.encodeSessions(): String =
    joinToString(RECORD_SEPARATOR) {
        listOf(
            it.id,
            it.chargerId,
            it.chargerName,
            it.connector.name,
            it.startedAtMillis,
            it.endedAtMillis ?: "",
            it.energyKwh,
            it.amount,
            it.status.name,
            it.paymentStatus.name,
            it.vehicleId
        ).joinToString(FIELD_SEPARATOR)
    }

private fun String.decodeSessions(): List<ChargingSession> =
    split(RECORD_SEPARATOR)
        .filter { it.isNotBlank() }
        .mapNotNull { record ->
            val fields = record.split(FIELD_SEPARATOR)
            runCatching {
                ChargingSession(
                    id = fields[0],
                    chargerId = fields[1].toInt(),
                    chargerName = fields[2],
                    connector = ConnectorType.valueOf(fields[3]),
                    startedAtMillis = fields[4].toLong(),
                    endedAtMillis = fields[5].takeIf { it.isNotBlank() }?.toLong(),
                    energyKwh = fields[6].toDouble(),
                    amount = fields[7].toDouble(),
                    status = ChargingSessionStatus.valueOf(fields[8]),
                    paymentStatus = PaymentStatus.valueOf(fields[9]),
                    vehicleId = fields.getOrNull(10).orEmpty()
                )
            }.getOrNull()
        }
