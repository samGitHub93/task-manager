package com.example.taskmanager.view_item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class TimeTextView extends TextInputEditText{

    private final Context context;
    private static final String EMPTY_STRING = "";
    private MaterialTimePicker timePicker;

    public TimeTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        initTimePicker();
        setOnClickListener(timeTextViewAction());
    }

    private void initTimePicker(){
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build();
        timePicker.addOnPositiveButtonClickListener(selectTimeAction());
    }

    private View.OnClickListener timeTextViewAction(){
        return view -> {
            ((TextInputEditText) view).setText(EMPTY_STRING);
            timePicker.show(((AppCompatActivity)context).getSupportFragmentManager(), null);
        };
    }

    @SuppressLint("SetTextI18n")
    private View.OnClickListener selectTimeAction() {
        return v -> this.setText(timePicker.getHour() + ":" + timePicker.getMinute());
    }
}
