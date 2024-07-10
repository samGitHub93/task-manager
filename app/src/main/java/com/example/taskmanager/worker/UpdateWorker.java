package com.example.taskmanager.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.enumerator.RecurringType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.notification.Notifier;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.StringUtil;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class UpdateWorker extends Worker {

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            DataManager dataManager = DataManager.getInstance(getApplicationContext());
            List<Task> allTasks = dataManager.getFromWeb();
            saveRecurringTasks(dataManager, allTasks);
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
            Log.e(UpdateWorker.class.getName(), e.getMessage(), e);
            return Result.failure();
        }
    }

    private boolean filter(Task task){
        return (task.getNotify().trim().isEmpty() ||
                task.isDone() ||
                DateUtil.fromStringDateTimeToMillis(task.getNotify()) + (1000*60*5) < DateUtil.nowInMillis() ||
                DateUtil.fromStringDateTimeToMillis(task.getNotify()) > DateUtil.nowInMillis() + (1000*60*60*24*3));
    }

    private void saveRecurringTasks(DataManager dataManager, List<Task> tasks) throws ParseException {
        List<Task> recurringTasks = tasks.stream().filter(t -> t.getRecurringType() != RecurringType.NONE).collect(Collectors.toList());
        try {
            for (Task task : recurringTasks) {
                Task newTask = createNewRecurringTask(task);
                List<Task> taskByDate = recurringTasks.stream().filter((t) -> t.isEqual(newTask)).collect(Collectors.toList());
                Log.i(UpdateWorker.class.getName(), "ADDING RECURRENT TASK: " + newTask.getTitle() + " | " + newTask.getText());
                if (taskByDate.isEmpty() && !DateUtil.getDateFromString(task.getDate()).after(DateUtil.getTodayWithoutTime()) && !DateUtil.getDateFromString(newTask.getDate()).before(DateUtil.getTodayWithoutTime())) {
                    dataManager.getDatabase().taskDao().insert(newTask);
                    dataManager.synchronizeFromRoom();
                    Log.i(UpdateWorker.class.getName(), "ADDED TASK: " + newTask.getTitle() + " | " + newTask.getText());
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Task createNewRecurringTask(Task task) throws ParseException {
        Task newTask = new Task();
        newTask.setDate(StringUtil.capFirstCharacter(manageDate(task)));
        newTask.setDone(false);
        newTask.setText(task.getText());
        newTask.setTitle(task.getTitle());
        newTask.setNotify(StringUtil.capFirstCharacter(manageNotify(task)));
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
                    return DateUtil.getDateTimeFormatter().format(DateUtil.getDateTimePlusDays(DateUtil.getDateTimeFormatter().parse(task.getNotify()), 1));
                case WEEKLY:
                    return DateUtil.getDateTimeFormatter().format(DateUtil.getDateTimePlusWeeks(DateUtil.getDateTimeFormatter().parse(task.getNotify()), 1));
                case MONTHLY:
                    return DateUtil.getDateTimeFormatter().format(DateUtil.getDateTimePlusMonths(DateUtil.getDateTimeFormatter().parse(task.getNotify()), 1));
                case YEARLY:
                    return DateUtil.getDateTimeFormatter().format(DateUtil.getDateTimePlusYears(DateUtil.getDateTimeFormatter().parse(task.getNotify()), 1));
                default:
                    return " ";
            }
        }else return " ";
    }
}
