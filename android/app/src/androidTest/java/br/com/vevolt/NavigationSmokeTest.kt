package br.com.vevolt

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import br.com.vevolt.billing.BillingUiState
import br.com.vevolt.data.external.ChargerFetchResult
import br.com.vevolt.data.EconomyPreferences
import br.com.vevolt.data.external.MarketplaceFetchResult
import br.com.vevolt.data.external.PostalCodeSearchRequest
import br.com.vevolt.model.Charger
import br.com.vevolt.model.ChargerStatus
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.Vehicle
import br.com.vevolt.model.MarketplaceCategory
import br.com.vevolt.model.MarketplacePlace
import br.com.vevolt.ui.navigation.Screen
import br.com.vevolt.ui.screens.ChargerDetailScreen
import br.com.vevolt.ui.screens.CondoScreen
import br.com.vevolt.data.condo.CondoUiState
import br.com.vevolt.ui.screens.EconomyScreen
import br.com.vevolt.ui.screens.MapScreen
import br.com.vevolt.ui.screens.MarketplaceScreen
import br.com.vevolt.ui.screens.PrivacyScreen
import br.com.vevolt.ui.screens.ProfileScreen
import br.com.vevolt.ui.screens.RouteScreen
import br.com.vevolt.ui.screens.ScanScreen
import br.com.vevolt.ui.theme.VeVoltTheme
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

class NavigationSmokeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mainFlowNavigatesAcrossScreens() {
        var screen by mutableStateOf(Screen.MAP)
        var selectedCharger: Charger = TestFixtures.chargers.first()

        composeRule.setContent {
            VeVoltTheme(darkTheme = false) {
                when (screen) {
                    Screen.MAP -> MapScreen(
                        vehicle = TestFixtures.vehicle,
                        chargers = TestFixtures.chargers,
                        premiumActive = false,
                        onNavigate = { screen = it },
                        onLocationClick = {},
                        onChargerClick = {
                            selectedCharger = it
                            screen = Screen.CHARGER_DETAIL
                        }
                    )
                    Screen.CHARGER_DETAIL -> ChargerDetailScreen(
                        charger = selectedCharger,
                        hasActiveReservation = false,
                        activeSession = null,
                        onBack = { screen = Screen.MAP },
                        onRoute = { screen = Screen.ROUTE },
                        onReserve = {},
                        onScan = { screen = Screen.SCAN }
                    )
                    Screen.ROUTE -> RouteScreen(
                        vehicle = TestFixtures.vehicle,
                        chargers = TestFixtures.chargers,
                        destinationCharger = selectedCharger,
                        onNavigate = { screen = it },
                        onStartRoute = {}
                    )
                    Screen.SCAN -> ScanScreen(
                        charger = selectedCharger,
                        activeSession = null,
                        onStartCharging = {},
                        onFinishCharging = { _, _ -> },
                        onNavigate = { screen = it }
                    )
                    Screen.ECONOMY -> EconomyScreen(
                        chargingSessions = emptyList(),
                        vehicle = TestFixtures.vehicle,
                        premiumActive = false,
                        economyPreferences = EconomyPreferences(),
                        onSaveEconomyPreferences = {},
                        onExportHistory = {},
                        onOpenPremium = { screen = Screen.PROFILE },
                        onNavigate = { screen = it }
                    )
                    Screen.MARKETPLACE -> MarketplaceScreen(
                        vehicle = TestFixtures.vehicle,
                        premiumActive = false,
                        loadState = MarketplaceFetchResult.Success(TestFixtures.marketplacePlaces),
                        searchAreaLabel = null,
                        postalCodeSearching = false,
                        postalCodeError = null,
                        onBack = { screen = Screen.MAP },
                        onRetry = {},
                        onSearchPostalCode = {},
                        onUseDeviceLocation = {},
                        onRoute = {},
                        onCall = {},
                        onOpenWhatsApp = {},
                        onOpenWebsite = {},
                        onOpenPremium = { screen = Screen.PROFILE }
                    )
                    Screen.PROFILE -> ProfileScreen(
                        vehicle = TestFixtures.vehicle,
                        vehicles = listOf(TestFixtures.vehicle),
                        reservations = emptyList(),
                        chargingSessions = emptyList(),
                        billingState = BillingUiState(),
                        onSubscribeMonthly = {},
                        onSubscribeYearly = {},
                        onManageSubscription = {},
                        onEditVehicle = {},
                        onAddVehicle = {},
                        onSelectVehicle = {},
                        onDeleteVehicle = {},
                        onExportBackup = {},
                        onImportBackup = {},
                        onClearLocalData = {},
                        onNavigate = { screen = it },
                        onPrivacy = { screen = Screen.PRIVACY }
                    )
                    Screen.CONDO -> CondoScreen(
                        state = CondoUiState(),
                        premiumActive = true,
                        onBack = { screen = Screen.PROFILE },
                        onRefresh = {},
                        onCreateCondo = { _, _, _, _, _ -> },
                        onJoinCondo = { _, _ -> },
                        onCreateInvite = {},
                        onAddCharger = { _, _, _, _ -> },
                        onReserve = { _, _, _ -> },
                        onCancelReservation = {},
                        onStartCharging = {},
                        onFinishCharging = { _, _ -> },
                        onOpenPremium = {},
                        onLeave = {},
                        onDeleteMembership = {}
                    )
                    Screen.PRIVACY -> PrivacyScreen(
                        onBack = { screen = Screen.PROFILE },
                        onOpenPrivacyPolicy = {},
                        onOpenTerms = {}
                    )
                    Screen.VEHICLE_SETUP -> Unit
                }
            }
        }

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText("Shopping Central"))
        composeRule.onNodeWithText("Shopping Central").assertIsDisplayed().performClick()
        composeRule.onNodeWithText(text(R.string.charger_details)).assertIsDisplayed()

        composeRule.onNodeWithText(text(R.string.get_directions)).performScrollTo().performClick()
        composeRule.onNodeWithText(text(R.string.route_title)).assertIsDisplayed()

        composeRule.onNodeWithText(text(R.string.nav_scan)).performClick()
        composeRule.onNodeWithText(text(R.string.scan_title)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.read_qr)).assertIsDisplayed()

        composeRule.onNodeWithText(text(R.string.nav_savings)).performClick()
        composeRule.onNodeWithText(text(R.string.no_finished_sessions)).assertIsDisplayed()

        composeRule.onNodeWithText(text(R.string.nav_profile)).performClick()
        composeRule.onNodeWithText("BYD Dolphin").assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.privacy_terms)).performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithText(text(R.string.view)).performScrollTo().performClick()
        composeRule.onNodeWithText(text(R.string.open_terms)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun productionEmptyStatesAreExplicit() {
        composeRule.setContent {
            VeVoltTheme(darkTheme = false) {
                MapScreen(
                    vehicle = TestFixtures.vehicle,
                    chargers = emptyList(),
                    premiumActive = false,
                    loadState = ChargerFetchResult.ConfigurationRequired,
                    onNavigate = {},
                    onLocationClick = {},
                    onChargerClick = {}
                )
            }
        }

        composeRule.onNodeWithText(text(R.string.search_unavailable)).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.charger_service_not_configured))
            .performScrollTo().assertIsDisplayed()
    }

    @Test
    fun internationalSearchRequiresRealLocation() {
        composeRule.setContent {
            VeVoltTheme(darkTheme = false) {
                MapScreen(
                    vehicle = TestFixtures.vehicle,
                    chargers = emptyList(),
                    premiumActive = false,
                    loadState = ChargerFetchResult.LocationRequired,
                    onNavigate = {},
                    onLocationClick = {},
                    onChargerClick = {}
                )
            }
        }

        composeRule.onNodeWithText(text(R.string.location_required)).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.location_required_body)).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.try_my_location)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun marketplaceOpensFromOperationalHome() {
        composeRule.setContent {
            VeVoltTheme(darkTheme = false) {
                MarketplaceScreen(
                    vehicle = TestFixtures.vehicle,
                    premiumActive = true,
                    loadState = MarketplaceFetchResult.Success(TestFixtures.marketplacePlaces),
                    searchAreaLabel = null,
                    postalCodeSearching = false,
                    postalCodeError = null,
                    onBack = {},
                    onRetry = {},
                    onSearchPostalCode = {},
                    onUseDeviceLocation = {},
                    onRoute = {},
                    onCall = {},
                    onOpenWhatsApp = {},
                    onOpenWebsite = {},
                    onOpenPremium = {}
                )
            }
        }

        composeRule.onNodeWithText(text(R.string.marketplace_title)).assertIsDisplayed()
        composeRule.onNodeWithText("BYD Test Center").assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.marketplace_ev_declared)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.call)).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.whatsapp)).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.open_website)).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.open_route)).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun marketplacePaginatesPremiumResultsByTen() {
        val places = (1..12).map { index ->
            TestFixtures.marketplacePlaces.first().copy(
                id = "node-$index",
                name = "Test Center $index"
            )
        }

        composeRule.setContent {
            VeVoltTheme(darkTheme = false) {
                MarketplaceScreen(
                    vehicle = TestFixtures.vehicle,
                    premiumActive = true,
                    loadState = MarketplaceFetchResult.Success(places),
                    searchAreaLabel = "Avenida Paulista, Sao Paulo/SP",
                    postalCodeSearching = false,
                    postalCodeError = null,
                    onBack = {},
                    onRetry = {},
                    onSearchPostalCode = {},
                    onUseDeviceLocation = {},
                    onRoute = {},
                    onCall = {},
                    onOpenWhatsApp = {},
                    onOpenWebsite = {},
                    onOpenPremium = {}
                )
            }
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val initialCount = context.getString(R.string.marketplace_showing_count, 10, 12)
        val finalCount = context.getString(R.string.marketplace_showing_count, 12, 12)
        val loadMore = context.resources.getQuantityString(R.plurals.marketplace_load_more, 2, 2)

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(initialCount))
        composeRule.onNodeWithText(initialCount).assertIsDisplayed()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(loadMore))
        composeRule.onNodeWithText(loadMore).assertIsDisplayed().performClick()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(finalCount))
        composeRule.onNodeWithText(finalCount).assertIsDisplayed()
    }

    @Test
    fun marketplaceFormatsFastPostalCodeInputWithoutReorderingDigits() {
        var searchedRequest: PostalCodeSearchRequest? = null
        composeRule.setContent {
            VeVoltTheme(darkTheme = false) {
                MarketplaceScreen(
                    vehicle = TestFixtures.vehicle,
                    premiumActive = false,
                    loadState = MarketplaceFetchResult.LocationRequired,
                    searchAreaLabel = null,
                    postalCodeSearching = false,
                    postalCodeError = null,
                    onBack = {},
                    onRetry = {},
                    onSearchPostalCode = { searchedRequest = it },
                    onUseDeviceLocation = {},
                    onRoute = {},
                    onCall = {},
                    onOpenWhatsApp = {},
                    onOpenWebsite = {},
                    onOpenPremium = {}
                )
            }
        }

        val postalCodeField = composeRule
            .onNodeWithText(text(R.string.marketplace_postal_code_label))
            .performClick()
        "01310930".forEach { digit -> postalCodeField.performTextInput(digit.toString()) }
        composeRule.onNodeWithText("01310-930").assertIsDisplayed()
        composeRule.onNodeWithContentDescription(text(R.string.marketplace_search_postal_code))
            .performClick()
        composeRule.runOnIdle {
            assertEquals(2, searchedRequest?.countryCode?.length)
            assertEquals("01310-930", searchedRequest?.postalCode)
        }
    }

    @Test
    fun premiumProfileShowsGarageAndPortableBackup() {
        val secondVehicle = TestFixtures.vehicle.copy(
            id = "second",
            brand = "GWM",
            model = "Ora 03"
        )
        composeRule.setContent {
            VeVoltTheme(darkTheme = false) {
                ProfileScreen(
                    vehicle = TestFixtures.vehicle,
                    vehicles = listOf(TestFixtures.vehicle, secondVehicle),
                    reservations = emptyList(),
                    chargingSessions = emptyList(),
                    billingState = BillingUiState(premiumActive = true, entitlementVerified = true),
                    onSubscribeMonthly = {},
                    onSubscribeYearly = {},
                    onManageSubscription = {},
                    onEditVehicle = {},
                    onAddVehicle = {},
                    onSelectVehicle = {},
                    onDeleteVehicle = {},
                    onExportBackup = {},
                    onImportBackup = {},
                    onClearLocalData = {},
                    onNavigate = {},
                    onPrivacy = {}
                )
            }
        }

        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(text(R.string.premium_garage_title)))
        composeRule.onNodeWithText(text(R.string.premium_garage_title)).assertIsDisplayed()
        composeRule.onNodeWithText("GWM Ora 03").assertIsDisplayed()
        composeRule.onNode(hasScrollAction()).performScrollToNode(hasText(text(R.string.premium_backup_title)))
        composeRule.onNodeWithText(text(R.string.premium_backup_title)).assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.export_backup)).performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(text(R.string.import_backup)).performScrollTo().assertIsDisplayed()
    }

    private fun text(@StringRes resourceId: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(resourceId)
}

private object TestFixtures {
    val vehicle = Vehicle(
        id = "primary",
        brand = "BYD",
        model = "Dolphin",
        year = "2024",
        rangeKm = 218,
        batteryKwh = 44.9,
        currentBatteryPercent = 62
    )

    val chargers = listOf(
        Charger(
            id = 1,
            name = "Shopping Central",
            distanceKm = 1.8,
            address = "Endereco de teste",
            city = "Sao Paulo, SP",
            status = ChargerStatus.OPERATIONAL,
            powerKw = 50,
            connector = ConnectorType.CCS2,
            pricePerKwh = null,
            rating = null,
            reviews = 0,
            parkingInfo = "",
            safetyNote = "",
            comments = emptyList()
        )
    )

    val marketplacePlaces = listOf(
        MarketplacePlace(
            id = "node-1",
            name = "BYD Test Center",
            category = MarketplaceCategory.DEALER,
            distanceKm = 2.0,
            address = "Sao Paulo, SP",
            brand = "BYD",
            phone = "+55 11 99999-9999",
            whatsApp = "5511999999999",
            website = "https://vevolt.app",
            photoUrl = null,
            openingHours = "Mo-Fr 08:00-18:00",
            electricServiceDeclared = true,
            vehicleBrandMatched = true,
            latitude = -23.55,
            longitude = -46.63
        )
    )
}
