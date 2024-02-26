package com.example.dessertclicker.ui.model

import androidx.annotation.DrawableRes

data class DessertUiState(
    val revenue: Int,
    val dessertsSold: Int,
    val currentDessertPrice: Int,
    @DrawableRes val currentDessertImageId: Int,
)