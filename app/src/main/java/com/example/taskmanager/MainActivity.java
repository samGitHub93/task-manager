package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.taskmanager.database.AppDatabase;
import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.fragment.AppFragment;
import com.example.taskmanager.fragment.DayFragment;
import com.example.taskmanager.fragment.PeriodsFragment;
import com.example.taskmanager.fragment.SearchFragment;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements TaskActivity {

    public AppDatabase database;
    public static AppFragment currentFragment;
    public static FragmentManager fragmentManager;
    public ProgressBar progressBar;
    private DataManager dataManager;
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
        database = AppDatabase.getDatabase(this);
        dataManager = DataManager.getInstance(getApplication());
        updateFromWeb();
        fragmentManager = getSupportFragmentManager();
        initFragments();
        initNavigationButton();
        initFloatingButton();
        initCalendar();
        setCurrentFragment(periodsFragment);
        WorkManager.getInstance(this).cancelAllWork();
        triggerUpdateWorker();
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
        if (id == R.id.calendarButton) showCalendar();
        else if(id == R.id.refreshButton) updateFromWeb();
        else if(id == R.id.warningButton) {
            Intent lateActivity = new Intent(MainActivity.this, LateTaskActivity.class);
            startActivity(lateActivity);
        }
        return super.onOptionsItemSelected(item);
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

    private void updateFromWeb(){
        try {
            if(dataManager.isActiveConnection(MainActivity.this).get()){
                dataManager.synchronizeFromWeb(MainActivity.this);
                if (currentFragment != null)
                    currentFragment.updateUI();
            }else
                Toast.makeText(this, "Cannot synchronize.", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
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
        currentFragment = (AppFragment) fragment;
    }

    public boolean areThereLateTasks(){
        TaskViewModel viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        return !Objects.requireNonNull(viewModel.getGetLateTasks().getValue()).isEmpty();
    }

    public void updateMenu(){
        if (menu != null) {
            menuItem = menu.getItem(0);
            if (areThereLateTasks()) {
                menuItem.setIcon(R.drawable.ic_baseline_warning_red_24);
            } else
                menuItem.setIcon(R.drawable.ic_baseline_warning_24);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMenu();
        updateUI();
    }

    private void triggerUpdateWorker(){
        WorkRequest workRequest = new PeriodicWorkRequest.Builder(UpdateWorker.class, 15, TimeUnit.MINUTES, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueue(workRequest);
        new WorkObserver().observe(this, workRequest.getId());
    }

    @Override
    public void updateUI(){
        currentFragment.updateUI();
    }

    @Override
    public void enableProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void disableProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void disableTouch() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void enableTouch() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
