package br.com.vevolt.model

enum class ChargerStatus {
    AVAILABLE,
    OPERATIONAL,
    BUSY,
    OFFLINE,
    UNKNOWN
}

enum class ConnectorType {
    TYPE_2,
    CCS2,
    CHADEMO,
    OTHER
}

data class Charger(
    val id: Int,
    val name: String,
    val distanceKm: Double,
    val address: String,
    val city: String,
    val status: ChargerStatus,
    val powerKw: Int,
    val connector: ConnectorType,
    val pricePerKwh: Double?,
    val usageCost: String? = null,
    val rating: Double?,
    val reviews: Int,
    val parkingInfo: String,
    val safetyNote: String,
    val comments: List<String>,
    val latitude: Double = -23.5505,
    val longitude: Double = -46.6333,
    val countryCode: String = "BR",
    val communitySummary: CommunitySummary? = null
)

val Charger.communityKey: String get() = "OCM:$id"

data class Vehicle(
    val id: String = "",
    val type: String = "electric",
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val rangeKm: Int = 0,
    val batteryKwh: Double = 0.0,
    val connector: ConnectorType = ConnectorType.CCS2,
    val currentBatteryPercent: Int = 50
)

enum class ReservationStatus {
    ACTIVE,
    CANCELLED,
    USED
}

data class ChargerReservation(
    val id: String,
    val chargerId: Int,
    val chargerName: String,
    val connector: ConnectorType,
    val createdAtMillis: Long,
    val status: ReservationStatus
)

enum class ChargingSessionStatus {
    ACTIVE,
    FINISHED
}

enum class PaymentStatus {
    PENDING,
    RECORDED_LOCAL
}

data class ChargingSession(
    val id: String,
    val chargerId: Int,
    val chargerName: String,
    val connector: ConnectorType,
    val startedAtMillis: Long,
    val endedAtMillis: Long?,
    val energyKwh: Double,
    val amount: Double,
    val status: ChargingSessionStatus,
    val paymentStatus: PaymentStatus,
    val vehicleId: String = ""
)

enum class MarketplaceCategory {
    DEALER,
    REPAIR
}

data class MarketplacePlace(
    val id: String,
    val name: String,
    val category: MarketplaceCategory,
    val distanceKm: Double,
    val address: String,
    val brand: String?,
    val phone: String?,
    val whatsApp: String?,
    val website: String?,
    val photoUrl: String?,
    val openingHours: String?,
    val electricServiceDeclared: Boolean,
    val vehicleBrandMatched: Boolean,
    val latitude: Double,
    val longitude: Double
)
