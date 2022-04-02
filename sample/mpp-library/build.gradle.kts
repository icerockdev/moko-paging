/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.mobile.multiplatform.ios-framework")
    id("dev.icerock.moko.gradle.detekt")
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
