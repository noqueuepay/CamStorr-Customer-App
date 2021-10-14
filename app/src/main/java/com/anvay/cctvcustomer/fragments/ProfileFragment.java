package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.databinding.FragmentProfileBinding;
import com.anvay.cctvcustomer.models.User;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private View loadingView;
    private String name, mobileNumber, city, zipcode, address;
    private Button editProfile, updateProfile;
    private SharedPreferences sharedPreferences;
    private EditText nameDisplay, mobileNumberDisplay, cityDisplay, zipcodeDisplay, addressDisplay;
    private FragmentProfileBinding binding;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack("Profile");
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        initUI();
        sharedPreferences = requireContext().getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        setupFields(false);
        name = sharedPreferences.getString(Constants.USER_NAME, "");
        mobileNumber = sharedPreferences.getString(Constants.MOBILE_NUMBER, "");
        city = sharedPreferences.getString(Constants.USER_CITY, "");
        zipcode = sharedPreferences.getString(Constants.USER_ZIPCODE, "");
        address = sharedPreferences.getString(Constants.USER_ADDRESS, "");
        nameDisplay.setText(name);
        mobileNumberDisplay.setText(mobileNumber);
        cityDisplay.setText(city);
        zipcodeDisplay.setText(zipcode);
        addressDisplay.setText(address);
        editProfile.setOnClickListener(view -> setupFields(true));
        updateProfile.setOnClickListener(view -> {
            name = nameDisplay.getText().toString();
            city = cityDisplay.getText().toString();
            zipcode = zipcodeDisplay.getText().toString();
            address = addressDisplay.getText().toString();
            if (name.isEmpty() || city.isEmpty() || zipcode.isEmpty() || address.isEmpty())
                Toast.makeText(getContext(), "Fill all details", Toast.LENGTH_SHORT).show();
            else
                updateFirebase();
        });
        return binding.getRoot();
    }

    private void setupFields(boolean editMode) {
        nameDisplay.setEnabled(editMode);
        cityDisplay.setEnabled(editMode);
        zipcodeDisplay.setEnabled(editMode);
        addressDisplay.setEnabled(editMode);
        if (editMode) {
            editProfile.setVisibility(View.INVISIBLE);
            updateProfile.setVisibility(View.VISIBLE);
        } else {
            editProfile.setVisibility(View.VISIBLE);
            updateProfile.setVisibility(View.INVISIBLE);
        }
    }

    private void initUI() {
        nameDisplay = binding.nameDisplay;
        mobileNumberDisplay = binding.mobileNumberDisplay;
        cityDisplay = binding.cityDisplay;
        zipcodeDisplay = binding.zipcodeDisplay;
        editProfile = binding.editProfile;
        addressDisplay = binding.addressDisplay;
        updateProfile = binding.updateProfile;
        loadingView = binding.loadingLayout.getRoot();
    }

    private void updateLocal(User user) {
        setupFields(false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USER_ZIPCODE, user.getZipcode());
        editor.putString(Constants.USER_NAME, user.getName());
        editor.putString(Constants.USER_CITY, user.getCity());
        editor.putString(Constants.USER_ADDRESS, user.getAddress());
        editor.apply();
        loadingView.setVisibility(View.INVISIBLE);
    }

    private void updateFirebase() {
        loadingView.setVisibility(View.VISIBLE);
        String firebaseId = sharedPreferences.getString(Constants.USER_FIREBASE_ID, "");
        User user = new User(name, mobileNumber, zipcode, address, city);
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.USER_NAME, name);
        map.put(Constants.USER_CITY, city);
        map.put(Constants.USER_ZIPCODE, zipcode);
        map.put(Constants.USER_ADDRESS, address);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_USERS_URL)
                .document(firebaseId)
                .update(map)
                .addOnSuccessListener(unused -> updateLocal(user))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }
}