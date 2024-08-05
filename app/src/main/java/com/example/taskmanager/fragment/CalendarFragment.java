package com.example.taskmanager.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.taskmanager.DayActivity;
import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.UiActions;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.StringUtil;
import com.example.taskmanager.view_item.CalendarRow;
import com.example.taskmanager.view_item.CalendarSingleDay;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CalendarFragment extends Fragment implements UiActions {

    private TaskViewModel viewModel;
    private Date theDate;
    private Date today;
    private Observer<List<Task>> currentObserver;
    private TextView arrowLeft;
    private TextView arrowRight;
    private List<CalendarRow> calendarRows;
    private TextView textMonth;
    private CalendarRow calendarRow1;
    private CalendarRow calendarRow2;
    private CalendarRow calendarRow3;
    private CalendarRow calendarRow4;
    private CalendarRow calendarRow5;
    private CalendarRow calendarRow6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void updateUI(){
        if(theDate == null){
            theDate = DateUtil.getTodayWithoutTime();
        }
        today = DateUtil.getTodayWithoutTime();
        if(currentObserver != null) removeObserver(theDate, currentObserver);
        currentObserver = getNewObserver(viewModel.getTasksByMonth(theDate).getValue());
        viewModel.getTasksByMonth(theDate).observe(getViewLifecycleOwner(), currentObserver);
    }

    private void removeObserver(Date date, Observer<List<Task>> observer){
        viewModel.getTasksByMonth(date).removeObserver(observer);
    }

    private Observer<List<Task>> getNewObserver(List<Task> tasks){
        return v -> createCalendar(tasks);
    }

    private void createCalendar(List<Task> tasks){
        bindElements();
        initElements();
        setElementsActions();
        List<Date> dates = tasks.stream().map(task -> DateUtil.getDateFromString(task.getDate())).collect(Collectors.toList());
        int positionOfFirstDay = DateUtil.getPositionOfFirstDay(theDate);
        int lastDayOfMonth = DateUtil.getLastDayOfMonth(theDate);
        int j=-positionOfFirstDay;
        int k=0;
        for (CalendarRow cr : calendarRows) {
            int i=0;
            for(; i<cr.getChildCount(); i++){
                CalendarSingleDay calendarSingleDay = (CalendarSingleDay) cr.getChildAt(i);
                calendarSingleDay.setBackgroundColor(getResources().getColor(R.color.gray_very_huge));
                calendarSingleDay.getCalendarCircle().setVisibility(View.INVISIBLE);
                if(j < 0){
                    calendarSingleDay.getCalendarDayNumber().setVisibility(View.INVISIBLE);
                }else if(j >= lastDayOfMonth) {
                    calendarSingleDay.getCalendarDayNumber().setVisibility(View.VISIBLE);
                    calendarSingleDay.getCalendarDayNumber().setText(String.valueOf(k+1));
                    calendarSingleDay.getCalendarDayNumber().setTextColor(getResources().getColor(R.color.gray));
                    k++;
                }else{
                    calendarSingleDay.getCalendarDayNumber().setVisibility(View.VISIBLE);
                    calendarSingleDay.getCalendarDayNumber().setText(String.valueOf(j+1));
                    calendarSingleDay.getCalendarDayNumber().setTextColor(getResources().getColor(R.color.white));
                    Date day = calendarSingleDay.getDate(theDate);
                    calendarSingleDay.setOnClickListener(openDayFragment(day));
                    dates.forEach(d -> {
                        if(day != null && day.compareTo(d) == 0){
                            calendarSingleDay.getCalendarCircle().setVisibility(View.VISIBLE);
                        }
                        if(day != null && day.compareTo(today) == 0){
                            calendarSingleDay.setBackgroundColor(getResources().getColor(R.color.green_huge));
                        }
                    });
                }
                j++;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUMED CALENDAR");
        requireActivity().runOnUiThread(() -> {
            updateMenu();
            updateUI();
        });
    }

    @Override
    public void updateMenu() {
        if (((MainActivity)requireActivity()).getMenu() != null) {
            ((MainActivity)requireActivity()).setMenuItem(((MainActivity)requireActivity()).getMenu().getItem(0));
            if (areThereLateTasks()) {
                ((MainActivity)requireActivity()).getMenuItem().setIcon(R.drawable.ic_baseline_warning_red_24);
            } else
                ((MainActivity)requireActivity()).getMenuItem().setIcon(R.drawable.ic_baseline_warning_24);
        }
    }

    @Override
    public void enableProgressBar() {
        ((MainActivity) requireActivity()).getProgressBar().setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void disableProgressBar() {
        ((MainActivity) requireActivity()).getProgressBar().setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public boolean areThereLateTasks(){
        TaskViewModel viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        return !Objects.requireNonNull(viewModel.getGetLateTasks().getValue()).isEmpty();
    }

    private View.OnClickListener getMonthBefore(){
        return v -> {
            theDate = DateUtil.getDatePlusMonths(theDate, -1);
            requireActivity().runOnUiThread(() -> {
                updateMenu();
                updateUI();
            });
        };
    }

    private View.OnClickListener getMonthAfter(){
        return v -> {
            theDate = DateUtil.getDatePlusMonths(theDate, 1);
            requireActivity().runOnUiThread(() -> {
                updateMenu();
                updateUI();
            });
        };
    }

    private void bindElements(){
        arrowLeft = requireActivity().findViewById(R.id.text_arrow_left);
        arrowRight = requireActivity().findViewById(R.id.text_arrow_right);
        textMonth = requireActivity().findViewById(R.id.text_month);
        calendarRow1 = requireActivity().findViewById(R.id.c_row_1);
        calendarRow2 = requireActivity().findViewById(R.id.c_row_2);
        calendarRow3 = requireActivity().findViewById(R.id.c_row_3);
        calendarRow4 = requireActivity().findViewById(R.id.c_row_4);
        calendarRow5 = requireActivity().findViewById(R.id.c_row_5);
        calendarRow6 = requireActivity().findViewById(R.id.c_row_6);
    }

    private void initElements(){
        String monthYear = StringUtil.capFirstCharacter(DateUtil.getSimpleFormatter().format(theDate).substring(3));
        textMonth.setText(monthYear);
        calendarRows = new ArrayList<>();
        calendarRows.add(calendarRow1);
        calendarRows.add(calendarRow2);
        calendarRows.add(calendarRow3);
        calendarRows.add(calendarRow4);
        calendarRows.add(calendarRow5);
        calendarRows.add(calendarRow6);
    }

    private void setElementsActions(){
        arrowLeft.setOnClickListener(getMonthBefore());
        arrowRight.setOnClickListener(getMonthAfter());
    }

    private View.OnClickListener openDayFragment(Date date){
        return (v) -> {
            Intent intent = new Intent(requireActivity(), DayActivity.class);
            intent.putExtra("day", DateUtil.getSimpleFormatter().format(date));
            startActivity(intent);
        };
    }
}
