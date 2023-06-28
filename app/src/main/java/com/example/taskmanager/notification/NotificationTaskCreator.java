package com.example.taskmanager.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.taskmanager.R;

public class NotificationTaskCreator {

    private final Context context;
    private NotificationChannel notificationChannel;
    private String title;
    private String text;

    private NotificationTaskCreator(Context context){
        this.context = context;
    }

    public static NotificationTaskCreator newNotification(Context context){
        return new NotificationTaskCreator(context);
    }

    public NotificationTaskCreator setChannel(NotificationChannel notificationChannel){
        this.notificationChannel = notificationChannel;
        return this;
    }

    public NotificationTaskCreator setTitle(String title){
        this.title = title;
        return this;
    }

    public NotificationTaskCreator setText(String text){
        this.text = text;
        return this;
    }

    public Notification create(){
        return new NotificationCompat.Builder(context, notificationChannel.getId())
                .setSmallIcon(R.drawable.circle_checked)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();
    }
}
