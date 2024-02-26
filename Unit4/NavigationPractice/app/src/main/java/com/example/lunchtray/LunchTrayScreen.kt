/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

@Composable
fun LunchTrayApp() {
    val controller = rememberNavController()
    val currentScreenOnBackStack by controller.currentBackStackEntryAsState()
    val currentScreen =
        Screen.valueOf(currentScreenOnBackStack?.destination?.route ?: Screen.START_ORDER.name)
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            LaunchAppBar(
                currentScreen = currentScreen,
                onClick = { controller.navigateUp() },
                showArrow = currentScreen.name != Screen.START_ORDER.name
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        NavHost(
            navController = controller,
            startDestination = Screen.START_ORDER.name
        ) {
            composable(Screen.START_ORDER.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        controller.navigate(Screen.ENTREE_MENU.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            composable(Screen.ENTREE_MENU.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = { cancel(controller, viewModel) },
                    onNextButtonClicked = {
                        controller.navigate(Screen.SIDE_DISH.name)
                    },
                    onSelectionChanged = {
                        viewModel.updateEntree(it)
                    },
                    modifier = Modifier.lunchModifier(innerPadding)
                )
            }
            composable(Screen.SIDE_DISH.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = { cancel(controller, viewModel) },
                    onNextButtonClicked = {
                        controller.navigate(Screen.ACCOMPANIMENT.name)
                    },
                    onSelectionChanged = {
                        viewModel.updateSideDish(it)
                    },
                    modifier = Modifier.lunchModifier(innerPadding)
                )
            }
            composable(Screen.ACCOMPANIMENT.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = { cancel(controller, viewModel) },
                    onNextButtonClicked = {
                        controller.navigate(Screen.CHECKOUT.name)
                    },
                    onSelectionChanged = {
                        viewModel.updateAccompaniment(it)
                    },
                    modifier = Modifier.lunchModifier(innerPadding)
                )
            }
            composable(Screen.CHECKOUT.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = { cancel(controller, viewModel) },
                    onNextButtonClicked = {
                        controller.navigate(Screen.START_ORDER.name) {
                            popUpTo(Screen.START_ORDER.name) {
                                inclusive = true
                            }
                        }
                    },
                    modifier = Modifier.lunchModifier(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaunchAppBar(
    currentScreen: Screen,
    onClick: () -> Unit,
    showArrow: Boolean,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = currentScreen.title)) },
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            if (showArrow)
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
        }
    )
}

private fun cancel(controller: NavHostController, viewModel: OrderViewModel) {
    viewModel.resetOrder()
    controller.navigate(Screen.START_ORDER.name) {
        popUpTo(Screen.START_ORDER.name) {
            inclusive = true
        }
    }
}

private fun Modifier.lunchModifier(innerPadding: PaddingValues) = composed { this
    .fillMaxSize()
    .padding(innerPadding)
    .verticalScroll(
        rememberScrollState()
    ) }


private enum class Screen(@StringRes val title: Int) {
    START_ORDER(R.string.start_order),
    ENTREE_MENU(R.string.choose_entree),
    SIDE_DISH(R.string.choose_side_dish),
    ACCOMPANIMENT(R.string.choose_accompaniment),
    CHECKOUT(R.string.order_checkout),
}
