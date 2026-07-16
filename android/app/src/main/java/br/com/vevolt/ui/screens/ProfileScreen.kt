package br.com.vevolt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Garage
import androidx.compose.material.icons.rounded.ImportExport
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import br.com.vevolt.R
import br.com.vevolt.billing.BillingUiState
import br.com.vevolt.data.external.ExternalApiConfig
import br.com.vevolt.data.MAX_GARAGE_VEHICLES
import br.com.vevolt.model.ChargerReservation
import br.com.vevolt.model.ChargingSession
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.Vehicle
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.BottomNavBar
import br.com.vevolt.ui.components.DarkOutlineActionButton
import br.com.vevolt.ui.components.GreenButton
import br.com.vevolt.ui.components.OutlineActionButton
import br.com.vevolt.ui.navigation.Screen
import br.com.vevolt.ui.theme.DeepBlue
import br.com.vevolt.ui.theme.DangerRed
import br.com.vevolt.ui.theme.AccessibleGreen
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatCurrency
import br.com.vevolt.ui.localization.localizedVehicleType
import br.com.vevolt.ui.localization.localizedLabel

@Composable
fun ProfileScreen(
    vehicle: Vehicle,
    vehicles: List<Vehicle>,
    reservations: List<ChargerReservation>,
    chargingSessions: List<ChargingSession>,
    billingState: BillingUiState,
    onSubscribeMonthly: () -> Unit,
    onSubscribeYearly: () -> Unit,
    onManageSubscription: () -> Unit,
    onEditVehicle: () -> Unit,
    onAddVehicle: () -> Unit,
    onSelectVehicle: (String) -> Unit,
    onDeleteVehicle: (String) -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onClearLocalData: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onPrivacy: () -> Unit
) {
    Scaffold(bottomBar = { BottomNavBar(current = Screen.PROFILE, onNavigate = onNavigate) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { ProfileHeader(billingState.premiumActive, onEditVehicle) }
            item {
                Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    VehicleCard(vehicle)
                    if (billingState.premiumActive) {
                        PremiumGarageCard(
                            vehicles = vehicles,
                            activeVehicle = vehicle,
                            onAddVehicle = onAddVehicle,
                            onSelectVehicle = onSelectVehicle,
                            onDeleteVehicle = onDeleteVehicle
                        )
                        PremiumBackupCard(
                            onExportBackup = onExportBackup,
                            onImportBackup = onImportBackup
                        )
                    }
                    if (billingState.premiumActive || ExternalApiConfig.premiumSalesEnabled) {
                        PremiumCard(
                            billingState = billingState,
                            onSubscribeMonthly = onSubscribeMonthly,
                            onSubscribeYearly = onSubscribeYearly,
                            onManageSubscription = onManageSubscription,
                            salesEnabled = ExternalApiConfig.premiumSalesEnabled
                        )
                    }
                    LocalActivityCard(reservations, chargingSessions)
                    CondoEntry { onNavigate(Screen.CONDO) }
                    PrivacyEntry(onPrivacy)
                    LocalDataEntry(onClearLocalData)
                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun CondoEntry(onClick: () -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(Icons.Rounded.Apartment, contentDescription = null, tint = EnergyGreen, modifier = Modifier.size(42.dp))
            Column(Modifier.weight(1f)) {
                Text("VeVolt Condo", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(
                    stringResource(R.string.condo_operational_subtitle),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
                )
            }
            GreenButton(
                stringResource(R.string.condo_open),
                modifier = Modifier.size(width = 98.dp, height = 48.dp),
                onClick = onClick
            )
        }
    }
}

@Composable
private fun PremiumGarageCard(
    vehicles: List<Vehicle>,
    activeVehicle: Vehicle,
    onAddVehicle: () -> Unit,
    onSelectVehicle: (String) -> Unit,
    onDeleteVehicle: (String) -> Unit
) {
    var pendingRemoval by remember { mutableStateOf<Vehicle?>(null) }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.Garage, contentDescription = null, tint = ElectricBlue)
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.premium_garage_title), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(
                        stringResource(R.string.premium_garage_body),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                        fontSize = 13.sp
                    )
                }
                Text("${vehicles.size}/$MAX_GARAGE_VEHICLES", fontWeight = FontWeight.Bold, color = AccessibleGreen)
            }
            vehicles.forEach { garageVehicle ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = garageVehicle.id == activeVehicle.id,
                        onClick = { onSelectVehicle(garageVehicle.id) }
                    )
                    Column(Modifier.weight(1f)) {
                        Text(
                            listOf(garageVehicle.brand, garageVehicle.model).filter { it.isNotBlank() }.joinToString(" "),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.vehicle_connector_summary, garageVehicle.connector.localizedLabel()),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f),
                            fontSize = 12.sp
                        )
                    }
                    if (vehicles.size > 1) {
                        IconButton(onClick = { pendingRemoval = garageVehicle }) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = stringResource(R.string.remove_vehicle),
                                tint = DangerRed
                            )
                        }
                    }
                }
            }
            if (vehicles.size < MAX_GARAGE_VEHICLES) {
                OutlineActionButton(
                    text = stringResource(R.string.add_vehicle),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAddVehicle
                )
            }
        }
    }
    pendingRemoval?.let { garageVehicle ->
        AlertDialog(
            onDismissRequest = { pendingRemoval = null },
            title = { Text(stringResource(R.string.remove_vehicle_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.remove_vehicle_body,
                        listOf(garageVehicle.brand, garageVehicle.model).filter { it.isNotBlank() }.joinToString(" ")
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteVehicle(garageVehicle.id)
                        pendingRemoval = null
                    }
                ) {
                    Text(stringResource(R.string.remove), color = DangerRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRemoval = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun PremiumBackupCard(
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit
) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.ImportExport, contentDescription = null, tint = ElectricBlue)
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.premium_backup_title), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(
                        stringResource(R.string.premium_backup_body),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                        fontSize = 13.sp
                    )
                }
            }
            GreenButton(
                text = stringResource(R.string.export_backup),
                modifier = Modifier.fillMaxWidth(),
                onClick = onExportBackup
            )
            OutlineActionButton(
                text = stringResource(R.string.import_backup),
                modifier = Modifier.fillMaxWidth(),
                onClick = onImportBackup
            )
        }
    }
}

@Composable
private fun ProfileHeader(premiumActive: Boolean, onEditVehicle: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .background(Brush.linearGradient(listOf(ElectricBlue, EnergyGreen)))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(56.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Person, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(34.dp))
                }
                Column {
                    Text(stringResource(R.string.profile_driver), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    Text(stringResource(R.string.saved_on_device), color = Color.White.copy(alpha = .86f), fontSize = 12.sp)
                    Box(Modifier.clip(RoundedCornerShape(20.dp)).background(AccessibleGreen).padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(
                            stringResource(if (premiumActive) R.string.premium_active else R.string.free_plan),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            IconButton(onClick = onEditVehicle) {
                Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.edit_vehicle), tint = Color.White)
            }
        }
    }
}

@Composable
private fun VehicleCard(vehicle: Vehicle) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(stringResource(R.string.my_vehicle), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
                    Text("${vehicle.brand} ${vehicle.model}", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        stringResource(R.string.vehicle_year_type, vehicle.year, localizedVehicleType(vehicle.type)),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
                    )
                }
                Icon(Icons.Rounded.DirectionsCar, contentDescription = null, tint = DeepBlue, modifier = Modifier.size(56.dp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column { Text(stringResource(R.string.battery)); Text(stringResource(R.string.battery_percent, vehicle.currentBatteryPercent), fontWeight = FontWeight.ExtraBold) }
                Column(horizontalAlignment = Alignment.End) { Text(stringResource(R.string.range)); Text(stringResource(R.string.distance_km, vehicle.rangeKm.toString()), fontWeight = FontWeight.ExtraBold) }
            }
        }
    }
}

@Composable
private fun PremiumCard(
    billingState: BillingUiState,
    onSubscribeMonthly: () -> Unit,
    onSubscribeYearly: () -> Unit,
    onManageSubscription: () -> Unit,
    salesEnabled: Boolean
) {
    val monthlyOffer = billingState.offers.firstOrNull {
        it.productId == ExternalApiConfig.premiumMonthlyProductId
    }
    val yearlyOffer = billingState.offers.firstOrNull {
        it.productId == ExternalApiConfig.premiumYearlyProductId
    }
    AppCard(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .background(Brush.linearGradient(listOf(DeepBlue, ElectricBlue)))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFD54F))
                Text("VeVolt Premium", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
            PremiumBenefit(stringResource(R.string.premium_benefit_history))
            PremiumBenefit(stringResource(R.string.premium_benefit_analytics))
            PremiumBenefit(stringResource(R.string.premium_benefit_marketplace))
            PremiumBenefit(stringResource(R.string.premium_benefit_export))
            PremiumBenefit(stringResource(R.string.premium_benefit_garage))
            PremiumBenefit(stringResource(R.string.premium_benefit_recommendation))
            PremiumBenefit(stringResource(R.string.premium_benefit_backup))
            if (!salesEnabled && !billingState.premiumActive) {
                Text(stringResource(R.string.premium_paused), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    stringResource(R.string.premium_paused_body),
                    color = Color.White.copy(alpha = .9f),
                    fontSize = 14.sp
                )
                Text(stringResource(R.string.premium_no_charge), color = Color.White, fontWeight = FontWeight.Bold)
            } else if (billingState.premiumActive) {
                Text(stringResource(R.string.premium_active), color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Text(billingState.message, color = Color.White.copy(alpha = .88f), fontSize = 14.sp)
                DarkOutlineActionButton(
                    text = stringResource(R.string.manage_subscription),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onManageSubscription
                )
            } else {
                Text(monthlyOffer?.price ?: stringResource(R.string.play_product), color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Text(stringResource(R.string.auto_renew_notice), color = Color.White.copy(alpha = .9f))
                GreenButton(stringResource(R.string.subscribe_monthly), modifier = Modifier.fillMaxWidth(), onClick = onSubscribeMonthly)
                DarkOutlineActionButton(
                    text = yearlyOffer?.let { stringResource(R.string.subscribe_yearly_price, it.price) }
                        ?: stringResource(R.string.subscribe_yearly),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onSubscribeYearly
                )
            }
        }
    }
}

@Composable
private fun PremiumBenefit(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = EnergyGreen, modifier = Modifier.size(20.dp))
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
private fun LocalActivityCard(
    reservations: List<ChargerReservation>,
    chargingSessions: List<ChargingSession>
) {
    val locale = currentLocale()
    val activeSession = chargingSessions.firstOrNull { it.status == ChargingSessionStatus.ACTIVE }
    val finishedSessions = chargingSessions.filter { it.status != ChargingSessionStatus.ACTIVE }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.local_activity), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(stringResource(R.string.saved_points_count, reservations.size))
            Text(stringResource(R.string.active_record_name, activeSession?.chargerName ?: stringResource(R.string.none)))
            Text(stringResource(R.string.finished_records_count, finishedSessions.size))
            val total = finishedSessions.sumOf { it.amount }
            if (total > 0.0) {
                Text(stringResource(R.string.total_recorded, formatCurrency(total, locale)))
            }
        }
    }
}

@Composable
private fun PrivacyEntry(onPrivacy: () -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(Icons.Rounded.Security, contentDescription = null, tint = DeepBlue, modifier = Modifier.size(42.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.privacy_terms), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(stringResource(R.string.privacy_links_body), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
            }
            GreenButton(stringResource(R.string.view), modifier = Modifier.size(width = 98.dp, height = 48.dp), onClick = onPrivacy)
        }
    }
}

@Composable
private fun LocalDataEntry(onClearLocalData: () -> Unit) {
    var confirming by remember { mutableStateOf(false) }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Icon(Icons.Rounded.Delete, contentDescription = null, tint = DangerRed, modifier = Modifier.size(42.dp))
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.local_data), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(stringResource(R.string.local_data_body), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
                }
            }
            if (confirming) {
                Text(stringResource(R.string.local_delete_notice), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f))
                GreenButton(stringResource(R.string.cancel), modifier = Modifier.fillMaxWidth(), onClick = { confirming = false })
                OutlineActionButton(stringResource(R.string.confirm_deletion), modifier = Modifier.fillMaxWidth(), onClick = onClearLocalData)
            } else {
                OutlineActionButton(stringResource(R.string.delete_local_data), modifier = Modifier.fillMaxWidth(), onClick = { confirming = true })
            }
        }
    }
}
