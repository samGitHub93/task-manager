package com.example.taskmanager.view_holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;

public class ListViewHolder extends RecyclerView.ViewHolder {

    private final ImageView icon;
    private final TextView textView1;
    private final TextView textView2;
    private final TextView textView3;

    public ListViewHolder(@NonNull View view) {
        super(view);
        this.icon = view.findViewById(R.id.list_item_icon);
        this.textView1 = view.findViewById(R.id.list_item_text_1);
        this.textView2 = view.findViewById(R.id.list_item_text_2);
        this.textView3 = view.findViewById(R.id.list_item_text_3);
    }

    public ImageView getIcon() {
        return icon;
    }

    public TextView getTextView1() {
        return textView1;
    }

    public TextView getTextView2() {
        return textView2;
    }

    public TextView getTextView3() {
        return textView3;
    }
}
