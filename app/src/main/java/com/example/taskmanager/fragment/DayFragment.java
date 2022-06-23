package com.example.taskmanager.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.DayAdapter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.swiper.TaskDeleteSwiper;
import com.example.taskmanager.swiper.TaskDoneSwiper;
import com.example.taskmanager.util.DateUtil;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.MainViewModel;

import java.util.Date;
import java.util.List;

public class DayFragment extends Fragment {

    private MainViewModel viewModel;
    private List<Task> tasks;
    private TextView textDate;
    private Date date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if(date == null) date = new Date();
        tasks = TaskSorter.sortByPriority(viewModel.getTasksByDate(date));
        textDate = requireActivity().findViewById(R.id.text_date);
        textDate.setText(DateUtil.getFormatter().format(date));
        createRecyclerView();
    }

    public void setDate(Date date) {
        this.date = date;
        tasks = TaskSorter.sortByPriority(viewModel.getTasksByDate(date));
        textDate.setText(DateUtil.getFormatter().format(date));
        createRecyclerView();
    }

    private void createRecyclerView(){
        DayAdapter dayAdapter = new DayAdapter(viewModel, tasks);
        RecyclerView recyclerView = requireActivity().findViewById(R.id.recycler_day);
        recyclerView.setAdapter(dayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelperDelete = new ItemTouchHelper(new TaskDeleteSwiper(getContext()));
        itemTouchHelperDelete.attachToRecyclerView(recyclerView);
        ItemTouchHelper itemTouchHelperDone = new ItemTouchHelper(new TaskDoneSwiper(getContext()));
        itemTouchHelperDone.attachToRecyclerView(recyclerView);
    }
}
