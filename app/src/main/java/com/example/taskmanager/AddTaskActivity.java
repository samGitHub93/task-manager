package com.example.taskmanager;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.TaskUtil;
import com.example.taskmanager.view_model.AddTaskViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class AddTaskActivity extends AppCompatActivity {

    private DataManager dataManager;
    private AddTaskViewModel addTaskViewModel;
    private MaterialDatePicker<Long> datePicker;
    private AutoCompleteTextView titleTextView;
    private AutoCompleteTextView detailsTextView;
    private TextInputEditText dateTextView;
    private AutoCompleteTextView priorityTextView;
    private AutoCompleteTextView recurringTextView;
    private Button addButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        dataManager = new DataManager(getApplicationContext());
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addTaskViewModel = new ViewModelProvider(this).get(AddTaskViewModel.class);
        initElements();
        initCalendar();
        dateTextView.setOnClickListener(dateTextViewAction());
        addButton.setOnClickListener(buttonAction());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initElements(){
        titleTextView = findViewById(R.id.title_text_view);
        detailsTextView = findViewById(R.id.details_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        priorityTextView = findViewById(R.id.priority_text_view);
        recurringTextView = findViewById(R.id.recurring_text_view);
        addButton = findViewById(R.id.add_button);
    }

    private View.OnClickListener dateTextViewAction(){
        return view -> showCalendar();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private View.OnClickListener buttonAction(){
        return view -> {
            Task task = new Task();
            task.setTitle(titleTextView.getText().toString());
            task.setText(detailsTextView.getText().toString());
            task.setDate(Objects.requireNonNull(dateTextView.getText()).toString());
            task.setPriorityType(TaskUtil.stringToPriorityType(priorityTextView.getText().toString()));
            task.setRecurringType(TaskUtil.stringToRecurringType(recurringTextView.getText().toString()));
            task.setDone(false);
            if(!dateTextView.getText().toString().equals("")) {
                addTaskViewModel.insertTask(task);
                dataManager.saveData();
                Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
                this.finish();
            }else Toast.makeText(this, "Fill the fields!", Toast.LENGTH_LONG).show();
        };
    }

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

    public MaterialPickerOnPositiveButtonClickListener<Long> selectDateAction() {
        return selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            calendar = DateUtil.getCalendarWithoutTime(calendar.getTime());
            Date date = calendar.getTime();
            dateTextView.setText(DateUtil.getFormatter().format(date));
        };
    }
}
