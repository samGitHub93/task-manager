package com.example.taskmanager.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.adapter.SearchAdapter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.MainViewModel;

import java.util.List;

public class SearchFragment extends Fragment {

    private MainViewModel viewModel;
    private Observer<List<Task>> currentObserver;
    private static final String INITIAL_VALUE = "######";
    private String typing = INITIAL_VALUE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        SearchView searchView = requireActivity().findViewById(R.id.search_view);
        searchView.setOnClickListener(clickAction());
        searchView.setOnQueryTextListener(searchAction());
        updateUI(typing);
    }

    private void updateUI(String typing){
        if(currentObserver!=null) removeObserver(this.typing, currentObserver);
        if(typing.equals("")) typing = INITIAL_VALUE;
        else this.typing = typing;
        currentObserver = getNewObserver();
        viewModel.getTasksByTitleOrTextOrDate(typing).observe(getViewLifecycleOwner(), currentObserver);
    }

    private void removeObserver(String typing, Observer<List<Task>> observer){
        viewModel.getTasksByTitleOrTextOrDate(typing).removeObserver(observer);
    }

    private Observer<List<Task>> getNewObserver(){
        return tasks -> createRecyclerView(TaskSorter.sortByPriority(tasks));
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
                updateUI(query);
                hideKeyboard();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                updateUI(query);
                return true;
            }
        };
    }

    private void createRecyclerView(List<Task> tasks){
        SearchAdapter searchAdapter = new SearchAdapter(tasks);
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
