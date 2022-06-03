package com.example.taskmanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.list_view.ListViewAdapter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.sorter.TaskSorter;
import com.example.taskmanager.swiper.TaskDeleteSwiper;
import com.example.taskmanager.swiper.TaskDoneSwiper;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.List;

public class PeriodsFragment extends Fragment {

    private List<Task> tasks;
    private RecyclerView recyclerView;
    private ListViewAdapter listViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View periodsView = inflater.inflate(R.layout.periods_fragment, container, false);
        TaskViewModel viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        tasks = TaskSorter.sortByDate(viewModel.getTasks());
        return periodsView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createRecyclerView();
        setUpRecyclerView();
    }

    private void createRecyclerView(){
        listViewAdapter = new ListViewAdapter(tasks);
        recyclerView = requireActivity().findViewById(R.id.recycler_periods);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listViewAdapter);
    }

    private void setUpRecyclerView() {
        recyclerView.setAdapter(listViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelperDelete = new ItemTouchHelper(new TaskDeleteSwiper(getContext(), listViewAdapter));
        itemTouchHelperDelete.attachToRecyclerView(recyclerView);
        ItemTouchHelper itemTouchHelperDone = new ItemTouchHelper(new TaskDoneSwiper(getContext(), listViewAdapter));
        itemTouchHelperDone.attachToRecyclerView(recyclerView);
    }
}
