
package com.lam.pedro.util

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import com.lam.pedro.data.datasource.SecurePreferencesManager.getMyContext
import com.lam.pedro.data.dateTimeWithOffsetOrDefault
import com.lam.pedro.presentation.theme.PedroDarkGray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Shows details of a given throwable in the snackbar
 */
fun showExceptionSnackbar(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    throwable: Throwable?
) {
    scope.launch(Dispatchers.IO) {
        snackbarHostState.showSnackbar(
            message = throwable?.localizedMessage ?: "Unknown exception",
            duration = SnackbarDuration.Short
        )
    }
}

fun formatDisplayTimeStartEnd(
    startTime: Instant,
    startZoneOffset: ZoneOffset?,
    endTime: Instant,
    endZoneOffset: ZoneOffset?
): String {
    val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    val start = timeFormatter.format(dateTimeWithOffsetOrDefault(startTime, startZoneOffset))
    val end = timeFormatter.format(dateTimeWithOffsetOrDefault(endTime, endZoneOffset))
    return "$start - $end"
}

fun Modifier.placeholder(
    isLoading: Boolean,
    backgroundColor: Color = Color.Unspecified,
    shape: Shape = RoundedCornerShape(15.dp),
    showShimmerAnimation: Boolean = true
): Modifier = composed {
    val highlight = if (showShimmerAnimation) {
        PlaceholderHighlight.shimmer()
    } else {
        null
    }
    val specifiedBackgroundColor = backgroundColor.takeOrElse { PedroDarkGray.copy(0.6f) }
    Modifier.placeholder(
        color = specifiedBackgroundColor,
        visible = isLoading,
        shape = shape,
        highlight = highlight
    )
}

// Funzione per formattare l'Instant
fun formatInstant(instant: Instant): String {
    // Converte Instant in LocalDateTime utilizzando il fuso orario di sistema
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

    // Crea un formatter per la data e l'ora
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy|HH:mm")

    // Restituisce la data formattata come stringaW
    return localDateTime.format(formatter)
}


fun vibrateOnClick(context: Context = getMyContext()) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
    vibrator?.let {
        if (it.hasVibrator()) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            it.vibrate(effect)
            Log.d("Vibrating", "Vibrating phone with click effect")
        }
    }
}

fun vibrateOnLongPress(context: Context = getMyContext()) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
    vibrator?.let {
        if (it.hasVibrator()) {
            val effect = VibrationEffect.createOneShot(100L,VibrationEffect.EFFECT_HEAVY_CLICK)
            it.vibrate(effect)
            Log.d("Vibrating", "Vibrating phone with heavy click effect")
        }
    }
}
