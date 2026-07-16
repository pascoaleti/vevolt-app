package br.com.vevolt.domain

import br.com.vevolt.model.Charger
import br.com.vevolt.model.Vehicle
import kotlin.math.max
import kotlin.math.roundToInt

internal data class RouteEstimate(
    val destinationLabel: String,
    val distanceKm: Double,
    val arrivalBatteryPercent: Int,
    val arrivalRangeKm: Double,
    val hasEstimatedRange: Boolean
)

internal fun estimateRoute(vehicle: Vehicle, charger: Charger): RouteEstimate {
    val distanceKm = charger.distanceKm.coerceAtLeast(0.0)
    val availableRangeKm = max(vehicle.rangeKm.toDouble(), 1.0)
    val fullRangeEstimateKm = availableRangeKm / (vehicle.currentBatteryPercent.coerceAtLeast(1) / 100.0)
    val arrivalRangeKm = max(availableRangeKm - distanceKm, 0.0)
    return RouteEstimate(
        destinationLabel = "${charger.name}, ${charger.city}",
        distanceKm = distanceKm,
        arrivalBatteryPercent = ((arrivalRangeKm / fullRangeEstimateKm) * 100).roundToInt().coerceIn(0, 100),
        arrivalRangeKm = arrivalRangeKm,
        hasEstimatedRange = availableRangeKm >= distanceKm + SAFETY_RESERVE_KM
    )
}

private const val SAFETY_RESERVE_KM = 30.0
