package br.com.vevolt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import br.com.vevolt.R
import br.com.vevolt.model.ConnectorType
import br.com.vevolt.model.Vehicle
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.BrandLogo
import br.com.vevolt.ui.components.PrimaryButton
import br.com.vevolt.ui.localization.VEHICLE_TYPE_ELECTRIC
import br.com.vevolt.ui.localization.VEHICLE_TYPE_PLUGIN_HYBRID
import br.com.vevolt.ui.localization.localizedLabel
import br.com.vevolt.ui.localization.normalizeVehicleType
import br.com.vevolt.ui.localization.currentLocale
import br.com.vevolt.ui.localization.formatDecimal
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSetupScreen(initialVehicle: Vehicle, onContinue: (Vehicle) -> Unit) {
    val locale = currentLocale()
    var type by remember { mutableStateOf(normalizeVehicleType(initialVehicle.type)) }
    var brand by remember { mutableStateOf(initialVehicle.brand.toBrandSelection()) }
    var customBrand by remember { mutableStateOf(initialVehicle.brand.toCustomBrand()) }
    var model by remember { mutableStateOf(initialVehicle.model.toModelSelection(initialVehicle.brand)) }
    var customModel by remember { mutableStateOf(initialVehicle.model.toCustomModel(initialVehicle.brand)) }
    var year by remember { mutableStateOf(initialVehicle.year) }
    var range by remember { mutableStateOf(initialVehicle.rangeKm.positiveText()) }
    var batteryKwh by remember { mutableStateOf(initialVehicle.batteryKwh.positiveText(locale)) }
    var connector by remember { mutableStateOf(initialVehicle.connector) }
    var battery by remember { mutableStateOf(initialVehicle.currentBatteryPercent.toString()) }
    var validationError by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(initialVehicle, locale) {
        type = normalizeVehicleType(initialVehicle.type)
        brand = initialVehicle.brand.toBrandSelection()
        customBrand = initialVehicle.brand.toCustomBrand()
        model = initialVehicle.model.toModelSelection(initialVehicle.brand)
        customModel = initialVehicle.model.toCustomModel(initialVehicle.brand)
        year = initialVehicle.year
        range = initialVehicle.rangeKm.positiveText()
        batteryKwh = initialVehicle.batteryKwh.positiveText(locale)
        connector = initialVehicle.connector
        battery = initialVehicle.currentBatteryPercent.toString()
        validationError = null
    }

    val otherLabel = stringResource(R.string.connector_other)
    val selectedBrand = if (brand == OTHER_OPTION_VALUE) customBrand.trim() else brand.trim()
    val modelOptions = vehicleModels[selectedBrand].orEmpty()
    val selectedModel = if (model == OTHER_OPTION_VALUE) customModel.trim() else model.trim()
    val typeOptions = listOf(
        SelectOption(VEHICLE_TYPE_ELECTRIC, stringResource(R.string.vehicle_type_electric)),
        SelectOption(VEHICLE_TYPE_PLUGIN_HYBRID, stringResource(R.string.vehicle_type_plugin_hybrid))
    )
    val connectorOptions = ConnectorType.entries.map { SelectOption(it.name, it.localizedLabel()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(28.dp))
        BrandLogo(compact = true)
        Spacer(Modifier.height(22.dp))
        Text(stringResource(R.string.vehicle_setup_title), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            stringResource(R.string.vehicle_setup_body),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )
        Spacer(Modifier.height(18.dp))
        AppCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                SelectField(
                    value = type,
                    onValueChange = { type = it },
                    label = stringResource(R.string.field_type),
                    options = typeOptions
                )
                SelectField(
                    value = brand,
                    onValueChange = {
                        brand = it
                        if (it != OTHER_OPTION_VALUE) {
                            customBrand = ""
                            model = vehicleModels[it]?.firstOrNull() ?: OTHER_OPTION_VALUE
                            customModel = ""
                        }
                    },
                    label = stringResource(R.string.field_brand),
                    options = vehicleBrands.map { SelectOption(it, it) } + SelectOption(OTHER_OPTION_VALUE, otherLabel)
                )
                if (brand == OTHER_OPTION_VALUE) {
                    InputField(
                        value = customBrand,
                        onValueChange = { customBrand = it },
                        label = stringResource(R.string.field_brand_other)
                    )
                }
                SelectField(
                    value = model,
                    onValueChange = { model = it },
                    label = stringResource(R.string.field_model),
                    options = modelOptions.map { SelectOption(it, it) } + SelectOption(OTHER_OPTION_VALUE, otherLabel),
                    enabled = brand.isNotBlank()
                )
                if (model == OTHER_OPTION_VALUE) {
                    InputField(
                        value = customModel,
                        onValueChange = { customModel = it },
                        label = stringResource(R.string.field_model_other)
                    )
                }
                InputField(value = year, onValueChange = { year = it }, label = stringResource(R.string.field_year), keyboardType = KeyboardType.Number)
                InputField(value = range, onValueChange = { range = it }, label = stringResource(R.string.field_average_range), keyboardType = KeyboardType.Number)
                InputField(value = batteryKwh, onValueChange = { batteryKwh = it }, label = stringResource(R.string.field_battery_capacity), keyboardType = KeyboardType.Decimal)
                SelectField(
                    value = connector.name,
                    onValueChange = { selected -> connector = ConnectorType.valueOf(selected) },
                    label = stringResource(R.string.field_connector_type),
                    options = connectorOptions
                )
                InputField(value = battery, onValueChange = { battery = it }, label = stringResource(R.string.field_current_battery), keyboardType = KeyboardType.Number)
            }
        }
        validationError?.let { messageResource ->
            Spacer(Modifier.height(10.dp))
            Text(stringResource(messageResource), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(22.dp))
        PrimaryButton(
            stringResource(R.string.enter_app),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val validRange = range.trim().toIntOrNull()?.takeIf { it > 0 }
                val validCapacity = batteryKwh.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0.0 }
                val validBattery = battery.trim().toIntOrNull()?.takeIf { it in 0..100 }
                val validYear = year.trim().toIntOrNull()?.takeIf { it in 1990..2100 }
                validationError = when {
                    selectedBrand.isBlank() -> R.string.validation_brand
                    selectedModel.isBlank() -> R.string.validation_model
                    validYear == null -> R.string.validation_year
                    validRange == null -> R.string.validation_range
                    validCapacity == null -> R.string.validation_capacity
                    validBattery == null -> R.string.validation_battery
                    else -> null
                }
                if (validationError == null) {
                    onContinue(
                        Vehicle(
                            id = initialVehicle.id,
                            type = type.trim(),
                            brand = selectedBrand,
                            model = selectedModel,
                            year = validYear.toString(),
                            rangeKm = validRange!!,
                            batteryKwh = validCapacity!!,
                            connector = connector,
                            currentBatteryPercent = validBattery!!
                        )
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<SelectOption>,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val uniqueOptions = remember(options) { options.distinctBy { it.value } }
    val displayValue = uniqueOptions.firstOrNull { it.value == value }?.label.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            uniqueOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

private data class SelectOption(val value: String, val label: String)

private const val OTHER_OPTION_VALUE = "__other__"

private fun String.toBrandSelection(): String = when {
    isBlank() || this in vehicleBrands -> this
    else -> OTHER_OPTION_VALUE
}

private fun String.toCustomBrand(): String = takeIf { it.isNotBlank() && it !in vehicleBrands }.orEmpty()

private fun String.toModelSelection(brand: String): String = when {
    isBlank() || this in vehicleModels[brand].orEmpty() -> this
    else -> OTHER_OPTION_VALUE
}

private fun String.toCustomModel(brand: String): String =
    takeIf { it.isNotBlank() && it !in vehicleModels[brand].orEmpty() }.orEmpty()

private fun Int.positiveText(): String = takeIf { it > 0 }?.toString().orEmpty()

private fun Double.positiveText(locale: Locale): String =
    takeIf { it > 0.0 }?.let { formatDecimal(it, locale, maximumFractionDigits = 2) }.orEmpty()

private val vehicleBrands = listOf(
    "BYD",
    "GWM",
    "Volvo",
    "BMW",
    "Mercedes-Benz",
    "Porsche",
    "Audi",
    "Nissan",
    "Renault",
    "Chevrolet",
    "Volkswagen",
    "Caoa Chery",
    "JAC",
    "Tesla"
)

private val vehicleModels = mapOf(
    "BYD" to listOf("Dolphin", "Dolphin Mini", "Dolphin Plus", "Yuan Plus", "Seal", "Song Plus", "Tan"),
    "GWM" to listOf("Ora 03", "Haval H6 PHEV", "Haval H6 GT"),
    "Volvo" to listOf("EX30", "EX40", "EC40", "XC40 Recharge", "C40 Recharge", "EX90"),
    "BMW" to listOf("i3", "i4", "iX1", "iX3", "iX", "i7", "330e", "X5 xDrive50e"),
    "Mercedes-Benz" to listOf("EQA", "EQB", "EQE", "EQS", "GLE 400e"),
    "Porsche" to listOf("Taycan", "Macan Electric", "Cayenne E-Hybrid", "Panamera E-Hybrid"),
    "Audi" to listOf("Q8 e-tron", "Q4 e-tron", "e-tron GT", "A3 TFSI e"),
    "Nissan" to listOf("Leaf"),
    "Renault" to listOf("Kwid E-Tech", "Megane E-Tech", "Kangoo E-Tech"),
    "Chevrolet" to listOf("Bolt EV", "Bolt EUV", "Blazer EV", "Equinox EV"),
    "Volkswagen" to listOf("ID.3", "ID.4", "ID. Buzz", "Golf GTE"),
    "Caoa Chery" to listOf("iCar", "Tiggo 8 Pro Plug-in Hybrid"),
    "JAC" to listOf("E-JS1", "E-JS4", "E-J7", "iEV40"),
    "Tesla" to listOf("Model 3", "Model Y", "Model S", "Model X")
)
