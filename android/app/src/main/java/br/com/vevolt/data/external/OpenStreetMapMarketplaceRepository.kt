package br.com.vevolt.data.external

import android.os.SystemClock
import br.com.vevolt.model.MarketplaceCategory
import br.com.vevolt.model.MarketplacePlace
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.Normalizer
import java.util.Locale
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

sealed interface MarketplaceFetchResult {
    data object Loading : MarketplaceFetchResult
    data object LocationRequired : MarketplaceFetchResult
    data object Empty : MarketplaceFetchResult
    data class Success(val places: List<MarketplacePlace>) : MarketplaceFetchResult
    data class NetworkError(val responseCode: Int? = null) : MarketplaceFetchResult
}

class OpenStreetMapMarketplaceRepository {
    private val fetchMutex = Mutex()
    private var cacheEntry: CacheEntry? = null

    suspend fun fetchNearbyPlaces(
        latitude: Double,
        longitude: Double,
        vehicleBrand: String
    ): MarketplaceFetchResult = withContext(Dispatchers.IO) {
        fetchMutex.withLock {
            cacheEntry?.takeIf { entry ->
                SystemClock.elapsedRealtime() - entry.createdAtElapsedMillis < CACHE_DURATION_MILLIS &&
                    abs(entry.latitude - latitude) < CACHE_COORDINATE_TOLERANCE &&
                    abs(entry.longitude - longitude) < CACHE_COORDINATE_TOLERANCE &&
                    entry.vehicleBrand.equals(vehicleBrand, ignoreCase = true)
            }?.let { return@withLock it.result }

            val query = buildOverpassQuery(latitude, longitude)
            val body = "data=" + URLEncoder.encode(query, Charsets.UTF_8.name())
            var lastResponseCode: Int? = null
            OVERPASS_ENDPOINTS.forEach { endpoint ->
                try {
                    val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = NETWORK_TIMEOUT_MILLIS
                    readTimeout = NETWORK_TIMEOUT_MILLIS
                    doOutput = true
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "VeVolt Android")
                }
                    try {
                        connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body) }
                        val responseCode = connection.responseCode
                        lastResponseCode = responseCode
                        if (responseCode in 200..299) {
                            val places = connection.inputStream.bufferedReader().use { reader ->
                                JSONObject(reader.readText()).optJSONArray("elements")
                                    .toMarketplacePlaces(latitude, longitude, vehicleBrand)
                            }
                            val result = if (places.isEmpty()) {
                                MarketplaceFetchResult.Empty
                            } else {
                                MarketplaceFetchResult.Success(places)
                            }
                            cacheEntry = CacheEntry(
                                latitude,
                                longitude,
                                vehicleBrand,
                                SystemClock.elapsedRealtime(),
                                result
                            )
                            return@withLock result
                        }
                    } finally {
                        connection.disconnect()
                    }
                } catch (_: Exception) {
                    // Try the next public instance.
                }
            }
            MarketplaceFetchResult.NetworkError(lastResponseCode)
        }
    }

    private fun JSONArray?.toMarketplacePlaces(
        userLatitude: Double,
        userLongitude: Double,
        vehicleBrand: String
    ): List<MarketplacePlace> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                val element = optJSONObject(index) ?: continue
                val tags = element.optJSONObject("tags") ?: continue
                val name = tags.optionalText("name") ?: continue
                val category = marketplaceCategory(tags.optString("shop")) ?: continue
                val latitude = element.coordinate("lat", "center") ?: continue
                val longitude = element.coordinate("lon", "center") ?: continue
                val brand = tags.optionalText("brand") ?: tags.optionalText("operator")
                val phone = tags.optionalText("contact:phone") ?: tags.optionalText("phone")
                val whatsApp = normalizeMarketplaceWhatsApp(
                    value = tags.optionalText("contact:whatsapp") ?: tags.optionalText("whatsapp"),
                    fallbackPhone = phone,
                    countryCode = tags.optionalText("addr:country")
                )
                val website = normalizeMarketplaceWebsite(
                    tags.optionalText("contact:website") ?: tags.optionalText("website")
                )
                val photoUrl = normalizeMarketplacePhoto(
                    image = tags.optionalText("image"),
                    wikimediaCommons = tags.optionalText("wikimedia_commons")
                )
                val address = listOfNotNull(
                    listOfNotNull(
                        tags.optionalText("addr:street"),
                        tags.optionalText("addr:housenumber")
                    ).joinToString(", ").takeIf { it.isNotBlank() },
                    tags.optionalText("addr:suburb"),
                    tags.optionalText("addr:city")
                ).joinToString(" - ")
                add(
                    MarketplacePlace(
                        id = "${element.optString("type")}-${element.optLong("id")}",
                        name = name,
                        category = category,
                        distanceKm = haversineDistanceKm(userLatitude, userLongitude, latitude, longitude),
                        address = address,
                        brand = brand,
                        phone = phone,
                        whatsApp = whatsApp,
                        website = website,
                        photoUrl = photoUrl,
                        openingHours = tags.optionalText("opening_hours"),
                        electricServiceDeclared = tags.declaresElectricService(),
                        vehicleBrandMatched = matchesVehicleBrand(name, brand, vehicleBrand),
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }
        }
            .distinctBy { it.id }
            .filter { place ->
                place.category == MarketplaceCategory.DEALER ||
                    place.electricServiceDeclared ||
                    place.vehicleBrandMatched ||
                    !place.brand.isNullOrBlank()
            }
            .sortedWith(
                compareByDescending<MarketplacePlace> { it.vehicleBrandMatched }
                    .thenByDescending { it.electricServiceDeclared }
                    .thenBy { it.distanceKm }
            )
            .take(MAX_PLACES)
    }

    private fun JSONObject.coordinate(key: String, centerKey: String): Double? {
        val direct = optDouble(key, Double.NaN)
        if (direct.isFinite()) return direct
        val center = optJSONObject(centerKey) ?: return null
        return center.optDouble(key, Double.NaN).takeIf { it.isFinite() }
    }

    private fun JSONObject.optionalText(key: String): String? =
        if (!has(key) || isNull(key)) null else normalizeExternalText(optString(key))

    private fun JSONObject.declaresElectricService(): Boolean {
        return declaresElectricVehicle(
            carType = optString("car:type"),
            electricVehicle = optString("electric_vehicle"),
            vehicleElectric = optString("vehicle:electric")
        )
    }

    private data class CacheEntry(
        val latitude: Double,
        val longitude: Double,
        val vehicleBrand: String,
        val createdAtElapsedMillis: Long,
        val result: MarketplaceFetchResult
    )

    private companion object {
        val OVERPASS_ENDPOINTS = listOf(
            "https://overpass-api.de/api/interpreter",
            "https://overpass.private.coffee/api/interpreter"
        )
        const val NETWORK_TIMEOUT_MILLIS = 12_000
        const val CACHE_DURATION_MILLIS = 10L * 60L * 1_000L
        const val CACHE_COORDINATE_TOLERANCE = 0.01
        const val SEARCH_RADIUS_METERS = 8_000
        const val OVERPASS_RESULT_LIMIT = 80
        const val MAX_PLACES = 80

        fun buildOverpassQuery(latitude: Double, longitude: Double): String =
            "[out:json][timeout:20];" +
                "nwr(around:$SEARCH_RADIUS_METERS,$latitude,$longitude)" +
                "[\"shop\"~\"^(car|car_repair)$\"][\"name\"];" +
                "out center $OVERPASS_RESULT_LIMIT;"
    }
}

internal fun marketplaceCategory(shop: String): MarketplaceCategory? = when (shop) {
    "car" -> MarketplaceCategory.DEALER
    "car_repair" -> MarketplaceCategory.REPAIR
    else -> null
}

internal fun matchesVehicleBrand(name: String, brand: String?, vehicleBrand: String): Boolean {
    val expected = vehicleBrand.normalizedForMatch()
    if (expected.length < 2) return false
    return name.normalizedForMatch().contains(expected) || brand.orEmpty().normalizedForMatch().contains(expected)
}

internal fun normalizeMarketplaceWebsite(value: String?): String? {
    val normalized = normalizeExternalText(value) ?: return null
    return when {
        normalized.startsWith("https://", ignoreCase = true) -> normalized
        normalized.startsWith("http://", ignoreCase = true) -> normalized
        normalized.contains('.') && !normalized.contains(' ') -> "https://$normalized"
        else -> null
    }
}

internal fun normalizeMarketplaceWhatsApp(
    value: String?,
    fallbackPhone: String?,
    countryCode: String?
): String? {
    val normalized = normalizeExternalText(value) ?: return null
    val candidate = if (normalized.equals("yes", ignoreCase = true)) fallbackPhone else normalized
    val digits = candidate.orEmpty().filter(Char::isDigit)
    if (digits.length !in 8..15) return null
    return if (countryCode.equals("BR", ignoreCase = true) && digits.length in 10..11) {
        "55$digits"
    } else {
        digits
    }
}

internal fun normalizeMarketplacePhoto(image: String?, wikimediaCommons: String?): String? {
    normalizeExternalText(image)
        ?.substringBefore(';')
        ?.trim()
        ?.takeIf { it.startsWith("https://", ignoreCase = true) }
        ?.let { return it }

    val commonsValue = normalizeExternalText(wikimediaCommons) ?: return null
    if (commonsValue.startsWith("https://", ignoreCase = true)) return commonsValue
    if (!commonsValue.startsWith("File:", ignoreCase = true)) return null
    val commonsFile = commonsValue.substringAfter(':').trim().takeIf { it.isNotBlank() } ?: return null
    val encodedFile = URLEncoder.encode(commonsFile, Charsets.UTF_8.name()).replace("+", "%20")
    return "https://commons.wikimedia.org/wiki/Special:Redirect/file/$encodedFile"
}

internal fun declaresElectricVehicle(
    carType: String,
    electricVehicle: String,
    vehicleElectric: String
): Boolean {
    val vehicleTypes = carType.normalizedForMatch().split(';', ',', ' ')
    val explicitTags = listOf(electricVehicle, vehicleElectric).map { it.lowercase(Locale.ROOT) }
    return vehicleTypes.any { it == "electric" || it == "eletrico" } ||
        explicitTags.any { it in setOf("yes", "only", "designated") }
}

internal fun haversineDistanceKm(
    latitudeA: Double,
    longitudeA: Double,
    latitudeB: Double,
    longitudeB: Double
): Double {
    val latitudeDelta = Math.toRadians(latitudeB - latitudeA)
    val longitudeDelta = Math.toRadians(longitudeB - longitudeA)
    val a = sin(latitudeDelta / 2).pow(2) +
        cos(Math.toRadians(latitudeA)) * cos(Math.toRadians(latitudeB)) *
        sin(longitudeDelta / 2).pow(2)
    return EARTH_RADIUS_KM * 2 * asin(sqrt(a.coerceIn(0.0, 1.0)))
}

private fun String.normalizedForMatch(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("\\p{M}+".toRegex(), "")
    .lowercase(Locale.ROOT)
    .trim()

private const val EARTH_RADIUS_KM = 6_371.0
