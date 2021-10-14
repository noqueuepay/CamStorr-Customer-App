package com.anvay.cctvcustomer.fragments;

import android.annotation.SuppressLint;
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

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.databinding.FragmentOngoingTaskDetailsBinding;
import com.anvay.cctvcustomer.models.OngoingTask;
import com.anvay.cctvcustomer.models.Task;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TaskViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class OngoingTaskDetailsFragment extends Fragment {
    private FragmentOngoingTaskDetailsBinding binding;
    private OngoingTask ongoingTask;
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
        binding = FragmentOngoingTaskDetailsBinding.inflate(inflater, container, false);
        TaskViewModel taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        ongoingTask = taskViewModel.getOngoingTask();
        if (taskViewModel.isCompletedTask())
            binding.completedButton.setVisibility(View.INVISIBLE);
        initUI();
        loadingView = binding.loadingLayout.getRoot();
        binding.receiptButton.setOnClickListener(view -> launchFragment());
        binding.completedButton.setOnClickListener(view -> setCompleted());
        return binding.getRoot();
    }

    private void setCompleted() {
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_ONGOING_TASKS_URL)
                .document(ongoingTask.getOngoingTaskId())
                .update(Constants.KEY_IS_TASK_COMPLETED, true)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    int count = fragmentManager.getBackStackEntryCount();
                    while (count-- > 0)
                        fragmentManager.popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }

    private void launchFragment() {
        ReceiptFragment fragment = new ReceiptFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        assert getParentFragment() != null;
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId()
                        , fragment)
                .addToBackStack("customerDetails")
                .commit();
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        Task task = ongoingTask.getTask();
        binding.typeDisplay.setText(task.getTypeOfService());
        binding.postedDateDisplay.setText(task.getDate());
        binding.brandDisplay.setText(task.getCameraBrand());
        binding.cameraTypeDisplay.setText(task.getCameraType());
        binding.hardDiskDisplay.setText(task.getHardDiskType());
        binding.wireLenDisplay.setText(task.getWireLength() + " meters");
        binding.quantityDisplay.setText(String.valueOf(task.getNumberOfCameras()));
        binding.descriptionDisplay.setText(task.getDescription());
        binding.storeNameDisplay.setText(ongoingTask.getStoreName());
        binding.orderIdDisplay.setText(ongoingTask.getOrderId());
        binding.transactionNoDisplay.setText(ongoingTask.getTransactionId());
        binding.paymentDateDisplay.setText(ongoingTask.getPaymentDate());
        binding.taskDateDisplay.setText(ongoingTask.getDateOfTask());
        binding.addressDisplay.setText(ongoingTask.getCustomerAddress());
    }
}
