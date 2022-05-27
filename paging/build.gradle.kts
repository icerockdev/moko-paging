/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.moko.gradle.multiplatform.mobile")
    id("dev.icerock.moko.gradle.publication")
    id("dev.icerock.moko.gradle.stub.javadoc")
    id("dev.icerock.moko.gradle.detekt")
}

kotlin {
  jvm()
}

dependencies {
    commonMainImplementation(libs.coroutines)
    commonMainApi(libs.mokoMvvmLiveData)
    commonMainApi(libs.mokoMvvmState)

    commonTestImplementation(libs.kotlinTestJUnit)
    androidTestImplementation(libs.androidCoreTesting)
    commonTestImplementation(libs.ktorClient)
    commonTestImplementation(libs.ktorClientMock)
    iosX64TestImplementation(libs.coroutines)
}
