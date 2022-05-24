package com.example.taskmanager.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.list_view.ListViewAdapter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.sorter.TaskSorter;
import com.example.taskmanager.swiper.TaskDeleteSwiper;
import com.example.taskmanager.swiper.TaskDoneSwiper;

import java.util.List;

public class TaskFragment extends Fragment {

    private final List<Task> tasks;
    private RecyclerView recyclerView;
    private ListViewAdapter listViewAdapter;

    public TaskFragment(List<Task> tasks) {
        this.tasks = TaskSorter.sortTasks(tasks);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAdapter = new ListViewAdapter(tasks);
        recyclerView = requireActivity().findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listViewAdapter);
        setUpRecyclerView();
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
