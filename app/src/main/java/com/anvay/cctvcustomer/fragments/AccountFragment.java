package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.databinding.FragmentAccountBinding;
import com.anvay.cctvcustomer.utils.TitleViewModel;

import org.jetbrains.annotations.NotNull;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private FragmentManager fragmentManager;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.setTitleStack("Account");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        Button profileButton = binding.profileButton;
        Button customerCareButton = binding.customerCareButton;
        Button tacButton = binding.tacButton;
        fragmentManager = getChildFragmentManager();
        profileButton.setOnClickListener(view -> setFragment(new ProfileFragment()));
        customerCareButton.setOnClickListener(view -> setFragment(new CustomerCareFragment()));
        tacButton.setOnClickListener(view -> setFragment(new TermsFragment()));
        return binding.getRoot();
    }

    private void setFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .add(binding.accountFragmentHost.getId(), fragment)
                .addToBackStack("fragment")
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}