package com.example.taskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanager.database.AppDatabase;
import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.fragment.AppFragment;
import com.example.taskmanager.fragment.DayFragment;
import com.example.taskmanager.fragment.PeriodsFragment;
import com.example.taskmanager.fragment.SearchFragment;
import com.example.taskmanager.util.DateUtil;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    public AppDatabase database;
    public static AppFragment currentFragment;
    public static FragmentManager fragmentManager;
    private DataManager dataManager;
    private DayFragment dayFragment;
    private PeriodsFragment periodsFragment;
    private SearchFragment searchFragment;
    private MaterialDatePicker<Long> datePicker;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = AppDatabase.getDatabase(this);
        dataManager = DataManager.getInstance(getApplication());
        dataManager.synchronizeFromWeb();
        fragmentManager = getSupportFragmentManager();
        initFragments();
        initNavigationButton();
        initFloatingButton();
        initCalendar();
        setCurrentFragment(dayFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calendarButton) showCalendar();
        else if(id == R.id.refreshButton) updateFromWeb();
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
                menu.getItem(0).setVisible(true);
                return true;
            } else if(item.getTitle() == getResources().getString(R.string.periods)){
                setCurrentFragment(periodsFragment);
                menu.getItem(0).setVisible(false);
                return true;
            } else if(item.getTitle() == getResources().getString(R.string.search)){
                setCurrentFragment(searchFragment);
                menu.getItem(0).setVisible(false);
                return true;
            }else return false;
        };
    }

    private void updateFromWeb(){
        try {
            Thread.sleep(1000);
            dataManager.synchronizeFromWeb();
            currentFragment.updateUI();
        } catch (InterruptedException e) {
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
}
