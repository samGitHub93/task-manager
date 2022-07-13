package com.example.taskmanager.swiper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.TaskAdapter;
import com.example.taskmanager.model.Task;

public class TaskSwiper extends ItemTouchHelper.SimpleCallback{

    private final Context context;
    private TaskAdapter taskAdapter;
    private Drawable icon;
    private ColorDrawable background;
    private Task task;

    public TaskSwiper(Context context) {
        super(0, ItemTouchHelper.LEFT);
        this.context = context;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        taskAdapter = (TaskAdapter) recyclerView.getAdapter();
        View itemView = viewHolder.itemView;
        assert taskAdapter != null;
        task = taskAdapter.getTask(viewHolder.getAdapterPosition());
        if(task.isDone()){
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_refresh_24);
            background = new ColorDrawable(Color.YELLOW);
        }else{
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_24);
            background = new ColorDrawable(Color.GREEN);
        }
        if (dX < 0) // Swiping to the left
            swipeLeftDraw(itemView, c, dX);
        else
            background.setBounds(0, 0, 0, 0);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if(task.isDone()){
            taskAdapter.undoneItem(position);
        }else{
            taskAdapter.doneItem(position);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    private void swipeLeftDraw(View itemView, Canvas c, float dX){
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
        int iconRight = itemView.getRight() - iconMargin;
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);
        icon.draw(c);
    }
}
