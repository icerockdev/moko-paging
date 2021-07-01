/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("multiplatform-library-convention")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform.android-manifest")
    id("publication-convention")
}

group = "dev.icerock.moko"
version = libs.versions.mokoPagingVersion.get()

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainApi(libs.mokoMvvmLiveData)
    commonMainApi(libs.mokoMvvmState)
    commonMainApi(libs.ktorClientMock)
    "androidMainImplementation"(libs.appCompat)

    commonTestImplementation(libs.kotlinTestJUnit)
    androidTestImplementation(libs.androidCoreTesting)
    commonTestImplementation(libs.ktorClient)
    iosX64TestImplementation(libs.coroutines)
}
