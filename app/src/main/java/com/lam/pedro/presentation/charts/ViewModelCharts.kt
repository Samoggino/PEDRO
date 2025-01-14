package com.lam.pedro.presentation.charts

import android.util.Log
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.toMonthNumber
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.activitySupabase.ActivitySupabaseRepositoryImpl
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModelCharts(
    private val uuid: String = getUUID()!!,
    private val activityRepository: ActivitySupabaseRepositoryImpl // Aggiungi il repository come dipendenza
) : ViewModel() {

    private val _chartState = MutableLiveData<ChartState>(ChartState.Loading)
    val chartState: LiveData<ChartState> = _chartState

    private val _error = MutableLiveData<ChartError?>()
    val error: LiveData<ChartError?> = _error

    private var selectedMetric: LabelMetrics = LabelMetrics.DURATION
    private var activities: List<GenericActivity> = emptyList()

    /**
     * Carica i dati iniziali e salva la lista di attività in cache
     */
    fun loadActivityData(activityEnum: ActivityEnum) {
        _chartState.value = ChartState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                activities = activityRepository.getActivitySession(
                    activityEnum,
                    uuid
                ) // Usa il repository
                if (activities.isEmpty()) {
                    _chartState.postValue(ChartState.Error(ChartError.NoData))
                } else {
                    _chartState.postValue(
                        ChartState.Success(
                            buildBarsList(
                                activities,
                                selectedMetric
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                _chartState.postValue(ChartState.Error(ChartError.DataError("Error loading data: ${e.message}")))
            }
        }
    }


    /**
     * Cambia la metrica selezionata e aggiorna il grafico
     */

    fun changeMetric(newMetric: LabelMetrics) {
        if (chartState.value is ChartState.Loading) return // Avoid changing metric while loading

        selectedMetric = newMetric
        Log.d("Charts", "Cambio metrica: $selectedMetric")

        // Update chartState with new data
        _chartState.value = ChartState.Success(buildBarsList(activities, selectedMetric))
    }


    /**
     * Calcola il valore della metrica selezionata.
     */
    private fun calculateMetricValue(
        selectedMetric: LabelMetrics,
        activity: GenericActivity
    ): Double {
        val duration = activity.basicActivity.durationInMinutes()

        return when (selectedMetric) {
            LabelMetrics.DISTANCE -> (activity as? GenericActivity.DistanceMetrics)?.distance?.inMeters
                ?: 0.0

            LabelMetrics.TOTAL_CALORIES -> (activity as? GenericActivity.EnergyMetrics)?.totalEnergy?.inKilocalories
                ?: 0.0

            LabelMetrics.ACTIVE_CALORIES -> (activity as? GenericActivity.EnergyMetrics)?.activeEnergy?.inKilocalories
                ?: 0.0

            LabelMetrics.DURATION -> duration
        }
    }

    /**
     * Genera la lista delle barre mensili per le attività selezionate.
     */
    private fun buildBarsList(
        activities: List<GenericActivity>,
        selectedMetric: LabelMetrics
    ): List<Bars> {
        val monthlyData = mutableMapOf<Int, MutableList<Double>>()

        for (activity in activities) {
            val month = activity.basicActivity.startTime.toMonthNumber()
            val value = calculateMetricValue(
                selectedMetric,
                activity
            ) // Extract calculation to a separate function
            monthlyData.getOrPut(month) { mutableListOf() }.add(value)
        }

        return monthlyData.entries.sortedBy { it.key }.map { (month, values) ->
            Bars(
                label = month.toString(),
                values = listOf(
                    Bars.Data(
                        label = selectedMetric.toString(),
                        value = values.sum(),
                        color = Brush.verticalGradient(
                            colors = listOf(
                                activities[0].activityEnum.color,
                                activities[0].activityEnum.color
                            )
                        )
                    )
                )
            )
        }
    }
}

sealed class ChartError {
    data class DataError(val message: String) : ChartError()
    data object NoData : ChartError()
}

sealed class ChartState {
    data object Loading : ChartState()
    data class Success(val data: List<Bars>) : ChartState()
    data class Error(val error: ChartError) : ChartState()
}

fun getAvailableMetricsForActivity(activityEnum: ActivityEnum) =
    when {
        activityEnum.fullEnergyDistanceMetrics -> LabelMetrics.entries
        activityEnum.distanceMetrics -> LabelMetrics.entries.filter { it != LabelMetrics.ACTIVE_CALORIES && it != LabelMetrics.TOTAL_CALORIES }
        activityEnum.energyMetrics -> LabelMetrics.entries.filter { it != LabelMetrics.DISTANCE }
        else -> LabelMetrics.entries.filter { it == LabelMetrics.DURATION }
    }

class ChartsViewModelFactory(
    private val uuid: String,
    private val activityRepository: ActivitySupabaseRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelCharts::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelCharts(
                uuid,
                activityRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}