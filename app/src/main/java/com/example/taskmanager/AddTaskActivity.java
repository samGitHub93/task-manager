package com.example.taskmanager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.notification.Notifier;
import com.example.taskmanager.receiver.NotificationReceiver;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.TaskUtil;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AddTaskActivity extends ProcessTaskActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_task);
        super.initActivity();
        initElements();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initElements() {
        super.initElements();
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(buttonAction());
    }

    private Task createTask(){
        Task task = new Task();
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

    private View.OnClickListener buttonAction(){
        return view -> {
            Task task = createTask();
            try {
                if(!getDataManager().isActiveConnection(AddTaskActivity.this).get()) {
                    Toast.makeText(getApplication().getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
                }else if(!areDateFilled()){
                    Toast.makeText(this, "Fill the fields!", Toast.LENGTH_LONG).show();
                }else if(!areRecurringCorrectlyFilled()){
                    Toast.makeText(this, "Fill the fields!", Toast.LENGTH_LONG).show();
                }else if(!isValidNotificationDate(task)){
                    Toast.makeText(this, "Please, check notification date!", Toast.LENGTH_LONG).show();
                }else {
                    if(!task.getNotify().trim().isEmpty()){
                        Notifier notifier = new Notifier();
                        notifier.cancelAlarm(AddTaskActivity.this, task);
                        notifier.createAlarm(AddTaskActivity.this, task);
                    }
                    getTaskViewModel().insertTask(task);
                    getDataManager().synchronizeFromRoom(AddTaskActivity.this, true);
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(AddTaskActivity.class.getName(), e.getMessage(), e);
            }
        };
    }

    private boolean isValidNotificationDate(Task task){
        if(
                (DateUtil.fromStringToMillis(task.getNotify()) == 0 && !task.getNotify().trim().isEmpty()) ||
                        (!task.getNotify().trim().isEmpty() && DateUtil.fromStringToMillis(task.getNotify()) - DateUtil.nowInMillis() < 0) ||
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
}
