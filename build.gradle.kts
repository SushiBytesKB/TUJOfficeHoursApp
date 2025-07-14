// build.gradle.kts (Project-level)
// Registers the plugins required for the entire project.

plugins {
    // The Android Application plugin, applied to sub-projects, not here.
    alias(libs.plugins.android.application) apply false

    // The Kotlin Android plugin for Kotlin support in Android.
    alias(libs.plugins.kotlin.android) apply false

    // The Kotlin Compose compiler plugin.
    alias(libs.plugins.kotlin.compose) apply false

    // Google Services plugin for Firebase integration.
    id("com.google.gms.google-services") version "4.4.2" apply false

    // KSP (Kotlin Symbol Processing) plugin for code generation (used by Room).
    id("com.google.devtools.ksp") version "2.0.0-1.0.22" apply false
}
