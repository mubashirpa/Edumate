import com.android.build.api.variant.BuildConfigField
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.com.google.dagger.hilt.android)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.com.google.firebase.crashlytics)
    alias(libs.plugins.com.google.gms.google.services)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "edumate.app"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "edumate.app"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "firebase-proguard-rules.pro",
                "jitsi-meet-proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = false
        }
    }
}

dependencies {

    val composeBom = platform(libs.compose.bom)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(libs.bundles.coil)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.hilt)
    ksp(libs.hilt.android.compiler)
    implementation(libs.bundles.ktor)
    implementation(libs.play.services.auth)
    implementation(libs.core.splashscreen)
    implementation(libs.lottie.compose)
    implementation(libs.onesignal)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jitsi.meet.sdk) { isTransitive = true }
    implementation(libs.jsoup)
    implementation(libs.google.ai.client.generativeai)
}

androidComponents {
    onVariants { variant ->
        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = Properties()
        localProperties.load(FileInputStream(localPropertiesFile))
        variant.buildConfigFields.put(
            "ONESIGNAL_APP_ID",
            BuildConfigField(
                "String",
                "\"" + localProperties["ONESIGNAL_APP_ID"] + "\"",
                "Onesignal Application ID",
            ),
        )
        variant.buildConfigFields.put(
            "GEMINI_API_KEY",
            BuildConfigField(
                "String",
                "\"" + localProperties["GEMINI_API_KEY"] + "\"",
                "Google Gemini Api Key",
            ),
        )
    }
}
