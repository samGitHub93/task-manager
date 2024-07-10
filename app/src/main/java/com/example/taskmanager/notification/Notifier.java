package com.example.taskmanager.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.receiver.NotificationReceiver;
import com.example.taskmanager.util.DateUtil;

public class Notifier {

    private Context context;
    private Task task;

    public void createAlarm(Context context, Task task){
        this.context = context;
        this.task = task;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = prepareIntent();
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.parseInt(String.valueOf(task.getId())), intent, PendingIntent.FLAG_IMMUTABLE);
        long millis = DateUtil.fromStringDateTimeToMillis(task.getNotify());
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, alarmIntent);
    }

    public void cancelAlarm(Context context, Task task){
        this.context = context;
        this.task = task;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = prepareIntent();
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.parseInt(String.valueOf(task.getId())), intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(alarmIntent);
    }

    private Intent prepareIntent(){
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("NOTIFY");
        intent.putExtra("ID", task.getId());
        intent.putExtra("TITLE", task.getTitle());
        intent.putExtra("TEXT", task.getText());
        intent.putExtra("DATE", task.getDate());
        return intent;
    }
}
