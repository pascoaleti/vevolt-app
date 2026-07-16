package br.com.vevolt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import br.com.vevolt.R
import br.com.vevolt.model.Charger
import br.com.vevolt.model.ChargerStatus
import br.com.vevolt.ui.theme.DangerRed
import br.com.vevolt.ui.theme.AccessibleGreen
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen
import br.com.vevolt.ui.theme.SoftGray
import br.com.vevolt.ui.theme.WarningYellow
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatCurrency
import br.com.vevolt.ui.localization.formatDecimal
import br.com.vevolt.ui.localization.localizedLabel
import br.com.vevolt.ui.localization.relativeCommunityTime

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) { content() }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GreenButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccessibleGreen)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OutlineActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(text, color = ElectricBlue, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DarkOutlineActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.82f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatusChip(status: ChargerStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        ChargerStatus.AVAILABLE -> EnergyGreen
        ChargerStatus.OPERATIONAL -> EnergyGreen
        ChargerStatus.BUSY -> WarningYellow
        ChargerStatus.OFFLINE -> DangerRed
        ChargerStatus.UNKNOWN -> Color.Gray
    }
    val textColor = when (status) {
        ChargerStatus.BUSY -> Color(0xFF7A4A00)
        ChargerStatus.AVAILABLE,
        ChargerStatus.OPERATIONAL -> AccessibleGreen
        ChargerStatus.UNKNOWN -> Color(0xFF5F6368)
        ChargerStatus.OFFLINE -> DangerRed
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .defaultMinSize(minWidth = 82.dp)
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Box(Modifier.size(7.dp).clip(RoundedCornerShape(50)).background(color))
        Spacer(Modifier.size(5.dp))
        Text(
            status.localizedLabel(),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun BatteryStatusCard(
    percent: Int,
    autonomyKm: Int,
    modifier: Modifier = Modifier,
    vehicleName: String = ""
) {
    AppCard(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            if (vehicleName.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        vehicleName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        Icons.Rounded.DirectionsCar,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.battery), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.battery_percent, percent), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(Modifier.size(8.dp))
                        Icon(Icons.Rounded.BatteryChargingFull, contentDescription = null, tint = EnergyGreen)
                    }
                    Box(
                        Modifier
                            .padding(top = 6.dp)
                            .size(width = 110.dp, height = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SoftGray)
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(percent.coerceIn(0, 100) / 100f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EnergyGreen)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(stringResource(R.string.range), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
                    Text(stringResource(R.string.distance_km, autonomyKm.toString()), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun ChargerCard(charger: Charger, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val locale = currentLocale()
    val priceLabel = charger.usageCost ?: charger.pricePerKwh?.let {
        stringResource(R.string.price_per_kwh, formatCurrency(it, locale))
    } ?: stringResource(R.string.price_not_provided)
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(EnergyGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        charger.name,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        stringResource(R.string.distance_km, formatDecimal(charger.distanceKm, locale)),
                        color = EnergyGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        if (charger.powerKw > 0) {
                            "${charger.connector.localizedLabel()} · ${stringResource(R.string.power_kw, charger.powerKw)}"
                        } else {
                            "${charger.connector.localizedLabel()} · ${stringResource(R.string.power_not_provided)}"
                        },
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    StatusChip(charger.status)
                }
                charger.rating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(16.dp))
                        Text(
                            " " + stringResource(R.string.rating_reviews, formatDecimal(rating, locale), charger.reviews),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                        )
                    }
                }
                Text(
                    priceLabel,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                val community = charger.communitySummary
                Text(
                    when {
                        community?.lastWorkingAtMillis != null -> stringResource(
                            R.string.community_confirmed_ago,
                            relativeCommunityTime(community.lastWorkingAtMillis)
                        )
                        community?.lastReportedAtMillis != null -> stringResource(
                            R.string.community_reported_ago,
                            relativeCommunityTime(community.lastReportedAtMillis)
                        )
                        else -> stringResource(R.string.community_unconfirmed)
                    },
                    color = ElectricBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                community?.reliabilityScore?.let { score ->
                    Text(
                        "${stringResource(R.string.community_reliability)} ${stringResource(R.string.community_reliability_score, score)}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
