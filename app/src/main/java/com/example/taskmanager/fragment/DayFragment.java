package com.example.taskmanager.fragment;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.UiActions;
import com.example.taskmanager.adapter.TaskAdapter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.swiper.TaskSwiper;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.StringUtil;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DayFragment extends Fragment implements UiActions {

    private TaskViewModel viewModel;
    private Observer<List<Task>> currentObserver;
    private TextView textDate;
    private Date date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textDate = requireActivity().findViewById(R.id.text_date);
    }

    @Override
    public void updateUI() {
        if(currentObserver != null) removeObserver(date, currentObserver);
        if(date == null) date = new Date();
        textDate.setText(StringUtil.capFirstCharacter(DateUtil.getFormatter().format(date)));
        currentObserver = getNewObserver();
        viewModel.getTasksByDate(date).observe(getViewLifecycleOwner(), currentObserver);
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

    public void setDate(Date date){
        this.date = date;
    }

    private void removeObserver(Date date, Observer<List<Task>> observer){
        viewModel.getTasksByDate(date).removeObserver(observer);
    }

    private Observer<List<Task>> getNewObserver(){
        return tasks -> createRecyclerView(TaskSorter.sortByPriority(tasks));
    }

    private void createRecyclerView(List<Task> tasks){
        TaskAdapter taskAdapter = new TaskAdapter(requireActivity(), viewModel, tasks);
        RecyclerView recyclerView = requireActivity().findViewById(R.id.recycler_day);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelperDone = new ItemTouchHelper(new TaskSwiper(getContext()));
        itemTouchHelperDone.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUMED DAY");
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
