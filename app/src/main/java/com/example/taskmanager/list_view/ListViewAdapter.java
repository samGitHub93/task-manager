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
import com.example.taskmanager.sorter.TaskSorter;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private List<Task> tasks;
    private final List<Task> doneTasks;
    private final List<Task> deletedTasks;

    public ListViewAdapter(List<Task> tasks) {
        this.tasks = tasks;
        doneTasks = new ArrayList<>();
        deletedTasks = new ArrayList<>();
        differentiateTasks(tasks);
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

    @SuppressLint("NotifyDataSetChanged")
    public void deleteItem(int position) {
        Task task = tasks.get(position);
        deletedTasks.add(task);
        doneTasks.remove(task);
        tasks.remove(task);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void doneItem(int position){
        Task task = tasks.get(position);
        task.setDone(true);
        doneTasks.add(task);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void restoreItem(int position) {
        Task task = deletedTasks.get(deletedTasks.size() - 1);
        deletedTasks.remove(task);
        if(task.isDone()) doneTasks.add(task);
        tasks.add(position, task);
        notifyDataSetChanged();
        tasks = TaskSorter.sortTasks(tasks);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void undoneItem(int position){
        Task task = doneTasks.get(doneTasks.size() - 1);
        tasks.get(position).setDone(false);
        doneTasks.remove(task);
        notifyDataSetChanged();
    }

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

    private void differentiateTasks(List<Task> tasks){
        for(Task task : tasks){
            if(task.isDone()){
                doneTasks.add(task);
            }
        }
    }
}
