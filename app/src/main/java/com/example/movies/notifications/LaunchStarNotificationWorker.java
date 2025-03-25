package com.example.movies.notifications;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.movies.handlers.NotificationHandler;

public class LaunchStarNotificationWorker extends Worker {
    private static NotificationHandler handler;
    private static int notifID;

    public LaunchStarNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        notifID = workerParams.getInputData().getInt("notifID", -1);
    }

    private void initHandler() {
        if (handler == null) {
            handler = new NotificationHandler(getApplicationContext());
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        if (notifID == -1) {
            return Result.failure();
        }

        initHandler();

        NotificationJob notifJob = new NotificationJob(notifID);
        Notification.Builder notification = handler.createNotification(notifJob, true);
        handler.getManager().notify(notifID, notification.build());
        handler.publishGroup(true);

        return Result.success();
    }

}
