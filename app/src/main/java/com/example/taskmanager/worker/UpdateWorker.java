package com.example.taskmanager.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.notification.Notifier;
import com.example.taskmanager.util.DateUtil;

import java.util.List;
import java.util.logging.Logger;

public class UpdateWorker extends Worker {

    private static final Logger LOGGER = Logger.getLogger(UpdateWorker.class.getName());

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            DataManager dataManager = DataManager.getInstance(getApplicationContext());
            List<Task> allTasks = dataManager.synchronizeFromWeb();
            boolean filtered = allTasks.removeIf(this::filter);
            if (!allTasks.isEmpty() && filtered) {
                Notifier notifier = new Notifier();
                allTasks.forEach(task -> {
                    notifier.cancelAlarm(getApplicationContext(), task);
                    notifier.createAlarm(getApplicationContext(), task);
                    Log.i("INFO WORKER", "Uploaded task with TITLE = " + task.getTitle());
                });
                return Result.success();
            }
            return Result.failure();
        }catch (Exception e){
            e.printStackTrace();
            return Result.failure();
        }
    }

    public boolean filter(Task task){
        return task.getNotify().trim().isEmpty() ||
                task.isDone() ||
                DateUtil.fromStringToMillis(task.getNotify()) + (1000*60*60) < DateUtil.nowInMillis() ||
                DateUtil.fromStringToMillis(task.getNotify()) > DateUtil.nowInMillis() + (1000*60*60*24*3);
    }
}
