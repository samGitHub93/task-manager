package com.example.taskmanager.sorter;

import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskSorter {

    public static List<Task> sortTasks(List<Task> unsortedTasks) {
        List<Task> sortedTasks = new ArrayList<>();
        addTasks(unsortedTasks, sortedTasks, PriorityType.HIGH);
        addTasks(unsortedTasks, sortedTasks, PriorityType.MEDIUM);
        addTasks(unsortedTasks, sortedTasks, PriorityType.LOW);
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
}
