package com.lam.pedro.presentation.charts

import androidx.compose.ui.graphics.Brush
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityType
import com.lam.pedro.data.activity.ActivityType.CYCLING
import com.lam.pedro.data.activity.ActivityType.DRIVE
import com.lam.pedro.data.activity.ActivityType.LIFT
import com.lam.pedro.data.activity.ActivityType.RUN
import com.lam.pedro.data.activity.ActivityType.TRAIN
import com.lam.pedro.data.activity.ActivityType.WALK
import com.lam.pedro.data.activity.ActivityType.YOGA
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.GenericActivity.CyclingSession
import com.lam.pedro.data.activity.GenericActivity.RunSession
import com.lam.pedro.data.activity.GenericActivity.TrainSession
import com.lam.pedro.data.activity.GenericActivity.WalkSession
import com.lam.pedro.data.activity.toMonthNumber
import com.lam.pedro.presentation.serialization.ViewModelRecords
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.launch


class ViewModelCharts(
    private val viewModelRecords: ViewModelRecords
) : ViewModel() {

    private val _barsList = MutableLiveData<List<Bars>>()
    val barsList: LiveData<List<Bars>> = _barsList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var selectedMetric: LabelMetrics = LabelMetrics.Distance
    private var cachedActivities: List<GenericActivity> = emptyList()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * Carica i dati iniziali e salva la lista di attività in cache
     */
    fun loadActivityData(activityType: ActivityType) {
        _isLoading.value = true // Inizia il caricamento
        viewModelScope.launch {
            try {
                val activities = viewModelRecords.getActivitySession(activityType)
                cachedActivities = activities
                _barsList.postValue(generateBarsList(activities, activityType, selectedMetric))
            } catch (e: Exception) {
                _error.postValue("Errore durante il caricamento dei dati: ${e.message}")
            } finally {
                _isLoading.postValue(false) // Fine caricamento
            }
        }
    }

    /**
     * Cambia la metrica selezionata e aggiorna il grafico
     */

    fun changeMetric(newMetric: LabelMetrics, activityType: ActivityType) {
        _isLoading.value = true // Inizia il caricamento
        selectedMetric = newMetric
        _barsList.postValue(generateBarsList(cachedActivities, activityType, selectedMetric))
        _isLoading.value = false // Fine caricamento
    }

    /**
     * Calcola il valore della metrica selezionata
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
            LabelMetrics.Distance -> distance?.inMeters ?: 0.0
            LabelMetrics.Elevation -> elevationGained?.inMeters ?: 0.0
            LabelMetrics.TotalCalories -> totalCalories?.inKilocalories ?: 0.0
            LabelMetrics.ActiveCalories -> activeCalories?.inKilocalories ?: 0.0
            LabelMetrics.Duration -> duration
        }
    }


    private fun generateBarsList(
        activities: List<GenericActivity>,
        activityType: ActivityType,
        selectedMetric: LabelMetrics
    ): List<Bars> {

        when {
            activityType.energyMetrics && activityType.distanceMetrics -> {

                when (activityType) {

                    WALK -> {
                        val sessions = activities.map { it as WalkSession }
                        return sessions.toMonthlyBarsList(
                            getValue = {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    distance = it.distance,
                                    elevationGained = it.elevationGained,
                                    totalCalories = it.totalEnergy,
                                    activeCalories = it.activeEnergy,
                                    duration = it.basicActivity.durationInMinutes()
                                )
                            },
                            label = selectedMetric
                        )
                    }

                    CYCLING -> {
                        val sessions = activities.map { it as CyclingSession }
                        return sessions.toMonthlyBarsList(
                            getValue = {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    distance = it.distance,
                                    elevationGained = it.elevationGained,
                                    totalCalories = it.totalEnergy,
                                    activeCalories = it.activeEnergy,
                                    duration = it.basicActivity.durationInMinutes()
                                )
                            },
                            label = selectedMetric
                        )
                    }

                    RUN -> {
                        val sessions = activities.map { it as RunSession }
                        return sessions.toMonthlyBarsList(
                            getValue = {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    distance = it.distance,
                                    elevationGained = it.elevationGained,
                                    totalCalories = it.totalEnergy,
                                    activeCalories = it.activeEnergy,
                                    duration = it.basicActivity.durationInMinutes()
                                )
                            },
                            label = selectedMetric
                        )
                    }

                    else -> return emptyList()
                }


            }

            activityType.distanceMetrics -> when (activityType) {

                DRIVE -> {
                    val sessions = activities.map { it as GenericActivity.DriveSession }
                    return sessions.toMonthlyBarsList(
                        getValue = {
                            metrics(
                                selectedMetric = selectedMetric,
                                distance = it.distance,
                                elevationGained = it.elevationGained,
                                duration = it.basicActivity.durationInMinutes()
                            )
                        },
                        label = selectedMetric
                    )
                }


                else -> return emptyList()
            }

            activityType.energyMetrics -> {

                when (activityType) {
                    TRAIN -> {
                        val sessions = activities.map { it as TrainSession }
                        return sessions.toMonthlyBarsList(
                            getValue = {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    totalCalories = it.totalEnergy,
                                    activeCalories = it.activeEnergy,
                                    duration = it.basicActivity.durationInMinutes()
                                )
                            },
                            label = selectedMetric
                        )
                    }

                    YOGA -> {
                        val sessions = activities.map { it as GenericActivity.YogaSession }
                        return sessions.toMonthlyBarsList(
                            getValue = {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    totalCalories = it.totalEnergy,
                                    activeCalories = it.activeEnergy,
                                    duration = it.basicActivity.durationInMinutes()
                                )
                            },
                            label = selectedMetric
                        )
                    }

                    LIFT -> {
                        val sessions = activities.map { it as GenericActivity.LiftSession }
                        return sessions.toMonthlyBarsList(
                            getValue = {
                                metrics(
                                    selectedMetric = selectedMetric,
                                    totalCalories = it.totalEnergy,
                                    activeCalories = it.activeEnergy,
                                    duration = it.basicActivity.durationInMinutes()
                                )
                            },
                            label = selectedMetric
                        )
                    }

                    else -> return emptyList()
                }

            }

            else -> return emptyList()
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
