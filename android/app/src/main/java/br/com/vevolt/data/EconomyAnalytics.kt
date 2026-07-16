package br.com.vevolt.data

import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.Vehicle

enum class EconomyPeriod(val days: Int?) {
    DAYS_30(30),
    DAYS_90(90),
    ALL(null)
}

data class EconomySummary(
    val sessions: Int,
    val kwh: Double,
    val electricCost: Double,
    val estimatedKm: Int,
    val gasolineCost: Double,
    val comparisonAvailable: Boolean,
    val vehicleEfficiencyUsed: Boolean
) {
    val savings: Double get() = (gasolineCost - electricCost).coerceAtLeast(0.0)
    val averageCostPerKwh: Double get() = if (kwh > 0.0) electricCost / kwh else 0.0
    val averageCostPerSession: Double get() = if (sessions > 0) electricCost / sessions else 0.0
}

data class StationEconomySummary(
    val name: String,
    val sessions: Int,
    val kwh: Double,
    val cost: Double
) {
    val averageCostPerKwh: Double get() = if (kwh > 0.0) cost / kwh else 0.0
}

data class ChargingInsights(
    val measuredSessions: Int,
    val totalDurationMinutes: Long,
    val averageDurationMinutes: Long,
    val averageRecordedPowerKw: Double,
    val freeSessions: Int,
    val mostUsedStation: String?,
    val lowestAverageCostStation: String?
)

fun filterFinishedSessions(
    sessions: List<ChargingSession>,
    period: EconomyPeriod,
    nowMillis: Long = System.currentTimeMillis()
): List<ChargingSession> {
    val cutoff = period.days?.let { days -> nowMillis - days * MILLIS_PER_DAY }
    return sessions.filter { session ->
        session.status == ChargingSessionStatus.FINISHED &&
            (cutoff == null || (session.endedAtMillis ?: session.startedAtMillis) >= cutoff)
    }
}

fun buildEconomySummary(
    sessions: List<ChargingSession>,
    vehicle: Vehicle,
    preferences: EconomyPreferences
): EconomySummary {
    val kwh = sessions.sumOf { it.energyKwh.coerceAtLeast(0.0) }
    val electricCost = sessions.sumOf { it.amount.coerceAtLeast(0.0) }
    val vehicleEfficiency = if (vehicle.rangeKm > 0 && vehicle.batteryKwh > 0.0) {
        vehicle.rangeKm / vehicle.batteryKwh
    } else {
        null
    }
    val kmPerKwh = vehicleEfficiency ?: FALLBACK_KM_PER_KWH
    val estimatedKm = (kwh * kmPerKwh).toInt().coerceAtLeast(0)
    val comparisonAvailable = estimatedKm > 0 &&
        preferences.fuelPricePerLiter > 0.0 &&
        preferences.gasolineKmPerLiter > 0.0
    val gasolineCost = if (comparisonAvailable) {
        estimatedKm / preferences.gasolineKmPerLiter * preferences.fuelPricePerLiter
    } else {
        0.0
    }
    return EconomySummary(
        sessions = sessions.size,
        kwh = kwh,
        electricCost = electricCost,
        estimatedKm = estimatedKm,
        gasolineCost = gasolineCost,
        comparisonAvailable = comparisonAvailable,
        vehicleEfficiencyUsed = vehicleEfficiency != null
    )
}

fun buildStationEconomySummaries(sessions: List<ChargingSession>): List<StationEconomySummary> =
    sessions
        .groupBy { it.chargerName.trim().ifBlank { "VeVolt" } }
        .map { (name, stationSessions) ->
            StationEconomySummary(
                name = name,
                sessions = stationSessions.size,
                kwh = stationSessions.sumOf { it.energyKwh.coerceAtLeast(0.0) },
                cost = stationSessions.sumOf { it.amount.coerceAtLeast(0.0) }
            )
        }
        .sortedWith(compareByDescending<StationEconomySummary> { it.sessions }.thenBy { it.name })

fun buildChargingInsights(sessions: List<ChargingSession>): ChargingInsights {
    val finished = sessions.filter { it.status == ChargingSessionStatus.FINISHED }
    val measured = finished.mapNotNull { session ->
        val endedAt = session.endedAtMillis ?: return@mapNotNull null
        val durationMillis = endedAt - session.startedAtMillis
        if (durationMillis <= 0L || durationMillis > MAX_INSIGHT_SESSION_MILLIS) return@mapNotNull null
        session to durationMillis
    }
    val totalDurationMillis = measured.sumOf { it.second }
    val measuredEnergy = measured.sumOf { it.first.energyKwh.coerceAtLeast(0.0) }
    val totalHours = totalDurationMillis / MILLIS_PER_HOUR.toDouble()
    val stations = buildStationEconomySummaries(finished)
    return ChargingInsights(
        measuredSessions = measured.size,
        totalDurationMinutes = totalDurationMillis / MILLIS_PER_MINUTE,
        averageDurationMinutes = if (measured.isEmpty()) 0L else {
            totalDurationMillis / measured.size / MILLIS_PER_MINUTE
        },
        averageRecordedPowerKw = if (totalHours > 0.0) measuredEnergy / totalHours else 0.0,
        freeSessions = finished.count { it.energyKwh > 0.0 && it.amount == 0.0 },
        mostUsedStation = stations.firstOrNull()?.name,
        lowestAverageCostStation = stations
            .filter { it.kwh > 0.0 && it.cost > 0.0 }
            .minByOrNull { it.averageCostPerKwh }
            ?.name
    )
}

private const val FALLBACK_KM_PER_KWH = 6.0
private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1_000L
private const val MILLIS_PER_HOUR = 60L * 60L * 1_000L
private const val MILLIS_PER_MINUTE = 60L * 1_000L
private const val MAX_INSIGHT_SESSION_MILLIS = 72L * MILLIS_PER_HOUR
