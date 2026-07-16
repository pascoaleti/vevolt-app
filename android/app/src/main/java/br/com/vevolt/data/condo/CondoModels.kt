package br.com.vevolt.data.condo

enum class CondoRole { ADMIN, RESIDENT }

data class CondoInfo(
    val id: String,
    val name: String,
    val countryCode: String,
    val currency: String
)

data class CondoMember(
    val id: String,
    val displayName: String,
    val unitLabel: String,
    val role: CondoRole
)

data class CondoCharger(
    val id: String,
    val name: String,
    val connector: String,
    val powerKw: Double,
    val pricePerKwh: Double,
    val enabled: Boolean
)

data class CondoReservation(
    val id: String,
    val chargerId: String,
    val memberId: String,
    val unitLabel: String,
    val startsAt: String,
    val endsAt: String,
    val status: String
)

data class CondoChargingSession(
    val id: String,
    val chargerId: String,
    val memberId: String,
    val unitLabel: String,
    val startedAt: String,
    val endedAt: String?,
    val energyKwh: Double,
    val amount: Double,
    val status: String
)

data class CondoDashboard(
    val condo: CondoInfo,
    val member: CondoMember,
    val members: List<CondoMember>,
    val chargers: List<CondoCharger>,
    val reservations: List<CondoReservation>,
    val sessions: List<CondoChargingSession>
)

data class CondoReportRow(
    val unitLabel: String,
    val displayName: String,
    val sessions: Int,
    val energyKwh: Double,
    val amount: Double
)

data class CondoReport(
    val month: String,
    val currency: String,
    val units: List<CondoReportRow>,
    val totalEnergyKwh: Double,
    val totalAmount: Double
)

data class CondoUiState(
    val loading: Boolean = false,
    val dashboard: CondoDashboard? = null,
    val report: CondoReport? = null,
    val inviteCode: String? = null,
    val errorCode: String? = null,
    val backendConfigured: Boolean = true
)
