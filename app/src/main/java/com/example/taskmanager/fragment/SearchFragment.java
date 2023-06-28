package com.example.taskmanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

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
import com.example.taskmanager.adapter.TaskAdapter;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.swiper.TaskSwiper;
import com.example.taskmanager.util.TaskSorter;
import com.example.taskmanager.view_model.TaskViewModel;

import java.util.List;

public class SearchFragment extends Fragment implements AppFragment {

    private TaskViewModel viewModel;
    private Observer<List<Task>> currentObserver;
    private static final String INITIAL_VALUE = "######";
    private String query = INITIAL_VALUE;
    private static boolean isListModified = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = requireActivity().findViewById(R.id.search_view);
        searchView.setOnClickListener(clickAction());
        searchView.setOnQueryTextListener(searchAction());
        updateUI();
    }

    public void updateUI(){
        if(currentObserver!=null) removeObserver(query, currentObserver);
        if(query.length() == 0) query = INITIAL_VALUE;
        currentObserver = getNewObserver();
        viewModel.getTasksByTitleOrTextOrDate(query).observe(getViewLifecycleOwner(), currentObserver);
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
            public boolean onQueryTextSubmit(String typing) {
                query = typing;
                updateUI();
                hideKeyboard();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String typing) {
                query = typing;
                updateUI();
                return true;
            }
        };
    }

    private void createRecyclerView(List<Task> tasks){
        TaskAdapter taskAdapter = new TaskAdapter(getContext(), viewModel, tasks);
        RecyclerView recyclerView = requireActivity().findViewById(R.id.recycler_search);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelperDone = new ItemTouchHelper(new TaskSwiper(getContext()));
        itemTouchHelperDone.attachToRecyclerView(recyclerView);
    }

    private void hideKeyboard(){
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean isListModified(){
        return isListModified;
    }

    @Override
    public void setListModified(){
        isListModified = true;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        System.out.println("ATTACHED SEARCH");
        isListModified = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUMED SEARCH");
        ((MainActivity) requireActivity()).updateMenu();
    }

    @Override
    public void enableProgressBar() {
        ((MainActivity) requireActivity()).enableProgressBar();
    }

    @Override
    public void disableProgressBar() {
        ((MainActivity) requireActivity()).disableProgressBar();
    }

    @Override
    public void disableTouch() {
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void enableTouch() {
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
