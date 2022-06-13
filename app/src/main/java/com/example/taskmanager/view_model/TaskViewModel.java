package com.example.taskmanager.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.taskmanager.enumerator.PeriodType;
import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.enumerator.RecurringType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskViewModel extends AndroidViewModel {

    private final List<Task> tasks = generateData();

    public TaskViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Task> getAllTasks(){
        return tasks;
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    public void deleteTask(Task task){
        tasks.remove(task);
    }

    public Task getTaskById(int id){
        for(Task t : tasks)
            if(t.getId() == id) return t;
        return new Task();
    }

    public List<Task> getTasksByDate(Date date){
        List<Task> taskList = new ArrayList<>();
        for(Task t : tasks)
            if(t.getDate().equals(DateUtil.getFormatter().format(date))) taskList.add(t);
        return taskList;
    }

    public List<Task> getTasksByPeriod(Date startDate, Date endDate) {
        List<Task> taskList = new ArrayList<>();
        try {
            List<Date> dateInRange = DateUtil.getDatesBetween(startDate, endDate);
            for (Task t : tasks)
                if (DateUtil.isDateInRange(dateInRange, DateUtil.getFormatter().parse(t.getDate())))
                    taskList.add(t);
        }catch(ParseException pe) {
            pe.getStackTrace();
        }
        return taskList;
    }

    public List<Task> getTasksByPeriod(PeriodType periodType) {
        List<Task> taskList = new ArrayList<>();
        Date today = new Date();
        switch (periodType){
            case _3_DAY:
                taskList = getTasksByPeriod(today, DateUtil.getDatePlusDays(today, 3));
                break;
            case _1_WEEK:
                taskList = getTasksByPeriod(today, DateUtil.getDatePlusWeeks(today, 1));
                break;
            case _2_WEEKS:
                taskList = getTasksByPeriod(today, DateUtil.getDatePlusWeeks(today, 2));
                break;
            case _1_MONTH:
                taskList = getTasksByPeriod(today, DateUtil.getDatePlusMonths(today, 1));
                break;
            case _3_MONTHS:
                taskList = getTasksByPeriod(today, DateUtil.getDatePlusMonths(today, 3));
                break;
            case _6_MONTHS:
                taskList = getTasksByPeriod(today, DateUtil.getDatePlusMonths(today, 6));
                break;
            case _1_YEAR:
                taskList = getTasksByPeriod(today, DateUtil.getDatePlusYears(today, 1));
        }
        return taskList;
    }

    public boolean isDone(Task task){
        for(Task t : tasks)
            if(t.getId() == task.getId()) return task.isDone();
        return false;
    }

    public void setDone(Task task){
        for(Task t : tasks)
            if(t.getId() == task.getId()) task.setDone(true);
    }

    public void setUnDone(Task task){
        for(Task t : tasks)
            if(t.getId() == task.getId()) task.setDone(false);
    }

    private List<Task> generateData(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        Task task1 = new Task("SAM","Task 1","Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of set sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",formatter.format(new Date()), PriorityType.MEDIUM, RecurringType.MONTHLY, false);
        Task task2 = new Task("SAM","Task 2","text 2", formatter.format(new Date()),PriorityType.HIGH, RecurringType.NONE, false);
        Task task3 = new Task("SAM","Task 3","text 3",formatter.format(new Date()), PriorityType.MEDIUM, RecurringType.DAILY,false);
        Task task4 = new Task("SAM","Task 4","text 1",formatter.format(new Date()), PriorityType.LOW, RecurringType.WEEKLY,false);
        Task task5 = new Task("SAM","Task 5","text 2",formatter.format(DateUtil.getDatePlusDays(new Date(), 3)), PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task6 = new Task("SAM","Task 6","text 3",formatter.format(DateUtil.getDatePlusDays(new Date(), 5)), PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task7 = new Task("SAM","Task 7","text 1",formatter.format(DateUtil.getDatePlusWeeks(new Date(), 1)), PriorityType.LOW, RecurringType.YEARLY,false);
        Task task8 = new Task("SAM","Task 8","text 2",formatter.format(DateUtil.getDatePlusWeeks(new Date(), 2)), PriorityType.HIGH, RecurringType.NONE,false);
        Task task9 = new Task("SAM","Task 9","text 3",formatter.format(DateUtil.getDatePlusWeeks(new Date(), 3)), PriorityType.HIGH, RecurringType.NONE,false);
        Task task10 = new Task("SAM","Task 10","text 1",formatter.format(DateUtil.getDatePlusMonths(new Date(), 1)), PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task11 = new Task("SAM","Task 11","text 2",formatter.format(DateUtil.getDatePlusMonths(new Date(), 4)), PriorityType.LOW, RecurringType.WEEKLY,true);
        Task task12 = new Task("SAM","Task 12","text 3",formatter.format(DateUtil.getDatePlusMonths(new Date(), 7)), PriorityType.MEDIUM, RecurringType.NONE,true);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);
        tasks.add(task6);
        tasks.add(task7);
        tasks.add(task8);
        tasks.add(task9);
        tasks.add(task10);
        tasks.add(task11);
        tasks.add(task12);
        return tasks;
    }
}
