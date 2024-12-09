package com.lam.pedro.presentation.charts

import android.util.Log
import androidx.compose.ui.graphics.Brush
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.toMonthNumber
import com.lam.pedro.presentation.serialization.ViewModelRecords
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ViewModelCharts(
    private val viewModelRecords: ViewModelRecords
) : ViewModel() {

    private val _barsList = MutableLiveData<List<Bars>>()
    val barsList: LiveData<List<Bars>> = _barsList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var selectedMetric: LabelMetrics = LabelMetrics.DURATION
    private var cachedActivities: List<GenericActivity> = emptyList()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Carica i dati iniziali e salva la lista di attività in cache
     */
    fun loadActivityData(activityType: ActivityType) {

        Log.d("Charts", "Caricamento dati nel viewModel $activityType")
        _isLoading.value = true // Inizia il caricamento
        viewModelScope.launch(Dispatchers.IO) {
            try {
                cachedActivities = viewModelRecords.getActivitySession(activityType)
                _barsList.postValue(
                    generateBarsList(cachedActivities, selectedMetric)
                )
                Log.d("Charts", "Dati caricati con successo")
            } catch (e: Exception) {
                _error.postValue("Errore durante il caricamento dei dati: ${e.message}")
            } finally {
                _isLoading.postValue(false) // Segnala la fine del caricamento
                Log.d("Charts", "Fine caricamento")
            }
        }

    }

    /**
     * Cambia la metrica selezionata e aggiorna il grafico
     */

    fun changeMetric(newMetric: LabelMetrics) {
        if (_isLoading.value == true) return // Evita di cambiare la metrica se già in caricamento
        _isLoading.value = true
        selectedMetric = newMetric
        Log.d("Charts", "Cambio metrica: $selectedMetric")
        _barsList.value = generateBarsList(cachedActivities, selectedMetric)
        _isLoading.value = false
    }


    /**
     * Calcola il valore della metrica selezionata.
     */
    private fun metrics(
        selectedMetric: LabelMetrics,
        distance: Length? = null,
        elevationGained: Length? = null,
        totalCalories: Energy? = null,
        activeCalories: Energy? = null,
        duration: Double
    ): Double {
        return when (selectedMetric) {
            LabelMetrics.DISTANCE -> distance?.inMeters ?: 0.0
            LabelMetrics.ELEVATION -> elevationGained?.inMeters ?: 0.0
            LabelMetrics.TOTAL_CALORIES -> totalCalories?.inKilocalories ?: 0.0
            LabelMetrics.ACTIVE_CALORIES -> activeCalories?.inKilocalories ?: 0.0
            LabelMetrics.DURATION -> duration
        }
    }

    /**
     * Genera la lista delle barre mensili per le attività selezionate.
     */
    private fun generateBarsList(
        activities: List<GenericActivity>,
        selectedMetric: LabelMetrics
    ): List<Bars> {
        return activities.map { it }
            .toMonthlyBarsList(
                getValue = { session ->

                    val duration = session.basicActivity.durationInMinutes()

                    try {
                        when (session) {
                            is GenericActivity.FullMetrics -> {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    distance = session.distance,
                                    elevationGained = session.elevationGained,
                                    totalCalories = session.totalEnergy,
                                    activeCalories = session.activeEnergy,
                                    duration = duration
                                )
                            }

                            is GenericActivity.DistanceMetrics -> {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    distance = session.distance,
                                    elevationGained = session.elevationGained,
                                    duration = duration
                                )
                            }

                            is GenericActivity.EnergyMetrics -> {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    totalCalories = session.totalEnergy,
                                    activeCalories = session.activeEnergy,
                                    duration = duration
                                )
                            }

                            else -> {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    duration = duration
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Charts", "Errore durante il calcolo della metrica: ${e.message}", e)
                        0.0 // In caso di errore, restituisci un valore di fallback
                    }
                },
                label = selectedMetric
            )
    }

}


private fun <T> List<T>.toMonthlyBarsList(
    getValue: (T) -> Double,
    label: LabelMetrics
): List<Bars> where T : GenericActivity {
    return this
        .groupBy { it.basicActivity.startTime.toMonthNumber() } // Raggruppiamo per mese
        .toList()
        .sortedBy { it.first }
        .map { (month, sessionsInMonth) ->
            Bars(
                label = month.toString(),
                values = listOf(
                    Bars.Data(
                        label = label.toString(),
                        value = sessionsInMonth.sumOf { session -> getValue(session) },
                        color = Brush.verticalGradient(
                            colors = listOf(
                                sessionsInMonth[0].activityType.color,
                                sessionsInMonth[0].activityType.color,
                            )
                        )
                    )
                )
            )
        }
}

fun availableMetricsFilter(activityType: ActivityType) =
    when {
        activityType.fullEnergyDistanceMetrics -> LabelMetrics.entries
        activityType.distanceMetrics -> LabelMetrics.entries.filter { it != LabelMetrics.ACTIVE_CALORIES && it != LabelMetrics.TOTAL_CALORIES }
        activityType.energyMetrics -> LabelMetrics.entries.filter { it != LabelMetrics.DISTANCE && it != LabelMetrics.ELEVATION }
        else -> LabelMetrics.entries.filter { it == LabelMetrics.DURATION }
    }


@Suppress("UNCHECKED_CAST")
class ViewModelChartsFactory :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelCharts::class.java)) {
            return ViewModelCharts(
                viewModelRecords = ViewModelRecords()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
