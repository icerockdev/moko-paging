/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.android.application")
    id("dev.icerock.mobile.multiplatform-units")
    id("kotlin-kapt")
}

android {
    buildFeatures.dataBinding = true

    defaultConfig {
        applicationId = "dev.icerock.moko.samples.paging"

        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(libs.appCompat)
    implementation(libs.recyclerView)
    implementation(libs.lifecycle)
    implementation(libs.swipeRefreshLayout)
    implementation(libs.mokoUnitsDataBinding)

    implementation(projects.sample.mppLibrary)
}

multiplatformUnits {
    classesPackage = "com.icerockdev"
    dataBindingPackage = "com.icerockdev"
    layoutsSourceSet = "main"
}
