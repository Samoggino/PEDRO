package com.lam.pedro.presentation.charts


sealed class ChartError {
    data class DataError(val message: String) : ChartError()
    data object NoData : ChartError()
}

sealed class ChartState {
    data object Loading : ChartState()
    data class Success(val data: Map<String, Double>) : ChartState()
    data class Error(val error: ChartError, val message: String) : ChartState()
}