package br.com.vevolt

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import br.com.vevolt.billing.PlayBillingManager
import br.com.vevolt.data.LocationRepository
import br.com.vevolt.data.LocalUserDataRepository
import br.com.vevolt.data.EconomyPreferencesRepository
import br.com.vevolt.data.VehiclePreferencesRepository
import br.com.vevolt.data.community.CommunityRepository
import br.com.vevolt.data.external.OpenChargeMapRepository
import br.com.vevolt.data.external.OpenStreetMapMarketplaceRepository
import br.com.vevolt.data.external.InternationalPostalCodeRepository
import br.com.vevolt.ui.navigation.VeVoltApp
import br.com.vevolt.ui.theme.VeVoltTheme

class MainActivity : ComponentActivity() {
    private val vehicleRepository by lazy { VehiclePreferencesRepository(applicationContext) }
    private val userDataRepository by lazy { LocalUserDataRepository(applicationContext) }
    private val economyPreferencesRepository by lazy { EconomyPreferencesRepository(applicationContext) }
    private val locationRepository by lazy { LocationRepository(applicationContext) }
    private val playBillingManager by lazy { PlayBillingManager(applicationContext) }
    private val openChargeMapRepository by lazy { OpenChargeMapRepository() }
    private val marketplaceRepository by lazy { OpenStreetMapMarketplaceRepository() }
    private val postalCodeRepository by lazy { InternationalPostalCodeRepository() }
    private val communityRepository by lazy { CommunityRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE)
        )
        setContent {
            VeVoltTheme(darkTheme = false) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                ) {
                    VeVoltApp(
                        activity = this@MainActivity,
                        vehicleRepository = vehicleRepository,
                        userDataRepository = userDataRepository,
                        economyPreferencesRepository = economyPreferencesRepository,
                        locationRepository = locationRepository,
                        playBillingManager = playBillingManager,
                        openChargeMapRepository = openChargeMapRepository,
                        marketplaceRepository = marketplaceRepository,
                        postalCodeRepository = postalCodeRepository,
                        communityRepository = communityRepository
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        playBillingManager.destroy()
        super.onDestroy()
    }
}
