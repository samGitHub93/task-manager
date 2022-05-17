package com.example.taskmanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.enumerator.RecurringType;
import com.example.taskmanager.fragment.TaskFragment;
import com.example.taskmanager.model.Task;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.MaterialCalendarTheme)
                .build();
        datePicker.show(getSupportFragmentManager(),null);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN);
        String dateStr = formatter.format(new Date());
        Task task1 = new Task("SAM","Task 1","Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",dateStr, PriorityType.MEDIUM, RecurringType.MONTHLY, false);
        Task task2 = new Task("SAM","Task 2","text 2",dateStr,PriorityType.HIGH, RecurringType.NONE, false);
        Task task3 = new Task("SAM","Task 3","text 3",dateStr, PriorityType.MEDIUM, RecurringType.DAILY,false);
        Task task4 = new Task("SAM","Task 4","text 1",dateStr, PriorityType.LOW, RecurringType.WEEKLY,true);
        Task task5 = new Task("SAM","Task 5","text 2",dateStr, PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task6 = new Task("SAM","Task 6","text 3",dateStr, PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task7 = new Task("SAM","Task 7","text 1",dateStr, PriorityType.LOW, RecurringType.YEARLY,false);
        Task task8 = new Task("SAM","Task 8","text 2",dateStr, PriorityType.HIGH, RecurringType.NONE,true);
        Task task9 = new Task("SAM","Task 9","text 3",dateStr, PriorityType.HIGH, RecurringType.NONE,false);
        Task task10 = new Task("SAM","Task 10","text 1",dateStr, PriorityType.MEDIUM, RecurringType.NONE,false);
        Task task11 = new Task("SAM","Task 11","text 2",dateStr, PriorityType.LOW, RecurringType.WEEKLY,false);
        Task task12 = new Task("SAM","Task 12","text 3",dateStr, PriorityType.MEDIUM, RecurringType.NONE,false);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        tasks.add(task5);
        tasks.add(task6);
        tasks.add(task7);
        tasks.add(task8);
        tasks.add(task9);
        tasks.add(task10);
        tasks.add(task11);
        tasks.add(task12);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        TaskFragment taskFragment = new TaskFragment(tasks);
        transaction.replace(R.id.fragment, taskFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calendarButton) {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTheme(R.style.MaterialCalendarTheme)
                    .build();
            datePicker.show(getSupportFragmentManager(),null);
        }
        return super.onOptionsItemSelected(item);
    }
}
