package com.example.taskmanager.model;

import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.enumerator.RecurringType;

import java.util.Random;

public class Task {
    private long id;
    private String author;
    private String title;
    private String text;
    private String date;
    private PriorityType priorityType;
    private RecurringType recurringType;
    private boolean isDone;

    public Task() {}

    public Task(String author, String title, String text, String date, PriorityType priorityType, RecurringType recurringType, boolean isDone) {
        Random random = new Random();
        this.id = random.nextInt((999999999-1) + 1);
        this.author = author;
        this.title = title;
        this.text = text;
        this.date = date;
        this.priorityType = priorityType;
        this.recurringType = recurringType;
        this.isDone = isDone;
    }

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
}
