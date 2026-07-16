package br.com.vevolt.data.external

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

enum class PostalCodeLookupError {
    INVALID,
    NOT_FOUND,
    COORDINATES_UNAVAILABLE,
    NETWORK
}

data class PostalCodeSearchRequest(
    val countryCode: String,
    val postalCode: String,
    val languageTag: String
)

sealed interface PostalCodeLookupResult {
    data class Success(
        val countryCode: String,
        val postalCode: String,
        val latitude: Double,
        val longitude: Double,
        val label: String
    ) : PostalCodeLookupResult

    data class Failure(val error: PostalCodeLookupError) : PostalCodeLookupResult
}

class InternationalPostalCodeRepository(
    private val backendBaseUrl: String = ExternalApiConfig.backendBaseUrl
) {
    suspend fun lookup(request: PostalCodeSearchRequest): PostalCodeLookupResult =
        withContext(Dispatchers.IO) {
            val normalized = normalizePostalCodeSearchRequest(request)
                ?: return@withContext PostalCodeLookupResult.Failure(PostalCodeLookupError.INVALID)
            if (!backendBaseUrl.startsWith("https://")) {
                return@withContext PostalCodeLookupResult.Failure(PostalCodeLookupError.NETWORK)
            }
            val url = URL(
                "$backendBaseUrl/v1/geocoding/postal-code" +
                    "?country=${normalized.countryCode.urlEncoded()}" +
                    "&postalCode=${normalized.postalCode.urlEncoded()}" +
                    "&language=${normalized.languageTag.urlEncoded()}"
            )
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = NETWORK_TIMEOUT_MILLIS
                readTimeout = NETWORK_TIMEOUT_MILLIS
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "VeVolt Android/${br.com.vevolt.BuildConfig.VERSION_NAME}")
            }
            try {
                when (connection.responseCode) {
                    in 200..299 -> connection.inputStream.bufferedReader().use { reader ->
                        parseInternationalPostalCodeResponse(reader.readText())
                    }
                    HttpURLConnection.HTTP_BAD_REQUEST ->
                        PostalCodeLookupResult.Failure(PostalCodeLookupError.INVALID)
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        PostalCodeLookupResult.Failure(PostalCodeLookupError.NOT_FOUND)
                    HTTP_UNPROCESSABLE_ENTITY ->
                        PostalCodeLookupResult.Failure(PostalCodeLookupError.COORDINATES_UNAVAILABLE)
                    else -> {
                        connection.errorStream?.close()
                        PostalCodeLookupResult.Failure(PostalCodeLookupError.NETWORK)
                    }
                }
            } catch (_: Exception) {
                PostalCodeLookupResult.Failure(PostalCodeLookupError.NETWORK)
            } finally {
                connection.disconnect()
            }
        }

    private companion object {
        const val NETWORK_TIMEOUT_MILLIS = 12_000
        const val HTTP_UNPROCESSABLE_ENTITY = 422
    }
}

internal fun normalizePostalCodeSearchRequest(
    request: PostalCodeSearchRequest
): PostalCodeSearchRequest? {
    val countryCode = request.countryCode.trim().uppercase()
    val postalCode = request.postalCode
        .trim()
        .uppercase()
        .filter { it.isLetterOrDigit() || it == ' ' || it == '-' }
        .replace(Regex("\\s+"), " ")
    val languageTag = request.languageTag.trim().takeIf { it.length in 2..35 } ?: "en"
    return request.copy(
        countryCode = countryCode,
        postalCode = postalCode,
        languageTag = languageTag
    ).takeIf {
        countryCode.matches(Regex("[A-Z]{2}")) && postalCode.length in 2..16
    }
}

internal fun parseInternationalPostalCodeResponse(body: String): PostalCodeLookupResult {
    return try {
        val payload = JSONObject(body)
        buildPostalCodeLookupResult(
            countryCode = payload.optionalString("countryCode"),
            postalCode = payload.optionalString("postalCode"),
            latitude = payload.optionalDouble("latitude"),
            longitude = payload.optionalDouble("longitude"),
            label = payload.optionalString("label")
        )
    } catch (_: Exception) {
        PostalCodeLookupResult.Failure(PostalCodeLookupError.NETWORK)
    }
}

internal fun buildPostalCodeLookupResult(
    countryCode: String?,
    postalCode: String?,
    latitude: Double?,
    longitude: Double?,
    label: String?
): PostalCodeLookupResult {
    if (
        countryCode.isNullOrBlank() ||
        postalCode.isNullOrBlank() ||
        latitude == null ||
        longitude == null ||
        !latitude.isFinite() ||
        !longitude.isFinite()
    ) {
        return PostalCodeLookupResult.Failure(PostalCodeLookupError.COORDINATES_UNAVAILABLE)
    }
    return PostalCodeLookupResult.Success(
        countryCode = countryCode,
        postalCode = postalCode,
        latitude = latitude,
        longitude = longitude,
        label = label?.takeIf { it.isNotBlank() } ?: postalCode
    )
}

private fun String.urlEncoded(): String =
    URLEncoder.encode(this, StandardCharsets.UTF_8.name())

private fun JSONObject.optionalString(key: String): String? =
    optString(key).trim().takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }

private fun JSONObject.optionalDouble(key: String): Double? = when (val value = opt(key)) {
    is Number -> value.toDouble()
    is String -> value.toDoubleOrNull()
    else -> null
}
