package br.com.vevolt.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import br.com.vevolt.data.external.ExternalApiConfig
import br.com.vevolt.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PremiumOffer(
    val productId: String,
    val title: String,
    val price: String,
    val details: ProductDetails,
    val offerToken: String
)

data class BillingUiState(
    val connected: Boolean = false,
    val premiumActive: Boolean = false,
    val entitlementVerified: Boolean = false,
    val verificationInProgress: Boolean = false,
    val offers: List<PremiumOffer> = emptyList(),
    val message: String = ""
)

class PlayBillingManager(context: Context) {
    private val appContext = context.applicationContext
    private val entitlementRepository = EntitlementRepository(appContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var verificationJob: Job? = null
    private val _state = MutableStateFlow(BillingUiState(message = appContext.getString(R.string.billing_connecting)))
    val state: StateFlow<BillingUiState> = _state.asStateFlow()

    private val billingClient = BillingClient.newBuilder(appContext)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                handlePurchases(purchases)
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                _state.value = _state.value.copy(message = appContext.getString(R.string.billing_purchase_cancelled))
            } else {
                _state.value = _state.value.copy(message = billingErrorMessage(billingResult))
            }
        }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .enableAutoServiceReconnection()
        .build()

    fun connect() {
        if (billingClient.isReady) {
            queryPremiumOffers()
            queryActiveSubscriptions()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _state.value = _state.value.copy(connected = true, message = appContext.getString(R.string.billing_connected))
                    queryPremiumOffers()
                    queryActiveSubscriptions()
                } else {
                    _state.value = _state.value.copy(connected = false, message = billingErrorMessage(billingResult))
                }
            }

            override fun onBillingServiceDisconnected() {
                _state.value = _state.value.copy(connected = false, message = appContext.getString(R.string.billing_unavailable))
            }
        })
    }

    fun launchPremiumPurchase(activity: Activity, productId: String) {
        if (!ExternalApiConfig.premiumSalesEnabled || !ExternalApiConfig.hasSecureBackend) {
            _state.value = _state.value.copy(message = appContext.getString(R.string.billing_sales_paused))
            return
        }
        val offer = _state.value.offers.firstOrNull { it.productId == productId }
        if (offer == null) {
            _state.value = _state.value.copy(message = appContext.getString(R.string.billing_install_from_play))
            return
        }
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(offer.details)
            .setOfferToken(offer.offerToken)
            .build()
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .setObfuscatedAccountId(entitlementRepository.obfuscatedAccountId)
            .build()
        billingClient.launchBillingFlow(activity, params)
    }

    fun destroy() {
        verificationJob?.cancel()
        scope.cancel()
        billingClient.endConnection()
    }

    fun clearEntitlementData(onComplete: () -> Unit) {
        val installationIdToDelete = entitlementRepository.installationId
        entitlementRepository.clearLocalIdentity()
        _state.value = _state.value.copy(
            premiumActive = false,
            entitlementVerified = true,
            verificationInProgress = false,
            message = appContext.getString(R.string.billing_free_active)
        )
        onComplete()
        if (!billingClient.isReady || !ExternalApiConfig.hasSecureBackend) return
        billingClient.queryPurchasesAsync(
            com.android.billingclient.api.QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { _, purchases ->
            scope.launch {
                purchases.forEach { purchase ->
                    purchase.products.firstOrNull {
                        it == ExternalApiConfig.premiumMonthlyProductId ||
                            it == ExternalApiConfig.premiumYearlyProductId
                    }?.let { productId ->
                        entitlementRepository.delete(
                            productId,
                            purchase.purchaseToken,
                            installationIdToDelete
                        )
                    }
                }
            }
        }
    }

    private fun queryPremiumOffers() {
        if (!ExternalApiConfig.premiumSalesEnabled || !ExternalApiConfig.hasSecureBackend) {
            _state.value = _state.value.copy(offers = emptyList())
            return
        }
        val products = listOf(
            ExternalApiConfig.premiumMonthlyProductId,
            ExternalApiConfig.premiumYearlyProductId
        ).map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }
        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(products).build()
        ) { result, queryResult ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                _state.value = _state.value.copy(message = billingErrorMessage(result))
                return@queryProductDetailsAsync
            }
            val offers = queryResult.productDetailsList.flatMap { details ->
                details.subscriptionOfferDetails.orEmpty().mapNotNull { offer ->
                    val phase = offer.pricingPhases.pricingPhaseList.firstOrNull()
                    phase?.let {
                        PremiumOffer(
                            productId = details.productId,
                            title = details.title,
                            price = it.formattedPrice,
                            details = details,
                            offerToken = offer.offerToken
                        )
                    }
                }
            }
            _state.value = _state.value.copy(
                offers = offers,
                message = if (offers.isEmpty()) {
                    appContext.getString(R.string.billing_play_only)
                } else {
                    appContext.getString(R.string.billing_plans_loaded)
                }
            )
        }
    }

    private fun queryActiveSubscriptions() {
        billingClient.queryPurchasesAsync(
            com.android.billingclient.api.QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases)
            }
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        val premiumPurchases = purchases.filter { purchase ->
            purchase.products.any {
                it == ExternalApiConfig.premiumMonthlyProductId ||
                    it == ExternalApiConfig.premiumYearlyProductId
            }
        }
        val premiumPurchase = premiumPurchases.firstOrNull { purchase ->
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                purchase.products.isNotEmpty()
        }
        if (premiumPurchase == null) {
            val hasPending = premiumPurchases.any { it.purchaseState == Purchase.PurchaseState.PENDING }
            _state.value = _state.value.copy(
                premiumActive = false,
                entitlementVerified = !hasPending,
                verificationInProgress = false,
                message = appContext.getString(
                    if (hasPending) R.string.billing_purchase_pending else R.string.billing_free_active
                )
            )
            return
        }

        val productId = premiumPurchase.products.firstOrNull {
            it == ExternalApiConfig.premiumMonthlyProductId ||
                it == ExternalApiConfig.premiumYearlyProductId
        } ?: return
        if (!ExternalApiConfig.hasSecureBackend) {
            _state.value = _state.value.copy(
                premiumActive = false,
                entitlementVerified = false,
                verificationInProgress = false,
                message = appContext.getString(R.string.billing_backend_required)
            )
            return
        }

        verificationJob?.cancel()
        verificationJob = scope.launch {
            _state.value = _state.value.copy(
                premiumActive = false,
                entitlementVerified = false,
                verificationInProgress = true,
                message = appContext.getString(R.string.billing_verifying)
            )
            when (val result = entitlementRepository.verify(productId, premiumPurchase.purchaseToken)) {
                is EntitlementVerificationResult.Verified -> {
                    val active = result.entitlement.isUsable(productId)
                    _state.value = _state.value.copy(
                        premiumActive = active,
                        entitlementVerified = true,
                        verificationInProgress = false,
                        message = appContext.getString(
                            if (active) R.string.billing_premium_active else R.string.billing_subscription_inactive
                        )
                    )
                }
                is EntitlementVerificationResult.Rejected -> {
                    _state.value = _state.value.copy(
                        premiumActive = false,
                        entitlementVerified = true,
                        verificationInProgress = false,
                        message = appContext.getString(R.string.billing_subscription_inactive)
                    )
                }
                EntitlementVerificationResult.ConfigurationRequired -> {
                    _state.value = _state.value.copy(
                        premiumActive = false,
                        entitlementVerified = false,
                        verificationInProgress = false,
                        message = appContext.getString(R.string.billing_backend_required)
                    )
                }
                EntitlementVerificationResult.Unavailable -> {
                    _state.value = _state.value.copy(
                        premiumActive = false,
                        entitlementVerified = false,
                        verificationInProgress = false,
                        message = appContext.getString(R.string.billing_verification_unavailable)
                    )
                }
            }
        }
    }

    private fun billingErrorMessage(result: BillingResult): String =
        when (result.responseCode) {
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> appContext.getString(R.string.billing_unavailable)
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> appContext.getString(R.string.billing_requires_play)
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> appContext.getString(R.string.billing_item_unavailable)
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> appContext.getString(R.string.billing_build_not_validated)
            else -> appContext.getString(R.string.billing_generic_error)
        }
}
