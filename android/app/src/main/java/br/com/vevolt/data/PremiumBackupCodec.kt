package br.com.vevolt.data

import br.com.vevolt.model.ChargerReservation
import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.PaymentStatus
import br.com.vevolt.model.ReservationStatus
import br.com.vevolt.model.Vehicle
import org.json.JSONArray
import org.json.JSONObject

data class PremiumBackup(
    val vehicles: List<Vehicle>,
    val activeVehicleId: String,
    val reservations: List<ChargerReservation>,
    val chargingSessions: List<ChargingSession>,
    val economyPreferences: EconomyPreferences
)

fun encodePremiumBackup(backup: PremiumBackup): String = JSONObject()
    .put("format", BACKUP_FORMAT)
    .put("version", BACKUP_VERSION)
    .put("exportedAt", System.currentTimeMillis())
    .put("activeVehicleId", backup.activeVehicleId)
    .put("vehicles", JSONArray().also { array -> backup.vehicles.forEach { array.put(it.toJson()) } })
    .put("reservations", JSONArray().also { array -> backup.reservations.forEach { array.put(it.toJson()) } })
    .put("sessions", JSONArray().also { array -> backup.chargingSessions.forEach { array.put(it.toJson()) } })
    .put(
        "economy",
        JSONObject()
            .put("fuelPricePerLiter", backup.economyPreferences.fuelPricePerLiter)
            .put("gasolineKmPerLiter", backup.economyPreferences.gasolineKmPerLiter)
    )
    .toString(2)

fun decodePremiumBackup(raw: String): PremiumBackup {
    require(raw.length <= MAX_BACKUP_CHARS) { "Backup too large" }
    val root = JSONObject(raw)
    require(root.optString("format") == BACKUP_FORMAT) { "Invalid backup format" }
    require(root.optInt("version") == BACKUP_VERSION) { "Unsupported backup version" }

    val vehicles = root.getJSONArray("vehicles").mapObjects(MAX_GARAGE_VEHICLES, ::vehicleFromJson)
        .distinctBy { it.id }
    require(vehicles.isNotEmpty()) { "Backup has no vehicles" }
    val vehicleIds = vehicles.mapTo(mutableSetOf()) { it.id }
    val activeVehicleId = root.optString("activeVehicleId").takeIf(vehicleIds::contains)
        ?: vehicles.first().id
    val reservations = root.optJSONArray("reservations")
        ?.mapObjects(MAX_BACKUP_RESERVATIONS, ::reservationFromJson)
        .orEmpty()
        .distinctBy { it.id }
    val sessions = root.optJSONArray("sessions")
        ?.mapObjects(MAX_BACKUP_SESSIONS, ::sessionFromJson)
        .orEmpty()
        .distinctBy { it.id }
        .map { session ->
            if (session.vehicleId.isBlank() || session.vehicleId in vehicleIds) session
            else session.copy(vehicleId = activeVehicleId)
        }
    val economy = root.optJSONObject("economy")
    return PremiumBackup(
        vehicles = vehicles,
        activeVehicleId = activeVehicleId,
        reservations = reservations,
        chargingSessions = sessions,
        economyPreferences = EconomyPreferences(
            fuelPricePerLiter = economy?.optDouble("fuelPricePerLiter")?.finiteNonNegative() ?: 0.0,
            gasolineKmPerLiter = economy?.optDouble("gasolineKmPerLiter")
                ?.finitePositive()
                ?: DEFAULT_GASOLINE_KM_PER_LITER
        )
    )
}

private fun Vehicle.toJson() = JSONObject()
    .put("id", id)
    .put("type", type)
    .put("brand", brand)
    .put("model", model)
    .put("year", year)
    .put("rangeKm", rangeKm)
    .put("batteryKwh", batteryKwh)
    .put("connector", connector.name)
    .put("batteryPercent", currentBatteryPercent)

private fun vehicleFromJson(item: JSONObject): Vehicle {
    val id = item.requiredText("id", 80)
    return Vehicle(
        id = id,
        type = item.optString("type", Vehicle().type).safeText(40),
        brand = item.optString("brand").safeText(80),
        model = item.optString("model").safeText(100),
        year = item.optString("year").safeText(8),
        rangeKm = item.optInt("rangeKm").coerceIn(0, 2_000),
        batteryKwh = item.optDouble("batteryKwh").finiteNonNegative().coerceAtMost(500.0),
        connector = item.optString("connector").toEnum(ConnectorType.CCS2),
        currentBatteryPercent = item.optInt("batteryPercent", 50).coerceIn(0, 100)
    )
}

private fun ChargerReservation.toJson() = JSONObject()
    .put("id", id)
    .put("chargerId", chargerId)
    .put("chargerName", chargerName)
    .put("connector", connector.name)
    .put("createdAt", createdAtMillis)
    .put("status", status.name)

private fun reservationFromJson(item: JSONObject) = ChargerReservation(
    id = item.requiredText("id", 80),
    chargerId = item.getInt("chargerId"),
    chargerName = item.requiredText("chargerName", 200),
    connector = item.optString("connector").toEnum(ConnectorType.OTHER),
    createdAtMillis = item.optLong("createdAt").coerceAtLeast(0L),
    status = item.optString("status").toEnum(ReservationStatus.ACTIVE)
)

private fun ChargingSession.toJson() = JSONObject()
    .put("id", id)
    .put("vehicleId", vehicleId)
    .put("chargerId", chargerId)
    .put("chargerName", chargerName)
    .put("connector", connector.name)
    .put("startedAt", startedAtMillis)
    .put("endedAt", endedAtMillis ?: JSONObject.NULL)
    .put("energyKwh", energyKwh)
    .put("amount", amount)
    .put("status", status.name)
    .put("paymentStatus", paymentStatus.name)

private fun sessionFromJson(item: JSONObject) = ChargingSession(
    id = item.requiredText("id", 80),
    chargerId = item.getInt("chargerId"),
    chargerName = item.requiredText("chargerName", 200),
    connector = item.optString("connector").toEnum(ConnectorType.OTHER),
    startedAtMillis = item.optLong("startedAt").coerceAtLeast(0L),
    endedAtMillis = if (item.isNull("endedAt")) null else item.optLong("endedAt").coerceAtLeast(0L),
    energyKwh = item.optDouble("energyKwh").finiteNonNegative().coerceAtMost(10_000.0),
    amount = item.optDouble("amount").finiteNonNegative().coerceAtMost(10_000_000.0),
    status = item.optString("status").toEnum(ChargingSessionStatus.FINISHED),
    paymentStatus = item.optString("paymentStatus").toEnum(PaymentStatus.RECORDED_LOCAL),
    vehicleId = item.optString("vehicleId").safeText(80)
)

private inline fun <T> JSONArray.mapObjects(limit: Int, transform: (JSONObject) -> T): List<T> {
    require(length() <= limit) { "Backup item limit exceeded" }
    return buildList {
        for (index in 0 until length()) add(transform(getJSONObject(index)))
    }
}

private fun JSONObject.requiredText(name: String, maxLength: Int): String =
    getString(name).trim().also { require(it.isNotBlank() && it.length <= maxLength) }

private fun String.safeText(maxLength: Int): String = trim().take(maxLength)

private inline fun <reified T : Enum<T>> String.toEnum(fallback: T): T =
    enumValues<T>().firstOrNull { it.name == this } ?: fallback

private fun Double.finiteNonNegative(): Double = takeIf { it.isFinite() && it >= 0.0 } ?: 0.0
private fun Double.finitePositive(): Double? = takeIf { it.isFinite() && it > 0.0 }

const val MAX_BACKUP_CHARS = 2_000_000
private const val MAX_BACKUP_SESSIONS = 5_000
private const val MAX_BACKUP_RESERVATIONS = 2_000
private const val BACKUP_FORMAT = "vevolt-local-backup"
private const val BACKUP_VERSION = 1
