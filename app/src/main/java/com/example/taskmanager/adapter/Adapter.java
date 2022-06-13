package com.example.taskmanager.adapter;

import com.example.taskmanager.model.Task;

public interface Adapter {

    void deleteItem(int position);

    void doneItem(int position);

    void restoreItem(int position);

    void undoneItem(int position);

    Task getTask(int position);
}
