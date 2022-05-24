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
import com.example.taskmanager.list_view.ListViewAdapter;
import com.google.android.material.snackbar.Snackbar;

public class TaskDeleteSwiper extends ItemTouchHelper.SimpleCallback{

    private final Drawable icon;
    private final ColorDrawable background;
    private final ListViewAdapter mAdapter;

    public TaskDeleteSwiper(Context context, ListViewAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        mAdapter = adapter;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24);
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        if (dX < 0) // Swiping to the left
            swipeLeftDraw(itemView, c, dX);
        else
            background.setBounds(0, 0, 0, 0);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        addUndoSnackBar(viewHolder, position);
        mAdapter.deleteItem(position);
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

    private void addUndoSnackBar(RecyclerView.ViewHolder viewHolder, int position){
        Snackbar snackbar = Snackbar.make(viewHolder.itemView ,"Item was removed from the list.", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", view -> mAdapter.restoreItem(position));
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}
