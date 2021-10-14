package com.anvay.cctvcustomer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anvay.cctvcustomer.databinding.ActivityProfileBinding;
import com.anvay.cctvcustomer.models.User;
import com.anvay.cctvcustomer.utils.Constants;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private View loadingView;
    private EditText nameDisplay, addressDisplay, mobileNumberDisplay, cityDisplay, zipcodeDisplay;
    private String userId, name, address, mobileNumber, city, zipcode;
    private Button createProfile;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Create Profile");
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString(Constants.MOBILE_NUMBER, "");
        userId = sharedPreferences.getString(Constants.USER_FIREBASE_ID, "");
        mobileNumberDisplay.setText(mobileNumber);
        createProfile.setOnClickListener(view -> {
            if (checkFields())
                uploadData();
            else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                loadingView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void uploadData() {
        loadingView.setVisibility(View.VISIBLE);
        User user = new User(name, mobileNumber, zipcode, address, city);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_USERS_URL)
                .document(userId)
                .set(user)
                .addOnSuccessListener(unused -> saveData())
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USER_ADDRESS, address);
        editor.putString(Constants.USER_CITY, city);
        editor.putString(Constants.USER_ZIPCODE, zipcode);
        editor.putString(Constants.USER_NAME, name);
        editor.putBoolean(Constants.IS_PROFILE_DONE, true);
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean checkFields() {
        name = nameDisplay.getText().toString();
        address = addressDisplay.getText().toString();
        city = cityDisplay.getText().toString();
        zipcode = zipcodeDisplay.getText().toString();
        return !name.isEmpty() && !address.isEmpty() && !city.isEmpty() && !zipcode.isEmpty();
    }

    private void initUI() {
        loadingView = binding.loadingLayout.getRoot();
        nameDisplay = binding.nameDisplay;
        addressDisplay = binding.addressDisplay;
        mobileNumberDisplay = binding.mobileNumberDisplay;
        cityDisplay = binding.cityDisplay;
        zipcodeDisplay = binding.zipcodeDisplay;
        createProfile = binding.createProfile;
    }
}