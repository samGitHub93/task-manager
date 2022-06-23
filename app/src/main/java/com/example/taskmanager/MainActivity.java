package com.example.taskmanager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanager.database.AppDatabase;
import com.example.taskmanager.database.DataManager;
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
    private static FragmentManager fragmentManager;
    private DataManager dataManager;
    private DayFragment dayFragment;
    private PeriodsFragment periodsFragment;
    private SearchFragment searchFragment;
    private MaterialDatePicker<Long> datePicker;
    private Menu menu;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = AppDatabase.getDatabase(this);
        dataManager = new DataManager(this);
        dataManager.updateRoomDatabase();
        fragmentManager = getSupportFragmentManager();
        dayFragment = new DayFragment();
        periodsFragment = new PeriodsFragment();
        searchFragment = new SearchFragment();
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation);
        navigationBarView.setOnItemSelectedListener(bottomNavigationAction());
        FloatingActionButton addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(buttonAction());
        initCalendar();
        initFragment();
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
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initCalendar(){
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.MaterialCalendarTheme)
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .build();
        datePicker.addOnPositiveButtonClickListener(selectDateAction());
    }

    private void showCalendar(){
        datePicker.show(getSupportFragmentManager(),null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NavigationBarView.OnItemSelectedListener bottomNavigationAction(){
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

    View.OnClickListener buttonAction() {
        return view -> {
            Intent addTaskActivity = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(addTaskActivity);
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MaterialPickerOnPositiveButtonClickListener<Long> selectDateAction() {
        return selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            calendar = DateUtil.getCalendarWithoutTime(calendar.getTime());
            Date date = calendar.getTime();
            dayFragment.setDate(date);
            setCurrentFragment(dayFragment);
        };
    }

    public void initFragment(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment, dayFragment).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setCurrentFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        dataManager.updateRoomDatabase();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        dataManager.updateRoomDatabase();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
        dataManager.updateRoomDatabase();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        dataManager.updateRoomDatabase();
    }
}
