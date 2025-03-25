package com.example.movies.handlers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.movies.MainActivity;
import com.example.movies.R;
import com.example.movies.notifications.NotificationJob;
import com.example.movies.notifications.NotificationReceiver;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NotificationHandler extends ContextWrapper {

    public static final String CHANNEL_HIGH_ID = "1";
    public static final String CHANNEL_LOW_ID = "2";

    public static final String CHANNEL_HIGH_NAME = "HIGH CHANNEL";
    public static final String CHANNEL_LOW_NAME = "LOW CHANNEL";

    private static final String GROUP_NAME = "GROUP";
    private static final int GROUP_ID = 24;

    private NotificationManager manager;


    public NotificationHandler(Context base) {
        super(base);
        // Creacion Canales
        createChannels();
    }


    private void createChannels () {

        // Crear Canal de Prioridad Alta
        NotificationChannel highChannel = new NotificationChannel(CHANNEL_HIGH_ID, CHANNEL_HIGH_NAME, NotificationManager.IMPORTANCE_HIGH);
        // Configuraciones
        highChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        // Creal Canal de Prioridad Baja
        NotificationChannel lowChannel = new NotificationChannel(CHANNEL_LOW_ID, CHANNEL_LOW_NAME, NotificationManager.IMPORTANCE_LOW);
        // Configuraciones
        lowChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);

        getManager().createNotificationChannel(highChannel);
        getManager().createNotificationChannel(lowChannel);

    }

    public NotificationManager getManager () {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        return manager;
    }

    public Notification.Builder createNotification (NotificationJob notifJob, boolean priority) {
        return priority ? sendRatingNotification(notifJob.getNotifID()):
                createNotificationsChannel(notifJob, CHANNEL_LOW_ID);
    }


    private Notification.Builder createNotificationsChannel(NotificationJob notifJob, String channel) {

        notifJob.setGroupID(GROUP_ID);
        notifJob.setGroupName(GROUP_NAME);

        PendingIntent notifIntent = createNotifIntent(notifJob);
        PendingIntent notifActionIntent = createActionNotifIntent(notifJob);

        // Creamos la accion Launch (Cambiar valor resId, he puesto 0)
        Notification.Action action = new Notification.Action.Builder(
                Icon.createWithResource(this, R.drawable.baseline_rocket_launch_24), "SEE LATER", notifActionIntent).build();
        Bitmap notifBm = BitmapFactory.decodeResource(getResources(), notifJob.getImageId());
        return new Notification.Builder(getApplicationContext(), channel)
                .setContentTitle(notifJob.getTitle())
                .setContentText(notifJob.getMsg())
                .setSmallIcon(R.drawable.ic_stat_popcorn)
                .setLargeIcon(notifBm)
                .setAutoCancel(true)
                .addAction(action)
                .setGroup(GROUP_NAME)
                .setContentIntent(notifIntent);
    }

    private Notification.Builder sendRatingNotification (int notifID) {

        // Layout personalizado
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_rating);

        // Asigna acciones a cada estrella
        for (int i = 1; i <= 5; i++) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.setAction("ACTION_RATE");
            intent.putExtra("rating", i);
            intent.putExtra("notifID", notifID);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Asocia el PendingIntent con el clic en la estrella
            remoteViews.setOnClickPendingIntent(getStarId(i), pendingIntent);
        }


        // Construcción de la notificación
        return new Notification.Builder(getApplicationContext(), CHANNEL_HIGH_ID)
                .setSmallIcon(R.drawable.ic_stat_popcorn)
                .setContentTitle("Califica con estrellas")
                .setContentText("Por favor, no te vayas... :( \n¿Te importaria valorar nuestra aplicacion?")
                .setCustomContentView(remoteViews)
                .setGroup(GROUP_NAME)
                .setAutoCancel(true);

    }

    public Notification.Builder updateStarNotification (int rating) {

        // Layout personalizado actualizado
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_rating);

        // Rellena las estrellas según la calificación seleccionada
        for (int i = 1; i <= 5; i++) {
            int icon = (i <= rating) ? R.drawable.baseline_star_rate_24 : R.drawable.baseline_star_outline_24;
            remoteViews.setImageViewResource(getStarId(i), icon);
        }

        remoteViews.setTextViewText(R.id.not_rating_tv, "¡Muchisimas gracias!");

        // Construye y actualiza la notificación
        return new Notification.Builder(getApplicationContext(), CHANNEL_HIGH_ID)
                .setSmallIcon(R.drawable.ic_stat_popcorn)
                .setCustomContentView(remoteViews)
                .setContentText("Tu opinión nos importa. Nos ayuda a prestar mejores servicios. De nuevo, ¡Gracias!")
                .setGroup(GROUP_NAME)
                .setAutoCancel(true);
    }

    public PendingIntent createActionNotifIntent (NotificationJob notifJob) {
        // Creamos el intent y le añadimos el titulo y mensaje (Cambiar el null por class que quieraas en el Itent)
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("notifCode", notifJob.getNotifCode());
        intent.putExtra("title", notifJob.getTitle());
        intent.putExtra("msg", notifJob.getMsg());
        intent.putExtra("imageUri", notifJob.getImageId());
        intent.putExtra("notifID", notifJob.getNotifID());
        intent.putExtra("groupName", notifJob.getGroupName());
        intent.putExtra("groupID", notifJob.getGroupID());
        // Configurar flags a nivel de intent
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // configurar la accion
        intent.setAction("ACTION_SEE_LATER");

        //Necesito saber cuando notificaciones van para cancelar el grupo
        SharedPreferences prefs = getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        int counter = prefs.getInt("counter", -1);
        if (counter != -1) {
            prefs.edit().putInt("counter", ++counter).apply();
        }
        // Creamos el Pending Intent
        return PendingIntent.getBroadcast(this, notifJob.getNotifID(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent createNotifIntent (NotificationJob notifJob) {
        // Creamos el intent y le añadimos el titulo y mensaje (Cambiar el null por class que quieraas en el Itent)
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notifCode", notifJob.getNotifCode());
        intent.putExtra("title", notifJob.getTitle());
        intent.putExtra("msg", notifJob.getMsg());
        intent.putExtra("imageUri", notifJob.getImageId());
        // Configurar flags a nivel de intent
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // Creamos el Pending Intent
        return PendingIntent.getActivity(this, notifJob.getNotifID(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // Devuelve el ID del recurso de la estrella
    private int getStarId(int starPosition) {
        switch (starPosition) {
            case 1: return R.id.star_1;
            case 2: return R.id.star_2;
            case 3: return R.id.star_3;
            case 4: return R.id.star_4;
            case 5: return R.id.star_5;
            default: throw new IllegalArgumentException("Posición inválida: " + starPosition);
        }
    }


    public void publishGroup (boolean priority) {
        String channel = priority ? CHANNEL_HIGH_ID:CHANNEL_LOW_ID;

        Notification groupNotification = new Notification.Builder(this, channel)
                .setGroup(GROUP_NAME)
                .setGroupSummary(true)
                .setSmallIcon(R.drawable.ic_stat_popcorn)
                        .setAutoCancel(true).build();

        getManager().notify(GROUP_ID, groupNotification);


    }

}

