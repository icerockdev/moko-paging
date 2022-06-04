/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.application")
    kotlin("android")
}

val composeVersion = "1.2.0-beta03"

android {
    compileSdk = 32
    buildFeatures {
        compose = true
    }
    defaultConfig {
        applicationId = "dev.icerock.moko.paging.sample.declarativeui.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    kotlinOptions {
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
}

dependencies {
    implementation(project(":sample-declarative-ui:shared"))


    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.7-alpha")
    implementation("androidx.compose.ui:ui:$composeVersion")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    // Material Design
    implementation("androidx.compose.material:material:$composeVersion")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    // Integration with observables
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")

    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    implementation("androidx.navigation:navigation-compose:2.4.2")
}
