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

import java.util.List;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private final List<Task> tasks;

    public ListViewAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_view_line, viewGroup, false);
        return new ListViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder viewHolder, final int position) {
        switchPriority(viewHolder, tasks.get(position).getPriorityType());
        viewHolder.getTextView1().setText(tasks.get(position).getTitle());
        viewHolder.getTextView2().setText(tasks.get(position).getText());
        viewHolder.getTextView3().setText(tasks.get(position).getDate());
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

    public void deleteItem(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    public void doneItem(int position){
        tasks.get(position).setDone(true);
        notifyItemChanged(position);
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
