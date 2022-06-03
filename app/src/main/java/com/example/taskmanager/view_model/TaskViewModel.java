package com.example.taskmanager.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.enumerator.RecurringType;
import com.example.taskmanager.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskViewModel extends AndroidViewModel {

    public TaskViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Task> getTasks(){
        return generateData();
    }

    private List<Task> generateData(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        Task task1 = new Task("SAM","Task 1","Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of set sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",formatter.format(new Date()), PriorityType.MEDIUM, RecurringType.MONTHLY, false);
        Task task2 = new Task("SAM","Task 2","text 2", "23/02/2022",PriorityType.HIGH, RecurringType.NONE, false);
        Task task3 = new Task("SAM","Task 3","text 3","02/07/2022", PriorityType.MEDIUM, RecurringType.DAILY,false);
        Task task4 = new Task("SAM","Task 4","text 1","09/09/2022", PriorityType.LOW, RecurringType.WEEKLY,false);
        Task task5 = new Task("SAM","Task 5","text 2","11/12/2022", PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task6 = new Task("SAM","Task 6","text 3","20/08/2021", PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task7 = new Task("SAM","Task 7","text 1","12/01/2022", PriorityType.LOW, RecurringType.YEARLY,false);
        Task task8 = new Task("SAM","Task 8","text 2","28/01/2023", PriorityType.HIGH, RecurringType.NONE,false);
        Task task9 = new Task("SAM","Task 9","text 3","13/09/2022", PriorityType.HIGH, RecurringType.NONE,false);
        Task task10 = new Task("SAM","Task 10","text 1","31/05/2022", PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task11 = new Task("SAM","Task 11","text 2","25/12/2022", PriorityType.LOW, RecurringType.WEEKLY,true);
        Task task12 = new Task("SAM","Task 12","text 3","31/12/2022", PriorityType.MEDIUM, RecurringType.NONE,true);
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
