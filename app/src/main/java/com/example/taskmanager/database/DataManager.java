package com.example.taskmanager.database;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.taskmanager.ProcessTaskAction;
import com.example.taskmanager.R;
import com.example.taskmanager.TaskActivity;
import com.example.taskmanager.exception.PullException;
import com.example.taskmanager.exception.PushException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.TaskUtil;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataManager {
    private AppDatabase database;
    private Context context;
    private static DataManager instance;
    private GitHub gitHub;
    private Git git;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DataManager(Application application) {
        try {
            context = application.getApplicationContext();
            database = AppDatabase.getDatabase(this.context);
            gitHub = GitHub.getInstance(context);
            git = executor.submit(gitHub::getGit).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(DataManager.class.getName(), e.getMessage(), e);
        }
    }

    private DataManager(Context context){
        try {
            this.context = context;
            database = AppDatabase.getDatabase(this.context);
            gitHub = GitHub.getInstance(context);
            git = executor.submit(gitHub::getGit).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(DataManager.class.getName(), e.getMessage(), e);
        }
    }

    public static DataManager getInstance(Application application){
        if (instance == null)
            instance = new DataManager(application);
        return instance;
    }

    public static DataManager getInstance(Context context){
        if (instance == null)
            instance = new DataManager(context);
        return instance;
    }


    public void synchronizeFromRoom(Activity activity, Boolean autoClose) throws ExecutionException, InterruptedException {
        ProcessTaskAction processAction = (ProcessTaskAction) activity;
        executor.submit(() -> {
            try {
                activity.runOnUiThread(() -> enableProgressBar(processAction));
                if(isActiveConnection()) {
                    List<Task> tasks = database.taskDao().getAll();
                    pushToRemote(tasks);
                    activity.runOnUiThread(() -> disableProgressBar(processAction));
                    if(activity instanceof TaskActivity)
                        activity.runOnUiThread(() -> updateUI((TaskActivity) activity));
                    if (autoClose)
                        activity.finish();
                    return true;
                }else {
                    manageLooper();
                    Toast.makeText(activity, "Cannot synchronize.", Toast.LENGTH_SHORT).show();
                    activity.runOnUiThread(() -> disableProgressBar(processAction));
                    return false;
                }
            } catch (Exception e) {
                activity.runOnUiThread(() -> disableProgressBar(processAction));
                Log.e(DataManager.class.getName(), e.getMessage(), e);
                return false;
            }
        });
    }

    public void synchronizeFromWeb(Activity activity) throws ExecutionException, InterruptedException {
        ProcessTaskAction processAction = (ProcessTaskAction) activity;
        executor.submit(() -> {
            try {
                activity.runOnUiThread(() -> enableProgressBar(processAction));
                if(isActiveConnection()) {
                    List<Task> tasks = pullFromRemote();
                    saveToRoom(tasks);
                    activity.runOnUiThread(() -> disableProgressBar(processAction));
                    if(activity instanceof TaskActivity)
                        activity.runOnUiThread(() -> updateUI((TaskActivity) activity));
                    return true;
                }else{
                    manageLooper();
                    Toast.makeText(activity, "Cannot synchronize.", Toast.LENGTH_SHORT).show();
                    activity.runOnUiThread(() -> disableProgressBar(processAction));
                    return false;
                }
            } catch (Exception e) {
                activity.runOnUiThread(() -> disableProgressBar(processAction));
                Log.e(DataManager.class.getName(), e.getMessage(), e);
                return false;
            }
        });
    }

    public List<Task> synchronizeFromWeb() throws ExecutionException, InterruptedException {
        return executor.submit(() -> {
            try {
                if(isActiveConnection()) {
                    return pullFromRemote();
                }else{
                    manageLooper();
                    Toast.makeText(context, "Cannot synchronize.", Toast.LENGTH_SHORT).show();
                    return new ArrayList<Task>();
                }
            } catch (Exception e) {
                Log.e(DataManager.class.getName(), e.getMessage(), e);
                return new ArrayList<Task>();
            }
        }).get();
    }

    private Boolean isActiveConnection() {
            try {
                String command = "ping -c 1 google.com";
                return Runtime.getRuntime().exec(command).waitFor() == 0;
            } catch (Exception e) {
                Log.e(DataManager.class.getName(), e.getMessage(), e);
                return false;
            }
    }

    private void pushToRemote(List<Task> tasks) throws PushException {
        try {
            File theDir = new File(git.getRepository().getDirectory().getParent(), "webapp");
            File myFile = new File(theDir, "csvFile.csv");
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Commit all changes including additions").call();
            PrintWriter writer = new PrintWriter(myFile);
            writeTasks(writer, tasks);
            writer.close();
            git.add().addFilepattern("*").call();
            git.commit().setAll(true).setMessage("Commit changes to all files").call();
            git.push().setCredentialsProvider(gitHub.getCredentials()).call();
        } catch (IOException | GitAPIException e) {
            Log.e(DataManager.class.getName(), e.getMessage(), e);
            throw new PushException();
        }
    }

    private List<Task> pullFromRemote() throws PullException {
        try {
            List<Task> tasks = downloadTasks();
            if (!tasks.isEmpty())
                return tasks;
            else throw new PullException();
        } catch (IOException e) {
            Log.e(DataManager.class.getName(), e.getMessage(), e);
            throw new PullException();
        }
    }

    private List<Task> downloadTasks() throws IOException {
        List<Task> tasks;
        File gitFolder = null;
        try {
            git = gitHub.getGit();
            tasks = new ArrayList<>();
            String path = git.getRepository().getDirectory().getPath().replace("/.git", "");
            gitFolder = new File(path + context.getString(R.string.file_path));
            BufferedReader reader = new BufferedReader(new FileReader(gitFolder));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineArray = line.split("\\|\\|\\|");
                Task task = readTask(lineArray);
                tasks.add(task);
            }
            reader.close();
            FileUtils.delete(gitFolder);
        }catch (IOException e){
            Log.e(DataManager.class.getName(), e.getMessage(), e);
            FileUtils.delete(gitFolder);
            throw new IOException();
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
        database.taskDao().deleteAll();
        database.taskDao().insertAll(tasks);
    }

    private void enableProgressBar(ProcessTaskAction processTaskAction) {
        processTaskAction.enableProgressBar();
        processTaskAction.disableTouch();
    }

    private void disableProgressBar(ProcessTaskAction processTaskAction){
        processTaskAction.disableProgressBar();
        processTaskAction.enableTouch();
    }

    private void updateUI(TaskActivity taskActivity){
        taskActivity.updateUI();
    }

    public AppDatabase getDatabase(){
        return database;
    }

    private void manageLooper(){
        if(Looper.myLooper() == null){
            Looper.prepare();
        }
    }
}
