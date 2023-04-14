@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove when updating to Gradle 8.1 (https://github.com/gradle/gradle/issues/22797)
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false // TODO: Remove when hilt added support for ksp (https://github.com/google/dagger/issues/2349)
    alias(libs.plugins.devtools.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.gradle.ktlint) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
}

apply("${project.rootDir}/buildscripts/toml-updater-config.gradle")