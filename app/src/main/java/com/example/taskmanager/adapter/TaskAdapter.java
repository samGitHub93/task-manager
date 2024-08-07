package com.example.taskmanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.DayActivity;
import com.example.taskmanager.LateTasksActivity;
import com.example.taskmanager.MainActivity;
import com.example.taskmanager.enumerator.RecurringType;
import com.example.taskmanager.process_activity.ModifyTaskActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.online_database.Synchronizer;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.StringUtil;
import com.example.taskmanager.view_holder.ListViewHolder;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.List;
import java.util.concurrent.Executors;

public class TaskAdapter extends RecyclerView.Adapter<ListViewHolder> implements Adapter<Task> {

    private final Context context;
    private final TaskViewModel viewModel;
    private final List<Task> tasks;

    public TaskAdapter(Context context, TaskViewModel viewModel, List<Task> tasks) {
        this.context = context;
        this.viewModel = viewModel;
        this.tasks = tasks;
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
        switchPriority(viewHolder, tasks.get(position).getPriorityType());
        viewHolder.getTextView1().setTextColor(Color.WHITE);
        viewHolder.getTextView2().setTextColor(Color.WHITE);
        viewHolder.getTextView3().setTextColor(Color.WHITE);
        viewHolder.getTextRec().setTextColor(Color.WHITE);
        viewHolder.getTextAlarm().setTextColor(Color.WHITE);
        String notify = tasks.get(position).getNotify();
        if(notify.trim().isEmpty() || DateUtil.fromStringDateTimeToMillis(notify) - DateUtil.nowInMillis() < 0) {
            viewHolder.getNotify().setImageResource(R.drawable.alarm_off);
            viewHolder.getTextAlarm().setText("");
        } else {
            viewHolder.getNotify().setImageResource(R.drawable.alarm_on);
                viewHolder.getTextAlarm().setText(DateUtil.getNotifyFormatter().format(DateUtil.fromStringDateTimeToMillis(notify)));
        }
        if(tasks.get(position).getRecurringType() != RecurringType.NONE){
            viewHolder.getTextRec().setText(StringUtil.capFirstCharacter(tasks.get(position).getRecurringType().name()));
        } else viewHolder.getTextRec().setText("");
        if(tasks.get(position).isDone()) {
            viewHolder.getIcon().setImageResource(R.drawable.circle_checked);
            viewHolder.getTextView1().setTextColor(Color.GRAY);
            viewHolder.getTextView2().setTextColor(Color.GRAY);
            viewHolder.getTextView3().setTextColor(Color.GRAY);
            viewHolder.getTextRec().setTextColor(Color.GRAY);
            viewHolder.getTextAlarm().setTextColor(Color.GRAY);
            viewHolder.getNotify().setImageResource(R.drawable.alarm_off);
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
        update(task);
        notifyDataSetChanged();
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void undoneItem(int position){
        Task task = tasks.get(position);
        task.setDone(false);
        update(task);
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

    private void update(Task task){
        if(context instanceof LateTasksActivity){
            updateLate(task);
        }else if(context instanceof MainActivity){
            updateNotLate(task);
        }else{
            updateInDay(task);
        }
    }

    private void updateNotLate(Task task){
        Executors.newSingleThreadExecutor().submit(() -> {
            ((MainActivity) context).runOnUiThread(() -> {
                enableProgressBar();
                ((MainActivity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
            viewModel.updateTask(task);
            new Synchronizer(context).synchronizeFromRoom();
            ((MainActivity) context).runOnUiThread(() -> {
                disableProgressBar();
                ((MainActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
        });
    }

    private void updateLate(Task task){
        Executors.newSingleThreadExecutor().submit(() -> {
            ((LateTasksActivity) context).runOnUiThread(() -> {
                enableProgressBar();
                ((LateTasksActivity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
            viewModel.updateTask(task);
            new Synchronizer(context).synchronizeFromRoom();
            ((LateTasksActivity) context).runOnUiThread(() -> {
                disableProgressBar();
                ((LateTasksActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
        });
    }

    private void updateInDay(Task task){
        Executors.newSingleThreadExecutor().submit(() -> {
            ((DayActivity) context).runOnUiThread(() -> {
                enableProgressBar();
                ((DayActivity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
            viewModel.updateTask(task);
            new Synchronizer(context).synchronizeFromRoom();
            ((DayActivity) context).runOnUiThread(() -> {
                disableProgressBar();
                ((DayActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
        });
    }

    @Override
    public void enableProgressBar() {
        if((context) instanceof MainActivity) {
            ((MainActivity) context).getProgressBar().setVisibility(View.VISIBLE);
            ((MainActivity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else if((context) instanceof LateTasksActivity){
            ((LateTasksActivity) context).getProgressBar().setVisibility(View.VISIBLE);
            ((LateTasksActivity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            ((DayActivity) context).getProgressBar().setVisibility(View.VISIBLE);
            ((DayActivity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void disableProgressBar() {
        if((context) instanceof MainActivity) {
            ((MainActivity) context).getProgressBar().setVisibility(View.GONE);
            ((MainActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else if((context) instanceof LateTasksActivity){
            ((LateTasksActivity) context).getProgressBar().setVisibility(View.GONE);
            ((LateTasksActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }else{
            ((DayActivity) context).getProgressBar().setVisibility(View.GONE);
            ((DayActivity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
