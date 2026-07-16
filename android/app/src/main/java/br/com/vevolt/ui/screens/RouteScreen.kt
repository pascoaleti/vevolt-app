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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import br.com.vevolt.R
import br.com.vevolt.model.Charger
import br.com.vevolt.model.Vehicle
import br.com.vevolt.domain.estimateRoute
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.BottomNavBar
import br.com.vevolt.ui.components.PrimaryButton
import br.com.vevolt.ui.navigation.Screen
import br.com.vevolt.ui.theme.AccessibleGreen
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatDecimal

@Composable
fun RouteScreen(
    vehicle: Vehicle,
    chargers: List<Charger>,
    destinationCharger: Charger? = null,
    onNavigate: (Screen) -> Unit,
    onStartRoute: () -> Unit
) {
    val locale = currentLocale()
    val routeTarget = destinationCharger ?: chargers.firstOrNull()
    val plan = remember(vehicle, routeTarget) {
        routeTarget?.let { estimateRoute(vehicle, it) }
    }

    Scaffold(bottomBar = { BottomNavBar(current = Screen.ROUTE, onNavigate = onNavigate) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(stringResource(R.string.route_title), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    stringResource(if (plan == null) R.string.route_choose_point else R.string.route_check_estimate),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f)
                )
            }
            if (plan == null) {
                item {
                    AppCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(stringResource(R.string.route_no_destination), fontWeight = FontWeight.ExtraBold)
                            Text(
                                stringResource(R.string.route_no_destination_body),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f)
                            )
                            PrimaryButton(stringResource(R.string.view_map), Modifier.fillMaxWidth(), onClick = { onNavigate(Screen.MAP) })
                        }
                    }
                }
                item { Spacer(Modifier.height(12.dp)) }
                return@LazyColumn
            }
            item {
                AppCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        RoutePoint(stringResource(R.string.route_from), stringResource(R.string.my_location), true)
                        RoutePoint(stringResource(R.string.route_to), plan.destinationLabel, false)
                    }
                }
            }
            item {
                AppCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.range_estimate), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        RouteMetric(
                            stringResource(R.string.straight_line_distance),
                            stringResource(R.string.distance_km, formatDecimal(plan.distanceKm, locale, 0))
                        )
                        RouteMetric(
                            stringResource(R.string.reported_battery),
                            stringResource(R.string.battery_percent, vehicle.currentBatteryPercent)
                        )
                        RouteMetric(
                            stringResource(R.string.estimated_battery_arrival),
                            stringResource(
                                R.string.battery_and_range,
                                plan.arrivalBatteryPercent,
                                formatDecimal(plan.arrivalRangeKm, locale, 0)
                            )
                        )
                        Text(
                            if (plan.hasEstimatedRange) {
                                stringResource(R.string.range_sufficient)
                            } else {
                                stringResource(R.string.range_insufficient)
                            },
                            color = if (plan.hasEstimatedRange) AccessibleGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            item {
                AppCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFF0D3B8E))
                        Text(
                            stringResource(R.string.route_disclaimer),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .78f)
                        )
                    }
                }
            }
            item {
                PrimaryButton(stringResource(R.string.open_navigation_app), Modifier.fillMaxWidth(), onClick = onStartRoute)
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun RoutePoint(label: String, value: String, origin: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = if (origin) AccessibleGreen else Color.Red)
        Column {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RouteMetric(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f))
        Text(value, fontWeight = FontWeight.Bold)
    }
}
