package com.example.taskmanager.notification.foreground_service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.taskmanager.R;
import com.example.taskmanager.notification.NotificationChannelCreator;
import com.example.taskmanager.notification.worker.UpdateWorker;
import com.example.taskmanager.notification.worker.WorkObserver;

import java.util.concurrent.TimeUnit;

public class ForegroundService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        triggerUpdateWorker();
        startForeground(2, createNotification());
        return START_STICKY;
    }

    private void triggerUpdateWorker(){
        WorkManager workManager = WorkManager.getInstance(this);
        WorkObserver.removeObserver(workManager);
        workManager.pruneWork();
        workManager.cancelAllWork();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
        workManager.enqueueUniquePeriodicWork("update_worker", ExistingPeriodicWorkPolicy.KEEP, workRequest);
        Observer<WorkInfo> newObserver = WorkObserver.createNewObserver(workManager);
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observeForever(newObserver);
    }

    private Notification createNotification(){
        NotificationChannel channel = createChannel();
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel.getId());
        builder.setOngoing(false)
                .setSmallIcon(R.drawable.circle_checked)
                .setContentTitle("Title")
                .setContentText("Text");
        return builder.build();
    }

    private NotificationChannel createChannel(){
        return NotificationChannelCreator.newChannel()
                .setChannelId("CHANNEL_FOREGROUND")
                .setChannelName(getApplicationContext().getString(R.string.channel_name))
                .setChannelDescription(getApplicationContext().getString(R.string.channel_description))
                .setChannelImportance(NotificationManager.IMPORTANCE_MIN)
                .create();
    }

}
