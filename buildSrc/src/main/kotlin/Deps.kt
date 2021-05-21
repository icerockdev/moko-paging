/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Deps {
    private const val kotlinVersion = "1.4.31"

    private const val androidAppCompatVersion = "1.1.0"
    private const val androidLifecycleVersion = "2.1.0"
    private const val androidCoreTestingVersion = "2.1.0"
    private const val recyclerViewVersion = "1.1.0"
    private const val swipeRefreshLayoutVersion = "1.1.0"

    private const val detektVersion = "1.15.0"

    private const val ktorClientVersion = "1.4.0"
    private const val coroutinesVersion = "1.4.2-native-mt"
    private const val mokoMvvmVersion = "0.9.2"
    private const val mokoResourcesVersion = "0.15.1"
    private const val mokoUnitsVersion = "0.4.2"
    const val mokoPagingVersion = "0.4.5"

    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 16
    }

    object Plugins {
        val androidApplication = GradlePlugin(id = "com.android.application")
        val androidLibrary = GradlePlugin(id = "com.android.library")
        val kotlinMultiplatform = GradlePlugin(id = "org.jetbrains.kotlin.multiplatform")
        val kotlinKapt = GradlePlugin(id = "kotlin-kapt")
        val kotlinAndroid = GradlePlugin(id = "kotlin-android")
        val kotlinAndroidExtensions = GradlePlugin(id = "kotlin-android-extensions")
        val mavenPublish = GradlePlugin(id = "org.gradle.maven-publish")
        val signing = GradlePlugin(id = "signing")

        val mobileMultiplatform = GradlePlugin(id = "dev.icerock.mobile.multiplatform")
        val iosFramework = GradlePlugin(id = "dev.icerock.mobile.multiplatform.ios-framework")

        val mokoResources = GradlePlugin(
            id = "dev.icerock.mobile.multiplatform-resources",
            module = "dev.icerock.moko:resources-generator:$mokoResourcesVersion"
        )
        val mokoUnits = GradlePlugin(
            id = "dev.icerock.mobile.multiplatform-units",
            module = "dev.icerock.moko:units-generator:$mokoUnitsVersion"
        )

        val detekt = GradlePlugin(
            id = "io.gitlab.arturbosch.detekt",
            version = detektVersion
        )
    }

    object Libs {
        object Android {
            const val appCompat = "androidx.appcompat:appcompat:$androidAppCompatVersion"
            const val recyclerView = "androidx.recyclerview:recyclerview:$recyclerViewVersion"
            const val lifecycle = "androidx.lifecycle:lifecycle-extensions:$androidLifecycleVersion"
            const val swipeRefreshLayout =
                "androidx.swiperefreshlayout:swiperefreshlayout:$swipeRefreshLayoutVersion"
            const val coroutines =
                "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
        }

        object MultiPlatform {
            const val coroutines =
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
            val ktorClient = MultiPlatformLibrary(
                common = "io.ktor:ktor-client-core:$ktorClientVersion",
                iosX64 = "io.ktor:ktor-client-ios:$ktorClientVersion",
                android = "io.ktor:ktor-client-okhttp:$ktorClientVersion"
            )
            const val mokoResources =
                "dev.icerock.moko:resources:$mokoResourcesVersion"
            val mokoUnits = "dev.icerock.moko:units:$mokoUnitsVersion"
                .defaultMPL(ios = true)
            val mokoMvvmLiveData = "dev.icerock.moko:mvvm-livedata:$mokoMvvmVersion"
                .defaultMPL(ios = true)
            val mokoMvvmState = "dev.icerock.moko:mvvm-state:$mokoMvvmVersion"
                .defaultMPL(ios = true)
            const val mokoPaging = "dev.icerock.moko:paging:$mokoPagingVersion"
        }

        object Tests {
            const val kotlinTestJUnit =
                "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
            const val androidCoreTesting =
                "androidx.arch.core:core-testing:$androidCoreTestingVersion"
        }

        object Detekt {
            const val detektFormatting =
                "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion"
        }
    }
}
