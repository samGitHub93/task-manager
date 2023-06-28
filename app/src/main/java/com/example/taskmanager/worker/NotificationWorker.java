package com.example.taskmanager.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.taskmanager.R;
import com.example.taskmanager.notification.NotificationChannelCreator;
import com.example.taskmanager.notification.NotificationTaskCreator;

public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "TASK_NOTIFICATION_CHANNEL";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String id = String.valueOf(getInputData().getLong("ID", 1));
        String title = getInputData().getString("TITLE");
        String text = getInputData().getString("TEXT");
        String date = getInputData().getString("DATE");
        if(id.equals("1") || title == null || text == null || date == null) {
            return Result.failure();
        }
        NotificationChannel channel = createChannel();
        Notification notification = NotificationTaskCreator.newNotification(getApplicationContext())
                .setTitle(title)
                .setText(date + " | " + text)
                .setChannel(channel)
                .create();
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(Integer.parseInt(id), notification);
        return Result.success();
    }

    private NotificationChannel createChannel(){
        return NotificationChannelCreator.newChannel()
                .setChannelId(CHANNEL_ID)
                .setChannelName(getApplicationContext().getString(R.string.channel_name))
                .setChannelDescription(getApplicationContext().getString(R.string.channel_description))
                .setChannelImportance(NotificationManager.IMPORTANCE_HIGH)
                .create();
    }
}
