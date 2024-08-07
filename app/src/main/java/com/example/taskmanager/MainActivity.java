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
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.taskmanager.fragment.CalendarFragment;
import com.example.taskmanager.repository.online_database.Synchronizer;
import com.example.taskmanager.fragment.PeriodsFragment;
import com.example.taskmanager.fragment.SearchFragment;
import com.example.taskmanager.process_activity.AddTaskActivity;
import com.example.taskmanager.view_model.TaskViewModel;
import com.example.taskmanager.notification.worker.UpdateWorker;
import com.example.taskmanager.notification.worker.WorkObserver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static UiActions currentFragment;
    private static FragmentManager fragmentManager;
    private ProgressBar progressBar;
    private CalendarFragment calendarFragment;
    private PeriodsFragment periodsFragment;
    private SearchFragment searchFragment;
    private Menu menu;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        updateFromWeb(true);
        fragmentManager = getSupportFragmentManager();
        initFragments();
        initNavigationButton();
        initFloatingButton();
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
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.refreshButton) {
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
        workManager.pruneWork();
        workManager.cancelAllWork();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
        workManager.enqueueUniquePeriodicWork("update_worker", ExistingPeriodicWorkPolicy.KEEP, workRequest);
        Observer<WorkInfo> newObserver = WorkObserver.createNewObserver(workManager);
        workManager.getWorkInfoByIdLiveData(workRequest.getId()).observeForever(newObserver);
        // startForegroundService(new Intent(this, ForegroundService.class));
    }

    private boolean areThereLateTasks(){
        TaskViewModel viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        return !Objects.requireNonNull(viewModel.getGetLateTasks().getValue()).isEmpty();
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

    private NavigationBarView.OnItemSelectedListener bottomNavigationAction(){
        return item -> {
            if(item.getTitle() == getResources().getString(R.string.calendar)) {
                setCurrentFragment(calendarFragment);
                return true;
            } else if(item.getTitle() == getResources().getString(R.string.periods)){
                setCurrentFragment(periodsFragment);
                return true;
            } else if(item.getTitle() == getResources().getString(R.string.search)){
                setCurrentFragment(searchFragment);
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
            new Synchronizer(getApplicationContext()).synchronizeFromWeb();
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

    private void initFragments(){
        calendarFragment = new CalendarFragment();
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
