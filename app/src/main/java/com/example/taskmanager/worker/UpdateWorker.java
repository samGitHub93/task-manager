package com.example.taskmanager.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.taskmanager.database.AppDatabase;
import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.DateUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
            boolean filtered = allTasks.removeIf(t -> t.getNotify().trim().length() == 0 || DateUtil.fromStringToMillis(t.getNotify()) < DateUtil.nowInMillis());
            if (filtered) {
                allTasks.forEach(task -> {
                    WorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                            .setInitialDelay(DateUtil.fromStringToMillis(task.getNotify()) - DateUtil.nowInMillis(), TimeUnit.MILLISECONDS)
                            .setInputData(new Data.Builder().putLong("ID", task.getId()).putString("TITLE", task.getTitle()).putString("TEXT", task.getText()).putString("DATE", task.getDate()).build())
                            .build();
                    WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
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
