package com.example.taskmanager.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.PeriodsAdapter;
import com.example.taskmanager.enumerator.OrderType;
import com.example.taskmanager.enumerator.PeriodType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.swiper.TaskDeleteSwiper;
import com.example.taskmanager.swiper.TaskDoneSwiper;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class PeriodsFragment extends Fragment {

    private MainViewModel viewModel;
    private List<Task> tasks;
    private PeriodType periodType;
    private OrderType orderType;

    public PeriodsFragment(){
        periodType = PeriodType._3_DAY;
        orderType = OrderType.DATE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_periods, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        tasks = new ArrayList<>(viewModel.getTasksByPeriod(periodType));
        createDropdowns();
        createRecyclerView();
    }

    private void createDropdowns(){
        AutoCompleteTextView dropdownUntil = requireActivity().findViewById(R.id.auto_complete_text_view_1);
        AutoCompleteTextView dropdownOrder = requireActivity().findViewById(R.id.auto_complete_text_view_2);
        dropdownUntil.setOnItemClickListener(dropdownUntilAction());
        dropdownOrder.setOnItemClickListener(dropdownOrderAction());
        addItemsToDropdowns(dropdownUntil, dropdownOrder);
    }

    private void createRecyclerView(){
        if(orderType == OrderType.PRIORITY) tasks = TaskSorter.sortByPriority(tasks);
        else if(orderType == OrderType.DATE) tasks = TaskSorter.sortByDate(tasks);
        PeriodsAdapter periodsAdapter = new PeriodsAdapter(viewModel, tasks);
        RecyclerView recyclerView = requireActivity().findViewById(R.id.recycler_periods);
        recyclerView.setAdapter(periodsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelperDelete = new ItemTouchHelper(new TaskDeleteSwiper(getContext()));
        itemTouchHelperDelete.attachToRecyclerView(recyclerView);
        ItemTouchHelper itemTouchHelperDone = new ItemTouchHelper(new TaskDoneSwiper(getContext()));
        itemTouchHelperDone.attachToRecyclerView(recyclerView);
    }

    private void addItemsToDropdowns(AutoCompleteTextView dropdownUntil, AutoCompleteTextView dropdownOrder){
        String[] itemsUntil = new String[] { get(R.string._3_days), get(R.string._1_week), get(R.string._2_weeks), get(R.string._1_month), get(R.string._3_months), get(R.string._6_months)};
        ArrayAdapter<String> adapterUntil = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, itemsUntil);
        dropdownUntil.setAdapter(adapterUntil);
        dropdownUntil.setText(dropdownUntil.getAdapter().getItem(0).toString(), false);
        String[] itemsOrder = new String[] { get(R.string.date_order), get(R.string.priority_order)};
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, itemsOrder);
        dropdownOrder.setAdapter(adapterOrder);
        dropdownOrder.setText(dropdownOrder.getAdapter().getItem(0).toString(), false);
    }

    private AdapterView.OnItemClickListener dropdownUntilAction() {
        return (parent, view, position, id) -> {
                if (id == 0) {
                    tasks = viewModel.getTasksByPeriod(PeriodType._3_DAY);
                    periodType = PeriodType._3_DAY;
                } else if (id == 1) {
                    tasks = viewModel.getTasksByPeriod(PeriodType._1_WEEK);
                    periodType = PeriodType._1_WEEK;
                } else if (id == 2) {
                    tasks = viewModel.getTasksByPeriod(PeriodType._2_WEEKS);
                    periodType = PeriodType._2_WEEKS;
                } else if (id == 3) {
                    tasks = viewModel.getTasksByPeriod(PeriodType._1_MONTH);
                    periodType = PeriodType._1_MONTH;
                } else if (id == 4) {
                    tasks = viewModel.getTasksByPeriod(PeriodType._3_MONTHS);
                    periodType = PeriodType._3_MONTHS;
                } else if (id == 5) {
                    tasks = viewModel.getTasksByPeriod(PeriodType._6_MONTHS);
                    periodType = PeriodType._6_MONTHS;
                }
            createRecyclerView();
        };
    }

    private AdapterView.OnItemClickListener dropdownOrderAction(){
        return (parent, view, position, id) -> {
            if (id == 0) {
                tasks = TaskSorter.sortByDate(tasks);
                orderType = OrderType.DATE;
            } else if (id == 1) {
                tasks = TaskSorter.sortByPriority(tasks);
                orderType = OrderType.PRIORITY;
            }
            createRecyclerView();
        };
    }

    private String get(@StringRes int id){
        return getResources().getString(id);
    }
}
