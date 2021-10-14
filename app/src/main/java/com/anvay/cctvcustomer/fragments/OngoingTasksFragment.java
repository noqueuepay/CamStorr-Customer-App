package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.anvay.cctvcustomer.adapters.OngoingTasksAdapter;
import com.anvay.cctvcustomer.databinding.FragmentOngoingTasksBinding;
import com.anvay.cctvcustomer.models.OngoingTask;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TaskViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OngoingTasksFragment extends Fragment implements OngoingTasksAdapter.OngoingTasksItemClickListener {
    private List<OngoingTask> ongoingTaskList;
    private View loadingView;
    private OngoingTasksAdapter adapter;
    private RecyclerView ongoingTasksRecycler;
    private boolean isCompleted = false;
    private TaskViewModel taskViewModel;
    private TextView emptyRecyclerText;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        isCompleted = taskViewModel.isCompletedTask();
        String title = "Ongoing Tasks";
        if (isCompleted)
            title = "Completed Tasks";
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack(title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentOngoingTasksBinding binding = FragmentOngoingTasksBinding.inflate(inflater, container, false);
        ongoingTaskList = new ArrayList<>();
        emptyRecyclerText = binding.emptyRecyclerText;
        ongoingTasksRecycler = binding.ongoingTasksRecycler;
        loadingView = binding.loadingLayout.getRoot();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        ongoingTasksRecycler.setLayoutManager(linearLayoutManager);
        getData();
        return binding.getRoot();
    }

    private void getData() {
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_ONGOING_TASKS_URL)
                .whereEqualTo(Constants.KEY_CUSTOMER_ID, MainActivity.userId)
                .whereEqualTo(Constants.KEY_IS_TASK_COMPLETED, isCompleted)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        OngoingTask task = documentSnapshot.toObject(OngoingTask.class);
                        if (task != null) {
                            task.setOngoingTaskId(documentSnapshot.getId());
                            ongoingTaskList.add(task);
                        }
                    }
                    adapter = new OngoingTasksAdapter(ongoingTaskList, this);
                    ongoingTasksRecycler.setAdapter(adapter);
                    checkForEmptyRecycler();
                    loadingView.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Loading Error", Toast.LENGTH_SHORT).show();
                    Log.e("ongoingTasks", e.getMessage() + e.getCause());
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }

    private void checkForEmptyRecycler() {
        if (ongoingTaskList.isEmpty())
            emptyRecyclerText.setVisibility(View.VISIBLE);
        else emptyRecyclerText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClicked(int position) {
        OngoingTaskDetailsFragment fragment = new OngoingTaskDetailsFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        taskViewModel.setOngoingTask(ongoingTaskList.get(position));
        assert getParentFragment() != null;
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId(), fragment)
                .addToBackStack(null)
                .commit();
    }
}
