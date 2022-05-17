package com.example.taskmanager.sorter;

import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskSorter {

    private final List<Task> tasks;
    private List<Task> sortedTasks;

    public TaskSorter(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> sortTasks(){
        sortedTasks = new ArrayList<>();
        addTasks(PriorityType.HIGH);
        addTasks(PriorityType.MEDIUM);
        addTasks(PriorityType.LOW);
        addDoneTasks();
        return sortedTasks;
    }

    private void addTasks(PriorityType priorityType){
        for(int i=0; i< tasks.size(); i++){
            if(tasks.get(i).getPriorityType() == priorityType){
                if(!tasks.get(i).isDone()) {
                    sortedTasks.add(tasks.get(i));
                    tasks.remove(tasks.get(i));
                    i--;
                }
            }
        }
    }

    private void addDoneTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isDone()) {
                sortedTasks.add(tasks.get(i));
                tasks.remove(tasks.get(i));
                i--;
            }
        }
    }
}
