package com.example.taskmanager.view_item;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.taskmanager.R;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.StringUtil;

import java.util.Date;

public class CalendarSingleDay extends LinearLayout {

    public CalendarSingleDay(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
    }

    public TextView getCalendarDayNumber(){
        return findViewById(R.id.calendar_day_number);
    }

    public TextView getCalendarCircle() {
        return findViewById(R.id.calendar_circle);
    }

    public Date getDate(Date date) {
        Date newDate = null;
        try {
            String monthYear = StringUtil.capFirstCharacter(DateUtil.getSimpleFormatter().format(date).substring(3));
            String dayNumber = getCalendarDayNumber().getText().toString();
            if (dayNumber.length() == 1) {
                dayNumber = "0" + dayNumber;
            }
            String newDateString = dayNumber + " " + monthYear;
            newDate = DateUtil.getSimpleFormatter().parse(newDateString);
        }catch(Exception e){
            Log.e(CalendarSingleDay.class.getName(), e.getMessage(), e);
            return newDate;
        }
        return newDate;
    }
}
