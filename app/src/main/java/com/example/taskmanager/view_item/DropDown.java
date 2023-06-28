package com.example.taskmanager.view_item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import androidx.annotation.IdRes;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.example.taskmanager.ProcessTaskAction;
import com.example.taskmanager.ProcessTaskActivity;

public class DropDown extends AppCompatAutoCompleteTextView {

    private final Context context;

    public DropDown(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void addItemsToDropdown(String[] itemsOrder, int itemStartPosition){
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, itemsOrder);
        this.setAdapter(adapterOrder);
        this.setText(this.getAdapter().getItem(itemStartPosition).toString(), false);
    }
}
