package br.com.vevolt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.vevolt.data.external.ChargerFetchResult
import br.com.vevolt.R
import br.com.vevolt.model.Charger
import br.com.vevolt.model.Vehicle
import br.com.vevolt.domain.ChargerRecommendation
import br.com.vevolt.domain.rankChargersForVehicle
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.BatteryStatusCard
import br.com.vevolt.ui.components.BottomNavBar
import br.com.vevolt.ui.components.ChargerCard
import br.com.vevolt.ui.components.PrimaryButton
import br.com.vevolt.ui.navigation.Screen
import br.com.vevolt.ui.theme.AccessibleGreen
import br.com.vevolt.ui.theme.DangerRed
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatDecimal
import br.com.vevolt.ui.localization.localizedLabel
import kotlinx.coroutines.delay
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

@Composable
fun MapScreen(
    vehicle: Vehicle,
    chargers: List<Charger>,
    premiumActive: Boolean,
    onNavigate: (Screen) -> Unit,
    onChargerClick: (Charger) -> Unit,
    onLocationClick: () -> Unit,
    loadState: ChargerFetchResult = ChargerFetchResult.Success(chargers)
) {
    var visibleChargerLimit by remember { mutableIntStateOf(NEARBY_PAGE_SIZE) }
    LaunchedEffect(chargers) { visibleChargerLimit = NEARBY_PAGE_SIZE }
    val visibleChargers = chargers.take(visibleChargerLimit)
    val recommendation = remember(chargers, vehicle, premiumActive) {
        if (premiumActive) rankChargersForVehicle(chargers, vehicle).firstOrNull() else null
    }
    Scaffold(bottomBar = { BottomNavBar(current = Screen.MAP, onNavigate = onNavigate) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { HomeHeader() }
            item {
                BatteryStatusCard(
                    percent = vehicle.currentBatteryPercent,
                    autonomyKm = vehicle.rangeKm,
                    vehicleName = listOf(vehicle.brand, vehicle.model).filter { it.isNotBlank() }.joinToString(" ")
                )
            }
            item {
                ChargerMap(
                    chargers = chargers,
                    onChargerClick = onChargerClick,
                    onLocationClick = onLocationClick,
                    modifier = Modifier.fillMaxWidth().height(230.dp)
                )
            }
            recommendation?.let { suggestion ->
                item {
                    SmartRecommendationCard(
                        recommendation = suggestion,
                        vehicle = vehicle,
                        onClick = { onChargerClick(suggestion.charger) }
                    )
                }
            }
            item { MarketplaceEntry(onClick = { onNavigate(Screen.MARKETPLACE) }) }
            item { LocationsHeader(chargers.size) }
            when {
                loadState is ChargerFetchResult.Loading -> item {
                    AppCard(Modifier.fillMaxWidth()) {
                        Box(Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ElectricBlue)
                        }
                    }
                }
                chargers.isEmpty() -> item { ChargerEmptyState(loadState, onLocationClick) }
                else -> {
                    item {
                        Text(
                            stringResource(R.string.nearby_showing_count, visibleChargers.size, chargers.size),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .64f),
                            fontSize = 12.sp
                        )
                    }
                    items(visibleChargers.size) { index ->
                        ChargerCard(
                            charger = visibleChargers[index],
                            onClick = { onChargerClick(visibleChargers[index]) }
                        )
                    }
                    if (visibleChargers.size < chargers.size) {
                        item {
                            val nextPageSize = minOf(NEARBY_PAGE_SIZE, chargers.size - visibleChargers.size)
                            PrimaryButton(
                                text = pluralStringResource(
                                    R.plurals.nearby_load_more,
                                    nextPageSize,
                                    nextPageSize
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { visibleChargerLimit += NEARBY_PAGE_SIZE }
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(72.dp)) }
        }
    }
}

private const val NEARBY_PAGE_SIZE = 10

@Composable
private fun SmartRecommendationCard(
    recommendation: ChargerRecommendation,
    vehicle: Vehicle,
    onClick: () -> Unit
) {
    val charger = recommendation.charger
    val locale = currentLocale()
    AppCard(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(ElectricBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color.White)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    stringResource(
                        R.string.smart_recommendation_title,
                        listOf(vehicle.brand, vehicle.model).filter { it.isNotBlank() }.joinToString(" ")
                    ),
                    color = ElectricBlue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
                Text(charger.name, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, maxLines = 1)
                Text(
                    if (charger.powerKw > 0) {
                        stringResource(
                            if (recommendation.connectorCompatible) {
                                R.string.smart_recommendation_compatible
                            } else {
                                R.string.smart_recommendation_alternative
                            },
                            charger.connector.localizedLabel(),
                            formatDecimal(charger.distanceKm, locale),
                            charger.powerKw
                        )
                    } else {
                        stringResource(
                            if (recommendation.connectorCompatible) {
                                R.string.smart_recommendation_compatible_no_power
                            } else {
                                R.string.smart_recommendation_alternative_no_power
                            },
                            charger.connector.localizedLabel(),
                            formatDecimal(charger.distanceKm, locale)
                        )
                    },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = stringResource(R.string.view_details), tint = ElectricBlue)
        }
    }
}

@Composable
private fun MarketplaceEntry(onClick: () -> Unit) {
    AppCard(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(ElectricBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Storefront, contentDescription = null, tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.marketplace_title), fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                Text(
                    stringResource(R.string.marketplace_home_body),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = stringResource(R.string.open_marketplace),
                tint = ElectricBlue
            )
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.vevolt_logo_mark),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ve", color = ElectricBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text("Volt", color = AccessibleGreen, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text(
                stringResource(R.string.brand_tagline),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .68f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LocationsHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            stringResource(if (count == 0) R.string.charging_points else R.string.nearby_points),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (count > 0) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(EnergyGreen.copy(alpha = .12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(Icons.Rounded.Bolt, contentDescription = null, tint = AccessibleGreen, modifier = Modifier.size(16.dp))
                Text(
                    pluralStringResource(R.plurals.locations_found, count, count),
                    color = AccessibleGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ChargerEmptyState(loadState: ChargerFetchResult, onRetry: () -> Unit) {
    val (title, message) = when (loadState) {
        ChargerFetchResult.ConfigurationRequired -> stringResource(R.string.search_unavailable) to
            stringResource(R.string.charger_service_not_configured)
        ChargerFetchResult.LocationRequired -> stringResource(R.string.location_required) to
            stringResource(R.string.location_required_body)
        ChargerFetchResult.Empty -> stringResource(R.string.no_points_found) to
            stringResource(R.string.no_points_nearby)
        is ChargerFetchResult.NetworkError -> stringResource(R.string.charger_search_failed) to
            loadState.responseCode?.let { stringResource(R.string.charger_service_unavailable_code, it) }
                .orEmpty().ifBlank { stringResource(R.string.charger_service_unavailable) }
        is ChargerFetchResult.Success -> stringResource(R.string.no_points_found) to
            stringResource(R.string.charger_source_empty)
        ChargerFetchResult.Loading -> stringResource(R.string.searching_chargers) to stringResource(R.string.please_wait)
    }
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.ExtraBold)
            Text(message, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f))
            if (loadState !is ChargerFetchResult.ConfigurationRequired) {
                PrimaryButton(stringResource(R.string.try_my_location), Modifier.fillMaxWidth(), onClick = onRetry)
            }
        }
    }
}

@Composable
private fun ChargerMap(
    chargers: List<Charger>,
    onChargerClick: (Charger) -> Unit,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FreeChargerMap(chargers, onChargerClick, onLocationClick, modifier)
}

@Composable
private fun FreeChargerMap(
    chargers: List<Charger>,
    onChargerClick: (Charger) -> Unit,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mapView = rememberMapViewWithLifecycle()
    val latestChargers by rememberUpdatedState(chargers)
    val latestOnChargerClick by rememberUpdatedState(onChargerClick)
    var mapController by remember { mutableStateOf<MapLibreMap?>(null) }
    var mapStyle by remember { mutableStateOf<Style?>(null) }
    var mapLoaded by remember { mutableStateOf(false) }
    var mapTimedOut by remember { mutableStateOf(false) }
    DisposableEffect(mapView) {
        val renderingListener = MapView.OnDidFinishRenderingMapListener { fully ->
            if (fully) {
                mapLoaded = true
                mapTimedOut = false
            }
        }
        mapView.addOnDidFinishRenderingMapListener(renderingListener)
        onDispose { mapView.removeOnDidFinishRenderingMapListener(renderingListener) }
    }
    LaunchedEffect(Unit) {
        delay(8_000)
        if (!mapLoaded) mapTimedOut = true
    }

    LaunchedEffect(mapController, mapStyle, chargers) {
        val controller = mapController ?: return@LaunchedEffect
        val style = mapStyle ?: return@LaunchedEffect
        val features = chargers.mapIndexed { index, charger ->
            val position = chargerLatLng(charger, index)
            Feature.fromGeometry(Point.fromLngLat(position.longitude, position.latitude)).apply {
                addNumberProperty(CHARGER_ID_PROPERTY, charger.id)
            }
        }
        val featureCollection = FeatureCollection.fromFeatures(features)
        val existingSource = style.getSourceAs<GeoJsonSource>(CHARGER_SOURCE_ID)
        if (existingSource == null) {
            style.addSource(GeoJsonSource(CHARGER_SOURCE_ID, featureCollection))
            style.addLayer(
                CircleLayer(CHARGER_LAYER_ID, CHARGER_SOURCE_ID).withProperties(
                    circleRadius(9f),
                    circleColor("#087F3C"),
                    circleStrokeColor("#FFFFFF"),
                    circleStrokeWidth(3f)
                )
            )
        } else {
            existingSource.setGeoJson(featureCollection)
        }
        val center = chargers.firstOrNull()?.let { chargerLatLng(it, 0) } ?: WORLD_CENTER
        controller.cameraPosition = CameraPosition.Builder()
            .target(center)
            .zoom(if (chargers.isEmpty()) 1.5 else 11.0)
            .build()
    }

    AppCard(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    mapView.apply {
                        getMapAsync { controller ->
                            controller.uiSettings.isCompassEnabled = false
                            controller.uiSettings.isLogoEnabled = false
                            controller.uiSettings.isAttributionEnabled = false
                            controller.addOnMapClickListener { position ->
                                val screenPoint = controller.projection.toScreenLocation(position)
                                val chargerId = controller
                                    .queryRenderedFeatures(screenPoint, CHARGER_LAYER_ID)
                                    .firstOrNull()
                                    ?.getNumberProperty(CHARGER_ID_PROPERTY)
                                    ?.toInt()
                                val selected = latestChargers.firstOrNull { it.id == chargerId }
                                selected?.let(latestOnChargerClick)
                                selected != null
                            }
                            controller.setStyle(OPEN_FREE_MAP_STYLE) { style ->
                                mapController = controller
                                mapStyle = style
                            }
                        }
                    }
                }
            )
            LocationButton(onLocationClick, Modifier.align(Alignment.TopEnd).padding(18.dp))
            Text(
                stringResource(R.string.map_attribution),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = .9f))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                color = Color(0xFF333333),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
            if (!mapLoaded) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = .94f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        stringResource(if (mapTimedOut) R.string.map_unavailable else R.string.map_loading),
                        color = if (mapTimedOut) DangerRed else Color(0xFF333333),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember {
        MapLibre.getInstance(context)
        val options = MapLibreMapOptions.createFromAttributes(context)
            .textureMode(true)
            .asyncRendererCleanup(true)
        MapView(context, options).also { it.onCreate(null) }
    }
    DisposableEffect(lifecycle, mapView) {
        var destroyed = false
        fun destroyMap() {
            if (!destroyed) {
                destroyed = true
                mapView.onDestroy()
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> destroyMap()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onStop()
            destroyMap()
        }
    }
    return mapView
}

@Composable
private fun LocationButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(52.dp).clickable(onClick = onClick).clip(CircleShape).background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Rounded.MyLocation, contentDescription = stringResource(R.string.use_my_location), tint = ElectricBlue)
    }
}

private val SAO_PAULO_CENTER = LatLng(-23.5505, -46.6333)
private val WORLD_CENTER = LatLng(15.0, 0.0)

private fun chargerLatLng(charger: Charger, index: Int): LatLng {
    if (charger.latitude != SAO_PAULO_CENTER.latitude || charger.longitude != SAO_PAULO_CENTER.longitude) {
        return LatLng(charger.latitude, charger.longitude)
    }
    val fallbackOffsets = listOf(
        -0.070 to -0.060,
        0.030 to 0.070,
        -0.025 to 0.110,
        0.060 to -0.025,
        -0.090 to 0.075
    )
    val offset = fallbackOffsets[index % fallbackOffsets.size]
    return LatLng(SAO_PAULO_CENTER.latitude + offset.first, SAO_PAULO_CENTER.longitude + offset.second)
}

private const val OPEN_FREE_MAP_STYLE = "https://tiles.openfreemap.org/styles/liberty"
private const val CHARGER_SOURCE_ID = "vevolt-chargers"
private const val CHARGER_LAYER_ID = "vevolt-chargers-layer"
private const val CHARGER_ID_PROPERTY = "chargerId"
