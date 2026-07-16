package br.com.vevolt.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.com.vevolt.model.Charger
import br.com.vevolt.R
import br.com.vevolt.model.ChargingSession
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.BottomNavBar
import br.com.vevolt.ui.components.GreenButton
import br.com.vevolt.ui.components.PrimaryButton
import br.com.vevolt.ui.navigation.Screen
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen
import br.com.vevolt.ui.localization.localizedLabel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun ScanScreen(
    charger: Charger?,
    activeSession: ChargingSession?,
    onStartCharging: () -> Unit,
    onFinishCharging: (energyKwh: Double, amount: Double) -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    var identified by remember { mutableStateOf(false) }
    var cameraActive by remember { mutableStateOf(false) }
    var scannedCode by remember { mutableStateOf<String?>(null) }
    var energyText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var validationMessage by remember { mutableStateOf<Int?>(null) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraActive = granted
    }
    Scaffold(bottomBar = { BottomNavBar(current = Screen.SCAN, onNavigate = onNavigate) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.scan_title), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                stringResource(if (charger == null) R.string.scan_select_point else R.string.scan_local_record),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
            )
            Spacer(Modifier.height(26.dp))
            if (charger == null) {
                AppCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.no_charger_selected), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(
                            stringResource(R.string.scan_no_charger_body),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
                        )
                        Spacer(Modifier.height(12.dp))
                        PrimaryButton(stringResource(R.string.view_map), modifier = Modifier.fillMaxWidth(), onClick = { onNavigate(Screen.MAP) })
                    }
                }
                return@Column
            }
            if (cameraActive && activeSession == null && !identified) {
                QrCameraPreview(
                    modifier = Modifier
                        .size(260.dp)
                        .border(4.dp, ElectricBlue, RoundedCornerShape(34.dp)),
                    onQrCode = { value ->
                        scannedCode = value
                        identified = true
                        cameraActive = false
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .border(4.dp, ElectricBlue, RoundedCornerShape(34.dp))
                        .background(Color.White, RoundedCornerShape(34.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.QrCodeScanner, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(96.dp))
                    Icon(Icons.Rounded.CenterFocusStrong, contentDescription = null, tint = EnergyGreen, modifier = Modifier.size(210.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
            if (activeSession == null && !identified) {
                PrimaryButton(
                    stringResource(if (cameraActive) R.string.waiting_qr else R.string.read_qr),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraActive = true
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )
            }
            if (identified || activeSession != null) {
                Spacer(Modifier.height(18.dp))
                AppCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(if (activeSession == null) R.string.qr_read else R.string.record_in_progress),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Text(
                            listOfNotNull(
                                charger.name,
                                charger.connector.localizedLabel(),
                                charger.powerKw.takeIf { it > 0 }?.let { stringResource(R.string.power_kw, it) }
                            ).joinToString(" - "),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
                        )
                        scannedCode?.let { code ->
                            Text(stringResource(R.string.qr_code_value, code.take(42)), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f))
                        }
                        Text(
                            stringResource(R.string.qr_local_only),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f)
                        )
                        Spacer(Modifier.height(12.dp))
                        if (activeSession == null) {
                            GreenButton(stringResource(R.string.start_local_record), modifier = Modifier.fillMaxWidth(), onClick = onStartCharging)
                        } else {
                            OutlinedTextField(
                                value = energyText,
                                onValueChange = { energyText = it; validationMessage = null },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.energy_charged)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it; validationMessage = null },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text(stringResource(R.string.amount_paid)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            validationMessage?.let { messageResource ->
                                Text(stringResource(messageResource), color = MaterialTheme.colorScheme.error)
                            }
                            Spacer(Modifier.height(10.dp))
                            GreenButton(
                                stringResource(R.string.finish_record),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    val energy = energyText.replace(',', '.').toDoubleOrNull()
                                    val amount = amountText.replace(',', '.').toDoubleOrNull()
                                    if (energy == null || energy <= 0.0 || amount == null || amount < 0.0) {
                                        validationMessage = R.string.validation_session_values
                                    } else {
                                        onFinishCharging(energy, amount)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QrCameraPreview(
    modifier: Modifier = Modifier,
    onQrCode: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val analyzer = remember { QrAnalyzer(onQrCode) }

    DisposableEffect(Unit) {
        onDispose {
            analyzer.close()
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            PreviewView(viewContext).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { it.setAnalyzer(cameraExecutor, analyzer) }

                    runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            analysis
                        )
                    }
                },
                ContextCompat.getMainExecutor(context)
            )
        }
    )
}

private class QrAnalyzer(
    private val onQrCode: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )
    private var handled = false

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (handled) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstOrNull()?.rawValue
                if (!value.isNullOrBlank()) {
                    handled = true
                    onQrCode(value)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun close() {
        scanner.close()
    }
}
