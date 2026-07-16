package br.com.vevolt.ui.localization

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import br.com.vevolt.R
import br.com.vevolt.model.ChargerStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.PaymentStatus
import java.text.NumberFormat
import java.util.Locale

const val VEHICLE_TYPE_ELECTRIC = "electric"
const val VEHICLE_TYPE_PLUGIN_HYBRID = "plugin_hybrid"

@Composable
fun ChargerStatus.localizedLabel(): String = stringResource(
    when (this) {
        ChargerStatus.AVAILABLE -> R.string.status_available
        ChargerStatus.OPERATIONAL -> R.string.status_operational
        ChargerStatus.BUSY -> R.string.status_busy
        ChargerStatus.OFFLINE -> R.string.status_offline
        ChargerStatus.UNKNOWN -> R.string.status_unknown
    }
)

@Composable
fun ConnectorType.localizedLabel(): String = stringResource(
    when (this) {
        ConnectorType.TYPE_2 -> R.string.connector_type_2
        ConnectorType.CCS2 -> R.string.connector_ccs2
        ConnectorType.CHADEMO -> R.string.connector_chademo
        ConnectorType.OTHER -> R.string.connector_other
    }
)

@Composable
fun ChargingSessionStatus.localizedLabel(): String = stringResource(
    if (this == ChargingSessionStatus.ACTIVE) R.string.session_active else R.string.session_finished
)

@Composable
fun PaymentStatus.localizedLabel(): String = stringResource(
    if (this == PaymentStatus.PENDING) R.string.payment_pending else R.string.payment_recorded_local
)

@Composable
fun localizedVehicleType(value: String): String = stringResource(vehicleTypeString(value))

fun normalizeVehicleType(value: String): String = when (value.trim().lowercase(Locale.ROOT)) {
    "eletrico", "elétrico", "electric" -> VEHICLE_TYPE_ELECTRIC
    "hibrido plug-in", "híbrido plug-in", "plug-in hybrid", "híbrido enchufable" ->
        VEHICLE_TYPE_PLUGIN_HYBRID
    else -> value.ifBlank { VEHICLE_TYPE_ELECTRIC }
}

@StringRes
fun vehicleTypeString(value: String): Int = when (normalizeVehicleType(value)) {
    VEHICLE_TYPE_PLUGIN_HYBRID -> R.string.vehicle_type_plugin_hybrid
    else -> R.string.vehicle_type_electric
}

@Composable
fun currentLocale(): Locale {
    return LocalLocale.current.platformLocale
}

fun formatCurrency(value: Double, locale: Locale): String =
    NumberFormat.getCurrencyInstance(locale).format(value)

fun formatDecimal(value: Double, locale: Locale, maximumFractionDigits: Int = 1): String =
    NumberFormat.getNumberInstance(locale).apply {
        minimumFractionDigits = 0
        this.maximumFractionDigits = maximumFractionDigits
    }.format(value)
