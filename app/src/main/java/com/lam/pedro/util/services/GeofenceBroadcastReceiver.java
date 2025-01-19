package com.lam.pedro.util.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.lam.pedro.R;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                // Gestisci l'errore
                int errorCode = geofencingEvent.getErrorCode();
                Log.e("GeofenceReceiver", "Error in geofence: " + errorCode);
                return;
            }

            // Identifica il tipo di transizione (entrata, uscita, permanenza)
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                    geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

                // Ottieni gli ID dei geofence attivati
                List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
                for (Geofence geofence : triggeringGeofences) {
                    String geofenceId = geofence.getRequestId();
                    Log.d("GeofenceReceiver", "Geofence triggered: " + geofenceId);
                }

                // Aggiungi qui la logica per gestire l'evento
                sendNotification(context, geofenceTransition);
            }
        }
    }

    private void sendNotification(Context context, int geofenceTransition) {
        // Esempio: Invio di una notifica
        String transitionType = "";
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                transitionType = "Entered geofence";
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                transitionType = "Exited geofence";
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                transitionType = "Dwelling in geofence";
                break;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "GeofenceChannel")
                .setSmallIcon(R.drawable.pedro_icon)
                .setContentTitle("Geofence Alert")
                .setContentText(transitionType)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}

