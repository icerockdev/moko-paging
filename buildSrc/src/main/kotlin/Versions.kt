/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

object Versions {
    object Android {
        const val compileSdk = 28
        const val targetSdk = 28
        const val minSdk = 16
    }

    const val kotlin = "1.3.61"
    const val mokoUnits = "0.2.2"

    object Plugins {
        const val android = "3.5.2"

        const val kotlin = Versions.kotlin
        const val mokoUnits = Versions.mokoUnits
    }

    object Libs {
        object Android {
            const val appCompat = "1.1.0"
            const val recyclerView = "1.0.0"
            const val lifecycle = "2.0.0"
        }

        object MultiPlatform {
            const val coroutines = "1.3.3"
            const val mokoPaging = "0.1.0"
            const val mokoMvvm = "0.4.0"
            const val mokoUnits = Versions.mokoUnits
        }
    }
}