package com.example.taskmanager.swiper;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.ModifyTaskActivity;
import com.example.taskmanager.adapter.TaskAdapter;
import com.example.taskmanager.model.Task;

public class TaskModifier implements View.OnClickListener {

    private final Context context;

    public TaskModifier(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        RecyclerView recyclerView = (RecyclerView) view.getParent();
        TaskAdapter taskAdapter = (TaskAdapter) recyclerView.getAdapter();
        assert taskAdapter != null;
        Task task = taskAdapter.getTask(recyclerView.getChildAdapterPosition(view));
        long id = task.getId();
        Intent modifyTaskActivity = new Intent(context, ModifyTaskActivity.class);
        modifyTaskActivity.putExtra("id", id);
        System.out.println(id);
        context.startActivity(modifyTaskActivity);
    }
}
