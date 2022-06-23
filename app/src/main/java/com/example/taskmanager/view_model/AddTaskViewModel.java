package com.example.taskmanager.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;

import java.util.List;

public class AddTaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;
    private MutableLiveData<List<Task>> mutableLiveData;

    public AddTaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        mutableLiveData = taskRepository.getTasks();
    }

    public MutableLiveData<List<Task>> getTasks() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }
        return mutableLiveData;
    }

    public void insertTask(Task task){
        taskRepository.insertTask(task);
    }
}
