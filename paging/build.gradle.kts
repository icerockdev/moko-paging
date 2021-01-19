/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.kotlinAndroidExtensions)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mavenPublish)
}

group = "dev.icerock.moko"
version = Deps.mokoPagingVersion

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)
    commonMainImplementation(Deps.Libs.MultiPlatform.mokoMvvmLiveData.common)
    commonMainImplementation(Deps.Libs.MultiPlatform.mokoMvvmState.common)

    androidMainImplementation(Deps.Libs.Android.appCompat)

    commonTestImplementation(Deps.Libs.Tests.kotlinTestJUnit)
    androidTestImplementation(Deps.Libs.Tests.androidCoreTesting)
    androidTestImplementation(Deps.Libs.MultiPlatform.ktorClient.android!!)
    commonTestImplementation(Deps.Libs.MultiPlatform.ktorClient.common)
    iosX64TestImplementation(Deps.Libs.MultiPlatform.ktorClient.iosX64!!)
    iosX64TestImplementation(Deps.Libs.MultiPlatform.coroutines) {
        isForce = true
    }
}

publishing {
    repositories.maven("https://api.bintray.com/maven/icerockdev/moko/moko-paging/;publish=1") {
        name = "bintray"

        credentials {
            username = System.getProperty("BINTRAY_USER")
            password = System.getProperty("BINTRAY_KEY")
        }
    }
}
