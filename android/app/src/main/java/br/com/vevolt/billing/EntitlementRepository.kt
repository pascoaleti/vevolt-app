package br.com.vevolt.billing

import android.content.Context
import androidx.core.content.edit
import br.com.vevolt.data.external.ExternalApiConfig
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class ServerEntitlement(
    val premiumActive: Boolean,
    val productId: String?,
    val state: String,
    val expiresAt: Instant?,
    val verifiedAt: Instant,
    val refreshAfterSeconds: Long,
    val testPurchase: Boolean
) {
    fun isUsable(requestedProductId: String, now: Instant = Instant.now()): Boolean {
        if (!premiumActive || productId != requestedProductId) return false
        val expiry = expiresAt ?: return false
        if (!expiry.isAfter(now)) return false
        if (verifiedAt.isAfter(now.plusSeconds(MAX_CLOCK_SKEW_SECONDS))) return false
        return verifiedAt.plusSeconds(refreshAfterSeconds + MAX_CLOCK_SKEW_SECONDS).isAfter(now)
    }

    private companion object {
        const val MAX_CLOCK_SKEW_SECONDS = 300L
    }
}

sealed interface EntitlementVerificationResult {
    data class Verified(val entitlement: ServerEntitlement) : EntitlementVerificationResult
    data class Rejected(val responseCode: Int) : EntitlementVerificationResult
    data object ConfigurationRequired : EntitlementVerificationResult
    data object Unavailable : EntitlementVerificationResult
}

class EntitlementRepository(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    val installationId: String
        get() = synchronized(preferences) {
            preferences.getString(INSTALLATION_ID_KEY, null)
                ?: UUID.randomUUID().toString().replace("-", "").also { generated ->
                    preferences.edit { putString(INSTALLATION_ID_KEY, generated) }
                }
        }

    val obfuscatedAccountId: String get() = installationId.sha256Hex()

    fun clearLocalIdentity() {
        preferences.edit { remove(INSTALLATION_ID_KEY) }
    }

    suspend fun verify(productId: String, purchaseToken: String): EntitlementVerificationResult =
        withContext(Dispatchers.IO) {
            val baseUrl = ExternalApiConfig.backendBaseUrl
            if (!ExternalApiConfig.hasSecureBackend) {
                return@withContext EntitlementVerificationResult.ConfigurationRequired
            }
            val endpoint = runCatching { URL("$baseUrl/v1/entitlements/verify") }.getOrNull()
                ?: return@withContext EntitlementVerificationResult.ConfigurationRequired
            if (endpoint.protocol != "https") {
                return@withContext EntitlementVerificationResult.ConfigurationRequired
            }

            try {
                val payload = JSONObject()
                    .put("packageName", appContext.packageName)
                    .put("productId", productId)
                    .put("purchaseToken", purchaseToken)
                    .put("installationId", installationId)
                    .toString()
                    .toByteArray(Charsets.UTF_8)
                val connection = (endpoint.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = REQUEST_TIMEOUT_MILLIS
                    readTimeout = REQUEST_TIMEOUT_MILLIS
                    instanceFollowRedirects = false
                    doOutput = true
                    setFixedLengthStreamingMode(payload.size)
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "VeVolt Android/${br.com.vevolt.BuildConfig.VERSION_NAME}")
                }
                try {
                    connection.outputStream.use { it.write(payload) }
                    val responseCode = connection.responseCode
                    if (responseCode !in 200..299) {
                        return@withContext if (responseCode in 400..499) {
                            EntitlementVerificationResult.Rejected(responseCode)
                        } else {
                            EntitlementVerificationResult.Unavailable
                        }
                    }
                    val response = connection.inputStream.bufferedReader().use { reader ->
                        reader.readText().take(MAX_RESPONSE_CHARS)
                    }
                    val entitlement = JSONObject(response).toServerEntitlement()
                    EntitlementVerificationResult.Verified(entitlement)
                } finally {
                    connection.disconnect()
                }
            } catch (_: Exception) {
                EntitlementVerificationResult.Unavailable
            }
        }

    suspend fun delete(
        productId: String,
        purchaseToken: String,
        installationIdToDelete: String
    ): Boolean = withContext(Dispatchers.IO) {
        if (!ExternalApiConfig.hasSecureBackend) return@withContext false
        val endpoint = runCatching {
            URL("${ExternalApiConfig.backendBaseUrl}/v1/entitlements/delete")
        }.getOrNull() ?: return@withContext false
        if (endpoint.protocol != "https") return@withContext false
        try {
            val payload = JSONObject()
                .put("packageName", appContext.packageName)
                .put("productId", productId)
                .put("purchaseToken", purchaseToken)
                .put("installationId", installationIdToDelete)
                .toString()
                .toByteArray(Charsets.UTF_8)
            val connection = (endpoint.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = REQUEST_TIMEOUT_MILLIS
                readTimeout = REQUEST_TIMEOUT_MILLIS
                instanceFollowRedirects = false
                doOutput = true
                setFixedLengthStreamingMode(payload.size)
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "VeVolt Android/${br.com.vevolt.BuildConfig.VERSION_NAME}")
            }
            try {
                connection.outputStream.use { it.write(payload) }
                connection.responseCode == HttpURLConnection.HTTP_NO_CONTENT
            } finally {
                connection.disconnect()
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun JSONObject.toServerEntitlement(): ServerEntitlement = ServerEntitlement(
        premiumActive = optBoolean("premiumActive", false),
        productId = optString("productId").takeIf { it.isNotBlank() },
        state = optString("state", "SUBSCRIPTION_STATE_UNSPECIFIED"),
        expiresAt = optString("expiresAt").toInstantOrNull(),
        verifiedAt = optString("verifiedAt").toInstantOrNull()
            ?: throw IllegalArgumentException("Missing verifiedAt"),
        refreshAfterSeconds = optLong("refreshAfterSeconds", 0L).coerceIn(0L, MAX_REFRESH_SECONDS),
        testPurchase = optBoolean("testPurchase", false)
    )

    private fun String.toInstantOrNull(): Instant? =
        takeIf { it.isNotBlank() }?.let { runCatching { Instant.parse(it) }.getOrNull() }

    private fun String.sha256Hex(): String = MessageDigest.getInstance("SHA-256")
        .digest(toByteArray(Charsets.UTF_8))
        .joinToString("") { byte -> "%02x".format(byte) }

    private companion object {
        const val PREFERENCES_NAME = "billing_identity"
        const val INSTALLATION_ID_KEY = "installation_id"
        const val REQUEST_TIMEOUT_MILLIS = 10_000
        const val MAX_RESPONSE_CHARS = 16_384
        const val MAX_REFRESH_SECONDS = 86_400L
    }
}
