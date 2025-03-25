package com.example.movies.notifications;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.movies.handlers.NotificationHandler;

public class UpdateStarNotificationWorker extends Worker {
    private static NotificationHandler handler;

    public UpdateStarNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private void initHandler() {
        if (handler == null) {
            handler = new NotificationHandler(getApplicationContext());
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        int rating = getInputData().getInt("rating", -1);
        int notifID = getInputData().getInt("notifID", -1);
        if (rating == -1 || notifID == -1) {
            return Result.failure();
        }

        initHandler();

        Notification.Builder notification = handler.updateStarNotification(rating);
        handler.getManager().notify(notifID, notification.build());
        handler.publishGroup(true);

        return Result.success();
    }
}
