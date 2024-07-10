package com.example.taskmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.enumerator.RecurringType;

import java.util.Random;

@Entity(tableName = "task")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "text")
    private String text;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "priority_type")
    private PriorityType priorityType;
    @ColumnInfo(name = "recurring_type")
    private RecurringType recurringType;
    @ColumnInfo(name = "recurring_until")
    private String recurringUntil;
    @ColumnInfo(name = "is_done")
    private boolean isDone;
    @ColumnInfo(name = "notify")
    private String notify;

    public Task() {}

    @Ignore
    public Task(String title, String text, String date, PriorityType priorityType, RecurringType recurringType, String recurringUntil, boolean isDone, String notify) {
        Random random = new Random();
        this.id = random.nextInt((999999999-1) + 1);
        this.title = title;
        this.text = text;
        this.date = date;
        this.priorityType = priorityType;
        this.recurringType = recurringType;
        this.recurringUntil = recurringUntil;
        this.isDone = isDone;
        this.notify = notify;
    }

    @Ignore
    public Task(long id, String title, String text, String date, PriorityType priorityType, RecurringType recurringType, String recurringUntil, boolean isDone, String notify) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.date = date;
        this.priorityType = priorityType;
        this.recurringType = recurringType;
        this.recurringUntil = recurringUntil;
        this.isDone = isDone;
        this.notify = notify;
    }

    @Ignore
    public Task(long id, String title, String text, String date, PriorityType priorityType, RecurringType recurringType, String recurringUntil, boolean isDone) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.date = date;
        this.priorityType = priorityType;
        this.recurringType = recurringType;
        this.recurringUntil = recurringUntil;
        this.isDone = isDone;
    }

    @Ignore
    public Task(String title, String text, String date, PriorityType priorityType, RecurringType recurringType, String recurringUntil, boolean isDone) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.priorityType = priorityType;
        this.recurringType = recurringType;
        this.recurringUntil = recurringUntil;
        this.isDone = isDone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public PriorityType getPriorityType() {
        return priorityType;
    }

    public void setPriorityType(PriorityType priorityType) {
        this.priorityType = priorityType;
    }

    public RecurringType getRecurringType() {
        return recurringType;
    }

    public void setRecurringType(RecurringType recurringType) {
        this.recurringType = recurringType;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getRecurringUntil() {
        return recurringUntil;
    }

    public void setRecurringUntil(String recurringUntil) {
        this.recurringUntil = recurringUntil;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public Boolean isEqual(Task task){
        return this.title.equals(task.getTitle()) &&
                this.text.equals(task.getText()) &&
                this.date.equals(task.getDate());
    }
}
