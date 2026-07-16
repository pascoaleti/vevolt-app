package br.com.vevolt.ui.localization

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.vevolt.R
import br.com.vevolt.model.CommunityOutcome
import br.com.vevolt.model.CommunityStatus
import kotlin.math.max

@Composable
fun relativeCommunityTime(timestampMillis: Long?): String {
    if (timestampMillis == null) return stringResource(R.string.community_unconfirmed)
    val elapsedMinutes = max(0, (System.currentTimeMillis() - timestampMillis) / 60_000)
    return when {
        elapsedMinutes < 1 -> stringResource(R.string.community_time_now)
        elapsedMinutes < 60 -> stringResource(R.string.community_time_minutes, elapsedMinutes)
        elapsedMinutes < 1_440 -> stringResource(R.string.community_time_hours, elapsedMinutes / 60)
        else -> stringResource(R.string.community_time_days, elapsedMinutes / 1_440)
    }
}

@Composable
fun CommunityOutcome.localizedLabel(): String = stringResource(labelResource())

@StringRes
fun CommunityOutcome.labelResource(): Int = when (this) {
    CommunityOutcome.WORKING -> R.string.community_outcome_working
    CommunityOutcome.BROKEN -> R.string.community_outcome_broken
    CommunityOutcome.QUEUE -> R.string.community_outcome_queue
    CommunityOutcome.ACCESS_ISSUE -> R.string.community_outcome_access
}

@Composable
fun CommunityStatus.localizedLabel(): String = stringResource(
    when (this) {
        CommunityStatus.UNCONFIRMED -> R.string.community_unconfirmed
        CommunityStatus.CONFIRMED_WORKING -> R.string.community_status_working
        CommunityStatus.REPORTED_BROKEN -> R.string.community_status_broken
        CommunityStatus.QUEUE_REPORTED -> R.string.community_status_queue
        CommunityStatus.ACCESS_ISSUE_REPORTED -> R.string.community_status_access
    }
)

@StringRes
fun communityMessageResource(code: String): Int = when (code) {
    "REPORT_PUBLISHED" -> R.string.community_report_published
    "REPORT_COOLDOWN" -> R.string.community_error_cooldown
    "REPORT_DAILY_LIMIT" -> R.string.community_error_daily_limit
    "COMMUNITY_POLICY_REQUIRED" -> R.string.community_error_policy
    "COMMENT_LINK_NOT_ALLOWED" -> R.string.community_error_links
    "PHOTO_INVALID" -> R.string.community_error_photo
    "PHOTO_STORAGE_UNAVAILABLE" -> R.string.community_error_photo_service
    "CONFIGURATION_REQUIRED" -> R.string.community_error_configuration
    else -> R.string.community_error_network
}
