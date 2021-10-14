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

import com.anvay.cctvcustomer.databinding.FragmentHomeBinding;
import com.anvay.cctvcustomer.models.SliderImages;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.setTitleStack("Home");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.serviceInstall.setOnClickListener(view -> launchFragment(Constants.SERVICE_INSTALL));
        binding.serviceRepair.setOnClickListener(view -> launchFragment(Constants.SERVICE_REPAIR));
        binding.buyCamera.setOnClickListener(view -> launchProductFragment(Constants.KEY_PRODUCT_CAMERA));
        binding.buyAccessory.setOnClickListener(view -> launchProductFragment(Constants.KEY_PRODUCT_ACCESSORY));
        setImageSlider();
        return binding.getRoot();
    }

    private void launchFragment(String typeOfService) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_TYPE_OF_SERVICE, typeOfService);
        FragmentManager fragmentManager = getChildFragmentManager();
        TaskFragment taskFragment = new TaskFragment();
        taskFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .add(binding.fragmentContainer.getId(), taskFragment)
                .addToBackStack("fragment")
                .commit();
    }

    private void launchProductFragment(String category) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_CATEGORY, category);
        FragmentManager fragmentManager = getChildFragmentManager();
        ProductsFragment fragment = new ProductsFragment();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .add(binding.fragmentContainer.getId(), fragment)
                .addToBackStack("fragment")
                .commit();
    }

    private void setImageSlider() {
        List<SlideModel> imageList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings
                .Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        db.collection(Constants.BASE_SLIDER_IMAGES_URL)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        SliderImages sliderImages = (documentSnapshot.toObject(SliderImages.class));
                        if (sliderImages != null) {
                            List<SlideModel> images = sliderImages.getImages();
                            if (images != null)
                                imageList.addAll(sliderImages.getImages());
                        }
                    }
                    binding.imageSlider.setImageList(imageList);
                });
    }
}