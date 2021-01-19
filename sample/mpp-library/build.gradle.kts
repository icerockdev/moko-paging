/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.iosFramework)
}

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)

    commonMainApi(Deps.Libs.MultiPlatform.mokoPaging)
    commonMainApi(Deps.Libs.MultiPlatform.mokoUnits.common)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmLiveData.common)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmState.common)
    commonMainApi(Deps.Libs.MultiPlatform.mokoResources)

    androidMainImplementation(Deps.Libs.Android.lifecycle)
}

framework {
    export(Deps.Libs.MultiPlatform.mokoUnits)
    export(Deps.Libs.MultiPlatform.mokoMvvmLiveData)
    export(Deps.Libs.MultiPlatform.mokoMvvmState)
}
