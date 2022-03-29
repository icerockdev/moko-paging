/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("com.android.library")
    id("dev.icerock.moko.gradle.android.base")
    id("dev.icerock.moko.gradle.detekt")
    id("org.jetbrains.kotlin.multiplatform")
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("dev.icerock.mobile.multiplatform.ios-framework")
}

dependencies {
    commonMainImplementation(libs.coroutines)

    commonMainApi(projects.paging)
    commonMainApi(libs.mokoUnits)
    commonMainApi(libs.mokoMvvmLiveData)
    commonMainApi(libs.mokoMvvmState)
    commonMainApi(libs.mokoResources)

    androidMainImplementation(libs.lifecycle)
}

framework {
    export(libs.mokoUnits)
    export(libs.mokoMvvmLiveData)
    export(libs.mokoMvvmState)
}
