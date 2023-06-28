package com.example.taskmanager.notification;

import android.app.NotificationChannel;

public class NotificationChannelCreator {

    private String id;
    private String name;
    private String description;
    private int importance;

    private NotificationChannelCreator(){}

    public static NotificationChannelCreator newChannel(){
        return new NotificationChannelCreator();
    }

    public NotificationChannelCreator setChannelId(String id){
        this.id = id;
        return this;
    }

    public NotificationChannelCreator setChannelName(String name){
        this.name = name;
        return this;
    }

    public NotificationChannelCreator setChannelDescription(String description){
        this.description = description;
        return this;
    }

    public NotificationChannelCreator setChannelImportance(int importance){
        this.importance = importance;
        return this;
    }

    public NotificationChannel create(){
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        return channel;
    }
}
