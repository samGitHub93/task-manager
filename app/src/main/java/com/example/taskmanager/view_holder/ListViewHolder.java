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
    private final TextView textRec;
    private final TextView textAlarm;
    private final ImageView notify;

    public ListViewHolder(@NonNull View view) {
        super(view);
        this.icon = view.findViewById(R.id.list_item_icon);
        this.textView1 = view.findViewById(R.id.list_item_text_1);
        this.textView2 = view.findViewById(R.id.list_item_text_2);
        this.textView3 = view.findViewById(R.id.list_item_text_3);
        this.textRec = view.findViewById(R.id.list_item_text_rec);
        this.textAlarm = view.findViewById(R.id.list_item_text_alarm);
        this.notify = view.findViewById(R.id.alarm);
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

    public ImageView getNotify() {
        return notify;
    }

    public TextView getTextRec() {
        return textRec;
    }

    public TextView getTextAlarm() {
        return textAlarm;
    }
}
