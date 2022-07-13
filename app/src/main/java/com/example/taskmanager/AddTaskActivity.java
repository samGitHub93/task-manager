package com.example.taskmanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.TaskUtil;
import com.example.taskmanager.view_model.TaskViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class AddTaskActivity extends AppCompatActivity {

    private DataManager dataManager;
    private TaskViewModel addTaskViewModel;
    private MaterialDatePicker<Long> datePicker;
    private AutoCompleteTextView titleTextView;
    private AutoCompleteTextView detailsTextView;
    private TextInputEditText dateTextView;
    private AutoCompleteTextView priorityTextView;
    private AutoCompleteTextView recurringTextView;
    private static final String EMPTY_STRING = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        dataManager = DataManager.getInstance(getApplication());
        addTaskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initElements();
        initCalendar();
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
        addItemsToDropdowns();
        dateTextView.setOnClickListener(dateTextViewAction());
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(buttonAction());
    }

    private View.OnClickListener dateTextViewAction(){
        return view -> showCalendar();
    }

    private View.OnClickListener buttonAction(){
        return view -> {
            Task task = new Task();
            task.setTitle(titleTextView.getText().toString());
            task.setText(detailsTextView.getText().toString());
            task.setDate(Objects.requireNonNull(dateTextView.getText()).toString());
            task.setPriorityType(TaskUtil.stringToPriorityType(priorityTextView.getText().toString()));
            task.setRecurringType(TaskUtil.stringToRecurringType(recurringTextView.getText().toString()));
            task.setDone(false);
            if(!dateTextView.getText().toString().equals(EMPTY_STRING)) {
                addTaskViewModel.insertTask(task);
                dataManager.synchronizeFromRoom();
                Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
                MainActivity.currentFragment.updateUI();
                this.finish();
            }else Toast.makeText(this, "Fill the fields!", Toast.LENGTH_LONG).show();
        };
    }

    private void initCalendar(){
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.MaterialCalendarTheme)
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
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

    private void addItemsToDropdowns(){
        String[] itemsUntil = new String[] { get(R.string.high), get(R.string.medium), get(R.string.low)};
        ArrayAdapter<String> adapterUntil = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, itemsUntil);
        priorityTextView.setAdapter(adapterUntil);
        priorityTextView.setText(priorityTextView.getAdapter().getItem(1).toString(), false);
        String[] itemsOrder = new String[] { get(R.string.none), get(R.string.daily), get(R.string.weekly), get(R.string.monthly), get(R.string.yearly)};
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, itemsOrder);
        recurringTextView.setAdapter(adapterOrder);
        recurringTextView.setText(recurringTextView.getAdapter().getItem(0).toString(), false);
    }

    private String get(@StringRes int id){
        return getResources().getString(id);
    }
}
