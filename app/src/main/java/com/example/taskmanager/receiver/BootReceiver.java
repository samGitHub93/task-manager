package com.example.taskmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.taskmanager.worker.UpdateWorker;

import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            WorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
            WorkManager.getInstance(context).enqueue(workRequest);
        }
    }
}
