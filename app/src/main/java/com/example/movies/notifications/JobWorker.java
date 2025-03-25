package com.example.movies.notifications;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.example.movies.R;
import com.example.movies.handlers.NotificationHandler;
import com.example.movies.utils.Scheduler;

public class JobWorker extends JobService {

    private static NotificationHandler handler;
    private static int limit = -1;

    @Override
    public boolean onStartJob(JobParameters params) {
        boolean out;
        initHandler();

        int counter = params.getTransientExtras().getInt("counter");
        boolean priority = params.getTransientExtras().getBoolean("priority");

        out = launchNotification(counter, priority);

        Scheduler.scheduleJob(getApplicationContext(), ++counter, priority); // reschedule the job0
        return out;
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void initHandler() {
        if (handler == null) {
            handler = new NotificationHandler(this);
        }
    }


    public boolean launchNotification (int counter, boolean priority) {

        initHandler();
        NotificationJob notifJob = selectNotification(counter);
        if (notifJob == null) {
            return false;
        }
        notifJob.setNotifID(counter);
        Notification.Builder notification = handler.createNotification(notifJob, priority);
        handler.getManager().notify(counter, notification.build());
        handler.publishGroup(priority);

        return true;
    }

    private NotificationJob selectNotification (int counter) {
        Resources res = getResources();
        NotificationJob notification = null;

        if (counter == 0) {
            SharedPreferences prefs = getSharedPreferences("Notifications", Context.MODE_PRIVATE);
            prefs.edit().putInt("counter", 0).apply();
        }

        try (TypedArray resArray = res.obtainTypedArray(R.array.notifications)) {
            if (limit == -1) {
                limit = resArray.length();
                SharedPreferences prefs = getSharedPreferences("Jobs", Context.MODE_PRIVATE);
                prefs.edit().putInt("limit", limit).apply();
            }

            if (counter >= resArray.length()) {
                return null;
            }

            int id = resArray.getResourceId(counter, 0);
            if (id > 0) {
                String [] resNot = res.getStringArray(id);
                int imgId = getResources().getIdentifier(resNot[2], "drawable", getPackageName());
                notification = new NotificationJob(res.getResourceEntryName(id), resNot[0], resNot[1], imgId);
            }
        }
        return notification;
    }

}
