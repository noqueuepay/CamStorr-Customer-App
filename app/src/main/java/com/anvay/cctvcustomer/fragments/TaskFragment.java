package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.databinding.FragmentTaskBinding;
import com.anvay.cctvcustomer.models.CameraBrand;
import com.anvay.cctvcustomer.models.Task;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends Fragment {
    private FragmentTaskBinding binding;
    private Spinner hardDiskSpinner, brandSpinner, cameraTypeSpinner;
    private EditText quantityDisplay, descriptionDisplay, wireLenDisplay;
    private Button postRequestButton;
    private View loadingView;
    private List<CameraBrand> cameraBrands;
    private ArrayAdapter<String> brandAdapter, typesAdapter, hddAdapter;
    private List<String> brandNames, typeNames, hddNames;
    private String selectedBrand, selectedType, selectedHdd, description, customerName, zipcode, typeOfService,
            customerId;
    private int quantity, wireLength;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        String title = "Post a new Task";
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack(title);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        initUI();
        cameraBrands = new ArrayList<>();
        brandNames = new ArrayList<>();
        typeNames = new ArrayList<>();
        hddNames = new ArrayList<>();
        brandAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, brandNames);
        typesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, typeNames);
        hddAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, hddNames);
        brandSpinner.setAdapter(brandAdapter);
        cameraTypeSpinner.setAdapter(typesAdapter);
        hardDiskSpinner.setAdapter(hddAdapter);
        getInitialData();
        assert getArguments() != null;
        typeOfService = getArguments().getString(Constants.KEY_TYPE_OF_SERVICE);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        customerName = sharedPreferences.getString(Constants.USER_NAME, "user");
        customerId = sharedPreferences.getString(Constants.USER_FIREBASE_ID, "");
        zipcode = sharedPreferences.getString(Constants.USER_ZIPCODE, "000");
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBrand = brandAdapter.getItem(i);
                setUpSpinners(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setUpSpinners(0);
            }
        });
        hardDiskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedHdd = hddAdapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedHdd = hddAdapter.getItem(0);
            }
        });
        cameraTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedType = typesAdapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedType = typesAdapter.getItem(0);
            }
        });
        postRequestButton.setOnClickListener(view -> {
            description = descriptionDisplay.getText().toString();
            try {
                String quantityString = quantityDisplay.getText().toString();
                if (!quantityString.isEmpty())
                    quantity = Integer.parseInt(quantityString);
                String wireLen = wireLenDisplay.getText().toString();
                if (!wireLen.isEmpty())
                    wireLength = Integer.parseInt(wireLen);
                if (!description.isEmpty() || quantity > 0) postData();
                else
                    Toast.makeText(getContext(), "Fill all details", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Quantity and Wire Length should be numbers only", Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }

    private void postData() {
        loadingView.setVisibility(View.VISIBLE);
        Task task = new Task(customerId, customerName, typeOfService, selectedBrand, selectedType, selectedHdd,
                zipcode, description, quantity, wireLength);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_TASKS_URL)
                .document()
                .set(task)
                .addOnSuccessListener(unused -> {
                    loadingView.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Requirement Posted", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    loadingView.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Error posting details", Toast.LENGTH_SHORT).show();
                });
    }

    private void getInitialData() {
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_CAMERA_BRANDS_URL)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        CameraBrand cameraBrand = documentSnapshot.toObject(CameraBrand.class);
                        cameraBrands.add(cameraBrand);
                        assert cameraBrand != null;
                        brandNames.add(cameraBrand.getBrandName());
                    }
                    brandAdapter.notifyDataSetChanged();
                    setUpSpinners(0);
                    loadingView.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> loadingView.setVisibility(View.INVISIBLE));
    }

    private void setUpSpinners(int index) {
        typeNames.clear();
        hddNames.clear();
        typeNames.addAll(cameraBrands.get(index).getCameraTypes());
        hddNames.addAll(cameraBrands.get(index).getHardDiskTypes());
        typesAdapter.notifyDataSetChanged();
        hddAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initUI() {
        brandSpinner = binding.brandSpinner;
        cameraTypeSpinner = binding.cameraTypeSpinner;
        hardDiskSpinner = binding.hardDiskSpinner;
        quantityDisplay = binding.quantityDisplay;
        descriptionDisplay = binding.descriptionDisplay;
        wireLenDisplay = binding.wireLenDisplay;
        postRequestButton = binding.postRequestButton;
        loadingView = binding.loadingLayout.getRoot();
    }
}
