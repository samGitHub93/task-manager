package com.example.taskmanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanager.fragment.DayFragment;
import com.example.taskmanager.fragment.PeriodsFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private static Fragment currentFragment;
    private static FragmentManager fragmentManager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        tabLayout = findViewById(R.id.tabs);
        addTabLayoutAction();

        initFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calendarButton) showCalendar();
        return super.onOptionsItemSelected(item);
    }

    private void showCalendar(){
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.MaterialCalendarTheme)
                .build();
        datePicker.show(getSupportFragmentManager(),null);
    }

    public void addTabLayoutAction(){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchFragment();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void initFragment(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        DayFragment dayFragment = new DayFragment();
        transaction.add(R.id.fragment, dayFragment).commit();
        currentFragment = dayFragment;
    }

    public void switchFragment() {
        if(currentFragment == null) initFragment();
        else setCurrentFragment(currentFragment);
    }

    public void setCurrentFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(fragment instanceof DayFragment) {
            PeriodsFragment periodsFragment = new PeriodsFragment();
            transaction.replace(R.id.fragment, periodsFragment);
            currentFragment = periodsFragment;
        } else if(fragment instanceof PeriodsFragment) {
            DayFragment dayFragment = new DayFragment();
            transaction.replace(R.id.fragment, dayFragment);
            currentFragment = dayFragment;
        }
        transaction.commit();
    }
}
