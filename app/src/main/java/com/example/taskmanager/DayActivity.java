package com.example.taskmanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.adapter.TaskAdapter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.swiper.TaskSwiper;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.TaskViewModel;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class DayActivity extends AppCompatActivity {

    private TaskViewModel viewModel;
    private Observer<List<Task>> currentObserver;
    private TextView textDate;
    private Date date;
    private String dayString;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        progressBar = findViewById(R.id.progressBar);
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        dayString = getIntent().getStringExtra("day");
        textDate = findViewById(R.id.text_date);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateList() {
        if(currentObserver != null) removeObserver(date, currentObserver);
        textDate.setText(dayString);
        try {
            date = DateUtil.getSimpleFormatter().parse(dayString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        currentObserver = getNewObserver();
        viewModel.getTasksByDate(date).observe(this, currentObserver);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private void removeObserver(Date date, Observer<List<Task>> observer){
        viewModel.getTasksByDate(date).removeObserver(observer);
    }

    private Observer<List<Task>> getNewObserver(){
        return tasks -> createRecyclerView(TaskSorter.sortByPriority(tasks));
    }

    private void createRecyclerView(List<Task> tasks){
        TaskAdapter taskAdapter = new TaskAdapter(this, viewModel, tasks);
        RecyclerView recyclerView = findViewById(R.id.recycler_day);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelperDone = new ItemTouchHelper(new TaskSwiper(this));
        itemTouchHelperDone.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }
}
