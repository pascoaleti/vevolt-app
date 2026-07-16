package br.com.vevolt.model

enum class CommunityOutcome {
    WORKING,
    BROKEN,
    QUEUE,
    ACCESS_ISSUE
}

enum class CommunityStatus {
    UNCONFIRMED,
    CONFIRMED_WORKING,
    REPORTED_BROKEN,
    QUEUE_REPORTED,
    ACCESS_ISSUE_REPORTED
}

data class CommunitySummary(
    val stationKey: String,
    val currentStatus: CommunityStatus = CommunityStatus.UNCONFIRMED,
    val reliabilityScore: Int? = null,
    val reportCount: Int = 0,
    val averageRating: Double? = null,
    val latestPricePerKwh: Double? = null,
    val lastReportedAtMillis: Long? = null,
    val lastWorkingAtMillis: Long? = null,
    val recoveredAtMillis: Long? = null
)

data class CommunityReport(
    val id: String,
    val authorPublicId: String,
    val outcome: CommunityOutcome,
    val pricePerKwh: Double? = null,
    val rating: Int? = null,
    val comment: String? = null,
    val photoUrl: String? = null,
    val createdAtMillis: Long,
    val isOwn: Boolean
)

data class CommunityStationState(
    val loading: Boolean = false,
    val summary: CommunitySummary? = null,
    val reports: List<CommunityReport> = emptyList(),
    val watched: Boolean = false,
    val errorCode: String? = null,
    val messageCode: String? = null
)

data class CommunityReportDraft(
    val outcome: CommunityOutcome,
    val pricePerKwh: Double?,
    val rating: Int?,
    val comment: String?,
    val policyAccepted: Boolean
)

fun CommunityStatus.toChargerStatus(fallback: ChargerStatus): ChargerStatus = when (this) {
    CommunityStatus.CONFIRMED_WORKING -> ChargerStatus.AVAILABLE
    CommunityStatus.REPORTED_BROKEN -> ChargerStatus.OFFLINE
    CommunityStatus.QUEUE_REPORTED -> ChargerStatus.BUSY
    CommunityStatus.ACCESS_ISSUE_REPORTED -> ChargerStatus.UNKNOWN
    CommunityStatus.UNCONFIRMED -> fallback
}
