package br.com.vevolt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import br.com.vevolt.R
import br.com.vevolt.data.EconomyPeriod
import br.com.vevolt.data.EconomyPreferences
import br.com.vevolt.data.EconomySummary
import br.com.vevolt.data.StationEconomySummary
import br.com.vevolt.data.ChargingInsights
import br.com.vevolt.data.buildChargingInsights
import br.com.vevolt.data.buildEconomySummary
import br.com.vevolt.data.buildStationEconomySummaries
import br.com.vevolt.data.filterFinishedSessions
import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.Vehicle
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.BottomNavBar
import br.com.vevolt.ui.components.OutlineActionButton
import br.com.vevolt.ui.components.PrimaryButton
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatCurrency
import br.com.vevolt.ui.localization.formatDecimal
import br.com.vevolt.ui.navigation.Screen
import br.com.vevolt.ui.theme.AccessibleGreen
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun EconomyScreen(
    chargingSessions: List<ChargingSession>,
    vehicle: Vehicle,
    premiumActive: Boolean,
    economyPreferences: EconomyPreferences,
    onSaveEconomyPreferences: (EconomyPreferences) -> Unit,
    onExportHistory: () -> Unit,
    onOpenPremium: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    var period by remember { mutableStateOf(EconomyPeriod.DAYS_30) }
    val filteredSessions = remember(chargingSessions, period) {
        filterFinishedSessions(chargingSessions, if (premiumActive) period else EconomyPeriod.ALL)
    }
    val summary = remember(filteredSessions, vehicle, economyPreferences) {
        buildEconomySummary(filteredSessions, vehicle, economyPreferences)
    }
    val stations = remember(filteredSessions) { buildStationEconomySummaries(filteredSessions) }
    val insights = remember(filteredSessions) { buildChargingInsights(filteredSessions) }

    Scaffold(bottomBar = { BottomNavBar(current = Screen.ECONOMY, onNavigate = onNavigate) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(Modifier.padding(top = 12.dp)) {
                    Text(stringResource(R.string.savings_title), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        stringResource(R.string.economy_subtitle),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .70f)
                    )
                }
            }
            if (premiumActive) item { PeriodSelector(period, onSelect = { period = it }) }
            item { EconomyHero(summary) }
            item { SummaryMetrics(summary) }
            if (premiumActive) {
                item {
                    ComparisonSettingsCard(
                        preferences = economyPreferences,
                        vehicle = vehicle,
                        onSave = onSaveEconomyPreferences
                    )
                }
                item { AdvancedMetricsCard(summary) }
                item { ChargingInsightsCard(insights) }
                item { StationAnalysisCard(stations) }
                item {
                    PremiumHistoryCard(
                        sessions = filteredSessions,
                        onExportHistory = onExportHistory
                    )
                }
            } else {
                item { PremiumEconomyTeaser(onOpenPremium) }
            }
            item { Spacer(Modifier.height(82.dp)) }
        }
    }
}

@Composable
private fun ChargingInsightsCard(insights: ChargingInsights) {
    val locale = currentLocale()
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.charging_insights_title), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(
                stringResource(R.string.charging_insights_body),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .70f),
                fontSize = 13.sp
            )
            if (insights.measuredSessions == 0) {
                Text(stringResource(R.string.charging_insights_empty))
            } else {
                MetricRow(
                    stringResource(R.string.total_charging_time),
                    formatDuration(insights.totalDurationMinutes)
                )
                MetricRow(
                    stringResource(R.string.average_session_time),
                    formatDuration(insights.averageDurationMinutes)
                )
                MetricRow(
                    stringResource(R.string.average_recorded_power),
                    stringResource(R.string.power_kw_decimal, formatDecimal(insights.averageRecordedPowerKw, locale))
                )
                if (insights.freeSessions > 0) {
                    MetricRow(stringResource(R.string.free_sessions), insights.freeSessions.toString())
                }
                insights.mostUsedStation?.let {
                    MetricRow(stringResource(R.string.most_used_station), it)
                }
                insights.lowestAverageCostStation?.let {
                    MetricRow(stringResource(R.string.lowest_cost_station), it)
                }
            }
        }
    }
}

@Composable
private fun PeriodSelector(selected: EconomyPeriod, onSelect: (EconomyPeriod) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        EconomyPeriod.entries.forEach { period ->
            FilterChip(
                selected = selected == period,
                onClick = { onSelect(period) },
                label = {
                    Text(
                        stringResource(
                            when (period) {
                                EconomyPeriod.DAYS_30 -> R.string.period_30_days
                                EconomyPeriod.DAYS_90 -> R.string.period_90_days
                                EconomyPeriod.ALL -> R.string.period_all
                            }
                        )
                    )
                }
            )
        }
    }
}

@Composable
private fun EconomyHero(summary: EconomySummary) {
    val locale = currentLocale()
    AppCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                stringResource(
                    when {
                        summary.sessions == 0 -> R.string.no_finished_sessions
                        summary.comparisonAvailable -> R.string.estimated_savings
                        else -> R.string.recorded_charging_cost
                    }
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .70f)
            )
            Text(
                formatCurrency(if (summary.comparisonAvailable) summary.savings else summary.electricCost, locale),
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EnergyGreen
            )
            Text(
                stringResource(
                    if (summary.comparisonAvailable) R.string.savings_disclaimer
                    else R.string.configure_fuel_for_comparison
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .66f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SummaryMetrics(summary: EconomySummary) {
    val locale = currentLocale()
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        MetricCard(
            stringResource(R.string.registered_energy),
            "${formatDecimal(summary.kwh, locale)} kWh",
            Modifier.weight(1f)
        )
        MetricCard(
            stringResource(R.string.recorded_charging_cost),
            formatCurrency(summary.electricCost, locale),
            Modifier.weight(1f)
        )
        MetricCard(
            stringResource(R.string.sessions),
            summary.sessions.toString(),
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    AppCard(modifier) {
        Column(
            Modifier.fillMaxWidth().height(104.dp).padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ElectricBlue)
        }
    }
}

@Composable
private fun ComparisonSettingsCard(
    preferences: EconomyPreferences,
    vehicle: Vehicle,
    onSave: (EconomyPreferences) -> Unit
) {
    var fuelPrice by remember { mutableStateOf(preferences.fuelPricePerLiter.toEditableNumber()) }
    var gasolineEfficiency by remember { mutableStateOf(preferences.gasolineKmPerLiter.toEditableNumber()) }
    var error by remember { mutableStateOf(false) }
    LaunchedEffect(preferences) {
        fuelPrice = preferences.fuelPricePerLiter.toEditableNumber()
        gasolineEfficiency = preferences.gasolineKmPerLiter.toEditableNumber()
    }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.personalized_comparison), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(
                stringResource(R.string.personalized_comparison_body),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .70f)
            )
            OutlinedTextField(
                value = fuelPrice,
                onValueChange = { fuelPrice = it; error = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.fuel_price_per_liter)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                singleLine = true,
                isError = error
            )
            OutlinedTextField(
                value = gasolineEfficiency,
                onValueChange = { gasolineEfficiency = it; error = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.gasoline_efficiency)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                singleLine = true,
                isError = error
            )
            Text(
                stringResource(
                    if (vehicle.rangeKm > 0 && vehicle.batteryKwh > 0.0) {
                        R.string.vehicle_efficiency_applied
                    } else {
                        R.string.estimated_efficiency_applied
                    }
                ),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .66f)
            )
            PrimaryButton(
                stringResource(R.string.save_comparison),
                Modifier.fillMaxWidth()
            ) {
                val parsedFuel = fuelPrice.parseLocalizedDouble()
                val parsedEfficiency = gasolineEfficiency.parseLocalizedDouble()
                error = parsedFuel == null || parsedFuel <= 0.0 || parsedEfficiency == null || parsedEfficiency <= 0.0
                if (!error) {
                    onSave(EconomyPreferences(parsedFuel!!, parsedEfficiency!!))
                }
            }
            if (error) Text(stringResource(R.string.invalid_comparison_values), color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun AdvancedMetricsCard(summary: EconomySummary) {
    val locale = currentLocale()
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.advanced_metrics), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            MetricRow(stringResource(R.string.average_cost_per_kwh), formatCurrency(summary.averageCostPerKwh, locale))
            MetricRow(stringResource(R.string.average_cost_per_session), formatCurrency(summary.averageCostPerSession, locale))
            MetricRow(stringResource(R.string.estimated_distance), stringResource(R.string.distance_km, summary.estimatedKm.toString()))
            if (summary.comparisonAvailable) {
                MetricRow(stringResource(R.string.estimated_fuel), formatCurrency(summary.gasolineCost, locale))
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .70f), modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold, color = AccessibleGreen)
    }
}

@Composable
private fun StationAnalysisCard(stations: List<StationEconomySummary>) {
    val locale = currentLocale()
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.station_analysis), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            if (stations.isEmpty()) {
                Text(stringResource(R.string.premium_no_sessions))
            } else {
                stations.take(MAX_VISIBLE_STATIONS).forEach { station ->
                    Column {
                        Text(station.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            pluralStringResource(
                                R.plurals.station_summary,
                                station.sessions,
                                station.sessions,
                                formatDecimal(station.kwh, locale),
                                formatCurrency(station.averageCostPerKwh, locale)
                            ),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumHistoryCard(sessions: List<ChargingSession>, onExportHistory: () -> Unit) {
    val locale = currentLocale()
    val formatter = remember(locale) {
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(locale)
            .withZone(ZoneId.systemDefault())
    }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.premium_recent_sessions), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            if (sessions.isEmpty()) {
                Text(stringResource(R.string.premium_no_sessions))
            } else {
                sessions.sortedByDescending { it.startedAtMillis }.take(MAX_VISIBLE_PREMIUM_SESSIONS).forEach { session ->
                    Column(Modifier.fillMaxWidth()) {
                        Text(session.chargerName, fontWeight = FontWeight.Bold)
                        Text(
                            formatter.format(Instant.ofEpochMilli(session.startedAtMillis)),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .66f),
                            fontSize = 12.sp
                        )
                        Text(
                            "${formatDecimal(session.energyKwh, locale)} kWh | ${formatCurrency(session.amount, locale)}",
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                OutlineActionButton(
                    text = stringResource(R.string.premium_export_csv),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onExportHistory
                )
            }
        }
    }
}

@Composable
private fun PremiumEconomyTeaser(onOpenPremium: () -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.premium_economy_title), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(
                stringResource(R.string.premium_economy_body),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .70f)
            )
            PrimaryButton(stringResource(R.string.view_premium), Modifier.fillMaxWidth(), onClick = onOpenPremium)
        }
    }
}

private fun String.parseLocalizedDouble(): Double? = trim().replace(',', '.').toDoubleOrNull()

private fun Double.toEditableNumber(): String = if (this <= 0.0) "" else toString()

private fun formatDuration(totalMinutes: Long): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h ${minutes}min" else "${minutes}min"
}

private const val MAX_VISIBLE_PREMIUM_SESSIONS = 20
private const val MAX_VISIBLE_STATIONS = 8
