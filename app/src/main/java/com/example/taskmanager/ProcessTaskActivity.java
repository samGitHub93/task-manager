package com.example.taskmanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.view_item.DateTextView;
import com.example.taskmanager.view_item.DropDown;
import com.example.taskmanager.view_item.TimeTextView;
import com.example.taskmanager.view_model.TaskViewModel;

public abstract class ProcessTaskActivity extends AppCompatActivity implements UiProcessAnimations {

    private DataManager dataManager;
    private TaskViewModel taskViewModel;
    private ProgressBar progressBar;
    public static final String EMPTY_STRING = "";
    private AutoCompleteTextView taskTitle;
    private AutoCompleteTextView taskDetails;
    private DateTextView taskDate;
    private DropDown taskPriority;
    private DropDown taskRecurring;
    private DateTextView taskRecurringUntil;
    private DateTextView taskNotifyDate;
    private TimeTextView taskNotifyTime;

    public AutoCompleteTextView getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(AutoCompleteTextView taskTitle) {
        this.taskTitle = taskTitle;
    }

    public AutoCompleteTextView getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(AutoCompleteTextView taskDetails) {
        this.taskDetails = taskDetails;
    }

    public DateTextView getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(DateTextView taskDate) {
        this.taskDate = taskDate;
    }

    public DropDown getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(DropDown taskPriority) {
        this.taskPriority = taskPriority;
    }

    public DropDown getTaskRecurring() {
        return taskRecurring;
    }

    public void setTaskRecurring(DropDown taskRecurring) {
        this.taskRecurring = taskRecurring;
    }

    public DateTextView getTaskRecurringUntil() {
        return taskRecurringUntil;
    }

    public void setTaskRecurringUntil(DateTextView taskRecurringUntil) {
        this.taskRecurringUntil = taskRecurringUntil;
    }

    public DateTextView getTaskNotifyDate() {
        return taskNotifyDate;
    }

    public void setTaskNotifyDate(DateTextView taskNotifyDate) {
        this.taskNotifyDate = taskNotifyDate;
    }

    public TimeTextView getTaskNotifyTime() {
        return taskNotifyTime;
    }

    public void setTaskNotifyTime(TimeTextView taskNotifyTime) {
        this.taskNotifyTime = taskNotifyTime;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public TaskViewModel getTaskViewModel() {
        return taskViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutRes){
        super.setContentView(layoutRes);
    }

    public void initActivity(){
        progressBar = findViewById(R.id.progressBar);
        dataManager = DataManager.getInstance(getApplication());
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initElements(){
        taskTitle = findViewById(R.id.title_text_view);
        taskDetails = findViewById(R.id.details_text_view);
        taskDate = findViewById(R.id.date_text_view);
        taskRecurringUntil = findViewById(R.id.until_text_view);
        taskNotifyDate = findViewById(R.id.notify_text_view);
        taskNotifyTime = findViewById(R.id.notify_time_text_view);
        taskPriority = findViewById(R.id.priority_text_view);
        String[] itemsUntil = new String[] { get(R.string.low), get(R.string.medium), get(R.string.high) };
        taskPriority.addItemsToDropdown(itemsUntil, 1);
        taskRecurring = findViewById(R.id.recurring_text_view);
        String[] itemsOrder = new String[] { get(R.string.none), get(R.string.daily), get(R.string.weekly), get(R.string.monthly), get(R.string.yearly) };
        taskRecurring.addItemsToDropdown(itemsOrder, 0);
    }

    public String get(@StringRes int id){
        return getResources().getString(id);
    }

    @Override
    public void enableProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void disableProgressBar() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
