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
    private MutableLiveData<List<Task>> getTasksByTitleOrTextOrDateLiveData;
    private MutableLiveData<List<Task>> getTasksByDateLiveData;
    private MutableLiveData<List<Task>> getTasksByPeriodLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(application);
    }

    public MutableLiveData<List<Task>> getTasksByDate(Date date){
        if (getTasksByDateLiveData == null) {
            getTasksByDateLiveData = new MutableLiveData<>();
            getTasksByDateLiveData = taskRepository.getTasksByDate(getTasksByDateLiveData, DateUtil.getFormatter().format(date));
        }
        return taskRepository.getTasksByDate(getTasksByDateLiveData, DateUtil.getFormatter().format(date));
    }

    public MutableLiveData<List<Task>> getTasksByPeriod(PeriodType periodType) {
        if (getTasksByPeriodLiveData == null) {
            getTasksByPeriodLiveData = new MutableLiveData<>();
            getTasksByPeriodLiveData = taskRepository.getTasksByPeriod(getTasksByPeriodLiveData, periodType);
        }
        return taskRepository.getTasksByPeriod(getTasksByPeriodLiveData, periodType);
    }

    public MutableLiveData<List<Task>> getTasksByTitleOrTextOrDate(String typing){
        if (getTasksByTitleOrTextOrDateLiveData == null) {
            getTasksByTitleOrTextOrDateLiveData = new MutableLiveData<>();
            getTasksByTitleOrTextOrDateLiveData = taskRepository.getTasksByTitleOrTextOrDate(getTasksByTitleOrTextOrDateLiveData, typing);
        }
        return taskRepository.getTasksByTitleOrTextOrDate(getTasksByTitleOrTextOrDateLiveData, typing);
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
