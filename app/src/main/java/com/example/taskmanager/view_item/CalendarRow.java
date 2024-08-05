package com.example.taskmanager.view_item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.taskmanager.R;

public class CalendarRow extends LinearLayout {

    public CalendarRow(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
    }

    public CalendarSingleDay getMonday() {
        return findViewById(R.id.c_square_1);
    }

    public CalendarSingleDay getTuesday() {
        return findViewById(R.id.c_square_2);
    }

    public CalendarSingleDay getWednesday() {
        return findViewById(R.id.c_square_3);
    }

    public CalendarSingleDay getThursday() {
        return findViewById(R.id.c_square_4);
    }

    public CalendarSingleDay getFriday() {
        return findViewById(R.id.c_square_5);
    }

    public CalendarSingleDay getSaturday() {
        return findViewById(R.id.c_square_6);
    }

    public CalendarSingleDay getSunday() {
        return findViewById(R.id.c_square_7);
    }
}
