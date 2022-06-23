package com.example.taskmanager.database;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.taskmanager.BuildConfig;
import com.example.taskmanager.R;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.TaskUtil;
import com.example.taskmanager.util.ThreadUtil;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private final AppDatabase database;
    private final Context context;
    private static final String REPO_URL = BuildConfig.REPO_URL;

    public DataManager(Context context) {
        this.context = context;
        database = AppDatabase.getDatabase(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateRoomDatabase() {
        ThreadUtil.runSynchronizedTask(readPublicExternalCsv(), 100);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData() {
        List<Task> tasks = database.taskDao().getAll();
        ThreadUtil.runSynchronizedTask(writePublicExternalCsv(tasks), 300);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Thread readPublicExternalCsv() {
        return new Thread(() ->
        {
            Looper.prepare();
            try {
                pullFromRemote();
            } catch (IOException | GitAPIException e) {
                Toast.makeText(context, "Try to modify token on GitHub!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Thread writePublicExternalCsv(List<Task> tasks) {
        return new Thread(() ->
        {
            Looper.prepare();
            try {
                pushToRemote(tasks);
            } catch (IOException | GitAPIException e) {
                Toast.makeText(context, "Try to modify token on GitHub!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pushToRemote(List<Task> tasks) throws IOException, GitAPIException {
        String userGit = context.getString(R.string.user_git);
        String tokenGit = context.getString(R.string.tkn_git);
        File localPath = File.createTempFile("GitRepository", "");
        if (!localPath.delete())
            throw new IOException("Could not delete temporary file " + localPath);
        UsernamePasswordCredentialsProvider credentials = new UsernamePasswordCredentialsProvider(userGit, tokenGit);
        Git git = Git.cloneRepository().setURI(REPO_URL).setCredentialsProvider(credentials).setDirectory(localPath).call();
        Repository repository = git.getRepository();
        File theDir = new File(repository.getDirectory().getParent(), "webapp");
        File myFile = new File(theDir, "csvFile.csv");
        git.add().addFilepattern(".").call();
        git.commit().setMessage("Commit all changes including additions").call();
        PrintWriter writer = new PrintWriter(myFile);
        writeTasks(writer, tasks);
        git.add().addFilepattern("*").call();
        git.commit().setAll(true).setMessage("Commit changes to all files").call();
        git.push().setCredentialsProvider(credentials).call();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pullFromRemote() throws IOException, GitAPIException {
        List<Task> tasks = new ArrayList<>();
        String userGit = context.getString(R.string.user_git);
        String tokenGit = context.getString(R.string.tkn_git);
        File localPath = File.createTempFile("GitRepository", "");
        if (!localPath.delete())
            throw new IOException("Could not delete temporary file " + localPath);
        String path = localPath.getPath();
        UsernamePasswordCredentialsProvider credentials = new UsernamePasswordCredentialsProvider(userGit, tokenGit);
        Git.cloneRepository().setURI(REPO_URL).setCredentialsProvider(credentials).setDirectory(localPath).call();
        File gitFolder = new File(path + context.getString(R.string.file_path));
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(gitFolder));
        while ((line = reader.readLine()) != null) {
            String[] lineArray = line.split("\\|\\|\\|");
            Task task = readTask(lineArray);
            tasks.add(task);
        }
        if (!tasks.isEmpty()) saveToRoom(tasks);
        FileUtils.delete(gitFolder);
    }

    private void writeTasks(PrintWriter writer, List<Task> tasks) {
        for (Task t : tasks) {
            writer.append(String.valueOf(t.getId())).append("|||")
                    .append(t.getTitle()).append("|||")
                    .append(t.getText()).append("|||")
                    .append(t.getDate()).append("|||")
                    .append(t.getPriorityType().toString()).append("|||")
                    .append(t.getRecurringType().toString()).append("|||")
                    .append(String.valueOf(t.isDone())).append("\n");
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
                Boolean.parseBoolean(lineArray[6])
        );
    }

    private void saveToRoom(List<Task> tasks) {
        System.out.println("save to ROOM");
        database.taskDao().deleteAll();
        database.taskDao().insertAll(tasks);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void readPrivateExternalCsv() {
        // TODO...
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writePrivateExternalCsv() {
        // TODO...
    }
}
