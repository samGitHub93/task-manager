package com.example.taskmanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

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
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.List;
import java.util.Objects;

public class LateTaskActivity extends AppCompatActivity implements TaskActivity {

    private TaskViewModel viewModel;
    private Observer<List<Task>> currentObserver;
    private List<Task> initialTasks;
    public ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_late_tasks);
        progressBar = findViewById(R.id.progressBar);
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialTasks = viewModel.getGetLateTasks().getValue();
        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateUI(){
        if(currentObserver != null) removeObserver(currentObserver);
        currentObserver = getNewObserver();
        viewModel.getGetLateTasks().observe(this, currentObserver);
    }

    private void removeObserver(Observer<List<Task>> observer){
        viewModel.getGetLateTasks().removeObserver(observer);
    }

    private Observer<List<Task>> getNewObserver(){
        return tasks -> createRecyclerView(TaskSorter.sortByPriority(tasks));
    }

    private void createRecyclerView(List<Task> tasks){
        TaskAdapter taskAdapter = new TaskAdapter(this, viewModel, tasks);
        RecyclerView recyclerView = findViewById(R.id.recycler_late);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelperDone = new ItemTouchHelper(new TaskSwiper(this));
        itemTouchHelperDone.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Objects.requireNonNull(viewModel.getGetLateTasks().getValue()).size() != initialTasks.size())
            MainActivity.currentFragment.setListModified();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
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
