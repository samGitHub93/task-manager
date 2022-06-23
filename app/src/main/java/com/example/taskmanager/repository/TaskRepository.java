package com.example.taskmanager.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.taskmanager.database.AppDatabase;
import com.example.taskmanager.database.TaskDao;
import com.example.taskmanager.enumerator.PeriodType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskRepository {

    private final TaskDao taskDao;

    public TaskRepository(Application application) {
        taskDao = AppDatabase.getDatabase(application.getApplicationContext()).taskDao();
    }

    public MutableLiveData<List<Task>> getTasks(){
        MutableLiveData<List<Task>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(taskDao.getAll());
        return mutableLiveData;
    }

    public void insertTask(Task task){
        taskDao.insert(task);
    }

    public void deleteTask(Task task){
        taskDao.delete(task);
    }

    public void updateTask(Task task){
        task.setDone(!task.isDone());
        taskDao.update(task);
    }

    public Task getTaskById(long id){
        return taskDao.getById(id);
    }

    public List<Task> getTasksByDate(String date){
        return taskDao.getByDate(date);
    }

    public List<Task> getTasksByPeriod(String startDate, String endDate){
        List<Task> taskList = new ArrayList<>();
        try {
            Date sDate = DateUtil.getFormatter().parse(startDate);
            Date eDate = DateUtil.getFormatter().parse(endDate);
            List<Date> dateInRange = DateUtil.getDatesBetween(sDate, eDate);
            for (Date date : dateInRange) {
                taskList.addAll(getTasksByDate(DateUtil.getFormatter().format(date)));
            }
        }catch (ParseException pe){
            pe.getStackTrace();
        }
        return taskList;
    }

    public List<Task> getTasksByPeriod(PeriodType periodType) {
        List<Task> taskList = new ArrayList<>();
        Date today = new Date();
        String strToday = DateUtil.getFormatter().format(today);
        switch (periodType) {
            case _3_DAY:
                taskList = getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusDays(today, 3)));
                break;
            case _1_WEEK:
                taskList = getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusWeeks(today, 1)));
                break;
            case _2_WEEKS:
                taskList = getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusWeeks(today, 2)));
                break;
            case _1_MONTH:
                taskList = getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusMonths(today, 1)));
                break;
            case _3_MONTHS:
                taskList = getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusMonths(today, 3)));
                break;
            case _6_MONTHS:
                taskList = getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusMonths(today, 6)));
                break;
            case _1_YEAR:
                taskList = getTasksByPeriod(strToday, DateUtil.getFormatter().format(DateUtil.getDatePlusYears(today, 1)));
        }
        return taskList;
    }

    public List<Task> getTasksByTitleOrTextOrDate(String typing){
        return taskDao.getTasksByTitleOrTextOrDate(typing);
    }
}
