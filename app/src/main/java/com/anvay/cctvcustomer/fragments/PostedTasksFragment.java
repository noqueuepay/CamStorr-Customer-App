package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.MainActivity;
import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.adapters.PostedTasksAdapter;
import com.anvay.cctvcustomer.databinding.FragmentPostedTasksBinding;
import com.anvay.cctvcustomer.models.Task;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TaskViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PostedTasksFragment extends Fragment implements PostedTasksAdapter.PostedTaskClickListener {
    private List<Task> postedTasks;
    private PostedTasksAdapter adapter;
    private View loadingLayout;
    private TaskViewModel taskViewModel;
    private FragmentPostedTasksBinding binding;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        String title = "Posted Tasks";
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack(title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostedTasksBinding.inflate(inflater, container, false);
        RecyclerView postedTasksRecycler = binding.postedTasksRecycler;
        loadingLayout = binding.loadingLayout.getRoot();
        postedTasks = new ArrayList<>();
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        postedTasksRecycler.setLayoutManager(linearLayoutManager);
        adapter = new PostedTasksAdapter(postedTasks, this);
        postedTasksRecycler.setAdapter(adapter);
        loadingLayout.setVisibility(View.VISIBLE);
        getData();
        return binding.getRoot();
    }

    private void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_TASKS_URL)
                .whereEqualTo(Constants.KEY_CUSTOMER_ID, MainActivity.userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Task task = snapshot.toObject(Task.class);
                        assert task != null;
                        task.setTaskId(snapshot.getId());
                        postedTasks.add(task);
                    }
                    adapter.notifyItemInserted(0);
                    checkForEmptyRecycler();
                    loadingLayout.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    loadingLayout.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkForEmptyRecycler() {
        if (postedTasks.isEmpty())
            binding.emptyRecyclerText.setVisibility(View.VISIBLE);
        else binding.emptyRecyclerText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTaskItemClicked(int position) {
        FragmentManager fragmentManager = getParentFragmentManager();
        PostedTaskDetailsFragment fragment = new PostedTaskDetailsFragment();
        taskViewModel.setPostedTask(postedTasks.get(position));
        assert getParentFragment() != null;
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId(),
                        fragment)
                .addToBackStack("taskDetails")
                .commit();
    }
}
