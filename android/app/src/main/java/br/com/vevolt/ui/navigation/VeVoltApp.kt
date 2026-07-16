package br.com.vevolt.ui.navigation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import br.com.vevolt.billing.PlayBillingManager
import br.com.vevolt.billing.sharePremiumChargingHistory
import br.com.vevolt.R
import br.com.vevolt.data.LocationRepository
import br.com.vevolt.data.LocalUserDataRepository
import br.com.vevolt.data.EconomyPreferences
import br.com.vevolt.data.EconomyPreferencesRepository
import br.com.vevolt.data.VehiclePreferencesRepository
import br.com.vevolt.data.VehicleGarage
import br.com.vevolt.data.PremiumBackup
import br.com.vevolt.data.MAX_BACKUP_CHARS
import br.com.vevolt.data.decodePremiumBackup
import br.com.vevolt.data.encodePremiumBackup
import br.com.vevolt.data.condo.CondoRepository
import br.com.vevolt.data.community.CommunityRepository
import br.com.vevolt.data.external.ExternalApiConfig
import br.com.vevolt.data.external.ChargerFetchResult
import br.com.vevolt.data.external.OpenChargeMapRepository
import br.com.vevolt.data.external.MarketplaceFetchResult
import br.com.vevolt.data.external.OpenStreetMapMarketplaceRepository
import br.com.vevolt.data.external.InternationalPostalCodeRepository
import br.com.vevolt.data.external.PostalCodeLookupError
import br.com.vevolt.data.external.PostalCodeLookupResult
import br.com.vevolt.data.external.PostalCodeSearchRequest
import br.com.vevolt.model.Charger
import br.com.vevolt.model.CommunityStationState
import br.com.vevolt.model.ChargingSessionStatus
import br.com.vevolt.model.Vehicle
import br.com.vevolt.model.MarketplacePlace
import br.com.vevolt.model.communityKey
import br.com.vevolt.ui.screens.ChargerDetailScreen
import br.com.vevolt.ui.screens.CondoScreen
import br.com.vevolt.ui.screens.EconomyScreen
import br.com.vevolt.ui.screens.MapScreen
import br.com.vevolt.ui.screens.MarketplaceScreen
import br.com.vevolt.ui.screens.PrivacyScreen
import br.com.vevolt.ui.screens.ProfileScreen
import br.com.vevolt.ui.screens.RouteScreen
import br.com.vevolt.ui.screens.ScanScreen
import br.com.vevolt.ui.screens.VehicleSetupScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun VeVoltApp(
    activity: Activity,
    vehicleRepository: VehiclePreferencesRepository,
    userDataRepository: LocalUserDataRepository,
    economyPreferencesRepository: EconomyPreferencesRepository,
    locationRepository: LocationRepository,
    playBillingManager: PlayBillingManager,
    openChargeMapRepository: OpenChargeMapRepository,
    marketplaceRepository: OpenStreetMapMarketplaceRepository,
    postalCodeRepository: InternationalPostalCodeRepository,
    communityRepository: CommunityRepository
) {
    val garage by vehicleRepository.garage.collectAsState(initial = VehicleGarage())
    val savedVehicle = garage.activeVehicle
    val reservations by userDataRepository.reservations.collectAsState(initial = emptyList())
    val chargingSessions by userDataRepository.chargingSessions.collectAsState(initial = emptyList())
    val economyPreferences by economyPreferencesRepository.preferences.collectAsState(
        initial = EconomyPreferences()
    )
    val billingState by playBillingManager.state.collectAsState()
    val communityStates by communityRepository.states.collectAsState()
    val condoRepository = remember(activity) { CondoRepository(activity) }
    val condoState by condoRepository.state.collectAsState()
    var vehicle by remember { mutableStateOf(Vehicle()) }
    var chargers by remember { mutableStateOf<List<Charger>>(emptyList()) }
    var chargerLoadState by remember { mutableStateOf<ChargerFetchResult>(ChargerFetchResult.Loading) }
    var marketplaceLoadState by remember {
        mutableStateOf<MarketplaceFetchResult>(MarketplaceFetchResult.LocationRequired)
    }
    var marketplaceSearchLocation by remember { mutableStateOf<MarketplaceSearchLocation?>(null) }
    var postalCodeSearching by remember { mutableStateOf(false) }
    var postalCodeError by remember { mutableStateOf<PostalCodeLookupError?>(null) }
    var screen by remember { mutableStateOf<Screen?>(null) }
    var selectedCharger by remember { mutableStateOf<Charger?>(null) }
    var vehicleSetupReturnScreen by remember { mutableStateOf(Screen.MAP) }
    var vehicleSetupDraft by remember { mutableStateOf<Vehicle?>(null) }
    val scope = rememberCoroutineScope()
    var pendingBackupPayload by remember { mutableStateOf<String?>(null) }
    val backupExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val payload = pendingBackupPayload
        pendingBackupPayload = null
        if (uri != null && payload != null) {
            runCatching { activity.writeBackup(uri, payload) }
                .onSuccess { Toast.makeText(activity, R.string.backup_exported, Toast.LENGTH_LONG).show() }
                .onFailure { Toast.makeText(activity, R.string.backup_failed, Toast.LENGTH_LONG).show() }
        }
    }
    val backupImportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching { decodePremiumBackup(activity.readBackup(uri)) }
                .onSuccess { backup ->
                    scope.launch {
                        vehicleRepository.replaceGarage(backup.vehicles, backup.activeVehicleId)
                        userDataRepository.replaceAll(backup.reservations, backup.chargingSessions)
                        economyPreferencesRepository.save(backup.economyPreferences)
                        vehicle = backup.vehicles.firstOrNull { it.id == backup.activeVehicleId }
                            ?: backup.vehicles.first()
                        Toast.makeText(activity, R.string.backup_imported, Toast.LENGTH_LONG).show()
                    }
                }
                .onFailure { Toast.makeText(activity, R.string.backup_invalid, Toast.LENGTH_LONG).show() }
        }
    }
    val activeSession = chargingSessions.firstOrNull { it.status == ChargingSessionStatus.ACTIVE }
    val activeVehicleSessions = chargingSessions.filter { session ->
        session.vehicleId == vehicle.id ||
            (session.vehicleId.isBlank() && garage.vehicles.firstOrNull()?.id == vehicle.id)
    }
    fun refreshChargersFromDeviceLocation() {
        scope.launch {
            chargerLoadState = ChargerFetchResult.Loading
            val location = locationRepository.getCurrentLocation()
            val result = if (location != null) {
                openChargeMapRepository.fetchNearbyChargers(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            } else {
                if (ExternalApiConfig.hasOpenChargeMap) {
                    ChargerFetchResult.LocationRequired
                } else {
                    ChargerFetchResult.ConfigurationRequired
                }
            }
            if (result is ChargerFetchResult.Success) {
                val enriched = communityRepository.enrich(result.chargers)
                chargerLoadState = ChargerFetchResult.Success(enriched)
                chargers = enriched
                selectedCharger = enriched.firstOrNull()
            } else {
                chargerLoadState = result
                chargers = emptyList()
                selectedCharger = null
            }
        }
    }
    fun refreshMarketplaceFromDeviceLocation() {
        marketplaceSearchLocation = null
        scope.launch {
            marketplaceLoadState = MarketplaceFetchResult.Loading
            val location = locationRepository.getCurrentLocation()
            marketplaceLoadState = if (location == null) {
                MarketplaceFetchResult.LocationRequired
            } else {
                marketplaceSearchLocation = MarketplaceSearchLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    label = null
                )
                marketplaceRepository.fetchNearbyPlaces(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    vehicleBrand = vehicle.brand
                )
            }
        }
    }
    fun refreshMarketplaceAtSelectedLocation() {
        val location = marketplaceSearchLocation ?: run {
            refreshMarketplaceFromDeviceLocation()
            return
        }
        scope.launch {
            marketplaceLoadState = MarketplaceFetchResult.Loading
            marketplaceLoadState = marketplaceRepository.fetchNearbyPlaces(
                latitude = location.latitude,
                longitude = location.longitude,
                vehicleBrand = vehicle.brand
            )
        }
    }
    fun searchMarketplaceByPostalCode(request: PostalCodeSearchRequest) {
        scope.launch {
            postalCodeSearching = true
            postalCodeError = null
            when (val lookup = postalCodeRepository.lookup(request)) {
                is PostalCodeLookupResult.Success -> {
                    val location = MarketplaceSearchLocation(
                        latitude = lookup.latitude,
                        longitude = lookup.longitude,
                        label = lookup.label
                    )
                    marketplaceSearchLocation = location
                    marketplaceLoadState = MarketplaceFetchResult.Loading
                    marketplaceLoadState = marketplaceRepository.fetchNearbyPlaces(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        vehicleBrand = vehicle.brand
                    )
                }
                is PostalCodeLookupResult.Failure -> postalCodeError = lookup.error
            }
            postalCodeSearching = false
        }
    }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            if (screen == Screen.MARKETPLACE) {
                refreshMarketplaceFromDeviceLocation()
            } else {
                refreshChargersFromDeviceLocation()
            }
        }
    }

    DisposableEffect(Unit) {
        playBillingManager.connect()
        onDispose { playBillingManager.destroy() }
    }

    LaunchedEffect(Unit) {
        val hasVehicleConfigured = vehicleRepository.vehicleConfigured.first()
        screen = if (hasVehicleConfigured) Screen.MAP else Screen.VEHICLE_SETUP
    }

    LaunchedEffect(savedVehicle) {
        vehicle = savedVehicle
    }

    LaunchedEffect(screen) {
        if (screen == Screen.MARKETPLACE) {
            if (locationRepository.hasLocationPermission()) {
                refreshMarketplaceFromDeviceLocation()
            } else {
                marketplaceLoadState = MarketplaceFetchResult.LocationRequired
            }
        }
        if (screen == Screen.CONDO) condoRepository.refresh()
    }

    LaunchedEffect(Unit) {
        val location = if (locationRepository.hasLocationPermission()) {
            locationRepository.getCurrentLocation()
        } else {
            null
        }
        val result = when {
            !ExternalApiConfig.hasOpenChargeMap -> ChargerFetchResult.ConfigurationRequired
            location == null -> ChargerFetchResult.LocationRequired
            else -> openChargeMapRepository.fetchNearbyChargers(location.latitude, location.longitude)
        }
        if (result is ChargerFetchResult.Success) {
            val enriched = communityRepository.enrich(result.chargers)
            chargerLoadState = ChargerFetchResult.Success(enriched)
            chargers = enriched
            selectedCharger = enriched.firstOrNull()
        } else {
            chargerLoadState = result
        }
    }

    val currentCharger = selectedCharger ?: chargers.firstOrNull()

    LaunchedEffect(screen, currentCharger?.id) {
        if (screen == Screen.CHARGER_DETAIL) currentCharger?.let { communityRepository.load(it) }
    }

    fun updateCommunityCharger(charger: Charger) {
        scope.launch {
            val updated = communityRepository.enrich(listOf(charger)).firstOrNull() ?: charger
            chargers = chargers.map { if (it.id == updated.id) updated else it }
            selectedCharger = updated
        }
    }

    val handlesSystemBack = when (screen) {
        Screen.CHARGER_DETAIL,
        Screen.MARKETPLACE,
        Screen.ROUTE,
        Screen.SCAN,
        Screen.ECONOMY,
        Screen.PROFILE,
        Screen.CONDO,
        Screen.PRIVACY -> true
        else -> false
    }

    BackHandler(enabled = handlesSystemBack) {
        screen = when (screen) {
            Screen.CONDO -> Screen.PROFILE
            Screen.PRIVACY -> Screen.PROFILE
            else -> Screen.MAP
        }
    }

    when (screen) {
        null -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        Screen.VEHICLE_SETUP -> VehicleSetupScreen(
            initialVehicle = vehicleSetupDraft ?: vehicle,
            onContinue = { updatedVehicle ->
                scope.launch {
                    vehicle = vehicleRepository.saveVehicle(updatedVehicle)
                    vehicleSetupDraft = null
                    screen = vehicleSetupReturnScreen
                }
            }
        )
        Screen.MAP -> MapScreen(
            vehicle = vehicle,
            chargers = chargers,
            premiumActive = billingState.premiumActive,
            loadState = chargerLoadState,
            onNavigate = { screen = it },
            onChargerClick = { charger -> selectedCharger = charger; screen = Screen.CHARGER_DETAIL },
            onLocationClick = {
                if (locationRepository.hasLocationPermission()) {
                    refreshChargersFromDeviceLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        )
        Screen.MARKETPLACE -> MarketplaceScreen(
            vehicle = vehicle,
            premiumActive = billingState.premiumActive,
            loadState = marketplaceLoadState,
            searchAreaLabel = marketplaceSearchLocation?.label,
            postalCodeSearching = postalCodeSearching,
            postalCodeError = postalCodeError,
            onBack = { screen = Screen.MAP },
            onRetry = { refreshMarketplaceAtSelectedLocation() },
            onSearchPostalCode = { searchMarketplaceByPostalCode(it) },
            onUseDeviceLocation = {
                postalCodeError = null
                if (locationRepository.hasLocationPermission()) {
                    refreshMarketplaceFromDeviceLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            onRoute = { activity.openExternalRoute(it) },
            onCall = { activity.openExternalPhone(it) },
            onOpenWhatsApp = { activity.openExternalWhatsApp(it) },
            onOpenWebsite = { activity.openExternalUrl(it) },
            onOpenPremium = { screen = Screen.PROFILE }
        )
        Screen.CHARGER_DETAIL -> currentCharger?.let { charger ->
            ChargerDetailScreen(
                charger = charger,
                communityState = communityStates[charger.communityKey]
                    ?: CommunityStationState(summary = charger.communitySummary),
                hasActiveReservation = reservations.any {
                    it.chargerId == charger.id
                },
                activeSession = activeSession,
                onBack = { screen = Screen.MAP },
                onRoute = { screen = Screen.ROUTE },
                onReserve = {
                    scope.launch {
                        if (reservations.any { it.chargerId == charger.id }) {
                            userDataRepository.removeSavedCharger(charger.id)
                        } else {
                            userDataRepository.reserve(charger)
                        }
                    }
                },
                onScan = { screen = Screen.SCAN },
                onCommunitySubmit = { draft, photo ->
                    scope.launch {
                        communityRepository.submit(charger, draft, photo)
                        updateCommunityCharger(charger)
                    }
                },
                onCommunityWatch = { communityRepository.toggleWatch(charger) },
                onCommunityFlag = { reportId ->
                    scope.launch {
                        communityRepository.flag(charger, reportId)
                        updateCommunityCharger(charger)
                    }
                },
                onCommunityDelete = { reportId ->
                    scope.launch {
                        communityRepository.delete(charger, reportId)
                        updateCommunityCharger(charger)
                    }
                },
                onCommunityBlock = communityRepository::blockAuthor,
                onOpenTerms = { activity.openExternalUrl("https://vevolt.app/termos") }
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        Screen.ROUTE -> RouteScreen(
            vehicle = vehicle,
            chargers = chargers,
            destinationCharger = selectedCharger,
            onNavigate = { screen = it },
            onStartRoute = { currentCharger?.let { activity.openExternalRoute(it) } }
        )
        Screen.SCAN -> ScanScreen(
            charger = currentCharger,
            activeSession = activeSession,
            onStartCharging = {
                currentCharger?.let { charger ->
                    scope.launch { userDataRepository.startCharging(charger, vehicle.id) }
                }
            },
            onFinishCharging = { energyKwh, amount ->
                scope.launch { userDataRepository.finishActiveCharging(energyKwh, amount) }
            },
            onNavigate = { screen = it }
        )
        Screen.ECONOMY -> EconomyScreen(
            chargingSessions = activeVehicleSessions,
            vehicle = vehicle,
            premiumActive = billingState.premiumActive,
            economyPreferences = economyPreferences,
            onSaveEconomyPreferences = { updated ->
                scope.launch { economyPreferencesRepository.save(updated) }
            },
            onExportHistory = { activity.sharePremiumChargingHistory(chargingSessions) },
            onOpenPremium = { screen = Screen.PROFILE },
            onNavigate = { screen = it }
        )
        Screen.PROFILE -> ProfileScreen(
            vehicle = vehicle,
            vehicles = garage.vehicles,
            reservations = reservations,
            chargingSessions = activeVehicleSessions,
            billingState = billingState,
            onSubscribeMonthly = {
                playBillingManager.launchPremiumPurchase(activity, ExternalApiConfig.premiumMonthlyProductId)
            },
            onSubscribeYearly = {
                playBillingManager.launchPremiumPurchase(activity, ExternalApiConfig.premiumYearlyProductId)
            },
            onManageSubscription = {
                activity.openExternalUrl(
                    "https://play.google.com/store/account/subscriptions?package=br.com.vevolt"
                )
            },
            onEditVehicle = {
                vehicleSetupReturnScreen = Screen.PROFILE
                vehicleSetupDraft = vehicle
                screen = Screen.VEHICLE_SETUP
            },
            onAddVehicle = {
                vehicleSetupReturnScreen = Screen.PROFILE
                vehicleSetupDraft = Vehicle()
                screen = Screen.VEHICLE_SETUP
            },
            onSelectVehicle = { vehicleId ->
                scope.launch { vehicleRepository.selectVehicle(vehicleId) }
            },
            onDeleteVehicle = { vehicleId ->
                scope.launch {
                    userDataRepository.removeVehicleData(vehicleId)
                    vehicleRepository.removeVehicle(vehicleId)
                }
            },
            onExportBackup = {
                pendingBackupPayload = encodePremiumBackup(
                    PremiumBackup(
                        vehicles = garage.vehicles,
                        activeVehicleId = vehicle.id,
                        reservations = reservations,
                        chargingSessions = chargingSessions,
                        economyPreferences = economyPreferences
                    )
                )
                val date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                backupExportLauncher.launch("vevolt-backup-$date.json")
            },
            onImportBackup = {
                backupImportLauncher.launch(arrayOf("application/json", "text/json", "text/plain"))
            },
            onClearLocalData = {
                playBillingManager.clearEntitlementData {
                    scope.launch {
                        userDataRepository.clearAll()
                        economyPreferencesRepository.clearAll()
                        vehicleRepository.clearAll()
                        vehicle = Vehicle()
                        vehicleSetupDraft = null
                        selectedCharger = chargers.firstOrNull()
                        vehicleSetupReturnScreen = Screen.MAP
                        screen = Screen.VEHICLE_SETUP
                    }
                }
            },
            onNavigate = { screen = it },
            onPrivacy = { screen = Screen.PRIVACY }
        )
        Screen.CONDO -> CondoScreen(
            state = condoState,
            premiumActive = billingState.premiumActive,
            onBack = { screen = Screen.PROFILE },
            onRefresh = { scope.launch { condoRepository.refresh() } },
            onCreateCondo = { name, displayName, unit, country, currency ->
                scope.launch { condoRepository.createCondo(name, displayName, unit, country, currency) }
            },
            onJoinCondo = { code, displayName ->
                scope.launch { condoRepository.joinCondo(code, displayName) }
            },
            onCreateInvite = { unit -> scope.launch { condoRepository.createInvite(unit) } },
            onAddCharger = { name, connector, power, price ->
                scope.launch { condoRepository.addCharger(name, connector, power, price) }
            },
            onReserve = { chargerId, startsAt, duration ->
                scope.launch { condoRepository.reserve(chargerId, startsAt, duration) }
            },
            onCancelReservation = { reservationId ->
                scope.launch { condoRepository.cancelReservation(reservationId) }
            },
            onStartCharging = { chargerId ->
                scope.launch { condoRepository.startCharging(chargerId) }
            },
            onFinishCharging = { sessionId, energyKwh ->
                scope.launch { condoRepository.finishCharging(sessionId, energyKwh) }
            },
            onOpenPremium = { screen = Screen.PROFILE },
            onLeave = { scope.launch { condoRepository.leave() } },
            onDeleteMembership = { scope.launch { condoRepository.deleteMembership() } }
        )
        Screen.PRIVACY -> PrivacyScreen(
            onBack = { screen = Screen.PROFILE },
            onOpenPrivacyPolicy = { activity.openExternalUrl("https://vevolt.app/politica") },
            onOpenTerms = { activity.openExternalUrl("https://vevolt.app/termos") }
        )
    }
}

private data class MarketplaceSearchLocation(
    val latitude: Double,
    val longitude: Double,
    val label: String?
)

private fun Activity.openExternalRoute(charger: Charger) {
    val coordinates = "${charger.latitude},${charger.longitude}"
    val uri = "geo:$coordinates?q=$coordinates(${Uri.encode(charger.name)})".toUri()
    openExternalIntent(Intent(Intent.ACTION_VIEW, uri), getString(R.string.open_route_chooser))
}

private fun Activity.writeBackup(uri: Uri, payload: String) {
    require(payload.length <= MAX_BACKUP_CHARS)
    contentResolver.openOutputStream(uri, "wt")?.bufferedWriter(Charsets.UTF_8)?.use { writer ->
        writer.write(payload)
    } ?: error("Unable to open backup destination")
}

private fun Activity.readBackup(uri: Uri): String {
    val result = StringBuilder()
    contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { reader ->
        val buffer = CharArray(8_192)
        while (true) {
            val count = reader.read(buffer)
            if (count < 0) break
            require(result.length + count <= MAX_BACKUP_CHARS)
            result.append(buffer, 0, count)
        }
    } ?: error("Unable to open backup source")
    return result.toString()
}

private fun Activity.openExternalRoute(place: MarketplacePlace) {
    val coordinates = "${place.latitude},${place.longitude}"
    val uri = "geo:$coordinates?q=$coordinates(${Uri.encode(place.name)})".toUri()
    openExternalIntent(Intent(Intent.ACTION_VIEW, uri), getString(R.string.open_route_chooser))
}

private fun Activity.openExternalPhone(phone: String) {
    openExternalIntent(
        Intent(Intent.ACTION_DIAL, "tel:${Uri.encode(phone)}".toUri()),
        getString(R.string.call_chooser)
    )
}

private fun Activity.openExternalWhatsApp(phone: String) {
    openExternalIntent(
        Intent(Intent.ACTION_VIEW, "https://wa.me/${Uri.encode(phone)}".toUri()),
        getString(R.string.open_whatsapp_chooser)
    )
}

private fun Activity.openExternalUrl(url: String) {
    openExternalIntent(Intent(Intent.ACTION_VIEW, url.toUri()), getString(R.string.open_link_chooser))
}

private fun Activity.openExternalIntent(intent: Intent, title: String) {
    val chooser = Intent.createChooser(intent, title)
    runCatching { startActivity(chooser) }
        .onFailure {
            Toast.makeText(this, getString(R.string.no_app_available), Toast.LENGTH_LONG).show()
        }
}
