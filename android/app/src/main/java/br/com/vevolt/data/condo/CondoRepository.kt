package br.com.vevolt.data.condo

import android.content.Context
import androidx.core.content.edit
import br.com.vevolt.BuildConfig
import br.com.vevolt.billing.EntitlementRepository
import br.com.vevolt.data.external.ExternalApiConfig
import java.net.HttpURLConnection
import java.net.URL
import java.time.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CondoRepository(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val identity = EntitlementRepository(appContext)
    private val _state = MutableStateFlow(
        CondoUiState(backendConfigured = ExternalApiConfig.hasSecureBackend)
    )
    val state: StateFlow<CondoUiState> = _state.asStateFlow()

    suspend fun refresh() {
        val token = accessToken
        if (token == null) {
            _state.value = CondoUiState(backendConfigured = ExternalApiConfig.hasSecureBackend)
            return
        }
        execute { loadReady(token) }
    }

    suspend fun createCondo(
        name: String,
        displayName: String,
        unitLabel: String,
        countryCode: String,
        currency: String
    ) = execute {
        val response = request(
            method = "POST",
            path = "/v1/condos",
            body = JSONObject()
                .put("installationId", identity.installationId)
                .put("name", name)
                .put("displayName", displayName)
                .put("unitLabel", unitLabel)
                .put("countryCode", countryCode)
                .put("currency", currency)
        )
        val token = response.requiredText("accessToken")
        preferences.edit(commit = true) { putString(ACCESS_TOKEN_KEY, token) }
        val dashboard = response.getJSONObject("dashboard").toDashboard()
        _state.value = CondoUiState(
            dashboard = dashboard,
            report = fetchReport(token),
            backendConfigured = true
        )
    }

    suspend fun joinCondo(inviteCode: String, displayName: String) = execute {
        val response = request(
            method = "POST",
            path = "/v1/condos/join",
            body = JSONObject()
                .put("installationId", identity.installationId)
                .put("inviteCode", inviteCode)
                .put("displayName", displayName)
        )
        val token = response.requiredText("accessToken")
        preferences.edit(commit = true) { putString(ACCESS_TOKEN_KEY, token) }
        val dashboard = response.getJSONObject("dashboard").toDashboard()
        _state.value = CondoUiState(
            dashboard = dashboard,
            report = fetchReport(token),
            backendConfigured = true
        )
    }

    suspend fun createInvite(unitLabel: String) = execute {
        val token = requireAccessToken()
        val response = request(
            method = "POST",
            path = "/v1/condos/invites",
            body = JSONObject().put("unitLabel", unitLabel),
            token = token
        )
        loadReady(token, inviteCode = response.requiredText("inviteCode"))
    }

    suspend fun addCharger(name: String, connector: String, powerKw: Double, pricePerKwh: Double) = execute {
        val token = requireAccessToken()
        request(
            method = "POST",
            path = "/v1/condos/chargers",
            body = JSONObject()
                .put("name", name)
                .put("connector", connector)
                .put("powerKw", powerKw)
                .put("pricePerKwh", pricePerKwh),
            token = token
        )
        loadReady(token)
    }

    suspend fun reserve(chargerId: String, startsAt: String, durationMinutes: Int) = execute {
        val token = requireAccessToken()
        request(
            method = "POST",
            path = "/v1/condos/reservations",
            body = JSONObject()
                .put("chargerId", chargerId)
                .put("startsAt", startsAt)
                .put("durationMinutes", durationMinutes),
            token = token
        )
        loadReady(token)
    }

    suspend fun cancelReservation(reservationId: String) = execute {
        val token = requireAccessToken()
        request("DELETE", "/v1/condos/reservations/$reservationId", token = token)
        loadReady(token)
    }

    suspend fun startCharging(chargerId: String) = execute {
        val token = requireAccessToken()
        request(
            method = "POST",
            path = "/v1/condos/sessions",
            body = JSONObject().put("chargerId", chargerId),
            token = token
        )
        loadReady(token)
    }

    suspend fun finishCharging(sessionId: String, energyKwh: Double) = execute {
        val token = requireAccessToken()
        request(
            method = "POST",
            path = "/v1/condos/sessions/$sessionId/finish",
            body = JSONObject().put("energyKwh", energyKwh),
            token = token
        )
        loadReady(token)
    }

    suspend fun leave() = execute {
        val token = accessToken
        if (token != null) runCatching { request("DELETE", "/v1/condos/session", token = token) }
        preferences.edit(commit = true) { remove(ACCESS_TOKEN_KEY) }
        _state.value = CondoUiState(backendConfigured = ExternalApiConfig.hasSecureBackend)
    }

    suspend fun deleteMembership() = execute {
        val token = requireAccessToken()
        request("DELETE", "/v1/condos/me", token = token)
        preferences.edit(commit = true) { remove(ACCESS_TOKEN_KEY) }
        _state.value = CondoUiState(backendConfigured = ExternalApiConfig.hasSecureBackend)
    }

    private suspend fun loadReady(token: String, inviteCode: String? = null) {
        val dashboard = request("GET", "/v1/condos/me", token = token).toDashboard()
        _state.value = CondoUiState(
            dashboard = dashboard,
            report = fetchReport(token),
            inviteCode = inviteCode,
            backendConfigured = true
        )
    }

    private suspend fun fetchReport(token: String): CondoReport {
        val month = YearMonth.now().toString()
        return request("GET", "/v1/condos/report?month=$month", token = token).toReport()
    }

    private suspend fun execute(block: suspend () -> Unit) {
        if (!ExternalApiConfig.hasSecureBackend) {
            _state.value = _state.value.copy(
                loading = false,
                errorCode = "CONFIGURATION_REQUIRED",
                backendConfigured = false
            )
            return
        }
        _state.value = _state.value.copy(loading = true, errorCode = null, inviteCode = null)
        try {
            block()
        } catch (error: CondoApiException) {
            if (error.status == HttpURLConnection.HTTP_UNAUTHORIZED) {
                preferences.edit(commit = true) { remove(ACCESS_TOKEN_KEY) }
                _state.value = CondoUiState(errorCode = error.code, backendConfigured = true)
            } else {
                _state.value = _state.value.copy(loading = false, errorCode = error.code)
            }
        } catch (_: Exception) {
            _state.value = _state.value.copy(loading = false, errorCode = "NETWORK_UNAVAILABLE")
        }
    }

    private suspend fun request(
        method: String,
        path: String,
        body: JSONObject? = null,
        token: String? = null
    ): JSONObject = withContext(Dispatchers.IO) {
        if (!path.startsWith("/v1/")) throw IllegalArgumentException("Invalid API path")
        val endpoint = URL("${ExternalApiConfig.backendBaseUrl}$path")
        if (endpoint.protocol != "https") throw IllegalStateException("HTTPS backend required")
        val payload = body?.toString()?.toByteArray(Charsets.UTF_8)
        val connection = (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = REQUEST_TIMEOUT_MILLIS
            readTimeout = REQUEST_TIMEOUT_MILLIS
            instanceFollowRedirects = false
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "VeVolt Android/${BuildConfig.VERSION_NAME}")
            token?.let { setRequestProperty("Authorization", "Bearer $it") }
            if (payload != null) {
                doOutput = true
                setFixedLengthStreamingMode(payload.size)
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
        }
        try {
            if (payload != null) connection.outputStream.use { it.write(payload) }
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText().take(MAX_ERROR_CHARS) }
                val code = runCatching { JSONObject(errorBody.orEmpty()).optString("error") }
                    .getOrNull()
                    .orEmpty()
                    .ifBlank { "HTTP_$responseCode" }
                throw CondoApiException(code, responseCode)
            }
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) return@withContext JSONObject()
            val response = connection.inputStream.bufferedReader().use { reader ->
                val text = reader.readText()
                if (text.length > MAX_RESPONSE_CHARS) throw IllegalStateException("Response too large")
                text
            }
            JSONObject(response)
        } finally {
            connection.disconnect()
        }
    }

    private val accessToken: String?
        get() = preferences.getString(ACCESS_TOKEN_KEY, null)?.takeIf { it.length in 32..128 }

    private fun requireAccessToken(): String = accessToken ?: throw CondoApiException("AUTH_REQUIRED", 401)

    private companion object {
        const val PREFERENCES_NAME = "condo_identity"
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REQUEST_TIMEOUT_MILLIS = 12_000
        const val MAX_RESPONSE_CHARS = 512_000
        const val MAX_ERROR_CHARS = 4_096
    }
}

private class CondoApiException(val code: String, val status: Int) : Exception(code)

private fun JSONObject.toDashboard(): CondoDashboard {
    val condoJson = getJSONObject("condo")
    return CondoDashboard(
        condo = CondoInfo(
            id = condoJson.requiredText("id"),
            name = condoJson.requiredText("name"),
            countryCode = condoJson.requiredText("countryCode"),
            currency = condoJson.requiredText("currency")
        ),
        member = getJSONObject("member").toMember(),
        members = getJSONArray("members").mapObjects { it.toMember() },
        chargers = getJSONArray("chargers").mapObjects { item ->
            CondoCharger(
                id = item.requiredText("id"),
                name = item.requiredText("name"),
                connector = item.requiredText("connector"),
                powerKw = item.getDouble("powerKw"),
                pricePerKwh = item.getDouble("pricePerKwh"),
                enabled = item.optBoolean("enabled", false)
            )
        },
        reservations = getJSONArray("reservations").mapObjects { item ->
            CondoReservation(
                id = item.requiredText("id"),
                chargerId = item.requiredText("chargerId"),
                memberId = item.requiredText("memberId"),
                unitLabel = item.requiredText("unitLabel"),
                startsAt = item.requiredText("startsAt"),
                endsAt = item.requiredText("endsAt"),
                status = item.requiredText("status")
            )
        },
        sessions = getJSONArray("sessions").mapObjects { item ->
            CondoChargingSession(
                id = item.requiredText("id"),
                chargerId = item.requiredText("chargerId"),
                memberId = item.requiredText("memberId"),
                unitLabel = item.requiredText("unitLabel"),
                startedAt = item.requiredText("startedAt"),
                endedAt = item.optString("endedAt").takeIf { it.isNotBlank() },
                energyKwh = item.optDouble("energyKwh", 0.0),
                amount = item.optDouble("amount", 0.0),
                status = item.requiredText("status")
            )
        }
    )
}

private fun JSONObject.toMember() = CondoMember(
    id = requiredText("id"),
    displayName = requiredText("displayName"),
    unitLabel = requiredText("unitLabel"),
    role = CondoRole.valueOf(requiredText("role"))
)

private fun JSONObject.toReport() = CondoReport(
    month = requiredText("month"),
    currency = requiredText("currency"),
    units = getJSONArray("units").mapObjects { item ->
        CondoReportRow(
            unitLabel = item.requiredText("unitLabel"),
            displayName = item.requiredText("displayName"),
            sessions = item.optInt("sessions", 0),
            energyKwh = item.optDouble("energyKwh", 0.0),
            amount = item.optDouble("amount", 0.0)
        )
    },
    totalEnergyKwh = optDouble("totalEnergyKwh", 0.0),
    totalAmount = optDouble("totalAmount", 0.0)
)

private inline fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> = buildList {
    for (index in 0 until length()) add(transform(getJSONObject(index)))
}

private fun JSONObject.requiredText(name: String): String =
    getString(name).trim().takeIf { it.isNotEmpty() } ?: throw IllegalArgumentException("Missing $name")
