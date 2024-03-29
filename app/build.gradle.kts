import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.gradle.ktlint)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt) // TODO: Remove when hilt added support for ksp (https://github.com/google/dagger/issues/2349)
    alias(libs.plugins.kotlin.serialization)
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "edumate.app"
    compileSdk = libs.versions.sdkVersion.get().toInt()

    defaultConfig {
        applicationId = "edumate.app"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.sdkVersion.get().toInt()
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
                "proguard-rules.pro"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.bundles.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    implementation(libs.bundles.hilt)
    kapt(libs.hilt.android.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.coil)
    implementation(libs.bundles.ktor)
    implementation(libs.google.play.services.auth)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.jsoup)
    implementation(libs.airbnb.lottie.compose)
    implementation(libs.onesignal)
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.jitsi.meet) { isTransitive = true }
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

apply("${project.rootDir}/buildscripts/ktlint-config.gradle")