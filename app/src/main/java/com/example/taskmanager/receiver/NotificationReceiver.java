package com.example.taskmanager.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.taskmanager.R;
import com.example.taskmanager.exception.NotValidTaskException;
import com.example.taskmanager.notification.NotificationChannelCreator;
import com.example.taskmanager.notification.NotificationTaskCreator;

public class NotificationReceiver extends BroadcastReceiver {

    private Context context;
    private static final String CHANNEL_ID = "TASK_NOTIFICATION_CHANNEL";
    private String id;
    private String title;
    private String text;
    private String date;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            this.context = context;
            retrieveData(intent);
            NotificationChannel channel = createChannel();
            Notification notification = createNotification(channel);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(Integer.parseInt(id), notification);
        }catch (NotValidTaskException e){
            Log.e(NotificationReceiver.class.getName(), e.getMessage(), e);
        }
    }

    private void retrieveData(Intent intent) throws NotValidTaskException {
        this.id = String.valueOf(intent.getLongExtra("ID", 1));
        this.title = intent.getStringExtra("TITLE");
        this.text = intent.getStringExtra("TEXT");
        this.date = intent.getStringExtra("DATE");
        if (id.equals("1") || title == null || text == null || date == null) {
            throw new NotValidTaskException();
        }
    }

    private Notification createNotification(NotificationChannel channel){
        return NotificationTaskCreator.newNotification(context)
                .setTitle(title)
                .setText(date + " | " + text)
                .setChannel(channel)
                .create();
    }

    private NotificationChannel createChannel(){
        return NotificationChannelCreator.newChannel()
                .setChannelId(CHANNEL_ID)
                .setChannelName(context.getString(R.string.channel_name))
                .setChannelDescription(context.getString(R.string.channel_description))
                .setChannelImportance(NotificationManager.IMPORTANCE_HIGH)
                .create();
    }
}
