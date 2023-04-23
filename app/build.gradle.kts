plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt) // TODO: Remove when hilt added support for ksp (https://github.com/google/dagger/issues/2349)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.gradle.ktlint)
    alias(libs.plugins.google.services)
}

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
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    packaging.resources {
        excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.bundles.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
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

    implementation(libs.google.play.services.auth)
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.bundles.accompanist)
    implementation(libs.jsoup)
    implementation(libs.burnoutcrew.reorderable)
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

apply("${project.rootDir}/buildscripts/ktlint-config.gradle")