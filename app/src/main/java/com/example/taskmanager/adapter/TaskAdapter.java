package com.example.taskmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.ModifyTaskActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.view_holder.ListViewHolder;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<ListViewHolder> implements Adapter<Task> {

    private final Context context;
    private final TaskViewModel viewModel;
    private final List<Task> tasks;
    private final DataManager dataManager;

    public TaskAdapter(Context context, TaskViewModel viewModel, List<Task> tasks) {
        this.context = context;
        this.viewModel = viewModel;
        this.tasks = tasks;
        dataManager = DataManager.getInstance(viewModel.getApplication());
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
        viewHolder.itemView.setOnClickListener(onClickAction(viewHolder));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void doneItem(int position){
        Task task = tasks.get(position);
        task.setDone(true);
        viewModel.updateTask(task);
        dataManager.synchronizeFromRoom();
        notifyDataSetChanged();
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void undoneItem(int position){
        Task task = tasks.get(position);
        task.setDone(false);
        viewModel.updateTask(task);
        dataManager.synchronizeFromRoom();
        notifyDataSetChanged();
    }

    @Override
    public Task getTask(int position){
        return tasks.get(position);
    }

    public View.OnClickListener onClickAction(RecyclerView.ViewHolder viewHolder) {
        return view -> {
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION)
                notifyItemChanged(position);
            long id = getTask(position).getId();
            Intent modifyTaskActivity = new Intent(context, ModifyTaskActivity.class);
            modifyTaskActivity.putExtra("id", id);
            context.startActivity(modifyTaskActivity);
        };
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
}
