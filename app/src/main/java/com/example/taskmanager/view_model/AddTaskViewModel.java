package com.example.taskmanager.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;

public class AddTaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;

    public AddTaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
    }

    public void insertTask(Task task){
        taskRepository.insertTask(task);
    }
}
