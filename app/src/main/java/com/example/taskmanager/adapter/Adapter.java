package com.example.taskmanager.adapter;

import com.example.taskmanager.UiProcessAnimations;

public interface Adapter<T> extends UiProcessAnimations {
    void doneItem(int position);
    void undoneItem(int position);
    T getTask(int position);
}
