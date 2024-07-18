package com.example.taskmanager.process_activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.taskmanager.R;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.notification.Notifier;
import com.example.taskmanager.repository.online_database.Synchronizer;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.TaskUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;
import java.util.concurrent.Executors;

public class ModifyTaskActivity extends ProcessTaskActivity {
    private Task retrievedTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_modify_task);
        super.initActivity();
        initElements();
        retrievedTask = retrieveTask();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void initElements(){
        super.initElements();
        Button modButton = findViewById(R.id.modify_button);
        Button delButton = findViewById(R.id.delete_button);
        modButton.setOnClickListener(modifyButtonAction());
        delButton.setOnClickListener(showDialog());
    }

    private Task retrieveTask(){
        Task retrievedTask = new Task();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long id = extras.getLong("id");
            retrievedTask = getTaskViewModel().getTaskById(id).getValue();
            assert retrievedTask != null;
            getTaskTitle().setText(retrievedTask.getTitle());
            getTaskDetails().setText(retrievedTask.getText());
            getTaskDate().setText(retrievedTask.getDate());
            getTaskPriority().setText(retrievedTask.getPriorityType().toString(), false);
            getTaskRecurring().setText(retrievedTask.getRecurringType().toString(), false);
            getTaskRecurringUntil().setText(retrievedTask.getRecurringUntil());
            String notify = retrievedTask.getNotify();
            if(!retrievedTask.getNotify().trim().isEmpty()){
                getTaskNotifyDate().setText(notify.substring(0, notify.indexOf(":") - 3));
                getTaskNotifyTime().setText(notify.substring(notify.indexOf(":") - 2));
            }
        }
        return retrievedTask;
    }

    private Task createTask(){
        Task task = new Task();
        task.setId(retrievedTask.getId());
        task.setTitle(getTaskTitle().getText().toString());
        task.setText(getTaskDetails().getText().toString());
        task.setDate(Objects.requireNonNull(getTaskDate().getText()).toString());
        task.setPriorityType(TaskUtil.stringToPriorityType(getTaskPriority().getText().toString()));
        task.setRecurringType(TaskUtil.stringToRecurringType(getTaskRecurring().getText().toString()));
        task.setRecurringUntil(Objects.requireNonNull(getTaskRecurringUntil().getText()).toString());
        task.setDone(false);
        task.setNotify(Objects.requireNonNull(getTaskNotifyDate().getText()) + " " + Objects.requireNonNull(getTaskNotifyTime().getText()));
        return task;
    }

    private View.OnClickListener modifyButtonAction(){
        return view -> {
            Task task = createTask();
            if(!areDateFilled()){
                Toast.makeText(this, "Fill the fields!", Toast.LENGTH_LONG).show();
            }else if(!areRecurringCorrectlyFilled()){
                Toast.makeText(this, "Fill the fields!", Toast.LENGTH_LONG).show();
            }else if(!isValidNotificationDate(task)){
                Toast.makeText(this, "Please, check notification date!", Toast.LENGTH_LONG).show();
            }else {
                if(!task.getNotify().trim().isEmpty()){
                    Notifier notifier = new Notifier();
                    notifier.cancelAlarm(ModifyTaskActivity.this, task);
                    notifier.createAlarm(ModifyTaskActivity.this, task);
                }
                update(task);
                finish();
            }
        };
    }

    private View.OnClickListener showDialog(){
        return view -> new MaterialAlertDialogBuilder(ModifyTaskActivity.this, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                .setTitle(getResources().getString(R.string.delete_dialog_title))
                .setNegativeButton(getResources().getString(R.string.cancel_dialog), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getResources().getString(R.string.confirm_dialog), (dialog, which) -> deleteTask()).show();
    }

    private void deleteTask() {
        delete(retrievedTask);
        finish();
    }

    private boolean isValidNotificationDate(Task task){
        if(
                (!task.getNotify().trim().isEmpty() && DateUtil.fromStringDateTimeToMillis(task.getNotify()) == 0) ||
                        (!task.getNotify().trim().isEmpty() && DateUtil.fromStringDateTimeToMillis(task.getNotify()) - DateUtil.nowInMillis() < 0) ||
                        (Objects.requireNonNull(getTaskNotifyDate().getText()).toString().equals(EMPTY_STRING) && !Objects.requireNonNull(getTaskNotifyTime().getText()).toString().equals(EMPTY_STRING)) ||
                        (!getTaskNotifyDate().getText().toString().equals(EMPTY_STRING)&& Objects.requireNonNull(getTaskNotifyTime().getText()).toString().equals(EMPTY_STRING))){
            getTaskNotifyDate().setBackgroundColor(ContextCompat.getColor(this, R.color.red_lite));
            getTaskNotifyTime().setBackgroundColor(ContextCompat.getColor(this, R.color.red_lite));
            return false;
        }else{
            return true;
        }
    }

    private boolean areDateFilled(){
        if((Objects.requireNonNull(getTaskDate().getText()).toString().equals(EMPTY_STRING))){
            getTaskDate().setBackgroundColor(ContextCompat.getColor(this, R.color.red_lite));
            return false;
        }else{
            return true;
        }
    }

    private boolean areRecurringCorrectlyFilled(){
        if(
                (getTaskRecurring().getText().toString().equalsIgnoreCase("NONE") && !Objects.requireNonNull(getTaskRecurringUntil().getText()).toString().equals(EMPTY_STRING)) ||
                        (!getTaskRecurring().getText().toString().equalsIgnoreCase("NONE") && Objects.requireNonNull(getTaskRecurringUntil().getText()).toString().equals(EMPTY_STRING))){
            getTaskRecurring().setBackgroundColor(ContextCompat.getColor(this, R.color.red_lite));
            getTaskRecurringUntil().setBackgroundColor(ContextCompat.getColor(this, R.color.red_lite));
            return false;
        }else{
            return true;
        }
    }

    private void update(Task taskToUpdate){
        Executors.newSingleThreadExecutor().submit(() -> {
            runOnUiThread(() -> {
                enableProgressBar();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
            getTaskViewModel().updateTask(taskToUpdate);
            new Synchronizer(getApplicationContext()).synchronizeFromRoom();
            if(taskToUpdate.getNotify() != null && !taskToUpdate.getNotify().replaceAll(" ", "").isEmpty())
                new Notifier().createAlarm(getApplicationContext(), taskToUpdate);
            runOnUiThread(() -> {
                disableProgressBar();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
        });
    }

    private void delete(Task taskToDelete){
        Executors.newSingleThreadExecutor().submit(() -> {
            runOnUiThread(() -> {
                enableProgressBar();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
            getTaskViewModel().deleteTask(taskToDelete);
            new Synchronizer(getApplicationContext()).synchronizeFromRoom();
            runOnUiThread(() -> {
                disableProgressBar();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
        });
    }
}
