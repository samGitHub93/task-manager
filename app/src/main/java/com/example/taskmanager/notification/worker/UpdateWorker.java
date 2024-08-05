package com.example.taskmanager.notification.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.taskmanager.notification.NotificationChannelCreator;
import com.example.taskmanager.notification.NotificationTaskCreator;
import com.example.taskmanager.repository.online_database.Synchronizer;
import com.example.taskmanager.enumerator.RecurringType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.notification.Notifier;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.StringUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UpdateWorker extends Worker {

    private List<Task> allTasks;
    private Synchronizer synchronizer;

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            synchronizer = new Synchronizer(getApplicationContext());
            allTasks = synchronizer.directlyGetAll();
            if(allTasks.isEmpty()) {
                Log.e(UpdateWorker.class.getName(), "Tasks list is empty.");
                return Result.failure();
            }
            saveRecurringTasks();
            deleteOldTasks();
            createAlarms();
            // sendNotification(); ONLY FOR TEST
            return Result.success();
        }catch (Exception e){
            Log.e(UpdateWorker.class.getName(), e.getMessage(), e);
            return Result.failure();
        }
    }

    private void saveRecurringTasks() throws ParseException {
        List<Task> tasks = new ArrayList<>(allTasks);
        long newId = createNewId(tasks);
        List<Task> recurringTasks = tasks.stream().filter(t -> t.getRecurringType() != RecurringType.NONE && !t.isDone() && DateUtil.fromStringDateToMillis(t.getRecurringUntil()) > DateUtil.nowInMillis()).collect(Collectors.toList());
        for (Task task : recurringTasks) {
            Task newTask = createNewRecurringTask(task, newId);
            List<Task> taskByDate = recurringTasks.stream().filter((t) -> t.isEqual(newTask)).collect(Collectors.toList());
            Log.i(UpdateWorker.class.getName(), "ADDING RECURRENT TASK: " + newTask.getTitle() + " | " + newTask.getText());
            if (taskByDate.isEmpty() && !DateUtil.getDateFromString(task.getDate()).after(DateUtil.getTodayWithoutTime()) && !DateUtil.getDateFromString(newTask.getDate()).before(DateUtil.getTodayWithoutTime())) {
                synchronizer.directlyInsert(allTasks, newTask);
                Log.i(UpdateWorker.class.getName(), "ADDED TASK: " + newTask.getTitle() + " | " + newTask.getText());
            }
        }
    }

    private void deleteOldTasks() {
        List<Task> tasks = new ArrayList<>(allTasks);
        List<Task> filteredTasks = tasks.stream().filter(t -> DateUtil.getDateFromString(t.getDate()).before(DateUtil.getDatePlusYears(DateUtil.getTodayWithoutTime(), -1)) && t.isDone()).collect(Collectors.toList());
        if(filteredTasks.isEmpty())
            return;
        synchronizer.directlyDeleteOldTasks(allTasks, filteredTasks);
    }

    private void createAlarms(){
        List<Task> tasks = new ArrayList<>(allTasks);
        List<Task> filteredTasks = tasks.stream().filter(this::hasNotification).collect(Collectors.toList());
        if (!filteredTasks.isEmpty()) {
            Notifier notifier = new Notifier();
            filteredTasks.forEach(task -> {
                notifier.cancelAlarm(getApplicationContext(), task);
                notifier.createAlarm(getApplicationContext(), task);
                Log.i("INFO WORKER", "Created alarm for " + task.getTitle());
            });
        }
    }

    private void sendNotification(){
        NotificationChannel channel = createChannel();
        Notification notification = createNotification(channel);
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(3, notification);
    }

    private boolean hasNotification(Task task){
        if(!task.getNotify().replaceAll(" ", "").isEmpty()) {
            long nowInMillis = DateUtil.nowInMillis();
            long notifyInMillis = DateUtil.fromStringDateTimeToMillis(task.getNotify());
            return !task.getNotify().replaceAll(" ", "").isEmpty() &&
                    !task.isDone() &&
                    notifyInMillis + (1000*60*5) > nowInMillis &&
                    notifyInMillis < nowInMillis + (1000*60*60*24*3);
        } else return false;
    }

    private Task createNewRecurringTask(Task task, long newId) throws ParseException {
        Task newTask = new Task();
        newTask.setId(newId);
        newTask.setDate(StringUtil.capFirstCharacter(manageDate(task)));
        newTask.setDone(false);
        newTask.setText(task.getText());
        newTask.setTitle(task.getTitle());
        newTask.setNotify(manageNotify(task));
        newTask.setPriorityType(task.getPriorityType());
        newTask.setRecurringType(task.getRecurringType());
        newTask.setRecurringUntil(task.getRecurringUntil());
        return newTask;
    }

    private String manageDate(Task task) throws ParseException {
        switch (task.getRecurringType()){
            case DAILY:
                return DateUtil.getFormatter().format(DateUtil.getDatePlusDays(DateUtil.getFormatter().parse(task.getDate()), 1));
            case WEEKLY:
                return DateUtil.getFormatter().format(DateUtil.getDatePlusWeeks(DateUtil.getFormatter().parse(task.getDate()), 1));
            case MONTHLY:
                return DateUtil.getFormatter().format(DateUtil.getDatePlusMonths(DateUtil.getFormatter().parse(task.getDate()), 1));
            case YEARLY:
                return DateUtil.getFormatter().format(DateUtil.getDatePlusYears(DateUtil.getFormatter().parse(task.getDate()), 1));
            default:
                return task.getDate();
        }
    }

    private String manageNotify(Task task) throws ParseException {
        if(task.getNotify() != null && !task.getNotify().replaceAll(" ", "").isEmpty()) {
            switch (task.getRecurringType()){
                case DAILY:
                    return StringUtil.capFirstCharacter(DateUtil.getDateTimeFormatter().format(DateUtil.getDateTimePlusDays(DateUtil.getDateTimeFormatter().parse(task.getNotify()), 1)));
                case WEEKLY:
                    return StringUtil.capFirstCharacter(DateUtil.getDateTimeFormatter().format(DateUtil.getDateTimePlusWeeks(DateUtil.getDateTimeFormatter().parse(task.getNotify()), 1)));
                case MONTHLY:
                    return StringUtil.capFirstCharacter(DateUtil.getDateTimeFormatter().format(DateUtil.getDateTimePlusMonths(DateUtil.getDateTimeFormatter().parse(task.getNotify()), 1)));
                case YEARLY:
                    return StringUtil.capFirstCharacter(DateUtil.getDateTimeFormatter().format(DateUtil.getDateTimePlusYears(DateUtil.getDateTimeFormatter().parse(task.getNotify()), 1)));
                default:
                    return " ";
            }
        }else return " ";
    }

    private long createNewId(List<Task> tasks){
        Task task = Collections.max(tasks, Comparator.comparing(Task::getId));
        return task.getId() + 1;
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Handler handler = new Handler(Looper.getMainLooper());
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        Log.i("WORKER UPDATE", "Worker [ id = " + this.getId() + " ] was stopped.");
        handler.post(() -> WorkObserver.removeObserver(workManager));
        workManager.pruneWork();
        workManager.cancelAllWork();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
        workManager.enqueueUniquePeriodicWork("update_worker", ExistingPeriodicWorkPolicy.KEEP, workRequest);
        Observer<WorkInfo> newObserver = WorkObserver.createNewObserver(workManager);
        handler.post(() -> workManager.getWorkInfoByIdLiveData(workRequest.getId()).observeForever(newObserver));
        Log.i("WORKER UPDATE", "New Worker created [ id = " + workRequest.getId() + " ]");
    }

    private Notification createNotification(NotificationChannel channel){
        return NotificationTaskCreator.newNotification(getApplicationContext())
                .setTitle("Worker")
                .setText("Updated")
                .setChannel(channel)
                .create();
    }

    private NotificationChannel createChannel(){
        return NotificationChannelCreator.newChannel()
                .setChannelId("worker_update_notification")
                .setChannelName("channel_worker_notification")
                .setChannelImportance(NotificationManager.IMPORTANCE_HIGH)
                .create();
    }
}
