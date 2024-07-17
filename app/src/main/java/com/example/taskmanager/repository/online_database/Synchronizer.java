package com.example.taskmanager.repository.online_database;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.taskmanager.enumerator.RetryNumber;
import com.example.taskmanager.exception.PullException;
import com.example.taskmanager.exception.PushException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.room.AppDatabase;
import com.example.taskmanager.repository.room.TaskDao;
import com.example.taskmanager.util.TaskUtil;

import org.eclipse.jgit.api.Git;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Synchronizer {
    private Context context;
    private static Synchronizer instance;
    private TaskDao taskDao;
    private GitHub gitHub;
    private Git gitWorker;
    private Git gitSync;
    private static final int MAX_RETRY = 5;

    private Synchronizer(Application application) {
        try {
            context = application.getApplicationContext();
            gitHub = GitHub.getInstance(context);
            taskDao = AppDatabase.getDatabase(context).taskDao();
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
        }
    }

    private Synchronizer(Context context){
        try {
            this.context = context;
            gitHub = GitHub.getInstance(context);
            taskDao = AppDatabase.getDatabase(context).taskDao();
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
        }
    }

    public static Synchronizer getInstance(Application application){
        if (instance == null)
            instance = new Synchronizer(application);
        return instance;
    }

    public static Synchronizer getInstance(Context context){
        if (instance == null)
            instance = new Synchronizer(context);
        return instance;
    }

    public void synchronizeFromWeb() {
        try {
            if (isActiveConnection()) {
                gitHub.clearCache(false);
                List<Task> tasks = pullFromRemote(false, RetryNumber._3);
                Log.i(Synchronizer.class.getName(), "Pulled Tasks : " + tasks.size());
                saveToRoom(tasks);
            } else {
                manageLooper();
                Toast.makeText(context, "Cannot synchronize.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
        }
    }

    public void synchronizeFromRoom() {
        try {
            if (isActiveConnection()) {
                List<Task> tasks = taskDao.getAll();
                pushToRemote(tasks, false, RetryNumber._3);
            } else {
                manageLooper();
                Toast.makeText(context, "Cannot synchronize.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
        }
    }

    public List<Task> directlyGetAll() {
        Future<List<Task>> future = Executors.newSingleThreadExecutor().submit(() -> {
            gitHub.clearCache(true);
            if (isActiveConnection()) {
                List<Task> pulledTasks = pullFromRemote(true, RetryNumber._3);
                Log.i(Synchronizer.class.getName(), "Pulled Tasks : " + pulledTasks.size());
                return pulledTasks;
            } else {
                manageLooper();
                Toast.makeText(context, "Cannot synchronize.", Toast.LENGTH_SHORT).show();
                return new ArrayList<>();
            }
        });
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void directlyDeleteOldTasks(List<Task> allTasks, List<Task> tasksToDelete) {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Log.i(Synchronizer.class.getName(), "Tasks to delete : " + tasksToDelete.size());
                Log.i(Synchronizer.class.getName(), "All tasks : " + allTasks.size());
                for (Task task : tasksToDelete) {
                    allTasks.remove(task);
                }
                pushToRemote(allTasks, true, RetryNumber._3);
            }catch (Exception e){
                Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            }
        });
    }

    public void directlyInsert(List<Task> allTasks, Task task) {
        Executors.newSingleThreadExecutor().submit(() -> {
            Log.i(Synchronizer.class.getName(), "All tasks : " + allTasks.size());
            try {
                allTasks.add(task);
                pushToRemote(allTasks, true, RetryNumber._3);
            } catch (Exception e) {
                Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            }
        });
    }

    private Boolean isActiveConnection() {
        try {
            String command = "ping -c 1 google.com";
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            return false;
        }
    }

    private void pushToRemote(List<Task> tasks, boolean forWorker, RetryNumber retryNumber) throws PushException, PullException, IOException {
        PrintWriter writer = null;
        Git git;
        int attemptNumber = MAX_RETRY - retryNumber.getNumber();
        try {
            if(forWorker)
                git = gitWorker;
            else git = gitSync;
            File gitFolder = gitHub.getClonePath(forWorker);
            Log.i(Synchronizer.class.getName(), "Push Repo: " + gitFolder);
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Commit all changes including additions").call();
            writer = new PrintWriter(gitFolder);
            writeTasks(writer, tasks);
            git.add().addFilepattern("*").call();
            git.commit().setAll(true).setMessage("Commit changes to all files").call();
            git.push().setCredentialsProvider(gitHub.getCredentials()).call();
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            pullFromRemote(forWorker, RetryNumber._3);
            Log.i(Synchronizer.class.getName(), "Attempt number push: " + attemptNumber);
            if(attemptNumber == MAX_RETRY)
                throw new PushException();
            else
                pushToRemote(tasks, forWorker, retryNumber.getRetryNumber(retryNumber.getNumber() - 1));
        }finally {
            if(writer != null) writer.close();
        }
    }

    private List<Task> pullFromRemote(boolean forWorker, RetryNumber retryNumber) throws PullException, IOException {
        List<Task> tasks = new ArrayList<>();
        File gitFolder;
        FileReader fileReader = null;
        BufferedReader reader = null;
        int attemptNumber = MAX_RETRY - retryNumber.getNumber();
        try {
            if(forWorker)
                gitWorker = gitHub.getGit(true);
            else gitSync = gitHub.getGit(false);
            tasks = new ArrayList<>();
            gitFolder = gitHub.getClonePath(forWorker);
            Log.i(Synchronizer.class.getName(), "Pull Repo: " + gitFolder);
            fileReader = new FileReader(gitFolder);
            reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineArray = line.split("\\|\\|\\|");
                Task task = readTask(lineArray);
                tasks.add(task);
            }
            if (!tasks.isEmpty())
                return tasks;
            else throw new PullException("Empty repository.");
        }catch (Exception e){
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            Log.i(Synchronizer.class.getName(), "Attempt number pull: " + attemptNumber);
            if(attemptNumber == MAX_RETRY)
                throw new PullException();
            else
                pullFromRemote(forWorker, retryNumber.getRetryNumber(retryNumber.getNumber() - 1));
        }finally {
            if(reader != null) reader.close();
            if(fileReader != null) fileReader.close();
        }
        return tasks;
    }

    private void writeTasks(Writer writer, List<Task> tasks) throws IOException {
        for (Task t : tasks) {
            writer.append(String.valueOf(t.getId())).append("|||")
                    .append(t.getTitle()).append("|||")
                    .append(t.getText()).append("|||")
                    .append(t.getDate()).append("|||")
                    .append(t.getPriorityType().toString()).append("|||")
                    .append(t.getRecurringType().toString()).append("|||")
                    .append(t.getRecurringUntil()).append("|||")
                    .append(String.valueOf(t.isDone())).append("|||")
                    .append(t.getNotify()).append("\n");
            writer.flush();
        }
    }

    private Task readTask(String[] lineArray) {
        return new Task(
                Long.parseLong(lineArray[0]),
                lineArray[1],
                lineArray[2],
                lineArray[3],
                TaskUtil.stringToPriorityType(lineArray[4]),
                TaskUtil.stringToRecurringType(lineArray[5]),
                lineArray[6],
                Boolean.parseBoolean(lineArray[7]),
                lineArray[8]
        );
    }

    private void saveToRoom(List<Task> tasks) {
        taskDao.deleteAll();
        taskDao.insertAll(tasks);
    }

    private void manageLooper(){
        if(Looper.myLooper() == null){
            Looper.prepare();
        }
    }
}
