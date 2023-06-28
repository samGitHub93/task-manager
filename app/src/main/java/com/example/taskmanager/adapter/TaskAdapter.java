package com.example.taskmanager.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.ModifyTaskActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.database.DataManager;
import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.view_holder.ListViewHolder;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TaskAdapter extends RecyclerView.Adapter<ListViewHolder> implements Adapter<Task> {

    private final Context context;
    private final TaskViewModel viewModel;
    private final List<Task> tasks;
    private final DataManager dataManager;

    public TaskAdapter(Context context, TaskViewModel viewModel, List<Task> tasks) {
        this.context = context;
        this.viewModel = viewModel;
        this.tasks = tasks;
        this.dataManager = DataManager.getInstance(viewModel.getApplication());
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_view_square, viewGroup, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder viewHolder, final int position) {
        viewHolder.getTextView1().setText(tasks.get(position).getTitle());
        viewHolder.getTextView2().setText(tasks.get(position).getText());
        viewHolder.getTextView3().setText(tasks.get(position).getDate());
        viewHolder.getTextView4().setText("PRICE"); // TODO
        switchPriority(viewHolder, tasks.get(position).getPriorityType());
        viewHolder.getTextView1().setTextColor(Color.WHITE);
        viewHolder.getTextView2().setTextColor(Color.WHITE);
        viewHolder.getTextView3().setTextColor(Color.WHITE);
        viewHolder.getTextView4().setTextColor(Color.WHITE);
        String notify = tasks.get(position).getNotify();
        if(notify.trim().length() == 0 || DateUtil.fromStringToMillis(notify) - DateUtil.nowInMillis() < 0)
            viewHolder.getNotify().setImageResource(R.drawable.alarm_off);
        else viewHolder.getNotify().setImageResource(R.drawable.alarm_on);
        if(tasks.get(position).isDone()) {
            viewHolder.getIcon().setImageResource(R.drawable.circle_checked);
            viewHolder.getTextView1().setTextColor(Color.GRAY);
            viewHolder.getTextView2().setTextColor(Color.GRAY);
            viewHolder.getTextView3().setTextColor(Color.GRAY);
            viewHolder.getTextView4().setTextColor(Color.GRAY);
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
        try {
            if(dataManager.isActiveConnection((Activity) context).get()){
                task.setDone(true);
                viewModel.updateTask(task);
                dataManager.synchronizeFromRoom((Activity) context, false);
            } else {
                Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void undoneItem(int position){
        Task task = tasks.get(position);
        try {
            if(dataManager.isActiveConnection((Activity) context).get()){
                task.setDone(false);
                viewModel.updateTask(task);
                dataManager.synchronizeFromRoom((Activity) context, false);
            } else {
                Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
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
