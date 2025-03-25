package com.example.movies.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.movies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if ("ACTION_SEE_LATER".equals(intent.getAction())) {
            // Quitamos la notificacion

            int notifID = intent.getIntExtra("notifID", -1);

            // Comunicacion asincrona con la actividad
            SharedPreferences prefs = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
            int counter = prefs.getInt("counter", -1);
            if (notifID != -1 && notificationManager != null) {
                notificationManager.cancel(notifID);
                prefs.edit().putInt("counter", --counter).apply();
                if (counter == 0) {
                    notificationManager.cancelAll();
                }
            }

            // Guardo la notificacion atrasada en un fichero JSON
            JSONObject jsonNotif = new JSONObject();
            try {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    for (String key : intent.getExtras().keySet()) {
                        jsonNotif.put(key, extras.get(key));
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            try {
                // Almaceno la notificacion atrasada con el resto de notificaciones atrasadas
                JSONArray notifArray = new JSONArray(prefs.getString("pending_notifs", "[]"));
                notifArray.put(jsonNotif);
                prefs.edit().putString("pending_notifs", notifArray.toString()).apply();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        if ("ACTION_RATE".equals(intent.getAction())) {
            int rating = intent.getIntExtra("rating", -1);
            int notifID = intent.getIntExtra("notifID", -1);

            if (rating != -1 && notifID != -1) {
                // Crear el WorkRequest con los datos necesarios
                Data inputData = new Data.Builder()
                        .putInt("rating", rating)
                        .putInt("notifID", notifID)
                        .build();

                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(UpdateStarNotificationWorker.class)
                        .setInputData(inputData)
                        .build();

                // Encolar el trabajo en WorkManager
                WorkManager.getInstance(context).enqueue(workRequest);
            }

        }
    }
}