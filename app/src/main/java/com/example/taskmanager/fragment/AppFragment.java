package com.example.taskmanager.fragment;

import com.example.taskmanager.TaskActivity;

public interface AppFragment extends TaskActivity {
    boolean isListModified();
    void setListModified();
}
