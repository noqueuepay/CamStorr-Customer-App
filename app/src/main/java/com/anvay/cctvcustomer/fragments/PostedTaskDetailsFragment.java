package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.databinding.FragmentPostTaskDetailsBinding;
import com.anvay.cctvcustomer.models.Task;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TaskViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class PostedTaskDetailsFragment extends Fragment {
    private FragmentPostTaskDetailsBinding binding;
    private TextView typeDisplay, brandDisplay, wireLenDisplay, quantityDisplay, descriptionDisplay,
            hardDiskTypeDisplay,
            cameraTypeDisplay, dateDisplay;
    private Button displayBidsButton;
    private Task currentTask;
    private View loadingView;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        String title = "Task Details";
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack(title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostTaskDetailsBinding.inflate(inflater, container, false);
        getUI();
        TaskViewModel taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        currentTask = taskViewModel.getPostedTask().getValue();
        displayBidsButton.setOnClickListener(view -> launchFragment());
        updateUI(currentTask);
        return binding.getRoot();
    }

    private void getData(String taskId) {
        if (taskId == null)
            return;
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_TASKS_URL)
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentTask = documentSnapshot.toObject(Task.class);
                    if (currentTask != null)
                        updateUI(currentTask);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }

    private void updateUI(Task task) {
        typeDisplay.setText(task.getTypeOfService());
        brandDisplay.setText(task.getCameraBrand());
        dateDisplay.setText(task.getDate());
        descriptionDisplay.setText(task.getDescription());
        hardDiskTypeDisplay.setText(task.getHardDiskType());
        cameraTypeDisplay.setText(task.getCameraType());
        wireLenDisplay.setText(String.valueOf(task.getWireLength()));
        quantityDisplay.setText(String.valueOf(task.getNumberOfCameras()));
        loadingView.setVisibility(View.INVISIBLE);
    }

    private void launchFragment() {
        if (currentTask == null)
            return;
        FragmentManager fragmentManager = getParentFragmentManager();
        BidsFragment fragment = new BidsFragment();
        assert getParentFragment() != null;
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId()
                        , fragment)
                .addToBackStack("bids")
                .commit();
    }

    private void getUI() {
        typeDisplay = binding.typeDisplay;
        quantityDisplay = binding.quantityDisplay;
        descriptionDisplay = binding.descriptionDisplay;
        hardDiskTypeDisplay = binding.hardDiskDisplay;
        cameraTypeDisplay = binding.cameraTypeDisplay;
        brandDisplay = binding.brandDisplay;
        wireLenDisplay = binding.wireLenDisplay;
        dateDisplay = binding.postedDateDisplay;
        displayBidsButton = binding.displayBidsButton;
        loadingView = binding.loadingLayout.getRoot();
    }
}
