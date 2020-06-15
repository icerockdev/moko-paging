/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Deps {
    object Plugins {
        const val android =
            "com.android.tools.build:gradle:${Versions.Plugins.android}"
        const val kotlin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Plugins.kotlin}"
        const val mokoUnits =
            "dev.icerock.moko:units-generator:${Versions.Plugins.mokoUnits}"
    }

    object Libs {
        object Android {
            val kotlinStdLib = AndroidLibrary(
                name = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
            )
            val appCompat = AndroidLibrary(
                name = "androidx.appcompat:appcompat:${Versions.Libs.Android.appCompat}"
            )
            val recyclerView = AndroidLibrary(
                name = "androidx.recyclerview:recyclerview:${Versions.Libs.Android.recyclerView}"
            )
            val lifecycle = AndroidLibrary(
                name = "androidx.lifecycle:lifecycle-extensions:${Versions.Libs.Android.lifecycle}"
            )
        }

        object MultiPlatform {
            val kotlinStdLib = MultiPlatformLibrary(
                android = Android.kotlinStdLib.name,
                common = "org.jetbrains.kotlin:kotlin-stdlib-common:${Versions.kotlin}"
            )
            val coroutines = MultiPlatformLibrary(
                android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Libs.MultiPlatform.coroutines}",
                common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.Libs.MultiPlatform.coroutines}",
                ios = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.Libs.MultiPlatform.coroutines}"
            )
            val mokoPaging = MultiPlatformLibrary(
                common = "dev.icerock.moko:paging:${Versions.Libs.MultiPlatform.mokoPaging}",
                iosX64 = "dev.icerock.moko:paging-iosx64:${Versions.Libs.MultiPlatform.mokoPaging}",
                iosArm64 = "dev.icerock.moko:paging-iosarm64:${Versions.Libs.MultiPlatform.mokoPaging}"
            )
            val mokoMvvm = MultiPlatformLibrary(
                common = "dev.icerock.moko:mvvm:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosX64 = "dev.icerock.moko:mvvm-iosx64:${Versions.Libs.MultiPlatform.mokoMvvm}",
                iosArm64 = "dev.icerock.moko:mvvm-iosarm64:${Versions.Libs.MultiPlatform.mokoMvvm}"
            )
            val mokoUnits = MultiPlatformLibrary(
                common = "dev.icerock.moko:units:${Versions.Libs.MultiPlatform.mokoUnits}",
                iosX64 = "dev.icerock.moko:units-iosx64:${Versions.Libs.MultiPlatform.mokoUnits}",
                iosArm64 = "dev.icerock.moko:units-iosarm64:${Versions.Libs.MultiPlatform.mokoUnits}"
            )
        }
    }

    object Tests {
        val kotlinTestCommon = MultiPlatformLibrary(
            android = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}",
            common = "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
        )
        val kotlinTestCommonAnnotations = MultiPlatformLibrary(
            android = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}",
            common = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Versions.kotlin}"
        )
        val androidCoreTesting = MultiPlatformLibrary(
            android = "androidx.arch.core:core-testing:${Versions.Tests.androidCoreTesting}"
        )
    }

    val plugins: Map<String, String> = mapOf(
        "com.android.application" to Plugins.android,
        "com.android.library" to Plugins.android,
        "org.jetbrains.kotlin.multiplatform" to Plugins.kotlin,
        "kotlin-kapt" to Plugins.kotlin,
        "kotlin-android" to Plugins.kotlin,
        "dev.icerock.mobile.multiplatform-units" to Plugins.mokoUnits
    )
}