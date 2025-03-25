package com.example.movies.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.movies.notifications.JobWorker;
import com.example.movies.notifications.LaunchStarNotificationWorker;
import com.example.movies.notifications.UpdateStarNotificationWorker;

import java.util.Map;

public class Scheduler {
    private static  int jobid = 0;

    public static void scheduleJob(Context context, int counter, boolean priority) {

        ComponentName serviceComponent= new ComponentName(context, JobWorker.class);
        JobInfo.Builder builder = new JobInfo.Builder(jobid++, serviceComponent);

        Bundle params = new Bundle();
        params.putInt("counter", counter);
        params.putBoolean("priority", priority);

        builder.setTransientExtras(params);


        //builder.setMinimumLatency(5 * 1000); // wait at least
        //builder.setPeriodic(15*1000*1000);
        //builder.setPersisted(true);
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler= context.getSystemService(JobScheduler.class);

        SharedPreferences prefs = context.getSharedPreferences("Jobs", Context.MODE_PRIVATE);
        int limit = prefs.getInt("limit", -1);
        if (limit != -1 && counter >= limit) {
            jobScheduler.cancelAll();
        }

        jobScheduler.schedule(builder.build());
    }

    public static void scheduleWork (Context context, int notifID) {
        Data inputData = new Data.Builder()
                .putInt("notifID", notifID)
                .build();

        OneTimeWorkRequest launchWorkRequest = new OneTimeWorkRequest.Builder(LaunchStarNotificationWorker.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context)
                .beginWith(launchWorkRequest)
                .enqueue();
    }
}
