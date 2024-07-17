package com.example.taskmanager.worker;

import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WorkObserver {

    private static UUID id;
    private static Observer<WorkInfo> observer;

    private static void observe(WorkManager workManager, UUID uuid) {
        Observer<WorkInfo> newObserver = createNewObserver(workManager);
        Log.i(WorkObserver.class.getName(), "Observing Worker " + uuid);
        workManager.getWorkInfoByIdLiveData(uuid).observeForever(newObserver);
    }

    public static void removeObserver(WorkManager workManager){
        if(observer != null && id != null)
            workManager.getWorkInfoByIdLiveData(id).removeObserver(observer);
    }

    public static Observer<WorkInfo> createNewObserver(WorkManager workManager){
        Observer<WorkInfo> newObserver = workInfo -> {
            if (workInfo.getState() == WorkInfo.State.CANCELLED) {
                Log.i("WORKER INFO", "Worker " + id + " was cancelled.");
                PeriodicWorkRequest newWorkRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
                workManager.enqueue(newWorkRequest);
                Log.i("WORKER INFO", "New worker " + newWorkRequest.getId() + " started.");
                observe(workManager, newWorkRequest.getId());
                id = newWorkRequest.getId();
            }
        };
        observer = newObserver;
        return newObserver;
    }
}
