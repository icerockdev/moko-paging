/*
 * Copyright 2022 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.paging.sample.declarativeui.android

import androidx.compose.runtime.Composable
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.compose.material.Text
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ComposeApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            ListScreen()
        }
    }
}
