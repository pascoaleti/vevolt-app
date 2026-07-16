import java.util.Properties

val releaseSigningPropertiesFile = rootProject.file("local-signing/keystore.properties")
val localPropertiesFile = rootProject.file("local.properties")
val localBuildProperties = Properties().apply {
    if (localPropertiesFile.isFile) {
        localPropertiesFile.inputStream().use(::load)
    }
}
val releaseSigningProperties = Properties().apply {
    if (releaseSigningPropertiesFile.isFile) {
        releaseSigningPropertiesFile.inputStream().use(::load)
    }
}
val releaseStoreFile = releaseSigningProperties.getProperty("storeFile")
val releaseStorePassword = releaseSigningProperties.getProperty("storePassword")
val releaseStoreType = releaseSigningProperties.getProperty("storeType")
val releaseKeyAlias = releaseSigningProperties.getProperty("keyAlias")
val releaseKeyPassword = releaseSigningProperties.getProperty("keyPassword")
val premiumSalesEnabled = localProperty("PREMIUM_SALES_ENABLED").equals("true", ignoreCase = true)
val hasReleaseSigningConfig = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }

fun localProperty(name: String): String =
    (localBuildProperties.getProperty(name) ?: providers.environmentVariable(name).orNull).orEmpty()

fun quoted(value: String): String = "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "br.com.vevolt"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.vevolt"
        minSdk = 26
        targetSdk = 36
        versionCode = 12
        versionName = "0.3.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OPEN_CHARGE_MAP_API_KEY", quoted(localProperty("OPEN_CHARGE_MAP_API_KEY")))
        buildConfigField("String", "PLAY_BILLING_PREMIUM_MONTHLY_PRODUCT_ID", quoted(localProperty("PLAY_BILLING_PREMIUM_MONTHLY_PRODUCT_ID").ifBlank { "vevolt_premium_monthly" }))
        buildConfigField("String", "PLAY_BILLING_PREMIUM_YEARLY_PRODUCT_ID", quoted(localProperty("PLAY_BILLING_PREMIUM_YEARLY_PRODUCT_ID").ifBlank { "vevolt_premium_yearly" }))
        buildConfigField("String", "VEVOLT_BACKEND_BASE_URL", quoted(localProperty("VEVOLT_BACKEND_BASE_URL")))
        buildConfigField("boolean", "PREMIUM_SALES_ENABLED", premiumSalesEnabled.toString())
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigningConfig) {
                storeFile = rootProject.file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                releaseStoreType?.takeIf { it.isNotBlank() }?.let { storeType = it }
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            if (hasReleaseSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kotlin {
        jvmToolchain(17)
    }
}

tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    doFirst {
        val missing = listOf(
            "OPEN_CHARGE_MAP_API_KEY"
        ).filter { localProperty(it).isBlank() }
        if (missing.isNotEmpty()) {
            throw GradleException(
                "Release bloqueado: configure ${missing.joinToString()} antes de gerar build de producao."
            )
        }
        if (premiumSalesEnabled && !localProperty("VEVOLT_BACKEND_BASE_URL").startsWith("https://")) {
            throw GradleException(
                "Release bloqueado: PREMIUM_SALES_ENABLED exige VEVOLT_BACKEND_BASE_URL HTTPS."
            )
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.06.00")
    implementation(composeBom)

    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("androidx.work:work-runtime-ktx:2.11.2")
    implementation("androidx.camera:camera-camera2:1.6.1")
    implementation("androidx.camera:camera-lifecycle:1.6.1")
    implementation("androidx.camera:camera-view:1.6.1")
    implementation("com.android.billingclient:billing-ktx:9.1.0")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("org.maplibre.gl:android-sdk-opengl:13.3.1")
    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.5.0")

    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    testImplementation("junit:junit:4.13.2")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
