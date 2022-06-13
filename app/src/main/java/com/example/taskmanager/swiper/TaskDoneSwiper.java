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
import com.example.taskmanager.adapter.Adapter;
import com.example.taskmanager.model.Task;
import com.google.android.material.snackbar.Snackbar;

public class TaskDoneSwiper extends ItemTouchHelper.SimpleCallback{

    private final Context context;
    private Adapter adapter;
    private Drawable icon;
    private ColorDrawable background;
    private Task task;

    public TaskDoneSwiper(Context context) {
        super(0, ItemTouchHelper.RIGHT);
        this.context = context;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        adapter = (Adapter) recyclerView.getAdapter();
        View itemView = viewHolder.itemView;
        assert adapter != null;
        task = adapter.getTask(viewHolder.getAdapterPosition());
        if(task.isDone()){
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_refresh_24);
            background = new ColorDrawable(Color.YELLOW);
        }else{
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_24);
            background = new ColorDrawable(Color.GREEN);
        }
        if (dX > 0) // Swiping to the right
            swipeRightDraw(itemView, c, dX);
        else
            background.setBounds(0, 0, 0, 0);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if(task.isDone()){
            adapter.undoneItem(position);
        }else{
            addUndoSnackBar(viewHolder, position);
            adapter.doneItem(position);
        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    private void swipeRightDraw(View itemView, Canvas c, float dX){
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        int iconLeft = itemView.getLeft() + iconMargin - icon.getIntrinsicWidth();
        int iconRight = itemView.getLeft() + iconMargin;
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
        background.draw(c);
        icon.draw(c);
    }

    private void addUndoSnackBar(RecyclerView.ViewHolder viewHolder, int position){
        Snackbar snackbar = Snackbar.make(viewHolder.itemView ,"Task done.", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", view -> adapter.undoneItem(position));
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}
