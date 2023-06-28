package com.example.taskmanager.view_item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskmanager.R;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.StringUtil;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTextView extends TextInputEditText {

    private final Context context;
    private static final String EMPTY_STRING = "";
    private MaterialDatePicker<Long> datePicker;

    public DateTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        initDatePicker();
        setOnClickListener(dateTextViewActionTaskDate());
    }

    private void initDatePicker(){
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.MaterialCalendarTheme)
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();
        datePicker.addOnPositiveButtonClickListener(selectDateAction());
    }

    private View.OnClickListener dateTextViewActionTaskDate(){
        return view -> {
            ((TextInputEditText) view).setText(EMPTY_STRING);
            datePicker.show(((AppCompatActivity)context).getSupportFragmentManager(),null);
        };
    }

    private MaterialPickerOnPositiveButtonClickListener<Long> selectDateAction() {
        return selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            calendar = DateUtil.getCalendarWithoutTime(calendar.getTime());
            Date date = calendar.getTime();
            this.setText(StringUtil.capFirstCharacter(DateUtil.getFormatter().format(date)));
        };
    }
}
