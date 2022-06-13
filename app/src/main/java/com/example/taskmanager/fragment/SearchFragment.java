package com.example.taskmanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.SearchAdapter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private List<Task> tasks;
    private List<Task> searchedTasks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        TaskViewModel viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        searchedTasks = new ArrayList<>();
        tasks = TaskSorter.sortByDate(viewModel.getAllTasks()); // PROVVISORIO
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = requireActivity().findViewById(R.id.search_view);
        searchView.setOnClickListener(clickAction());
        searchView.setOnQueryTextListener(searchAction());
    }

    private SearchView.OnClickListener clickAction(){
        return view -> {
            if(view.getId() == R.id.search_view){
                ((SearchView) view).setIconified(false);
            }
        };
    }

    private SearchView.OnQueryTextListener searchAction(){
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchedTasks(query);
                createRecyclerView();
                hideKeyboard();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        };
    }

    private void getSearchedTasks(String s){
        searchedTasks.clear();
        for(Task task : tasks){
            if(task.getText().toLowerCase().contains(s.toLowerCase()) || task.getTitle().toLowerCase().contains(s.toLowerCase()) || task.getDate().contains(s)){
                searchedTasks.add(task);
            }
        }
    }

    private void createRecyclerView(){
        SearchAdapter searchAdapter = new SearchAdapter(searchedTasks);
        RecyclerView recyclerView = requireActivity().findViewById(R.id.recycler_search);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void hideKeyboard(){
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
