package com.example.taskmanager.adapter;

public interface Adapter<T> {
    void doneItem(int position);
    void undoneItem(int position);
    T getTask(int position);
}
