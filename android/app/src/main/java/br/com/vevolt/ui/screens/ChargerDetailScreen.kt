package br.com.vevolt.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EvStation
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import br.com.vevolt.R
import br.com.vevolt.model.Charger
import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.CommunityOutcome
import br.com.vevolt.model.CommunityReport
import br.com.vevolt.model.CommunityReportDraft
import br.com.vevolt.model.CommunityStationState
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.GreenButton
import br.com.vevolt.ui.components.OutlineActionButton
import br.com.vevolt.ui.components.PrimaryButton
import br.com.vevolt.ui.components.StatusChip
import br.com.vevolt.ui.localization.communityMessageResource
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatCurrency
import br.com.vevolt.ui.localization.formatDecimal
import br.com.vevolt.ui.localization.localizedLabel
import br.com.vevolt.ui.localization.relativeCommunityTime
import br.com.vevolt.ui.theme.AccessibleGreen
import br.com.vevolt.ui.theme.DangerRed
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen
import br.com.vevolt.ui.theme.WarningYellow
import coil3.compose.AsyncImage

@Composable
fun ChargerDetailScreen(
    charger: Charger,
    hasActiveReservation: Boolean,
    activeSession: ChargingSession?,
    communityState: CommunityStationState = CommunityStationState(),
    onBack: () -> Unit,
    onRoute: () -> Unit,
    onReserve: () -> Unit,
    onScan: () -> Unit,
    onCommunitySubmit: (CommunityReportDraft, Uri?) -> Unit = { _, _ -> },
    onCommunityWatch: () -> Unit = {},
    onCommunityFlag: (String) -> Unit = {},
    onCommunityDelete: (String) -> Unit = {},
    onCommunityBlock: (String) -> Unit = {},
    onOpenTerms: () -> Unit = {}
) {
    val locale = currentLocale()
    val context = LocalContext.current
    var showReportDialog by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<String?>(null) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) onCommunityWatch() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            Text(stringResource(R.string.charger_details), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        }
        HeroChargerImage()
        Spacer(Modifier.height(12.dp))
        StatusChip(charger.status)
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                Text(charger.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "${stringResource(R.string.distance_km, formatDecimal(charger.distanceKm, locale))} | ${charger.address}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                )
                Text(charger.city, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f))
            }
            charger.rating?.let { rating ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = WarningYellow)
                    Text(formatDecimal(rating, locale), fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            InfoBox(
                Icons.Rounded.Bolt,
                stringResource(R.string.power),
                charger.powerKw.takeIf { it > 0 }?.let { stringResource(R.string.power_kw, it) }
                    ?: stringResource(R.string.not_provided),
                Modifier.weight(1f)
            )
            InfoBox(
                Icons.Rounded.EvStation,
                stringResource(R.string.connector),
                charger.connector.localizedLabel(),
                Modifier.weight(1f)
            )
            InfoBox(
                Icons.Rounded.Payments,
                stringResource(R.string.price),
                charger.usageCost
                    ?: charger.pricePerKwh?.let { formatCurrency(it, locale) }
                    ?: stringResource(R.string.not_provided),
                Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(14.dp))
        CommunitySummaryPanel(
            state = communityState,
            onConfirm = { showReportDialog = true },
            onWatch = {
                val needsPermission = !communityState.watched &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED
                if (needsPermission) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    onCommunityWatch()
                }
            }
        )
        communityState.messageCode?.let {
            CommunityFeedback(communityMessageResource(it), AccessibleGreen)
        }
        communityState.errorCode?.let {
            CommunityFeedback(communityMessageResource(it), DangerRed)
        }
        Spacer(Modifier.height(16.dp))
        CommunityReports(
            reports = communityState.reports,
            onFlag = onCommunityFlag,
            onDelete = { reportToDelete = it },
            onBlock = onCommunityBlock
        )
        Spacer(Modifier.height(14.dp))
        if (hasActiveReservation || activeSession?.chargerId == charger.id) {
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.local_status), fontWeight = FontWeight.ExtraBold)
                    if (hasActiveReservation) Text(stringResource(R.string.point_saved_device))
                    if (activeSession?.chargerId == charger.id) Text(stringResource(R.string.local_session_active))
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        PrimaryButton(stringResource(R.string.get_directions), modifier = Modifier.fillMaxWidth(), onClick = onRoute)
        Spacer(Modifier.height(10.dp))
        if (hasActiveReservation) {
            OutlineActionButton(stringResource(R.string.remove_saved_point), Modifier.fillMaxWidth(), onClick = onReserve)
        } else {
            GreenButton(stringResource(R.string.save_point), Modifier.fillMaxWidth(), onClick = onReserve)
        }
        Spacer(Modifier.height(10.dp))
        OutlineActionButton(stringResource(R.string.scan_qr_code), modifier = Modifier.fillMaxWidth(), onClick = onScan)
        Spacer(Modifier.height(14.dp))
        AppCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.information), fontWeight = FontWeight.ExtraBold)
                Text(stringResource(R.string.usage_rules, charger.parkingInfo.ifBlank { stringResource(R.string.not_provided) }))
                Text(stringResource(R.string.operator, charger.safetyNote))
                Text(stringResource(R.string.source), fontWeight = FontWeight.ExtraBold)
                charger.comments.forEach { Text(it, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)) }
            }
        }
        Spacer(Modifier.height(18.dp))
    }

    if (showReportDialog) {
        CommunityReportDialog(
            loading = communityState.loading,
            onDismiss = { if (!communityState.loading) showReportDialog = false },
            onOpenTerms = onOpenTerms,
            onSubmit = { draft, photo ->
                onCommunitySubmit(draft, photo)
                showReportDialog = false
            }
        )
    }
    reportToDelete?.let { reportId ->
        AlertDialog(
            onDismissRequest = { reportToDelete = null },
            text = { Text(stringResource(R.string.community_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    onCommunityDelete(reportId)
                    reportToDelete = null
                }) { Text(stringResource(R.string.community_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { reportToDelete = null }) {
                    Text(stringResource(R.string.community_cancel))
                }
            }
        )
    }
}

@Composable
private fun CommunitySummaryPanel(
    state: CommunityStationState,
    onConfirm: () -> Unit,
    onWatch: () -> Unit
) {
    val summary = state.summary
    val status = summary?.currentStatus
    val freshness = if (status == br.com.vevolt.model.CommunityStatus.CONFIRMED_WORKING) {
        stringResource(R.string.community_confirmed_ago, relativeCommunityTime(summary.lastWorkingAtMillis))
    } else if (summary?.lastReportedAtMillis != null) {
        stringResource(R.string.community_reported_ago, relativeCommunityTime(summary.lastReportedAtMillis))
    } else {
        stringResource(R.string.community_unconfirmed)
    }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Groups, contentDescription = null, tint = ElectricBlue)
                    Text(stringResource(R.string.community_title), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                if (state.loading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(status?.localizedLabel() ?: stringResource(R.string.community_unconfirmed), fontWeight = FontWeight.Bold)
                    if (summary?.lastReportedAtMillis != null) {
                        Text(freshness, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f), fontSize = 13.sp)
                    }
                    val reportCount = summary?.reportCount ?: 0
                    Text(
                        pluralStringResource(R.plurals.community_report_count, reportCount, reportCount),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                        fontSize = 12.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(stringResource(R.string.community_reliability), fontSize = 12.sp)
                    Text(
                        summary?.reliabilityScore?.let {
                            stringResource(R.string.community_reliability_score, it)
                        } ?: "--",
                        color = ElectricBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                }
            }
            Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.community_confirm_status))
            }
            OutlinedButton(onClick = onWatch, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    if (state.watched) Icons.Rounded.NotificationsOff else Icons.Rounded.Notifications,
                    contentDescription = null
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    stringResource(
                        if (state.watched) R.string.community_stop_watch else R.string.community_watch
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun CommunityReports(
    reports: List<CommunityReport>,
    onFlag: (String) -> Unit,
    onDelete: (String) -> Unit,
    onBlock: (String) -> Unit
) {
    Text(stringResource(R.string.community_recent_reports), fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
    Spacer(Modifier.height(8.dp))
    if (reports.isEmpty()) {
        Text(
            stringResource(R.string.community_no_reports),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    } else {
        reports.forEach { report ->
            CommunityReportItem(report, onFlag, onDelete, onBlock)
            Spacer(Modifier.height(10.dp))
        }
    }
    Text(
        stringResource(R.string.community_moderation_notice),
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
    )
}

@Composable
private fun CommunityReportItem(
    report: CommunityReport,
    onFlag: (String) -> Unit,
    onDelete: (String) -> Unit,
    onBlock: (String) -> Unit
) {
    val locale = currentLocale()
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(report.outcome.localizedLabel(), fontWeight = FontWeight.ExtraBold)
                    Text(
                        stringResource(
                            R.string.community_by_author,
                            report.authorPublicId,
                            relativeCommunityTime(report.createdAtMillis)
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
                report.rating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(18.dp))
                        Text(rating.toString(), fontWeight = FontWeight.Bold)
                    }
                }
            }
            report.photoUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = stringResource(R.string.community_photo_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            report.pricePerKwh?.let {
                Text(stringResource(R.string.community_price_value, formatCurrency(it, locale)), fontWeight = FontWeight.SemiBold)
            }
            report.comment?.let { Text(it) }
            if (report.isOwn) {
                TextButton(onClick = { onDelete(report.id) }) {
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text(stringResource(R.string.community_delete_report))
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = { onFlag(report.id) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.Flag, contentDescription = null)
                        Spacer(Modifier.size(4.dp))
                        Text(stringResource(R.string.community_report_content), maxLines = 2)
                    }
                    TextButton(onClick = { onBlock(report.authorPublicId) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.Block, contentDescription = null)
                        Spacer(Modifier.size(4.dp))
                        Text(stringResource(R.string.community_block_user), maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityReportDialog(
    loading: Boolean,
    onDismiss: () -> Unit,
    onOpenTerms: () -> Unit,
    onSubmit: (CommunityReportDraft, Uri?) -> Unit
) {
    var outcome by remember { mutableStateOf(CommunityOutcome.WORKING) }
    var price by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var photo by remember { mutableStateOf<Uri?>(null) }
    var accepted by remember { mutableStateOf(false) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        photo = it
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(stringResource(R.string.community_report_title), fontSize = 22.sp) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CommunityOutcome.values().toList().chunked(2).forEach { rowOutcomes ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowOutcomes.forEach { option ->
                            FilterChip(
                                selected = outcome == option,
                                onClick = { outcome = option },
                                label = {
                                    Text(option.localizedLabel(), maxLines = 2, overflow = TextOverflow.Ellipsis)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Text(stringResource(R.string.community_rating), fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    (1..5).forEach { value ->
                        IconButton(onClick = { rating = value }, modifier = Modifier.size(38.dp)) {
                            Icon(
                                Icons.Rounded.Star,
                                contentDescription = value.toString(),
                                tint = if (value <= rating) WarningYellow else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = price,
                    onValueChange = { candidate ->
                        if (candidate.length <= 8 && candidate.all { it.isDigit() || it == ',' || it == '.' }) price = candidate
                    },
                    label = { Text(stringResource(R.string.community_price_found)) },
                    keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it.take(280) },
                    label = { Text(stringResource(R.string.community_comment)) },
                    supportingText = { Text(stringResource(R.string.community_comment_counter, comment.length)) },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = {
                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.AddAPhoto, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(if (photo == null) R.string.community_add_photo else R.string.community_change_photo))
                }
                photo?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = stringResource(R.string.community_photo_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { accepted = !accepted },
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(checked = accepted, onCheckedChange = { accepted = it })
                    Text(stringResource(R.string.community_policy_accept), fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
                }
                TextButton(onClick = onOpenTerms) { Text(stringResource(R.string.open_terms)) }
            }
        },
        confirmButton = {
            Button(
                enabled = accepted && !loading,
                onClick = {
                    onSubmit(
                        CommunityReportDraft(
                            outcome = outcome,
                            pricePerKwh = price.replace(',', '.').toDoubleOrNull(),
                            rating = rating.takeIf { it > 0 },
                            comment = comment.trim().takeIf { it.isNotBlank() },
                            policyAccepted = accepted
                        ),
                        photo
                    )
                }
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(stringResource(R.string.community_publish))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !loading) {
                Text(stringResource(R.string.community_cancel))
            }
        }
    )
}

@Composable
private fun CommunityFeedback(message: Int, color: Color) {
    Text(
        stringResource(message),
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
private fun HeroChargerImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(ElectricBlue.copy(alpha = 0.92f), EnergyGreen.copy(alpha = 0.85f)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.EvStation, contentDescription = null, tint = Color.White, modifier = Modifier.size(58.dp))
            Text(stringResource(R.string.charging_location), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
            Text(stringResource(R.string.confirm_conditions), color = Color.White.copy(alpha = 0.82f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun InfoBox(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    AppCard(modifier) {
        Column(Modifier.padding(horizontal = 8.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = EnergyGreen, modifier = Modifier.size(24.dp))
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
            Text(
                value,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
