package br.com.vevolt.data.external

import br.com.vevolt.model.Charger
import br.com.vevolt.model.ChargerStatus
import br.com.vevolt.model.ConnectorType
import android.os.SystemClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.Normalizer
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

sealed interface ChargerFetchResult {
    data object Loading : ChargerFetchResult
    data object ConfigurationRequired : ChargerFetchResult
    data object LocationRequired : ChargerFetchResult
    data object Empty : ChargerFetchResult
    data class Success(val chargers: List<Charger>) : ChargerFetchResult
    data class NetworkError(val responseCode: Int? = null) : ChargerFetchResult
}

class OpenChargeMapRepository {
    private val fetchMutex = Mutex()
    private var cacheEntry: CacheEntry? = null

    suspend fun fetchNearbyChargers(
        latitude: Double,
        longitude: Double
    ): ChargerFetchResult = withContext(Dispatchers.IO) {
        fetchMutex.withLock {
        if (!ExternalApiConfig.hasOpenChargeMap) {
            return@withLock ChargerFetchResult.ConfigurationRequired
        }

        cacheEntry?.takeIf { entry ->
            SystemClock.elapsedRealtime() - entry.createdAtElapsedMillis < CACHE_DURATION_MILLIS &&
                abs(entry.latitude - latitude) < CACHE_COORDINATE_TOLERANCE &&
                abs(entry.longitude - longitude) < CACHE_COORDINATE_TOLERANCE
        }?.let { return@withLock it.result }

        try {
            val url = URL(
                "https://api.openchargemap.io/v3/poi/" +
                    "?output=json" +
                    "&latitude=$latitude" +
                    "&longitude=$longitude" +
                    "&distance=40" +
                    "&distanceunit=KM" +
                    "&maxresults=25" +
                    "&compact=false" +
                    "&verbose=true"
            )
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("X-API-Key", ExternalApiConfig.openChargeMapApiKey)
                setRequestProperty("User-Agent", "VeVolt Android")
            }

            try {
                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    return@withLock ChargerFetchResult.NetworkError(responseCode)
                }
                val chargers = connection.inputStream.bufferedReader().use { reader ->
                    JSONArray(reader.readText()).toChargers()
                }
                val result = if (chargers.isEmpty()) ChargerFetchResult.Empty else ChargerFetchResult.Success(chargers)
                cacheEntry = CacheEntry(latitude, longitude, SystemClock.elapsedRealtime(), result)
                result
            } finally {
                connection.disconnect()
            }
        } catch (_: Exception) {
            ChargerFetchResult.NetworkError()
        }
        }
    }

    private fun JSONArray.toChargers(): List<Charger> = buildList {
        for (index in 0 until length()) {
            val item = optJSONObject(index) ?: continue
            val addressInfo = item.optJSONObject("AddressInfo") ?: continue
            val title = addressInfo.optString("Title").ifBlank { "Open Charge Map" }
            if (title.isNonProductionChargerName()) continue
            val latitude = addressInfo.optDouble("Latitude", Double.NaN)
            val longitude = addressInfo.optDouble("Longitude", Double.NaN)
            if (!latitude.isFinite() || !longitude.isFinite()) continue
            val connections = item.optJSONArray("Connections")
            val dataProvider = item.optJSONObject("DataProvider")
            val userRatings = item.userRatings()
            val connectionInfo = (0 until (connections?.length() ?: 0))
                .mapNotNull { connections?.optJSONObject(it) }
                .maxByOrNull { it.optDouble("PowerKW", 0.0) }

            add(
                Charger(
                    id = item.optInt("ID", index + 10_000),
                    name = title,
                    distanceKm = max(addressInfo.optDouble("Distance", 0.0), 0.0),
                    address = listOfNotNull(
                        normalizeExternalText(addressInfo.optString("AddressLine1")),
                        normalizeExternalText(addressInfo.optString("AddressLine2"))
                    ).joinToString(", "),
                    city = listOfNotNull(
                        normalizeExternalText(addressInfo.optString("Town")),
                        normalizeExternalText(addressInfo.optString("StateOrProvince"))
                    ).joinToString(", "),
                    status = item.toChargerStatus(),
                    powerKw = connectionInfo?.optDouble("PowerKW", 0.0)?.toInt()?.coerceAtLeast(0) ?: 0,
                    connector = connectionInfo.toConnectorType(),
                    pricePerKwh = null,
                    usageCost = item.optionalText("UsageCost")?.take(80),
                    rating = userRatings.takeIf { it.isNotEmpty() }?.average(),
                    reviews = userRatings.size,
                    parkingInfo = item.optJSONObject("UsageType")?.optString("Title").orEmpty()
                        .ifBlank { "" },
                    safetyNote = item.optJSONObject("OperatorInfo")?.optString("Title").orEmpty()
                        .ifBlank { "Open Charge Map" },
                    comments = listOfNotNull(
                        "Open Charge Map",
                        dataProvider?.optString("Title")?.takeIf { it.isNotBlank() },
                        dataProvider?.optString("License")?.takeIf { it.isNotBlank() }
                    ),
                    latitude = latitude,
                    longitude = longitude,
                    countryCode = addressInfo.optJSONObject("Country")?.optString("ISOCode")
                        ?.trim()?.uppercase(Locale.ROOT)?.takeIf { it.length == 2 } ?: "BR"
                )
            )
        }
    }

    private fun org.json.JSONObject?.toConnectorType(): ConnectorType {
        val title = this?.optJSONObject("ConnectionType")?.optString("Title").orEmpty()
        return when {
            title.contains("CCS", ignoreCase = true) -> ConnectorType.CCS2
            title.contains("Type 2", ignoreCase = true) || title.contains("Mennekes", ignoreCase = true) -> ConnectorType.TYPE_2
            title.contains("CHAdeMO", ignoreCase = true) -> ConnectorType.CHADEMO
            else -> ConnectorType.OTHER
        }
    }

    private fun JSONObject.userRatings(): List<Int> {
        val userComments = optJSONArray("UserComments") ?: return emptyList()
        return (0 until userComments.length())
            .mapNotNull { userComments.optJSONObject(it) }
            .map { it.optInt("Rating", 0) }
            .filter { it in 1..5 }
    }

    private fun JSONObject.optionalText(key: String): String? {
        if (!has(key) || isNull(key)) return null
        return normalizeExternalText(optString(key))
    }

    private companion object {
        const val CACHE_DURATION_MILLIS = 60_000L
        const val CACHE_COORDINATE_TOLERANCE = 0.001
    }

    private data class CacheEntry(
        val latitude: Double,
        val longitude: Double,
        val createdAtElapsedMillis: Long,
        val result: ChargerFetchResult
    )
}

internal fun JSONObject.toChargerStatus(): ChargerStatus {
    val statusType = optJSONObject("StatusType")
    val isOperational = statusType
        ?.takeIf { it.has("IsOperational") && !it.isNull("IsOperational") }
        ?.optBoolean("IsOperational")

    return mapOpenChargeMapStatus(isOperational, optInt("StatusTypeID", 0))
}

internal fun mapOpenChargeMapStatus(isOperational: Boolean?, statusTypeId: Int): ChargerStatus {
    return when {
        isOperational == true -> ChargerStatus.OPERATIONAL
        isOperational == false -> ChargerStatus.UNKNOWN
        statusTypeId == OPEN_CHARGE_MAP_OPERATIONAL_STATUS_ID -> ChargerStatus.OPERATIONAL
        statusTypeId == OPEN_CHARGE_MAP_OFFLINE_STATUS_ID -> ChargerStatus.UNKNOWN
        else -> ChargerStatus.UNKNOWN
    }
}

internal fun normalizeExternalText(value: String?): String? = value
    ?.trim()
    ?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }

internal fun String.isNonProductionChargerName(): Boolean {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
        .lowercase(Locale.ROOT)
    return NON_PRODUCTION_NAME_PATTERN.containsMatchIn(normalized)
}

private val NON_PRODUCTION_NAME_PATTERN =
    "(^|[^a-z0-9])(test|teste|demo|dummy|sample|exemplo|homologacao)([^a-z0-9]|$)".toRegex()

private const val OPEN_CHARGE_MAP_OPERATIONAL_STATUS_ID = 50
private const val OPEN_CHARGE_MAP_OFFLINE_STATUS_ID = 100
