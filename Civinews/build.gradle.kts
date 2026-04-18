plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Upgrade version from 2.52 to 2.57
    id("com.google.dagger.hilt.android") version "2.57" apply false
    kotlin("kapt") version "2.0.21" apply false
}