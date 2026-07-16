package br.com.vevolt.data.external

import br.com.vevolt.BuildConfig

object ExternalApiConfig {
    const val MAPLIBRE_NATIVE = "MapLibre Native"
    const val OPEN_FREE_MAP = "OpenFreeMap"
    const val OPEN_CHARGE_MAP = "Open Charge Map"
    const val GOOGLE_PLAY_BILLING = "Google Play Billing"
    const val FIREBASE_CLOUD_MESSAGING = "Firebase Cloud Messaging"

    val openChargeMapApiKey: String = BuildConfig.OPEN_CHARGE_MAP_API_KEY
    val premiumMonthlyProductId: String = BuildConfig.PLAY_BILLING_PREMIUM_MONTHLY_PRODUCT_ID
    val premiumYearlyProductId: String = BuildConfig.PLAY_BILLING_PREMIUM_YEARLY_PRODUCT_ID
    val backendBaseUrl: String = BuildConfig.VEVOLT_BACKEND_BASE_URL.trimEnd('/')
    val premiumSalesEnabled: Boolean = BuildConfig.PREMIUM_SALES_ENABLED

    val hasOpenChargeMap: Boolean get() = openChargeMapApiKey.isNotBlank()
    val hasGooglePlayBilling: Boolean get() = true
    val hasBackend: Boolean get() = backendBaseUrl.isNotBlank()
    val hasSecureBackend: Boolean get() = backendBaseUrl.startsWith("https://")
}
