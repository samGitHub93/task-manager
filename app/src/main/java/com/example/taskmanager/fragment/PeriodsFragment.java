package com.example.taskmanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.UiActions;
import com.example.taskmanager.adapter.TaskAdapter;
import com.example.taskmanager.enumerator.OrderType;
import com.example.taskmanager.enumerator.PeriodType;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.swiper.TaskSwiper;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.List;
import java.util.Objects;

public class PeriodsFragment extends Fragment implements UiActions {

    private TaskViewModel viewModel;
    private PeriodType periodType;
    private OrderType orderType;
    private Observer<List<Task>> currentObserver;

    public PeriodsFragment(){
        periodType = PeriodType._1_WEEK;
        orderType = OrderType.DATE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_periods, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createDropdowns();
    }

    @Override
    public void updateUI(){
        if(currentObserver != null) removeObserver(this.periodType, currentObserver);
        currentObserver = getNewObserver(orderType);
        viewModel.getTasksByPeriod(periodType).observe(getViewLifecycleOwner(), currentObserver);
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

    private void removeObserver(PeriodType periodType, Observer<List<Task>> observer){
        viewModel.getTasksByPeriod(periodType).removeObserver(observer);
    }

    private Observer<List<Task>> getNewObserver(OrderType orderType){
        if (orderType == OrderType.PRIORITY)
            return tasks -> createRecyclerView(TaskSorter.sortByPriority(tasks));
        return tasks -> createRecyclerView(TaskSorter.sortByDate(tasks));
    }

    private void createDropdowns(){
        AutoCompleteTextView dropdownUntil = requireActivity().findViewById(R.id.auto_complete_text_view_1);
        AutoCompleteTextView dropdownOrder = requireActivity().findViewById(R.id.auto_complete_text_view_2);
        dropdownUntil.setOnItemClickListener(dropdownUntilAction());
        dropdownOrder.setOnItemClickListener(dropdownOrderAction());
        addItemsToDropdowns(dropdownUntil, dropdownOrder);
    }

    private void createRecyclerView(List<Task> tasks){
        TaskAdapter taskAdapter = new TaskAdapter(requireActivity(), viewModel, tasks);
        RecyclerView recyclerView = requireActivity().findViewById(R.id.recycler_periods);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelperDone = new ItemTouchHelper(new TaskSwiper(requireActivity().getApplicationContext()));
        itemTouchHelperDone.attachToRecyclerView(recyclerView);
    }

    private void addItemsToDropdowns(AutoCompleteTextView dropdownUntil, AutoCompleteTextView dropdownOrder){
        String[] itemsUntil = new String[] {  get(R.string._1_day), get(R.string._3_days), get(R.string._1_week), get(R.string._2_weeks), get(R.string._1_month), get(R.string._3_months), get(R.string._6_months)};
        ArrayAdapter<String> adapterUntil = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, itemsUntil);
        dropdownUntil.setAdapter(adapterUntil);
        dropdownUntil.setText(get(R.string._1_week), false);
        dropdownUntil.setTextColor(getResources().getColor(R.color.white));
        String[] itemsOrder = new String[] { get(R.string.date_order), get(R.string.priority_order)};
        ArrayAdapter<String> adapterOrder = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, itemsOrder);
        dropdownOrder.setAdapter(adapterOrder);
        dropdownOrder.setText(get(R.string.date_order), false);
        dropdownOrder.setTextColor(getResources().getColor(R.color.white));
    }

    private AdapterView.OnItemClickListener dropdownUntilAction() {
        return (parent, view, position, id) -> {
            if (id == 0) {
                periodType = PeriodType._1_DAY;
            } else if (id == 1) {
                periodType = PeriodType._3_DAY;
            } else if (id == 2) {
                periodType = PeriodType._1_WEEK;
            } else if (id == 3) {
                periodType = PeriodType._2_WEEKS;
            } else if (id == 4) {
                periodType = PeriodType._1_MONTH;
            } else if (id == 5) {
                periodType = PeriodType._3_MONTHS;
            } else if (id == 6) {
                periodType = PeriodType._6_MONTHS;
            }
            updateUI();
        };
    }

    private AdapterView.OnItemClickListener dropdownOrderAction(){
        return (parent, view, position, id) -> {
            if (id == 0) {
                orderType = OrderType.DATE;
            } else if (id == 1) {
                orderType = OrderType.PRIORITY;
            }
            updateUI();
        };
    }

    private String get(@StringRes int id){
        return getResources().getString(id);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUMED PERIODS");
        requireActivity().runOnUiThread(() -> {
            updateMenu();
            updateUI();
        });
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
}
