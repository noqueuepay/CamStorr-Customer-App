package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.databinding.FragmentDashboardBinding;
import com.anvay.cctvcustomer.utils.TaskViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;

import org.jetbrains.annotations.NotNull;

public class DashboardFragment extends Fragment {
    private FragmentManager fragmentManager;
    private FragmentDashboardBinding binding;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.setTitleStack("Dashboard");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        fragmentManager = getChildFragmentManager();
        binding.postedTasks.setOnClickListener(view -> setFragment(new PostedTasksFragment()));
        binding.ongoingTasks.setOnClickListener(view -> setFragment(new OngoingTasksFragment(), false));
        binding.completedTasks.setOnClickListener(view -> setFragment(new OngoingTasksFragment(), true));
        binding.orders.setOnClickListener(view -> setFragment(new OrdersFragment()));
        return binding.getRoot();
    }

    private void setFragment(Fragment fragment, boolean isCompleted) {
        TaskViewModel taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        taskViewModel.setCompletedTask(isCompleted);
        fragmentManager.beginTransaction()
                .add(binding.fragmentContainer.getId(), fragment)
                .addToBackStack("fragment")
                .commit();
    }

    private void setFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .add(binding.fragmentContainer.getId(), fragment)
                .addToBackStack("fragment")
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}