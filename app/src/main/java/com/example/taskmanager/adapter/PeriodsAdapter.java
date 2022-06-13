package com.example.taskmanager.list_view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class PeriodsAdapter extends RecyclerView.Adapter<ListViewHolder> implements Adapter {

    private final TaskViewModel viewModel;
    private List<Task> tasks;
    private final List<Task> doneTasks;
    private final List<Task> deletedTasks;

    public PeriodsAdapter(TaskViewModel viewModel, List<Task> tasks) {
        this.viewModel = viewModel;
        this.tasks = tasks;
        doneTasks = new ArrayList<>();
        deletedTasks = new ArrayList<>();
        differentiateTasks();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_view_line, viewGroup, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder viewHolder, final int position) {
        viewHolder.getTextView1().setText(tasks.get(position).getTitle());
        viewHolder.getTextView2().setText(tasks.get(position).getText());
        viewHolder.getTextView3().setText(tasks.get(position).getDate());
        switchPriority(viewHolder, tasks.get(position).getPriorityType());
        viewHolder.getTextView1().setTextColor(Color.WHITE);
        viewHolder.getTextView2().setTextColor(Color.WHITE);
        viewHolder.getTextView3().setTextColor(Color.WHITE);
        if(tasks.get(position).isDone()) {
            viewHolder.getIcon().setImageResource(R.drawable.circle_checked);
            viewHolder.getTextView1().setTextColor(Color.GRAY);
            viewHolder.getTextView2().setTextColor(Color.GRAY);
            viewHolder.getTextView3().setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void deleteItem(int position) {
        Task task = tasks.get(position);
        deletedTasks.add(task);
        doneTasks.remove(task);
        viewModel.deleteTask(task);
        tasks.remove(task);
        notifyDataSetChanged();
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void doneItem(int position){
        Task task = tasks.get(position);
        task.setDone(true);
        viewModel.setDone(task);
        doneTasks.add(task);
        notifyDataSetChanged();
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void restoreItem(int position) {
        Task task = deletedTasks.get(deletedTasks.size() - 1);
        deletedTasks.remove(task);
        if(task.isDone()) doneTasks.add(task);
        tasks.add(position, task);
        viewModel.addTask(task);
        notifyDataSetChanged();
        tasks = TaskSorter.sortByPriority(tasks);
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void undoneItem(int position){
        Task task = doneTasks.get(doneTasks.size() - 1);
        tasks.get(position).setDone(false);
        viewModel.setUnDone(task);
        doneTasks.remove(task);
        notifyDataSetChanged();
    }

    @Override
    public Task getTask(int position){
        return tasks.get(position);
    }

    private void switchPriority(ListViewHolder viewHolder, PriorityType priorityType){
        switch (priorityType){
            case LOW:
                viewHolder.getIcon().setImageResource(R.drawable.circle_green);
                break;
            case MEDIUM:
                viewHolder.getIcon().setImageResource(R.drawable.circle_yellow);
                break;
            case HIGH:
                viewHolder.getIcon().setImageResource(R.drawable.circle_red);
                break;
        }
    }

    private void differentiateTasks(){
        for(Task task : tasks){
            if(task.isDone()){
                doneTasks.add(task);
            }
        }
    }
}
