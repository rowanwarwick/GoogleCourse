package com.example.dessertclicker.ui

import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import com.example.dessertclicker.ui.model.DessertUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertViewModel : ViewModel() {

    private val data = Datasource.dessertList.sortedBy{ it.startProductionAmount }
    private val state: DessertUiState = DessertUiState(
        revenue = 0,
        dessertsSold = 0,
        currentDessertPrice = data.first().price,
        currentDessertImageId = data.first().imageId,
    )

    private val _uiState: MutableStateFlow<DessertUiState> = MutableStateFlow(state)
    val uiState = _uiState.asStateFlow()

    fun clickOnDessert() {
        val dessertToShow = determineDessertToShow(_uiState.value.dessertsSold)
        _uiState.update { state ->
            state.copy(
                revenue = state.revenue + state.currentDessertPrice,
                dessertsSold = state.dessertsSold.inc(),
                currentDessertImageId = dessertToShow.imageId,
                currentDessertPrice = dessertToShow.price
            )
        }
    }

    private fun determineDessertToShow(dessertsSold: Int): Dessert {
        val dataReverse = data.reversed()
        for (dessert in dataReverse) {
            if (dessertsSold >= dessert.startProductionAmount - 1) return dessert
        }
        return data.first()
    }

}