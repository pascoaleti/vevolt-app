package br.com.vevolt.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.vevolt.R
import br.com.vevolt.data.condo.CondoCharger
import br.com.vevolt.data.condo.CondoChargingSession
import br.com.vevolt.data.condo.CondoDashboard
import br.com.vevolt.data.condo.CondoReport
import br.com.vevolt.data.condo.CondoReservation
import br.com.vevolt.data.condo.CondoRole
import br.com.vevolt.data.condo.CondoUiState
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.GreenButton
import br.com.vevolt.ui.components.OutlineActionButton
import br.com.vevolt.ui.components.PrimaryButton
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatDecimal
import br.com.vevolt.ui.theme.AccessibleGreen
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen
import java.text.DateFormat
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Currency
import java.util.Locale

@Composable
fun CondoScreen(
    state: CondoUiState,
    premiumActive: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onCreateCondo: (String, String, String, String, String) -> Unit,
    onJoinCondo: (String, String) -> Unit,
    onCreateInvite: (String) -> Unit,
    onAddCharger: (String, String, Double, Double) -> Unit,
    onReserve: (String, String, Int) -> Unit,
    onCancelReservation: (String) -> Unit,
    onStartCharging: (String) -> Unit,
    onFinishCharging: (String, Double) -> Unit,
    onOpenPremium: () -> Unit,
    onLeave: () -> Unit,
    onDeleteMembership: () -> Unit
) {
    val dashboard = state.dashboard
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { CondoHeader(dashboard?.condo?.name, state.loading, onBack, onRefresh) }
        state.errorCode?.let { code -> item { CondoErrorCard(code) } }
        state.inviteCode?.let { code -> item { InviteCodeCard(code) } }
        if (dashboard == null) {
            item {
                CondoSetup(
                    enabled = !state.loading && state.backendConfigured,
                    premiumActive = premiumActive,
                    onCreateCondo = onCreateCondo,
                    onJoinCondo = onJoinCondo,
                    onOpenPremium = onOpenPremium
                )
            }
        } else {
            item { CondoSummary(dashboard) }
            if (dashboard.member.role == CondoRole.ADMIN && premiumActive) {
                item { AdminActions(onCreateInvite, onAddCharger) }
            } else if (dashboard.member.role == CondoRole.ADMIN) {
                item { PremiumAdminCard(onOpenPremium) }
            }
            val activeSession = dashboard.sessions.firstOrNull {
                it.status == "ACTIVE" && it.memberId == dashboard.member.id
            }
            activeSession?.let { session ->
                item { ActiveSessionCard(session, dashboard, onFinishCharging) }
            }
            item { SectionTitle(stringResource(R.string.condo_shared_chargers)) }
            if (dashboard.chargers.isEmpty()) {
                item { EmptyCondoCard(R.string.condo_no_chargers, R.string.condo_no_chargers_body) }
            } else {
                items(dashboard.chargers, key = { it.id }) { charger ->
                    ChargerManagementCard(
                        charger = charger,
                        currencyCode = dashboard.condo.currency,
                        sessionActive = activeSession != null,
                        onReserve = onReserve,
                        onStartCharging = onStartCharging
                    )
                }
            }
            item { SectionTitle(stringResource(R.string.condo_upcoming_reservations)) }
            if (dashboard.reservations.isEmpty()) {
                item { EmptyCondoCard(R.string.condo_no_reservations, R.string.condo_no_reservations_body) }
            } else {
                items(dashboard.reservations, key = { it.id }) { reservation ->
                    ReservationCard(
                        reservation = reservation,
                        dashboard = dashboard,
                        onCancel = onCancelReservation
                    )
                }
            }
            item { MonthlyReportCard(state.report) }
            item { CondoPrivacyActions(onLeave, onDeleteMembership) }
        }
        item { Spacer(Modifier.height(28.dp)) }
    }
}

@Composable
private fun CondoPrivacyActions(onLeave: () -> Unit, onDeleteMembership: () -> Unit) {
    var confirmingDeletion by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        TextButton(onClick = onLeave, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.condo_leave))
        }
        TextButton(onClick = { confirmingDeletion = true }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.condo_delete_membership), color = MaterialTheme.colorScheme.error)
        }
    }
    if (confirmingDeletion) {
        AlertDialog(
            onDismissRequest = { confirmingDeletion = false },
            title = { Text(stringResource(R.string.condo_delete_title)) },
            text = { Text(stringResource(R.string.condo_delete_body)) },
            confirmButton = {
                TextButton(onClick = { confirmingDeletion = false; onDeleteMembership() }) {
                    Text(stringResource(R.string.confirm_deletion), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmingDeletion = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun PremiumAdminCard(onOpenPremium: () -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.condo_management), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(
                stringResource(R.string.condo_create_premium),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f)
            )
            PrimaryButton(
                text = stringResource(R.string.view_premium),
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenPremium
            )
        }
    }
}

@Composable
private fun CondoHeader(name: String?, loading: Boolean, onBack: () -> Unit, onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
        }
        Column(Modifier.weight(1f)) {
            Text("VeVolt Condo", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                name ?: stringResource(R.string.condo_operational_subtitle),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                fontSize = 13.sp
            )
        }
        if (loading) {
            CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 3.dp, color = ElectricBlue)
        } else if (name != null) {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Rounded.Refresh, contentDescription = stringResource(R.string.condo_refresh))
            }
        }
    }
}

@Composable
private fun CondoSetup(
    enabled: Boolean,
    premiumActive: Boolean,
    onCreateCondo: (String, String, String, String, String) -> Unit,
    onJoinCondo: (String, String) -> Unit,
    onOpenPremium: () -> Unit
) {
    var createMode by remember { mutableStateOf(true) }
    var condoName by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var unitLabel by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    val locale = currentLocale()
    var countryCode by remember { mutableStateOf(locale.country.ifBlank { "BR" }.uppercase()) }
    var currency by remember {
        mutableStateOf(runCatching { Currency.getInstance(locale).currencyCode }.getOrDefault("BRL"))
    }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.Apartment, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(42.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.condo_setup_title), fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    Text(
                        stringResource(R.string.condo_setup_body),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                        fontSize = 13.sp
                    )
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = createMode,
                    onClick = { createMode = true },
                    label = { Text(stringResource(R.string.condo_create_tab)) }
                )
                FilterChip(
                    selected = !createMode,
                    onClick = { createMode = false },
                    label = { Text(stringResource(R.string.condo_join_tab)) }
                )
            }
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it.take(60) },
                label = { Text(stringResource(R.string.condo_your_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (createMode) {
                OutlinedTextField(
                    value = condoName,
                    onValueChange = { condoName = it.take(80) },
                    label = { Text(stringResource(R.string.condo_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = unitLabel,
                    onValueChange = { unitLabel = it.take(30) },
                    label = { Text(stringResource(R.string.condo_unit)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = countryCode,
                        onValueChange = { countryCode = it.filter(Char::isLetter).take(2).uppercase() },
                        label = { Text(stringResource(R.string.condo_country)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it.filter(Char::isLetter).take(3).uppercase() },
                        label = { Text(stringResource(R.string.condo_currency)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                PrimaryButton(
                    text = stringResource(
                        if (premiumActive) R.string.condo_create_action else R.string.view_premium
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (premiumActive) {
                            onCreateCondo(condoName, displayName, unitLabel, countryCode, currency)
                        } else {
                            onOpenPremium()
                        }
                    }
                )
                if (!premiumActive) {
                    Text(
                        stringResource(R.string.condo_create_premium),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                        fontSize = 13.sp
                    )
                }
            } else {
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { inviteCode = it.filter(Char::isLetterOrDigit).take(8).uppercase() },
                    label = { Text(stringResource(R.string.condo_invite_code)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                GreenButton(
                    text = stringResource(R.string.condo_join_action),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onJoinCondo(inviteCode, displayName) }
                )
            }
            if (!enabled) {
                Text(
                    stringResource(R.string.condo_backend_required),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun CondoSummary(dashboard: CondoDashboard) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.Apartment, contentDescription = null, tint = EnergyGreen, modifier = Modifier.size(36.dp))
                Column(Modifier.weight(1f)) {
                    Text(dashboard.condo.name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text(
                        "${dashboard.member.unitLabel} · ${stringResource(if (dashboard.member.role == CondoRole.ADMIN) R.string.condo_admin else R.string.condo_resident)}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f)
                    )
                }
            }
            MetricRow(stringResource(R.string.condo_members), dashboard.members.size.toString())
            MetricRow(stringResource(R.string.condo_shared_chargers), dashboard.chargers.size.toString())
            MetricRow(stringResource(R.string.condo_upcoming_reservations), dashboard.reservations.size.toString())
        }
    }
}

@Composable
private fun AdminActions(
    onCreateInvite: (String) -> Unit,
    onAddCharger: (String, String, Double, Double) -> Unit
) {
    var inviteDialog by remember { mutableStateOf(false) }
    var chargerDialog by remember { mutableStateOf(false) }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.condo_management), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlineActionButton(
                    text = stringResource(R.string.condo_invite_resident),
                    modifier = Modifier.weight(1f),
                    onClick = { inviteDialog = true }
                )
                PrimaryButton(
                    text = stringResource(R.string.condo_add_charger),
                    modifier = Modifier.weight(1f),
                    onClick = { chargerDialog = true }
                )
            }
        }
    }
    if (inviteDialog) InviteDialog(
        onDismiss = { inviteDialog = false },
        onConfirm = { unit -> inviteDialog = false; onCreateInvite(unit) }
    )
    if (chargerDialog) ChargerDialog(
        onDismiss = { chargerDialog = false },
        onConfirm = { name, connector, power, price ->
            chargerDialog = false
            onAddCharger(name, connector, power, price)
        }
    )
}

@Composable
private fun ChargerManagementCard(
    charger: CondoCharger,
    currencyCode: String,
    sessionActive: Boolean,
    onReserve: (String, String, Int) -> Unit,
    onStartCharging: (String) -> Unit
) {
    var reservationDialog by remember { mutableStateOf(false) }
    val locale = currentLocale()
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.Bolt, contentDescription = null, tint = EnergyGreen, modifier = Modifier.size(34.dp))
                Column(Modifier.weight(1f)) {
                    Text(charger.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(
                        "${charger.connector} · ${formatDecimal(charger.powerKw, locale)} kW",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f)
                    )
                }
                Text(
                    formatCondoCurrency(charger.pricePerKwh, currencyCode, locale) + "/kWh",
                    color = AccessibleGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlineActionButton(
                    text = stringResource(R.string.condo_reserve),
                    modifier = Modifier.weight(1f),
                    onClick = { reservationDialog = true }
                )
                GreenButton(
                    text = stringResource(R.string.condo_start_charge),
                    modifier = Modifier.weight(1f),
                    onClick = { if (!sessionActive) onStartCharging(charger.id) }
                )
            }
        }
    }
    if (reservationDialog) ReservationDialog(
        chargerName = charger.name,
        onDismiss = { reservationDialog = false },
        onConfirm = { startsAt, duration ->
            reservationDialog = false
            onReserve(charger.id, startsAt, duration)
        }
    )
}

@Composable
private fun ActiveSessionCard(
    session: CondoChargingSession,
    dashboard: CondoDashboard,
    onFinishCharging: (String, Double) -> Unit
) {
    var energy by remember { mutableStateOf("") }
    val chargerName = dashboard.chargers.firstOrNull { it.id == session.chargerId }?.name
        ?: stringResource(R.string.condo_charger)
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.condo_active_charge), color = AccessibleGreen, fontWeight = FontWeight.ExtraBold)
            Text(chargerName, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
            Text(
                stringResource(R.string.condo_started_at, formatDateTime(session.startedAt)),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f)
            )
            OutlinedTextField(
                value = energy,
                onValueChange = { energy = it.filter { char -> char.isDigit() || char == ',' || char == '.' }.take(7) },
                label = { Text(stringResource(R.string.condo_energy_to_finish)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            GreenButton(
                text = stringResource(R.string.condo_finish_charge),
                modifier = Modifier.fillMaxWidth(),
                onClick = { energy.toDecimalOrNull()?.let { onFinishCharging(session.id, it) } }
            )
        }
    }
}

@Composable
private fun ReservationCard(
    reservation: CondoReservation,
    dashboard: CondoDashboard,
    onCancel: (String) -> Unit
) {
    val charger = dashboard.chargers.firstOrNull { it.id == reservation.chargerId }
    val canCancel = reservation.memberId == dashboard.member.id || dashboard.member.role == CondoRole.ADMIN
    AppCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Rounded.EventAvailable, contentDescription = null, tint = ElectricBlue)
            Column(Modifier.weight(1f)) {
                Text(charger?.name ?: stringResource(R.string.condo_charger), fontWeight = FontWeight.Bold)
                Text(
                    "${reservation.unitLabel} · ${formatDateTime(reservation.startsAt)}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                    fontSize = 13.sp
                )
            }
            if (canCancel) {
                TextButton(onClick = { onCancel(reservation.id) }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun MonthlyReportCard(report: CondoReport?) {
    val locale = currentLocale()
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text(stringResource(R.string.condo_monthly_report), fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
            if (report == null || report.units.isEmpty()) {
                Text(
                    stringResource(R.string.condo_report_empty),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f)
                )
            } else {
                report.units.forEach { row ->
                    MetricRow(
                        "${row.unitLabel} · ${row.sessions}",
                        "${formatDecimal(row.energyKwh, locale)} kWh · ${formatCondoCurrency(row.amount, report.currency, locale)}"
                    )
                }
            }
            MetricRow(
                stringResource(R.string.condo_total),
                "${formatDecimal(report?.totalEnergyKwh ?: 0.0, locale)} kWh · " +
                    formatCondoCurrency(report?.totalAmount ?: 0.0, report?.currency ?: "BRL", locale)
            )
        }
    }
}

@Composable
private fun InviteCodeCard(code: String) {
    val context = LocalContext.current
    AppCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.PersonAdd, contentDescription = null, tint = EnergyGreen)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(stringResource(R.string.condo_invite_ready), fontWeight = FontWeight.Bold)
                Text(code, fontWeight = FontWeight.ExtraBold, fontSize = 23.sp, color = ElectricBlue)
                Text(stringResource(R.string.condo_invite_expiry), fontSize = 12.sp)
            }
            IconButton(onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("VeVolt Condo", code))
            }) {
                Icon(Icons.Rounded.ContentCopy, contentDescription = stringResource(R.string.condo_copy_code))
            }
        }
    }
}

@Composable
private fun CondoErrorCard(code: String) {
    val message = when (code) {
        "CONFIGURATION_REQUIRED" -> stringResource(R.string.condo_backend_required)
        "NETWORK_UNAVAILABLE" -> stringResource(R.string.condo_network_error)
        "INVALID_ACCESS_TOKEN", "AUTH_REQUIRED" -> stringResource(R.string.condo_session_expired)
        "INVITE_INVALID_OR_EXPIRED" -> stringResource(R.string.condo_invite_invalid)
        "RESERVATION_CONFLICT" -> stringResource(R.string.condo_reservation_conflict)
        "CHARGER_OR_MEMBER_BUSY" -> stringResource(R.string.condo_charger_busy)
        "ADMIN_REQUIRED" -> stringResource(R.string.condo_admin_required)
        "PREMIUM_REQUIRED" -> stringResource(R.string.condo_create_premium)
        "ADMIN_HAS_MEMBERS" -> stringResource(R.string.condo_admin_has_members)
        else -> stringResource(R.string.condo_generic_error, code)
    }
    AppCard(Modifier.fillMaxWidth()) {
        Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun EmptyCondoCard(title: Int, body: Int) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(stringResource(title), fontWeight = FontWeight.Bold)
            Text(stringResource(body), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, modifier = Modifier.padding(top = 4.dp))
}

@Composable
private fun InviteDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var unit by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Rounded.Group, contentDescription = null) },
        title = { Text(stringResource(R.string.condo_invite_resident)) },
        text = {
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it.take(30) },
                label = { Text(stringResource(R.string.condo_unit)) },
                singleLine = true
            )
        },
        confirmButton = { TextButton(onClick = { onConfirm(unit) }) { Text(stringResource(R.string.generate)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

@Composable
private fun ChargerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var connector by remember { mutableStateOf("CCS2") }
    var power by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.condo_add_charger)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it.take(60) }, label = { Text(stringResource(R.string.condo_charger_name)) }, singleLine = true)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("TYPE_2", "CCS2", "CHADEMO", "OTHER").forEach { option ->
                        FilterChip(selected = connector == option, onClick = { connector = option }, label = { Text(option) })
                    }
                }
                OutlinedTextField(
                    power,
                    { power = it.filterDecimal().take(7) },
                    label = { Text(stringResource(R.string.condo_power_kw)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    price,
                    { price = it.filterDecimal().take(7) },
                    label = { Text(stringResource(R.string.condo_price_per_kwh)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val parsedPower = power.toDecimalOrNull()
                val parsedPrice = price.toDecimalOrNull()
                if (parsedPower != null && parsedPrice != null) onConfirm(name, connector, parsedPower, parsedPrice)
            }) { Text(stringResource(R.string.add)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

@Composable
private fun ReservationDialog(
    chargerName: String,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    val context = LocalContext.current
    var startsAt by remember { mutableStateOf(LocalDateTime.now().plusMinutes(15)) }
    var duration by remember { mutableIntStateOf(60) }
    val pickTime = {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                TimePickerDialog(
                    context,
                    { _, hour, minute -> startsAt = LocalDateTime.of(year, month + 1, day, hour, minute) },
                    startsAt.hour,
                    startsAt.minute,
                    true
                ).show()
            },
            startsAt.year,
            startsAt.monthValue - 1,
            startsAt.dayOfMonth
        ).show()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.condo_reserve_charger, chargerName)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlineActionButton(
                    text = formatLocalDateTime(startsAt),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = pickTime
                )
                Text(stringResource(R.string.condo_duration), fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30, 60, 120).forEach { minutes ->
                        FilterChip(
                            selected = duration == minutes,
                            onClick = { duration = minutes },
                            label = { Text(stringResource(R.string.condo_minutes, minutes)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(startsAt.atZone(ZoneId.systemDefault()).toInstant().toString(), duration)
            }) { Text(stringResource(R.string.condo_reserve)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f), modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun formatDateTime(iso: String): String {
    val date = runCatching { java.util.Date.from(Instant.parse(iso)) }.getOrNull() ?: return iso
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, currentLocale()).format(date)
}

@Composable
private fun formatLocalDateTime(value: LocalDateTime): String {
    val date = java.util.Date.from(value.atZone(ZoneId.systemDefault()).toInstant())
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, currentLocale()).format(date)
}

private fun formatCondoCurrency(value: Double, currencyCode: String, locale: Locale): String {
    val formatter = NumberFormat.getCurrencyInstance(locale)
    runCatching { formatter.currency = Currency.getInstance(currencyCode) }
    return formatter.format(value)
}

private fun String.filterDecimal(): String = filter { it.isDigit() || it == ',' || it == '.' }
private fun String.toDecimalOrNull(): Double? = replace(',', '.').toDoubleOrNull()?.takeIf { it > 0.0 }
