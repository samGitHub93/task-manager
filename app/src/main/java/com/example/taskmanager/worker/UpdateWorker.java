package com.example.taskmanager.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.taskmanager.database.AppDatabase;
import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.notification.Notifier;

import java.util.List;

public class UpdateWorker extends Worker {

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            DataManager dataManager = DataManager.getInstance(getApplicationContext());
            dataManager.synchronizeFromWeb();
            AppDatabase database = AppDatabase.getDatabase(getApplicationContext());
            List<Task> allTasks = database.taskDao().getAll();
            boolean filtered = allTasks.removeIf(t -> t.getNotify().trim().length() == 0 || t.isDone());
            if (filtered) {
                Notifier notifier = new Notifier();
                allTasks.forEach(task -> {
                    notifier.cancelAlarm(getApplicationContext(), task);
                    notifier.createAlarm(getApplicationContext(), task);
                });
                return Result.success();
            }
            return Result.failure();
        }catch (Exception e){
            e.printStackTrace();
            return Result.failure();
        }
    }
}
