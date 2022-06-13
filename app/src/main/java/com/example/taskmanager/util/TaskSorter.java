package com.example.taskmanager.sorter;

import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.model.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskSorter {

    public static List<Task> sortByPriority(List<Task> unsortedTasks) {
        List<Task> sortedTasks = new ArrayList<>();
        addTasks(unsortedTasks, sortedTasks, PriorityType.HIGH);
        addTasks(unsortedTasks, sortedTasks, PriorityType.MEDIUM);
        addTasks(unsortedTasks, sortedTasks, PriorityType.LOW);
        return sortedTasks;
    }

    public static List<Task> sortByDate(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>();
        Collections.sort(tasks, TaskSorter::compareByDate);
        for(Task task : tasks)
            sortedTasks.add(0, task);
        return sortedTasks;
    }

    private static void addTasks(List<Task> unsortedTasks, List<Task> sortedTasks, PriorityType priorityType) {
        for (int i = 0; i < unsortedTasks.size(); i++) {
            if (unsortedTasks.get(i).getPriorityType() == priorityType) {
                sortedTasks.add(unsortedTasks.get(i));
                unsortedTasks.remove(unsortedTasks.get(i));
                i--;
            }
        }
    }

    private static int compareByDate(Task task1, Task task2){
        int result = 0;
        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
            Date date1 = formatter.parse(task1.getDate());
            Date date2 = formatter.parse(task2.getDate());
            assert date1 != null;
            result = date1.compareTo(date2);
        } catch (ParseException pe) {
            pe.getStackTrace();
        }
        return result;
    }
}
