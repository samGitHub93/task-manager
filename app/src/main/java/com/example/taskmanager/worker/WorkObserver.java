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

    private static WorkObserver INSTANCE;
    private final Context context;

    private WorkObserver(Context context){
        this.context = context;
    }

    public static WorkObserver getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new WorkObserver(context);
            return INSTANCE;
        }
        return INSTANCE;
    }

    public void observe(UUID id) {
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(id)
                .observe((LifecycleOwner) context, workInfo -> {
                    if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                        Log.i("WORKER INFO", "Worker " + id + " was cancelled.");
                        WorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
                        WorkManager.getInstance(context).enqueue(workRequest);
                        Log.i("WORKER INFO", "New worker " + id + " started.");
                        this.observe(workRequest.getId());
                    }
                });
    }
}
