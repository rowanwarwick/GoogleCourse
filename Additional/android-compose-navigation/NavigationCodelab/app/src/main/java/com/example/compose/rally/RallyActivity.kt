/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.collection.forEach
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.compose.rally.ui.accounts.AccountsScreen
import com.example.compose.rally.ui.accounts.SingleAccountScreen
import com.example.compose.rally.ui.bills.BillsScreen
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.overview.OverviewScreen
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
}

@Composable
fun RallyApp() {
    RallyTheme {
        val controller: NavHostController = rememberNavController()
        val currentBackStack by controller.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = rallyTabRowScreens,
                    onTabSelected = { screen -> controller.navigateSingleTapTo(screen.route) },
                    currentScreen = rallyTabRowScreens.find { it.route == currentDestination?.route }
                        ?: Overview
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = controller,
                startDestination = Overview.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Overview.route) {
                    OverviewScreen(
                        onClickSeeAllAccounts = { controller.navigateSingleTapTo(Accounts.route) },
                        onClickSeeAllBills = { controller.navigateSingleTapTo(Bills.route) },
                        onAccountClick = { type -> controller.navigateSingleTapTo("${SingleAccount.route}/$type") }
                    )
                }
                composable(route = Accounts.route) {
                    AccountsScreen(
                        onAccountClick = { controller.navigateSingleTapTo("${SingleAccount.route}/$it") }
                    )
                }
                composable(route = Bills.route) {
                    BillsScreen()
                }
                composable(
                    route = "${SingleAccount.route}/{${SingleAccount.accountTypeArg}}",
                    arguments = listOf(
                        navArgument(SingleAccount.accountTypeArg) { type = NavType.StringType }
                    ),
                    deepLinks = listOf(
                        navDeepLink { uriPattern = "rally://${SingleAccount.route}/{${SingleAccount.accountTypeArg}}" }
                    )
                ) {
                    val accountType = it.arguments?.getString(SingleAccount.accountTypeArg)
                    SingleAccountScreen(accountType)
                }
            }
        }
    }
}

private fun NavHostController.navigateSingleTapTo(route: String) {
    this.navigate(route) {
        popUpTo(this@navigateSingleTapTo.graph.id) {
            saveState = true
        }
        restoreState = true
        launchSingleTop = true
    }
}