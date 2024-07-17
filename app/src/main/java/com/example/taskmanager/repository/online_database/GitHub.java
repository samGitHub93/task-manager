package com.example.taskmanager.repository.online_database;

import android.content.Context;
import android.util.Log;

import com.example.taskmanager.BuildConfig;
import com.example.taskmanager.R;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

class GitHub {
    private static final String REPO_URL = BuildConfig.REPO_URL;
    private static GitHub instance;
    private final UsernamePasswordCredentialsProvider credentials;
    private CloneCommand cloneCommand;
    private Git gitWorker;
    private Git gitSync;
    private final static int TIMEOUT = 3;

    private GitHub(Context context) {
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

    UsernamePasswordCredentialsProvider getCredentials(){
        return credentials;
    }

    Git getGit(boolean forWorker) {
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
                cloneCommand = Git.cloneRepository().setURI(REPO_URL).setCredentialsProvider(credentials);
                return getGit(forWorker);
            }
            return null;
        }
    }

    File getClonePath(boolean forWorker) {
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

    void clearCache(boolean forWorker){
        File cacheDir;
        try {
            if(gitSync != null)
                cacheDir = Objects.requireNonNull(gitSync.getRepository().getDirectory().getParentFile()).getParentFile();
            else if(gitWorker != null)
                cacheDir = Objects.requireNonNull(gitWorker.getRepository().getDirectory().getParentFile()).getParentFile();
            else return;
            Log.i(GitHub.class.getName(), "Cache path: " + cacheDir);
            assert cacheDir != null;
            int fileInCache = Objects.requireNonNull(cacheDir.listFiles()).length;
            Log.i(GitHub.class.getName(), "Files in cache: " + fileInCache);
            if(fileInCache > 20) {
                if(forWorker)
                    clearWorkerCache(Objects.requireNonNull(cacheDir));
                else
                    clearSyncCache(Objects.requireNonNull(cacheDir));
            }
        } catch (Exception e) {
            Log.e(GitHub.class.getName(), e.getMessage(), e);
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
