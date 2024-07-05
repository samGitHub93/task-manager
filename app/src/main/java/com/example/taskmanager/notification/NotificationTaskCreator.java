package com.example.taskmanager.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.receiver.BootReceiver;
import com.example.taskmanager.receiver.NotificationReceiver;

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
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(context, notificationChannel.getId())
                .setSmallIcon(R.drawable.circle_checked)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }
}
