package br.com.vevolt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import br.com.vevolt.R
import br.com.vevolt.data.external.MarketplaceFetchResult
import br.com.vevolt.data.external.PostalCodeLookupError
import br.com.vevolt.data.external.PostalCodeSearchRequest
import br.com.vevolt.model.MarketplaceCategory
import br.com.vevolt.model.MarketplacePlace
import br.com.vevolt.model.Vehicle
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.PrimaryButton
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatDecimal
import br.com.vevolt.ui.theme.AccessibleGreen
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen
import coil3.compose.AsyncImage
import java.text.Collator
import java.util.Locale

@Composable
fun MarketplaceScreen(
    vehicle: Vehicle,
    premiumActive: Boolean,
    loadState: MarketplaceFetchResult,
    searchAreaLabel: String?,
    postalCodeSearching: Boolean,
    postalCodeError: PostalCodeLookupError?,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSearchPostalCode: (PostalCodeSearchRequest) -> Unit,
    onUseDeviceLocation: () -> Unit,
    onRoute: (MarketplacePlace) -> Unit,
    onCall: (String) -> Unit,
    onOpenWhatsApp: (String) -> Unit,
    onOpenWebsite: (String) -> Unit,
    onOpenPremium: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<MarketplaceCategory?>(null) }
    var vehicleBrandOnly by remember { mutableStateOf(false) }
    var premiumVisibleLimit by remember { mutableIntStateOf(PREMIUM_PAGE_SIZE) }
    val allPlaces = (loadState as? MarketplaceFetchResult.Success)?.places.orEmpty()
    val filteredPlaces = remember(allPlaces, selectedCategory, vehicleBrandOnly) {
        allPlaces.filter { place ->
            (selectedCategory == null || place.category == selectedCategory) &&
                (!vehicleBrandOnly || place.vehicleBrandMatched)
        }
    }
    LaunchedEffect(allPlaces, selectedCategory, vehicleBrandOnly) {
        premiumVisibleLimit = PREMIUM_PAGE_SIZE
    }
    val visiblePlaces = filteredPlaces.take(
        if (premiumActive) premiumVisibleLimit else FREE_PLACE_LIMIT
    )

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MarketplaceHeader(onBack)
            }
            item {
                MarketplacePostalCodeSearch(
                    searchAreaLabel = searchAreaLabel,
                    searching = postalCodeSearching,
                    error = postalCodeError,
                    onSearch = onSearchPostalCode,
                    onUseDeviceLocation = onUseDeviceLocation
                )
            }
            if (loadState is MarketplaceFetchResult.Success) {
                item {
                    MarketplaceFilters(
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        vehicleBrand = vehicle.brand,
                        premiumActive = premiumActive,
                        vehicleBrandOnly = vehicleBrandOnly,
                        onVehicleBrandOnly = { vehicleBrandOnly = it }
                    )
                }
            }
            when (loadState) {
                MarketplaceFetchResult.Loading -> item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ElectricBlue)
                    }
                }
                MarketplaceFetchResult.LocationRequired -> item {
                    MarketplaceStateCard(
                        title = stringResource(R.string.location_required),
                        body = stringResource(R.string.marketplace_location_body),
                        action = stringResource(R.string.try_my_location),
                        onAction = onRetry
                    )
                }
                MarketplaceFetchResult.Empty -> item {
                    MarketplaceStateCard(
                        title = stringResource(R.string.marketplace_empty_title),
                        body = stringResource(R.string.marketplace_empty_body),
                        action = stringResource(R.string.try_again),
                        onAction = onRetry
                    )
                }
                is MarketplaceFetchResult.NetworkError -> item {
                    MarketplaceStateCard(
                        title = stringResource(R.string.marketplace_error_title),
                        body = loadState.responseCode?.let {
                            stringResource(R.string.marketplace_error_code, it)
                        } ?: stringResource(R.string.marketplace_error_body),
                        action = stringResource(R.string.try_again),
                        onAction = onRetry
                    )
                }
                is MarketplaceFetchResult.Success -> {
                    if (visiblePlaces.isEmpty()) {
                        item {
                            MarketplaceStateCard(
                                title = stringResource(R.string.marketplace_filter_empty_title),
                                body = stringResource(R.string.marketplace_filter_empty_body),
                                action = stringResource(R.string.show_all),
                                onAction = {
                                    selectedCategory = null
                                    vehicleBrandOnly = false
                                }
                            )
                        }
                    } else {
                        item {
                            Text(
                                stringResource(
                                    R.string.marketplace_showing_count,
                                    visiblePlaces.size,
                                    filteredPlaces.size
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .64f),
                                fontSize = 12.sp
                            )
                        }
                        items(visiblePlaces, key = { it.id }) { place ->
                            MarketplacePlaceCard(
                                place = place,
                                onRoute = { onRoute(place) },
                                onCall = place.phone?.let { phone -> { onCall(phone) } },
                                onOpenWhatsApp = place.whatsApp?.let { phone -> { onOpenWhatsApp(phone) } },
                                onOpenWebsite = place.website?.let { website -> { onOpenWebsite(website) } }
                            )
                        }
                        if (!premiumActive && filteredPlaces.size > FREE_PLACE_LIMIT) {
                            item {
                                MarketplacePremiumCard(
                                    remaining = filteredPlaces.size - FREE_PLACE_LIMIT,
                                    onOpenPremium = onOpenPremium
                                )
                            }
                        } else if (premiumActive && visiblePlaces.size < filteredPlaces.size) {
                            item {
                                val nextPageSize = minOf(
                                    PREMIUM_PAGE_SIZE,
                                    filteredPlaces.size - visiblePlaces.size
                                )
                                PrimaryButton(
                                    text = pluralStringResource(
                                        R.plurals.marketplace_load_more,
                                        nextPageSize,
                                        nextPageSize
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { premiumVisibleLimit += PREMIUM_PAGE_SIZE }
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    stringResource(R.string.marketplace_source_notice),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = .64f),
                    fontSize = 12.sp
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun MarketplacePostalCodeSearch(
    searchAreaLabel: String?,
    searching: Boolean,
    error: PostalCodeLookupError?,
    onSearch: (PostalCodeSearchRequest) -> Unit,
    onUseDeviceLocation: () -> Unit
) {
    val locale = currentLocale()
    var countryCode by remember(locale) { mutableStateOf(defaultPostalCountry(locale)) }
    var postalCodeValue by remember { mutableStateOf(TextFieldValue()) }
    val focusManager = LocalFocusManager.current
    val submitSearch = {
        focusManager.clearFocus()
        onSearch(
            PostalCodeSearchRequest(
                countryCode = countryCode,
                postalCode = postalCodeValue.text,
                languageTag = locale.toLanguageTag()
            )
        )
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        CountrySelectField(
            countryCode = countryCode,
            locale = locale,
            enabled = !searching,
            onCountrySelected = { selected ->
                countryCode = selected
                postalCodeValue = TextFieldValue()
            }
        )
        OutlinedTextField(
            value = postalCodeValue,
            onValueChange = { value ->
                val formatted = formatInternationalPostalCodeInput(countryCode, value.text)
                postalCodeValue = TextFieldValue(
                    text = formatted,
                    selection = TextRange(formatted.length)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.marketplace_postal_code_label)) },
            placeholder = { Text(postalCodeExample(countryCode)) },
            singleLine = true,
            enabled = !searching,
            leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null) },
            trailingIcon = {
                if (searching) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    IconButton(onClick = submitSearch, enabled = postalCodeValue.text.length >= 2) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = stringResource(R.string.marketplace_search_postal_code)
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (postalCodeUsesNumericKeyboard(countryCode)) {
                    KeyboardType.Number
                } else {
                    KeyboardType.Ascii
                },
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { submitSearch() })
        )
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onUseDeviceLocation, enabled = !searching) {
                Icon(Icons.Rounded.MyLocation, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.size(6.dp))
                Text(stringResource(R.string.marketplace_use_device_location))
            }
        }
        Text(
            if (searchAreaLabel == null) {
                stringResource(R.string.marketplace_current_location_active)
            } else {
                stringResource(R.string.marketplace_postal_code_active, searchAreaLabel)
            },
            color = AccessibleGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        if (error != null) {
            Text(
                stringResource(error.postalCodeErrorMessage()),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountrySelectField(
    countryCode: String,
    locale: Locale,
    enabled: Boolean,
    onCountrySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val countries = remember(locale, countryCode) { postalCountries(locale, countryCode) }
    val selectedLabel = countries.firstOrNull { it.code == countryCode }?.label ?: countryCode
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(stringResource(R.string.marketplace_country_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countries.forEach { country ->
                DropdownMenuItem(
                    text = { Text(country.label) },
                    onClick = {
                        onCountrySelected(country.code)
                        expanded = false
                    }
                )
            }
        }
    }
}

internal fun formatInternationalPostalCodeInput(countryCode: String, input: String): String {
    val country = countryCode.uppercase(Locale.ROOT)
    val compact = input.uppercase(Locale.ROOT).filter { it in 'A'..'Z' || it.isDigit() }
    val digits = compact.filter(Char::isDigit)
    return when (country) {
        "BR" -> digits.take(8).masked(5, '-')
        "US" -> digits.take(9).masked(5, '-')
        "CA" -> compact.take(6).masked(3, ' ')
        "GB" -> compact.take(7).let { value ->
            if (value.length < 5) value else "${value.dropLast(3)} ${value.takeLast(3)}"
        }
        "IE" -> compact.take(7).masked(3, ' ')
        "PT" -> digits.take(7).masked(4, '-')
        "NL" -> compact.take(6).masked(4, ' ')
        "JP" -> digits.take(7).masked(3, '-')
        "PL" -> digits.take(5).masked(2, '-')
        "SE", "CZ", "SK", "GR" -> digits.take(5).masked(3, ' ')
        "FR" -> digits.take(5).masked(2, ' ')
        "DE", "ES", "IT", "MX", "KR" -> digits.take(5)
        "AU", "NZ", "AT", "BE", "CH", "DK", "NO" -> digits.take(4)
        "CL" -> digits.take(7)
        "IN", "CN", "SG" -> digits.take(6)
        else -> input
            .uppercase(Locale.ROOT)
            .filter { it in 'A'..'Z' || it.isDigit() || it == ' ' || it == '-' }
            .take(16)
    }
}

internal fun postalCodeExample(countryCode: String): String = when (countryCode.uppercase(Locale.ROOT)) {
    "BR" -> "00000-000"
    "US" -> "10001 / 90210-1234"
    "CA" -> "A1A 1A1"
    "GB" -> "SW1A 1AA"
    "IE" -> "D02 X285"
    "PT" -> "1000-001"
    "NL" -> "1012 AB"
    "JP" -> "100-0001"
    "PL" -> "00-001"
    "SE" -> "111 22"
    "CZ" -> "110 00"
    "SK" -> "811 01"
    "GR" -> "105 58"
    "FR" -> "75 001"
    "DE" -> "10115"
    "ES" -> "28013"
    "IT" -> "00118"
    "MX" -> "06000"
    "KR" -> "04524"
    "AU" -> "2000"
    "NZ" -> "1010"
    "AT" -> "1010"
    "BE" -> "1000"
    "CH" -> "8001"
    "DK" -> "1050"
    "NO" -> "0150"
    "CL" -> "8320000"
    "IN" -> "110001"
    "CN" -> "100000"
    "SG" -> "018956"
    "AR" -> "C1000AAA"
    else -> "12345 / A1A 1A1"
}

internal fun postalCodeUsesNumericKeyboard(countryCode: String): Boolean =
    countryCode.uppercase(Locale.ROOT) in setOf(
        "BR", "US", "PT", "JP", "PL", "SE", "CZ", "SK", "GR", "FR", "DE", "ES",
        "IT", "MX", "KR", "AU", "NZ", "AT", "BE", "CH", "DK", "NO", "CL", "IN",
        "CN", "SG"
    )

private fun String.masked(splitAt: Int, separator: Char): String =
    if (length <= splitAt) this else "${take(splitAt)}$separator${drop(splitAt)}"

private fun defaultPostalCountry(locale: Locale): String {
    val supported = Locale.getISOCountries().toSet()
    return locale.country.uppercase(Locale.ROOT).takeIf { it in supported } ?: when (locale.language) {
        "pt" -> "BR"
        "es" -> "ES"
        else -> "US"
    }
}

private fun postalCountries(locale: Locale, selectedCountry: String): List<PostalCountry> {
    val collator = Collator.getInstance(locale)
    return Locale.getISOCountries()
        .map { code ->
            val label = Locale.Builder().setRegion(code).build().getDisplayCountry(locale).ifBlank { code }
            PostalCountry(code, "$label ($code)")
        }
        .sortedWith { left, right ->
            when {
                left.code == selectedCountry -> -1
                right.code == selectedCountry -> 1
                else -> collator.compare(left.label, right.label)
            }
        }
}

private data class PostalCountry(val code: String, val label: String)

private fun PostalCodeLookupError.postalCodeErrorMessage(): Int = when (this) {
    PostalCodeLookupError.INVALID -> R.string.marketplace_postal_code_invalid
    PostalCodeLookupError.NOT_FOUND -> R.string.marketplace_postal_code_not_found
    PostalCodeLookupError.COORDINATES_UNAVAILABLE -> R.string.marketplace_postal_code_without_coordinates
    PostalCodeLookupError.NETWORK -> R.string.marketplace_postal_code_network_error
}

@Composable
private fun MarketplaceHeader(onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
        }
        Column {
            Text(stringResource(R.string.marketplace_title), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                stringResource(R.string.marketplace_subtitle),
                color = AccessibleGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MarketplaceFilters(
    selectedCategory: MarketplaceCategory?,
    onSelectCategory: (MarketplaceCategory?) -> Unit,
    vehicleBrand: String,
    premiumActive: Boolean,
    vehicleBrandOnly: Boolean,
    onVehicleBrandOnly: (Boolean) -> Unit
) {
    val filterColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = ElectricBlue.copy(alpha = .12f),
        selectedLabelColor = ElectricBlue
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onSelectCategory(null) },
                label = { Text(stringResource(R.string.show_all)) },
                colors = filterColors
            )
            FilterChip(
                selected = selectedCategory == MarketplaceCategory.DEALER,
                onClick = { onSelectCategory(MarketplaceCategory.DEALER) },
                label = { Text(stringResource(R.string.marketplace_dealers)) },
                colors = filterColors
            )
            FilterChip(
                selected = selectedCategory == MarketplaceCategory.REPAIR,
                onClick = { onSelectCategory(MarketplaceCategory.REPAIR) },
                label = { Text(stringResource(R.string.marketplace_repairs)) },
                colors = filterColors
            )
        }
        if (premiumActive && vehicleBrand.isNotBlank()) {
            FilterChip(
                selected = vehicleBrandOnly,
                onClick = { onVehicleBrandOnly(!vehicleBrandOnly) },
                label = { Text(stringResource(R.string.marketplace_my_brand, vehicleBrand)) },
                colors = filterColors
            )
        }
    }
}

@Composable
private fun MarketplacePlaceCard(
    place: MarketplacePlace,
    onRoute: () -> Unit,
    onCall: (() -> Unit)?,
    onOpenWhatsApp: (() -> Unit)?,
    onOpenWebsite: (() -> Unit)?
) {
    val locale = currentLocale()
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MarketplaceThumbnail(place)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            place.name,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            stringResource(R.string.distance_km, formatDecimal(place.distanceKm, locale)),
                            color = AccessibleGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        stringResource(
                            if (place.category == MarketplaceCategory.DEALER) {
                                R.string.marketplace_dealer
                            } else {
                                R.string.marketplace_repair
                            }
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .66f),
                        fontSize = 12.sp
                    )
                    if (place.address.isNotBlank()) {
                        MarketplaceInfoLine(Icons.Rounded.LocationOn, place.address, maxLines = 2)
                    }
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                place.brand?.let {
                    MarketplaceBadge(stringResource(R.string.marketplace_brand_declared, it), ElectricBlue)
                }
                MarketplaceBadge(
                    stringResource(
                        if (place.electricServiceDeclared) {
                            R.string.marketplace_ev_declared
                        } else {
                            R.string.marketplace_ev_confirm
                        }
                    ),
                    if (place.electricServiceDeclared) AccessibleGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
                )
            }
            place.phone?.let { MarketplaceInfoLine(Icons.Rounded.Phone, it, maxLines = 1) }
            place.openingHours?.let {
                MarketplaceInfoLine(Icons.Rounded.Schedule, stringResource(R.string.marketplace_hours, it), maxLines = 2)
            }
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (onCall != null) {
                    MarketplaceActionChip(stringResource(R.string.call), Icons.Rounded.Phone, ElectricBlue, onCall)
                }
                if (onOpenWhatsApp != null) {
                    MarketplaceActionChip(
                        stringResource(R.string.whatsapp),
                        Icons.AutoMirrored.Rounded.Chat,
                        AccessibleGreen,
                        onOpenWhatsApp
                    )
                }
                if (onOpenWebsite != null) {
                    MarketplaceActionChip(stringResource(R.string.open_website), Icons.Rounded.Language, ElectricBlue, onOpenWebsite)
                }
                MarketplaceActionChip(stringResource(R.string.open_route), Icons.Rounded.Directions, AccessibleGreen, onRoute)
            }
        }
    }
}

@Composable
private fun MarketplaceThumbnail(place: MarketplacePlace) {
    val background = if (place.category == MarketplaceCategory.DEALER) ElectricBlue else EnergyGreen
    Box(
        Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (place.category == MarketplaceCategory.DEALER) Icons.Rounded.Storefront else Icons.Rounded.Build,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )
        place.photoUrl?.let { photoUrl ->
            AsyncImage(
                model = photoUrl,
                contentDescription = stringResource(R.string.marketplace_photo_description, place.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun MarketplaceInfoLine(icon: ImageVector, text: String, maxLines: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .56f),
            modifier = Modifier.size(15.dp)
        )
        Text(
            text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f),
            fontSize = 12.sp,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun MarketplaceActionChip(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(17.dp)) },
        modifier = Modifier.height(34.dp),
        colors = AssistChipDefaults.assistChipColors(
            labelColor = color,
            leadingIconContentColor = color
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
private fun MarketplaceBadge(text: String, color: Color) {
    Text(
        text,
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = .12f)).padding(horizontal = 8.dp, vertical = 4.dp),
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
}

@Composable
private fun MarketplaceStateCard(title: String, body: String, action: String, onAction: () -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(body, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f))
            PrimaryButton(action, Modifier.fillMaxWidth(), onClick = onAction)
        }
    }
}

@Composable
private fun MarketplacePremiumCard(remaining: Int, onOpenPremium: () -> Unit) {
    AppCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(stringResource(R.string.marketplace_premium_title), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(
                pluralStringResource(R.plurals.marketplace_premium_body, remaining, remaining),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f)
            )
            PrimaryButton(stringResource(R.string.view_premium), Modifier.fillMaxWidth(), onClick = onOpenPremium)
        }
    }
}

private const val FREE_PLACE_LIMIT = 5
private const val PREMIUM_PAGE_SIZE = 10
