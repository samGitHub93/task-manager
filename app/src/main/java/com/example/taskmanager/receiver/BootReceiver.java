package com.example.taskmanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Observer;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.taskmanager.worker.UpdateWorker;
import com.example.taskmanager.worker.WorkObserver;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            WorkManager workManager = WorkManager.getInstance(context);
            WorkObserver.removeObserver(workManager);
            workManager.cancelAllWork();
            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
            workManager.enqueue(workRequest);
            Observer<WorkInfo> newObserver = WorkObserver.createNewObserver(workManager);
            workManager.getWorkInfoByIdLiveData(workRequest.getId()).observeForever(newObserver);
        }
    }
}
