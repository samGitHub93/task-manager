package com.example.taskmanager.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.taskmanager.enumerator.PeriodType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.util.DateUtil;

import java.util.Date;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;
    private MutableLiveData<List<Task>> mutableLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
        mutableLiveData = taskRepository.getTasks();
    }

    public MutableLiveData<List<Task>> getAll(){
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }
        return mutableLiveData;
    }

    public List<Task> getTasksByTitleOrTextOrDate(String typing){
        return taskRepository.getTasksByTitleOrTextOrDate(typing);
    }

    public List<Task> getTasksByDate(Date date){
        return taskRepository.getTasksByDate(DateUtil.getFormatter().format(date));
    }

    public List<Task> getTasksByPeriod(PeriodType periodType) {
        return taskRepository.getTasksByPeriod(periodType);
    }

    public void insertTask(Task task){
        taskRepository.insertTask(task);
    }

    public void updateTask(Task task){
        taskRepository.updateTask(task);
    }

    public void deleteTask(Task task){
        taskRepository.deleteTask(task);
    }
}
