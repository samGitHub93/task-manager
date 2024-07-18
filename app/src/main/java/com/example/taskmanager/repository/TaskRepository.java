package com.example.taskmanager.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.taskmanager.enumerator.PeriodType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.room.AppDatabase;
import com.example.taskmanager.repository.room.TaskDao;
import com.example.taskmanager.util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskRepository {

    private final TaskDao taskDao;

    public TaskRepository(Application application) {
        taskDao = AppDatabase.getDatabase(application).taskDao();
    }

    public MutableLiveData<Task> getTaskById(MutableLiveData<Task> mutableLiveData, long id){
        mutableLiveData.setValue(taskDao.getById(id));
        return mutableLiveData;
    }

    public MutableLiveData<List<Task>> getTasksByDate(MutableLiveData<List<Task>> mutableLiveData, String date){
        mutableLiveData.setValue(taskDao.getByDate(date));
        return mutableLiveData;
    }

    public MutableLiveData<List<Task>> getTasksByPeriod(MutableLiveData<List<Task>> mutableLiveData, PeriodType periodType) {
        Date today = new Date();
        String strToday = DateUtil.getFormatter().format(today);
        switch (periodType) {
            case _3_DAY:
                mutableLiveData.setValue(getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusDays(today, 3))));
                break;
            case _1_WEEK:
                mutableLiveData.setValue(getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusWeeks(today, 1))));
                break;
            case _2_WEEKS:
                mutableLiveData.setValue(getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusWeeks(today, 2))));
                break;
            case _1_MONTH:
                mutableLiveData.setValue(getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusMonths(today, 1))));
                break;
            case _3_MONTHS:
                mutableLiveData.setValue(getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusMonths(today, 3))));
                break;
            case _6_MONTHS:
                mutableLiveData.setValue(getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusMonths(today, 6))));
                break;
            case _1_YEAR:
                mutableLiveData.setValue(getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusYears(today, 1))));
        }
        return mutableLiveData;
    }

    public MutableLiveData<List<Task>> getLateTasks(MutableLiveData<List<Task>> mutableLiveData){
        Date today = new Date();
        String strYesterday = DateUtil.getFormatter().format(DateUtil.getDatePlusDays(today, -1));
        List<Task> undoneTasks = getUndoneTasks(getTasksByPeriod(DateUtil.getFormatter().format(DateUtil.getDatePlusMonths(today, -6)), strYesterday));
        mutableLiveData.setValue(undoneTasks);
        return mutableLiveData;
    }

    public MutableLiveData<List<Task>> getTasksByTitleOrTextOrDate(MutableLiveData<List<Task>> mutableLiveData, String typing){
        mutableLiveData.setValue(taskDao.getTasksByTitleOrTextOrDate(typing));
        return mutableLiveData;
    }

    public void insertTask(Task task){
        taskDao.insert(task);
    }

    public void deleteTask(Task task){
        taskDao.delete(task);
    }

    public void updateTask(Task task){
        taskDao.update(task);
    }

    public MutableLiveData<List<Task>> getAll(MutableLiveData<List<Task>> mutableLiveData){
        mutableLiveData.setValue(taskDao.getAll());
        return mutableLiveData;
    }

    private List<Task> getRawTasksByDate(String date){
        return new ArrayList<>(taskDao.getByDate(date));
    }

    private List<Task> getTasksByPeriod(String startDate, String endDate){
        List<Task> taskList = new ArrayList<>();
        try {
            Date sDate = DateUtil.getFormatter().parse(startDate);
            Date eDate = DateUtil.getFormatter().parse(endDate);
            List<Date> dateInRange = DateUtil.getDatesBetween(sDate, eDate);
            for (Date date : dateInRange) {
                taskList.addAll(getRawTasksByDate(DateUtil.getFormatter().format(date)));
            }
        }catch (ParseException pe){
            pe.getStackTrace();
        }
        return taskList;
    }

    private List<Task> getUndoneTasks(List<Task> tasks){
        List<Task> taskList = new ArrayList<>();
        for(Task task : tasks){
            if(!task.isDone()) taskList.add(task);
        }
        return taskList;
    }
}
