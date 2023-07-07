package com.example.taskmanager.worker;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WorkObserver {

    public void observe(Context context, UUID id) {
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(id)
                .observe((LifecycleOwner) context, workInfo -> {
                    if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                        Log.i("WORKER INFO", "Worker " + id + " was cancelled.");
                        WorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
                        WorkManager.getInstance(context).enqueue(workRequest);
                        Log.i("WORKER INFO", "New worker " + id + " started.");
                        this.observe(context, workRequest.getId());
                    }
                });
    }
}
