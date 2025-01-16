package com.lam.pedro.presentation.charts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lam.pedro.data.activity.ActivityEnum
import com.lam.pedro.data.activity.GenericActivity
import com.lam.pedro.data.activity.toMonthNumber
import com.lam.pedro.data.datasource.SecurePreferencesManager.getUUID
import com.lam.pedro.data.datasource.activitySupabase.ActivitySupabaseSupabaseRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.IsoFields


fun Instant.toWeekOfYear(zoneId: ZoneId = ZoneId.of("UTC")): Int {
    // Converte l'Instant in ZonedDateTime con il fuso orario specificato
    val zonedDateTime = this.atZone(zoneId)

    // Restituisce il numero della settimana dell'anno
    return zonedDateTime.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
}

class ViewModelCharts(
    private val activityRepository: ActivitySupabaseSupabaseRepositoryImpl // Aggiungi il repository come dipendenza
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
    fun loadActivityData(
        activityEnum: ActivityEnum,
        metric: LabelMetrics,
        uuid: String = getUUID()!!,
        timePeriod: TimePeriod
    ) {
        _chartState.value = ChartState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                activities = activityRepository.getActivitySession(activityEnum, uuid)

                if (activities.isEmpty()) {
                    _chartState.postValue(ChartState.Error(ChartError.NoData, "No data available"))
                } else {
                    _chartState.postValue(
                        ChartState.Success(
                            buildBarsList(
                                activities,
                                metric,
                                timePeriod
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                _chartState.postValue(
                    ChartState.Error(
                        ChartError.DataError("Error loading data: ${e.message}"),
                        e.message ?: "Unknown error"
                    )
                )
            }
        }
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
    fun buildBarsList(
        activities: List<GenericActivity>,
        selectedMetric: LabelMetrics,
        timePeriod: TimePeriod
    ): Map<String, Double> {
        val timeData = mutableMapOf<String, MutableList<Double>>()

        // Popola la mappa con i dati delle attività, raggruppati per il periodo selezionato
        for (activity in activities) {
            val value = calculateMetricValue(selectedMetric, activity)
            val periodKey = when (timePeriod) {

                TimePeriod.WEEKLY -> {
                    val weekOfYear = activity.basicActivity.startTime.toWeekOfYear()
                    "Week $weekOfYear"
                }

                TimePeriod.MONTHLY -> {
                    val month = activity.basicActivity.startTime.toMonthNumber()
                    val monthNames = listOf(
                        "Jan",
                        "Feb",
                        "Mar",
                        "Apr",
                        "May",
                        "Jun",
                        "Jul",
                        "Aug",
                        "Sep",
                        "Oct",
                        "Nov",
                        "Dec"
                    )
                    monthNames[month - 1] // Ottieni il nome del mese
                }

                else -> {
                    val day =
                        activity.basicActivity.startTime.atZone(ZoneId.systemDefault()).dayOfMonth
                    day.toString()
                }
            }


            // Aggiungi il valore alla lista corrispondente al periodo
            timeData.getOrPut(periodKey) { mutableListOf() }.add(value)
        }

        // Restituisci una mappa con il periodo come chiave e il valore aggregato
        return timeData.mapValues { entry -> entry.value.sum() }
    }


}


fun getAvailableMetricsForActivity(activityEnum: ActivityEnum) =
    when {
        activityEnum.fullEnergyDistanceMetrics -> LabelMetrics.entries
        activityEnum.distanceMetrics -> LabelMetrics.entries.filter { it != LabelMetrics.ACTIVE_CALORIES && it != LabelMetrics.TOTAL_CALORIES }
        activityEnum.energyMetrics -> LabelMetrics.entries.filter { it != LabelMetrics.DISTANCE }
        else -> LabelMetrics.entries.filter { it == LabelMetrics.DURATION }
    }

class ChartsViewModelFactory(
    private val activityRepository: ActivitySupabaseSupabaseRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelCharts::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelCharts(activityRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}