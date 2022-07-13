package com.example.taskmanager.util;

import com.example.taskmanager.enumerator.PriorityType;
import com.example.taskmanager.enumerator.RecurringType;

public class TaskUtil {

    public static PriorityType stringToPriorityType(String string){
        if(string.toUpperCase().equals(PriorityType.HIGH.toString())){
            return PriorityType.HIGH;
        }else if(string.toUpperCase().equals(PriorityType.MEDIUM.toString())){
            return PriorityType.MEDIUM;
        }else if(string.toUpperCase().equals(PriorityType.LOW.toString())){
            return PriorityType.LOW;
        }else return PriorityType.MEDIUM;
    }

    public static RecurringType stringToRecurringType(String string){
        if(string.toUpperCase().equals(RecurringType.DAILY.toString())){
            return RecurringType.DAILY;
        }else if(string.toUpperCase().equals(RecurringType.WEEKLY.toString())){
            return RecurringType.WEEKLY;
        }else if(string.toUpperCase().equals(RecurringType.MONTHLY.toString())){
            return RecurringType.MONTHLY;
        }else if(string.toUpperCase().equals(RecurringType.YEARLY.toString())){
            return RecurringType.YEARLY;
        }else return RecurringType.NONE;
    }
}
