package com.example.taskmanager.database;

import android.content.Context;

import com.example.taskmanager.BuildConfig;
import com.example.taskmanager.R;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class GitHub {
    private static final String REPO_URL = BuildConfig.REPO_URL;
    private static GitHub instance;
    private final UsernamePasswordCredentialsProvider credentials;
    private final CloneCommand cloneCommand;

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

    Git getGit() {
        Git git = null;
        try {
            File localPath = File.createTempFile("GitRepository", "");
            if (!localPath.delete())
                throw new IOException("Could not delete temporary file " + localPath);
            git = cloneCommand.setDirectory(localPath).call();
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
        return git;
    }

    public UsernamePasswordCredentialsProvider getCredentials(){
        return credentials;
    }
}
