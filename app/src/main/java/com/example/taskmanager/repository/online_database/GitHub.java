package com.example.taskmanager.repository.online_database;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.taskmanager.BuildConfig;
import com.example.taskmanager.R;
import com.example.taskmanager.enumerator.RetryNumber;
import com.example.taskmanager.exception.PullException;
import com.example.taskmanager.exception.PushException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.TaskUtil;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class GitHub {
    private Context context;
    private static final String REPO_URL = BuildConfig.REPO_URL;
    private static GitHub instance;
    private final UsernamePasswordCredentialsProvider credentials;
    private CloneCommand cloneCommand;
    private Git gitWorker;
    private Git gitSync;
    private final static int TIMEOUT = 3;
    private static final int MAX_RETRY = 5;

    private GitHub(Context context) {
        this.context = context;
        String userGit = context.getString(R.string.user_git);
        String tokenGit = context.getString(R.string.tkn_git);
        credentials = new UsernamePasswordCredentialsProvider(userGit, tokenGit);
        cloneCommand = Git.cloneRepository().setURI(REPO_URL).setCredentialsProvider(credentials);
    }

    static GitHub getInstance(Context context){
        if (instance == null)
            instance = new GitHub(context);
        return instance;
    }

    void pushToRemote(List<Task> tasks, boolean forWorker, RetryNumber retryNumber) throws PushException, PullException {
        PrintWriter writer = null;
        Git git;
        int attemptNumber = MAX_RETRY - retryNumber.getNumber();
        try {
            if(forWorker)
                git = gitWorker;
            else git = gitSync;
            File gitFolder = getClonePath(forWorker);
            Log.i(Synchronizer.class.getName(), "Push Repo: " + gitFolder);
            git.add().addFilepattern(".").call();
            git.commit().setMessage("Commit all changes including additions").call();
            writer = new PrintWriter(gitFolder);
            writeTasks(writer, tasks);
            git.add().addFilepattern("*").call();
            git.commit().setAll(true).setMessage("Commit changes to all files").call();
            git.push().setCredentialsProvider(getCredentials()).call();
        } catch (Exception e) {
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            pullFromRemote(forWorker, RetryNumber._3);
            Log.i(Synchronizer.class.getName(), "Attempt number push: " + attemptNumber);
            if(attemptNumber == MAX_RETRY) {
                Looper.prepare();
                Toast.makeText(context, "Connection issue.", Toast.LENGTH_LONG).show();
                throw new PushException();
            } else
                pushToRemote(tasks, forWorker, retryNumber.getRetryNumber(retryNumber.getNumber() - 1));
        }finally {
            if(writer != null) writer.close();
        }
    }

    List<Task> pullFromRemote(boolean forWorker, RetryNumber retryNumber) throws PullException {
        File gitFolder;
        List<Task> tasks = new ArrayList<>();
        int attemptNumber = MAX_RETRY - retryNumber.getNumber();
        try {
            clearCache(forWorker);
            if(forWorker)
                gitWorker = getGit(true);
            else gitSync = getGit(false);
            gitFolder = getClonePath(forWorker);
            Log.i(Synchronizer.class.getName(), "Pull Repo: " + gitFolder);
            tasks = readTasks(gitFolder);
            if (!tasks.isEmpty())
                return tasks;
            else throw new PullException("Empty repository.");
        }catch (Exception e){
            Log.e(Synchronizer.class.getName(), e.getMessage(), e);
            Log.i(Synchronizer.class.getName(), "Attempt number pull: " + attemptNumber);
            if(e instanceof TransportException)
                cloneCommand = Git.cloneRepository().setURI(REPO_URL).setCredentialsProvider(credentials);
            if(attemptNumber == MAX_RETRY) {
                Looper.prepare();
                Toast.makeText(context, "Connection issue.", Toast.LENGTH_LONG).show();
                throw new PullException();
            } else
                pullFromRemote(forWorker, retryNumber.getRetryNumber(retryNumber.getNumber() - 1));
        }
        return tasks;
    }

    private void clearCache(boolean forWorker){
        File cacheDir;
        try {
            if(gitSync != null)
                cacheDir = Objects.requireNonNull(gitSync.getRepository().getDirectory().getParentFile()).getParentFile();
            else if(gitWorker != null)
                cacheDir = Objects.requireNonNull(gitWorker.getRepository().getDirectory().getParentFile()).getParentFile();
            else return;
            Log.i(GitHub.class.getName(), "Cache path: " + cacheDir);
            assert cacheDir != null;
            List<File> filesInCache = Arrays.asList(cacheDir.listFiles());
            List<File> workerCacheFiles = filesInCache.stream().filter(f -> f.getName().contains("GitWorker")).collect(Collectors.toList());
            Log.i(GitHub.class.getName(), "Worker files in cache: " + workerCacheFiles.size());
            List<File> syncCacheFiles = filesInCache.stream().filter(f -> f.getName().contains("GitRepo")).collect(Collectors.toList());
            Log.i(GitHub.class.getName(), "Sync files in cache: " + syncCacheFiles.size());
            if(workerCacheFiles.size() >= 8 && forWorker)
                clearWorkerCache(Objects.requireNonNull(cacheDir));
            if(syncCacheFiles.size() >= 8 && !forWorker)
                clearSyncCache(Objects.requireNonNull(cacheDir));
        } catch (Exception e) {
            Log.e(GitHub.class.getName(), e.getMessage(), e);
        }
    }

    private UsernamePasswordCredentialsProvider getCredentials(){
        return credentials;
    }

    private Git getGit(boolean forWorker) throws TransportException {
        File clonePath; // /data/user/0/com.example.taskmanager/cache/GitRepository3826014892690754971
        try {
            if(forWorker) {
                clonePath = setClonePathWorker();
                gitWorker = cloneCommand.setTimeout(TIMEOUT).setDirectory(clonePath).call();
                return gitWorker;
            }else {
                clonePath = setClonePathSync();
                gitSync = cloneCommand.setTimeout(TIMEOUT).setDirectory(clonePath).call();
                return gitSync;
            }
        } catch (Exception e) {
            Log.e(GitHub.class.getName(), e.getMessage(), e);
            if(e instanceof TransportException) {
                throw new TransportException("Connection issue.");
            }
            return null;
        }
    }

    private File getClonePath(boolean forWorker) {
        try{
            File theDir; // /data/user/0/com.example.taskmanager/cache/GitRepository3826014892690754971/webapp/csvFile.csv
            if(forWorker){
                theDir = new File(gitWorker.getRepository().getDirectory().getParent(), "webapp");
            }else{
                theDir = new File(gitSync.getRepository().getDirectory().getParent(), "webapp");
            }
            File localFile = new File(theDir, "csvFile.csv");
            Log.i(GitHub.class.getName(), "Local file: " + localFile);
            return localFile;
        } catch (Exception e) {
            Log.e(GitHub.class.getName(), e.getMessage(), e);
            return null;
        }
    }

    private File setClonePathWorker() {
        try {
            File localPath = File.createTempFile("GitWorker", "");
            // /data/user/0/com.example.taskmanager/cache/GitWorker3826014892690754971
            if (!localPath.delete())
                throw new IOException("Could not delete temporary file " + localPath);
            Log.i(GitHub.class.getName(), "Local path: " + localPath);
            return localPath;
        } catch (Exception e) {
            Log.e(GitHub.class.getName(), e.getMessage(), e);
            return null;
        }
    }

    private File setClonePathSync() {
        try {
            File localPath = File.createTempFile("GitRepo", "");
            // /data/user/0/com.example.taskmanager/cache/GitRepo3826014892690754971
            if (!localPath.delete())
                throw new IOException("Could not delete temporary file " + localPath);
            Log.i(GitHub.class.getName(), "Local path: " + localPath);
            return localPath;
        } catch (Exception e) {
            Log.e(GitHub.class.getName(), e.getMessage(), e);
            return null;
        }
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

    private List<Task> readTasks(File gitFolder) throws IOException{
        FileReader fileReader = null;
        BufferedReader reader = null;
        List<Task> tasks = new ArrayList<>();
        try {
            fileReader = new FileReader(gitFolder);
            reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] lineArray = line.split("\\|\\|\\|");
                Task task = readTask(lineArray);
                tasks.add(task);
            }
        }catch(IOException e){
            Log.e(GitHub.class.getName(), e.getMessage(), e);
            throw e;
        }finally {
            if(reader != null) reader.close();
            if(fileReader != null) fileReader.close();
        }
        return tasks;
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

    private void clearSyncCache(File dir) {
        Log.i(GitHub.class.getName(), "Cache folder: " + dir.toURI());
        try {
            Files.walk(Paths.get(dir.getPath()))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .filter(f -> f.getPath().contains("GitRepo"))
                    .forEach(f -> {
                        if(f.getName().contains("GitRepo"))
                            Log.i(GitHub.class.getName(), "Deleted: " + f.getName());
                        f.delete();
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearWorkerCache(File dir) {
        Log.i(GitHub.class.getName(), "Cache folder: " + dir.toURI());
        try {
            Files.walk(Paths.get(dir.getPath()))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .filter(f -> f.getPath().contains("GitWorker"))
                    .forEach(f -> {
                        if(f.getName().contains("GitWorker"))
                            Log.i(GitHub.class.getName(), "Deleted: " + f.getName());
                        f.delete();
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
