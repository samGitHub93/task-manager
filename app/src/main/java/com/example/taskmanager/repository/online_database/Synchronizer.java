package com.example.taskmanager.repository.online_database;

import android.content.Context;
import android.util.Log;

import com.example.taskmanager.enumerator.RetryNumber;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.room.AppDatabase;
import com.example.taskmanager.repository.room.TaskDao;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Synchronizer {
    private TaskDao taskDao;
    private GitHub gitHub;

    public Synchronizer(Context context){
        try {
            gitHub = GitHub.getInstance(context);
            taskDao = AppDatabase.getDatabase(context).taskDao();
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
        }
    }

    public void synchronizeFromWeb() {
        try {
                List<Task> tasks = gitHub.pullFromRemote(false, RetryNumber._3);
                Log.i(Synchronizer.class.getName(), "Pulled Tasks : " + tasks.size());
                saveToRoom(tasks);
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
        }
    }

    public void synchronizeFromRoom() {
        try {
                List<Task> tasks = taskDao.getAll();
                gitHub.pushToRemote(tasks, false, RetryNumber._3);
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
        }
    }

    public List<Task> directlyGetAll() {
        Future<List<Task>> future = Executors.newSingleThreadExecutor().submit(() -> {
                List<Task> pulledTasks = gitHub.pullFromRemote(true, RetryNumber._3);
                Log.i(Synchronizer.class.getName(), "Pulled Tasks : " + pulledTasks.size());
                return pulledTasks;
        });
        try {
            return future.get();
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void directlyDeleteOldTasks(List<Task> allTasks, List<Task> tasksToDelete) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Log.i(Synchronizer.class.getName(), "Tasks to delete : " + tasksToDelete.size());
                for (Task task : tasksToDelete) {
                    allTasks.remove(task);
                }
                gitHub.pushToRemote(allTasks, true, RetryNumber._3);
            }catch (Exception e){
                Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            }
        });
    }

    public void directlyInsert(List<Task> allTasks, Task task) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                allTasks.add(task);
                gitHub.pushToRemote(allTasks, true, RetryNumber._3);
            } catch (Exception e) {
                Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            }
        });
    }

    private void saveToRoom(List<Task> tasks) {
        taskDao.deleteAll();
        taskDao.insertAll(tasks);
    }
}
