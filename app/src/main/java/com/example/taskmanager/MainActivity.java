package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.taskmanager.repository.online_database.Synchronizer;
import com.example.taskmanager.fragment.DayFragment;
import com.example.taskmanager.fragment.PeriodsFragment;
import com.example.taskmanager.fragment.SearchFragment;
import com.example.taskmanager.process_activity.AddTaskActivity;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.view_model.TaskViewModel;
import com.example.taskmanager.worker.UpdateWorker;
import com.example.taskmanager.worker.WorkObserver;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static UiActions currentFragment;
    private static FragmentManager fragmentManager;
    private ProgressBar progressBar;
    private Synchronizer synchronizer;
    private DayFragment dayFragment;
    private PeriodsFragment periodsFragment;
    private SearchFragment searchFragment;
    private MaterialDatePicker<Long> datePicker;
    private Menu menu;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        synchronizer = Synchronizer.getInstance(getApplicationContext());
        fragmentManager = getSupportFragmentManager();
        updateFromWeb(true);
        initFragments();
        initNavigationButton();
        initFloatingButton();
        initCalendar();
        setCurrentFragment(periodsFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.getItem(0);
        if(areThereLateTasks()){
            menuItem.setIcon(R.drawable.ic_baseline_warning_red_24);
        }else menuItem.setIcon(R.drawable.ic_baseline_warning_24);
        if(currentFragment instanceof PeriodsFragment || currentFragment instanceof SearchFragment)
            this.menu.getItem(1).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calendarButton) {
            showCalendar();
        }else if(id == R.id.refreshButton) {
            updateFromWeb(false);
        }else if(id == R.id.warningButton) {
            Intent lateActivity = new Intent(MainActivity.this, LateTasksActivity.class);
            startActivity(lateActivity);
        }
        return super.onOptionsItemSelected(item);
    }

    public Menu getMenu() {
        return menu;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem){
        this.menuItem = menuItem;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private void triggerUpdateWorker(){
        WorkManager workManager = WorkManager.getInstance(this);
        WorkObserver.removeObserver(workManager);
        workManager.cancelAllWork();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
        workManager.enqueue(workRequest);
        Observer<WorkInfo> newObserver = WorkObserver.createNewObserver(workManager);
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observeForever(newObserver);
    }

    private boolean areThereLateTasks(){
        TaskViewModel viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        return !Objects.requireNonNull(viewModel.getGetLateTasks().getValue()).isEmpty();
    }

    private void initCalendar(){
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.MaterialCalendarTheme)
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();
        datePicker.addOnPositiveButtonClickListener(selectDateAction());
    }

    private void initNavigationButton(){
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation);
        navigationBarView.setSelectedItemId(R.id.page_2);
        navigationBarView.setOnItemSelectedListener(bottomNavigationAction());
    }

    private void initFloatingButton(){
        FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(buttonAction());
    }

    private void showCalendar(){
        datePicker.show(getSupportFragmentManager(),null);
    }

    private NavigationBarView.OnItemSelectedListener bottomNavigationAction(){
        return item -> {
            if(item.getTitle() == getResources().getString(R.string.day)) {
                setCurrentFragment(dayFragment);
                menu.getItem(1).setVisible(true);
                return true;
            } else if(item.getTitle() == getResources().getString(R.string.periods)){
                setCurrentFragment(periodsFragment);
                menu.getItem(1).setVisible(false);
                return true;
            } else if(item.getTitle() == getResources().getString(R.string.search)){
                setCurrentFragment(searchFragment);
                menu.getItem(1).setVisible(false);
                return true;
            }else return false;
        };
    }

    private void updateFromWeb(boolean triggerWorker) {
        Executors.newSingleThreadExecutor().submit(() -> {
            runOnUiThread(() -> {
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
            synchronizer.synchronizeFromWeb();
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                currentFragment.updateMenu();
                currentFragment.updateUI();
            });
            if(triggerWorker)
                triggerUpdateWorker();
        });
    }

    private View.OnClickListener buttonAction() {
        return view -> {
            Intent addTaskActivity = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(addTaskActivity);
        };
    }

    private MaterialPickerOnPositiveButtonClickListener<Long> selectDateAction() {
        return selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            calendar = DateUtil.getCalendarWithoutTime(calendar.getTime());
            Date date = calendar.getTime();
            dayFragment.setDate(date);
            dayFragment.updateUI();
            setCurrentFragment(dayFragment);
        };
    }

    private void initFragments(){
        dayFragment = new DayFragment();
        periodsFragment = new PeriodsFragment();
        searchFragment = new SearchFragment();
    }

    private void setCurrentFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        currentFragment = (UiActions) fragment;
    }
}
