package br.com.vevolt.domain

import br.com.vevolt.model.Charger
import br.com.vevolt.model.ChargerStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.Vehicle

data class ChargerRecommendation(
    val charger: Charger,
    val connectorCompatible: Boolean,
    val score: Double
)

fun rankChargersForVehicle(
    chargers: List<Charger>,
    vehicle: Vehicle
): List<ChargerRecommendation> = chargers
    .asSequence()
    .filter { it.status != ChargerStatus.OFFLINE }
    .map { charger ->
        val compatible = vehicle.connector == ConnectorType.OTHER || charger.connector == vehicle.connector
        val statusScore = when (charger.status) {
            ChargerStatus.AVAILABLE -> 30.0
            ChargerStatus.OPERATIONAL -> 22.0
            ChargerStatus.BUSY -> 5.0
            ChargerStatus.UNKNOWN -> 0.0
            ChargerStatus.OFFLINE -> -100.0
        }
        val distanceScore = (20.0 - charger.distanceKm.coerceIn(0.0, 20.0))
        val powerScore = (charger.powerKw.coerceAtLeast(0) / 10.0).coerceAtMost(15.0)
        val ratingScore = charger.rating?.coerceIn(0.0, 5.0) ?: 0.0
        val informationScore = if (charger.usageCost != null || charger.pricePerKwh != null) 3.0 else 0.0
        ChargerRecommendation(
            charger = charger,
            connectorCompatible = compatible,
            score = (if (compatible) 50.0 else 0.0) + statusScore + distanceScore + powerScore + ratingScore + informationScore
        )
    }
    .sortedWith(
        compareByDescending<ChargerRecommendation> { it.score }
            .thenBy { it.charger.distanceKm }
    )
    .toList()
