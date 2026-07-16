package br.com.vevolt.data.community

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import br.com.vevolt.BuildConfig
import br.com.vevolt.data.external.ExternalApiConfig
import br.com.vevolt.model.Charger
import br.com.vevolt.model.CommunityOutcome
import br.com.vevolt.model.CommunityReport
import br.com.vevolt.model.CommunityReportDraft
import br.com.vevolt.model.CommunityStationState
import br.com.vevolt.model.CommunityStatus
import br.com.vevolt.model.CommunitySummary
import br.com.vevolt.model.communityKey
import br.com.vevolt.model.toChargerStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.util.UUID
import kotlin.math.max

class CommunityRepository(private val context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val _states = MutableStateFlow<Map<String, CommunityStationState>>(emptyMap())
    val states: StateFlow<Map<String, CommunityStationState>> = _states.asStateFlow()

    suspend fun enrich(chargers: List<Charger>): List<Charger> {
        if (!ExternalApiConfig.hasSecureBackend || chargers.isEmpty()) return chargers
        return runCatching {
            val keys = chargers.take(MAX_BATCH_SIZE).joinToString(",") { it.communityKey }
            val response = request("GET", "/v1/community/stations?keys=$keys")
            val summaries = response.getJSONArray("stations").mapObjects { it.toSummary() }
                .associateBy { it.stationKey }
            chargers.map { charger ->
                val summary = summaries[charger.communityKey]
                if (summary == null) charger else {
                    updateState(charger.communityKey) { current ->
                        current.copy(summary = summary, watched = isWatched(charger.communityKey))
                    }
                    charger.copy(
                        status = summary.currentStatus.toChargerStatus(charger.status),
                        pricePerKwh = summary.latestPricePerKwh ?: charger.pricePerKwh,
                        rating = summary.averageRating ?: charger.rating,
                        reviews = if (summary.averageRating != null) summary.reportCount else charger.reviews,
                        communitySummary = summary
                    )
                }
            }
        }.getOrDefault(chargers)
    }

    suspend fun load(charger: Charger) {
        if (!ExternalApiConfig.hasSecureBackend) {
            updateState(charger.communityKey) { it.copy(errorCode = "CONFIGURATION_REQUIRED") }
            return
        }
        updateState(charger.communityKey) { it.copy(loading = true, errorCode = null, messageCode = null) }
        try {
            val response = request(
                "GET",
                "/v1/community/stations/OCM/${charger.id}",
                includeInstallationId = true
            )
            val blocked = blockedAuthors
            val summary = response.getJSONObject("summary").toSummary()
            val reports = response.getJSONArray("reports").mapObjects { it.toReport() }
                .filterNot { it.authorPublicId in blocked }
            _states.value = _states.value + (charger.communityKey to CommunityStationState(
                loading = false,
                summary = summary,
                reports = reports,
                watched = isWatched(charger.communityKey)
            ))
        } catch (error: CommunityApiException) {
            updateState(charger.communityKey) { it.copy(loading = false, errorCode = error.code) }
        } catch (_: Exception) {
            updateState(charger.communityKey) { it.copy(loading = false, errorCode = "NETWORK_UNAVAILABLE") }
        }
    }

    suspend fun submit(charger: Charger, draft: CommunityReportDraft, photo: Uri?) {
        updateState(charger.communityKey) { it.copy(loading = true, errorCode = null, messageCode = null) }
        try {
            val body = JSONObject()
                .put("station", JSONObject()
                    .put("provider", "OCM")
                    .put("id", charger.id.toString())
                    .put("name", charger.name)
                    .put("countryCode", charger.countryCode)
                    .put("latitude", charger.latitude)
                    .put("longitude", charger.longitude))
                .put("outcome", draft.outcome.name)
                .put("pricePerKwh", draft.pricePerKwh ?: JSONObject.NULL)
                .put("rating", draft.rating ?: JSONObject.NULL)
                .put("comment", draft.comment?.takeIf { it.isNotBlank() } ?: JSONObject.NULL)
                .put("policyAccepted", draft.policyAccepted)
            val created = request("POST", "/v1/community/reports", body = body, includeInstallationId = true)
            if (photo != null) {
                val jpeg = sanitizePhoto(photo)
                requestBytes(
                    "PUT",
                    "/v1/community/stations/OCM/${charger.id}/reports/${created.getString("id")}/photo",
                    jpeg
                )
            }
            load(charger)
            updateState(charger.communityKey) { it.copy(messageCode = "REPORT_PUBLISHED") }
        } catch (error: CommunityApiException) {
            updateState(charger.communityKey) { it.copy(loading = false, errorCode = error.code) }
        } catch (_: PhotoException) {
            updateState(charger.communityKey) { it.copy(loading = false, errorCode = "PHOTO_INVALID") }
        } catch (_: Exception) {
            updateState(charger.communityKey) { it.copy(loading = false, errorCode = "NETWORK_UNAVAILABLE") }
        }
    }

    suspend fun flag(charger: Charger, reportId: String) = mutate(charger) {
        request(
            "POST",
            "/v1/community/stations/OCM/${charger.id}/reports/$reportId/flags",
            JSONObject().put("reason", "INAPPROPRIATE"),
            includeInstallationId = true
        )
    }

    suspend fun delete(charger: Charger, reportId: String) = mutate(charger) {
        request(
            "DELETE",
            "/v1/community/stations/OCM/${charger.id}/reports/$reportId",
            includeInstallationId = true
        )
    }

    fun blockAuthor(authorPublicId: String) {
        blockedAuthors = blockedAuthors + authorPublicId
        _states.value = _states.value.mapValues { (_, state) ->
            state.copy(reports = state.reports.filterNot { it.authorPublicId == authorPublicId })
        }
    }

    fun toggleWatch(charger: Charger) {
        val entries = watchEntries.toMutableMap()
        if (entries.remove(charger.communityKey) == null) {
            val recoveredAt = _states.value[charger.communityKey]?.summary?.recoveredAtMillis ?: 0L
            entries[charger.communityKey] = WatchEntry(charger.communityKey, charger.name, recoveredAt, recoveredAt)
        }
        saveWatchEntries(entries)
        updateState(charger.communityKey) { it.copy(watched = charger.communityKey in entries) }
        CommunityAlertWorker.schedule(context, entries.isNotEmpty())
    }

    suspend fun recoveryAlerts(): List<WatchEntry> {
        val entries = watchEntries
        if (entries.isEmpty() || !ExternalApiConfig.hasSecureBackend) return emptyList()
        val keys = entries.keys.take(MAX_BATCH_SIZE).joinToString(",")
        return runCatching {
            val response = request("GET", "/v1/community/stations?keys=$keys")
            response.getJSONArray("stations").mapObjects { it.toSummary() }.mapNotNull { summary ->
                val watched = entries[summary.stationKey] ?: return@mapNotNull null
                val recoveredAt = summary.recoveredAtMillis ?: return@mapNotNull null
                watched.takeIf { recoveredAt > it.lastNotifiedAtMillis && recoveredAt > it.baselineRecoveredAtMillis }
                    ?.copy(lastNotifiedAtMillis = recoveredAt)
            }
        }.getOrDefault(emptyList())
    }

    fun markNotified(entry: WatchEntry) {
        val entries = watchEntries.toMutableMap()
        if (entry.stationKey in entries) {
            entries[entry.stationKey] = entry
            saveWatchEntries(entries)
        }
    }

    private suspend fun mutate(charger: Charger, action: suspend () -> Unit) {
        updateState(charger.communityKey) { it.copy(loading = true, errorCode = null, messageCode = null) }
        try {
            action()
            load(charger)
        } catch (error: CommunityApiException) {
            updateState(charger.communityKey) { it.copy(loading = false, errorCode = error.code) }
        } catch (_: Exception) {
            updateState(charger.communityKey) { it.copy(loading = false, errorCode = "NETWORK_UNAVAILABLE") }
        }
    }

    private suspend fun request(
        method: String,
        path: String,
        body: JSONObject? = null,
        includeInstallationId: Boolean = false
    ): JSONObject = withContext(Dispatchers.IO) {
        val payload = body?.toString()?.toByteArray(Charsets.UTF_8)
        connection(method, path, includeInstallationId).run {
            if (payload != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setFixedLengthStreamingMode(payload.size)
            }
            try {
                if (payload != null) outputStream.use { it.write(payload) }
                readJsonResponse(this)
            } finally {
                disconnect()
            }
        }
    }

    private suspend fun requestBytes(method: String, path: String, payload: ByteArray): JSONObject = withContext(Dispatchers.IO) {
        connection(method, path, includeInstallationId = true).run {
            doOutput = true
            setRequestProperty("Content-Type", "image/jpeg")
            setFixedLengthStreamingMode(payload.size)
            try {
                outputStream.use { it.write(payload) }
                readJsonResponse(this)
            } finally {
                disconnect()
            }
        }
    }

    private fun connection(method: String, path: String, includeInstallationId: Boolean): HttpURLConnection {
        if (!path.startsWith("/v1/")) throw IllegalArgumentException("Invalid API path")
        val endpoint = URL("${ExternalApiConfig.backendBaseUrl}$path")
        if (endpoint.protocol != "https") throw IllegalStateException("HTTPS backend required")
        return (endpoint.openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = REQUEST_TIMEOUT_MILLIS
            readTimeout = REQUEST_TIMEOUT_MILLIS
            instanceFollowRedirects = false
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "VeVolt Android/${BuildConfig.VERSION_NAME}")
            if (includeInstallationId) setRequestProperty("X-VeVolt-Installation-Id", installationId)
        }
    }

    private fun readJsonResponse(connection: HttpURLConnection): JSONObject {
        val responseCode = connection.responseCode
        if (responseCode !in 200..299) {
            val errorText = connection.errorStream?.bufferedReader()?.use { it.readText().take(MAX_ERROR_CHARS) }.orEmpty()
            val code = runCatching { JSONObject(errorText).optString("error") }.getOrNull().orEmpty()
                .ifBlank { "HTTP_$responseCode" }
            throw CommunityApiException(code, responseCode)
        }
        if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) return JSONObject()
        val text = connection.inputStream.bufferedReader().use { it.readText() }
        if (text.length > MAX_RESPONSE_CHARS) throw IllegalStateException("Community response too large")
        return JSONObject(text)
    }

    private fun sanitizePhoto(uri: Uri): ByteArray {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            ?: throw PhotoException()
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw PhotoException()
        var sample = 1
        while (max(bounds.outWidth / sample, bounds.outHeight / sample) > MAX_PHOTO_DIMENSION * 2) sample *= 2
        val decoded = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: throw PhotoException()
        val scale = (MAX_PHOTO_DIMENSION.toFloat() / max(decoded.width, decoded.height)).coerceAtMost(1f)
        val sanitized = if (scale < 1f) {
            Bitmap.createScaledBitmap(decoded, (decoded.width * scale).toInt(), (decoded.height * scale).toInt(), true)
        } else decoded
        return ByteArrayOutputStream().use { output ->
            if (!sanitized.compress(Bitmap.CompressFormat.JPEG, 82, output)) throw PhotoException()
            if (sanitized !== decoded) sanitized.recycle()
            decoded.recycle()
            output.toByteArray().takeIf { it.size <= MAX_PHOTO_BYTES } ?: throw PhotoException()
        }
    }

    private fun updateState(key: String, transform: (CommunityStationState) -> CommunityStationState) {
        _states.value = _states.value + (key to transform(_states.value[key] ?: CommunityStationState()))
    }

    private val installationId: String
        get() = preferences.getString(INSTALLATION_ID_KEY, null)?.takeIf { it.length >= 16 }
            ?: UUID.randomUUID().toString().also {
                preferences.edit().putString(INSTALLATION_ID_KEY, it).apply()
            }

    private var blockedAuthors: Set<String>
        get() = preferences.getStringSet(BLOCKED_AUTHORS_KEY, emptySet())?.toSet().orEmpty()
        set(value) { preferences.edit().putStringSet(BLOCKED_AUTHORS_KEY, value).apply() }

    private val watchEntries: Map<String, WatchEntry>
        get() = preferences.getStringSet(WATCHED_STATIONS_KEY, emptySet()).orEmpty()
            .mapNotNull(::parseWatchEntry)
            .associateBy { it.stationKey }

    private fun isWatched(stationKey: String) = stationKey in watchEntries

    private fun saveWatchEntries(entries: Map<String, WatchEntry>) {
        preferences.edit().putStringSet(WATCHED_STATIONS_KEY, entries.values.map { it.serialize() }.toSet()).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "community_identity"
        private const val INSTALLATION_ID_KEY = "installation_id"
        private const val BLOCKED_AUTHORS_KEY = "blocked_authors"
        private const val WATCHED_STATIONS_KEY = "watched_stations"
        private const val REQUEST_TIMEOUT_MILLIS = 12_000
        private const val MAX_RESPONSE_CHARS = 768_000
        private const val MAX_ERROR_CHARS = 4_096
        private const val MAX_BATCH_SIZE = 25
        private const val MAX_PHOTO_DIMENSION = 1600
        private const val MAX_PHOTO_BYTES = 2 * 1024 * 1024
    }
}

data class WatchEntry(
    val stationKey: String,
    val stationName: String,
    val baselineRecoveredAtMillis: Long,
    val lastNotifiedAtMillis: Long
) {
    fun serialize() = listOf(
        stationKey,
        stationName.replace('|', ' '),
        baselineRecoveredAtMillis,
        lastNotifiedAtMillis
    ).joinToString("|")
}

private fun parseWatchEntry(value: String): WatchEntry? {
    val pieces = value.split('|')
    if (pieces.size != 4 || !pieces[0].startsWith("OCM:")) return null
    return WatchEntry(
        stationKey = pieces[0],
        stationName = pieces[1],
        baselineRecoveredAtMillis = pieces[2].toLongOrNull() ?: return null,
        lastNotifiedAtMillis = pieces[3].toLongOrNull() ?: return null
    )
}

private fun JSONObject.toSummary() = CommunitySummary(
    stationKey = getString("stationKey"),
    currentStatus = CommunityStatus.valueOf(optString("currentStatus", "UNCONFIRMED")),
    reliabilityScore = nullableInt("reliabilityScore"),
    reportCount = optInt("reportCount", 0),
    averageRating = nullableDouble("averageRating"),
    latestPricePerKwh = nullableDouble("latestPricePerKwh"),
    lastReportedAtMillis = instantMillis("lastReportedAt"),
    lastWorkingAtMillis = instantMillis("lastWorkingAt"),
    recoveredAtMillis = instantMillis("recoveredAt")
)

private fun JSONObject.toReport() = CommunityReport(
    id = getString("id"),
    authorPublicId = getString("authorPublicId"),
    outcome = CommunityOutcome.valueOf(getString("outcome")),
    pricePerKwh = nullableDouble("pricePerKwh"),
    rating = nullableInt("rating"),
    comment = optString("comment").takeIf { it.isNotBlank() },
    photoUrl = optString("photoUrl").takeIf { it.isNotBlank() }?.let {
        if (it.startsWith("/")) "${ExternalApiConfig.backendBaseUrl}$it" else it
    },
    createdAtMillis = Instant.parse(getString("createdAt")).toEpochMilli(),
    isOwn = optBoolean("isOwn", false)
)

private fun JSONObject.instantMillis(key: String): Long? = optString(key).takeIf { it.isNotBlank() }
    ?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() }

private fun JSONObject.nullableDouble(key: String): Double? =
    if (!has(key) || isNull(key)) null else optDouble(key).takeIf { it.isFinite() }

private fun JSONObject.nullableInt(key: String): Int? =
    if (!has(key) || isNull(key)) null else optInt(key)

private inline fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> = buildList {
    for (index in 0 until length()) add(transform(getJSONObject(index)))
}

private class CommunityApiException(val code: String, val status: Int) : Exception(code)
private class PhotoException : Exception()
