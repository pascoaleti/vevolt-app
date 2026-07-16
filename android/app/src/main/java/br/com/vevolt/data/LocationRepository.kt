package br.com.vevolt.data

import android.annotation.SuppressLint
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)

class LocationRepository(private val context: Context) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): UserLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) return@withContext null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null

        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { provider -> runCatching { locationManager.isProviderEnabled(provider) }.getOrDefault(false) }

        val fallbackLocation = providers
            .mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
            .maxByOrNull(Location::getTime)
        if (fallbackLocation != null &&
            System.currentTimeMillis() - fallbackLocation.time <= RECENT_LOCATION_MAX_AGE_MILLIS
        ) {
            return@withContext UserLocation(
                latitude = fallbackLocation.latitude,
                longitude = fallbackLocation.longitude
            )
        }

        val freshLocation = withTimeoutOrNull(CURRENT_LOCATION_TIMEOUT_MILLIS) {
            locationManager.awaitLocation(providers)
        }

        (freshLocation ?: fallbackLocation)
            ?.let { UserLocation(latitude = it.latitude, longitude = it.longitude) }
    }

    fun hasLocationPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private suspend fun LocationManager.awaitLocation(providers: List<String>): Location? =
        suspendCancellableCoroutine { continuation ->
            if (providers.isEmpty()) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    removeUpdates(this)
                    if (continuation.isActive) continuation.resume(location)
                }
            }

            continuation.invokeOnCancellation { removeUpdates(listener) }
            val registered = providers.count { provider ->
                runCatching {
                    requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
                }.isSuccess
            }
            if (registered == 0 && continuation.isActive) continuation.resume(null)
        }

    private companion object {
        const val CURRENT_LOCATION_TIMEOUT_MILLIS = 6_000L
        const val RECENT_LOCATION_MAX_AGE_MILLIS = 5L * 60L * 1_000L
    }
}
