package com.example.taskmanager.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.view_holder.ListViewHolder;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<ListViewHolder>{

    private final List<Task> tasks;

    public SearchAdapter(List<Task> tasks) {
        this.tasks = tasks;
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
