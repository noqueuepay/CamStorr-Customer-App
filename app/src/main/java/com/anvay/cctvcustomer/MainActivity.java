package com.anvay.cctvcustomer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.anvay.cctvcustomer.databinding.ActivityMainBinding;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static String userId, userAddress, userName, mobileNumber;
    private BottomNavigationView navView;
    private TitleViewModel titleViewModel;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        titleViewModel.popTitleStack();
        if (navView.getSelectedItemId() == R.id.navigation_cart && titleViewModel.isCartPurchased())
            navView.setSelectedItemId(R.id.navigation_home);
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navView = findViewById(R.id.nav_view);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        userId = sharedPreferences.getString(Constants.USER_FIREBASE_ID, "");
        userAddress = sharedPreferences.getString(Constants.USER_ADDRESS, "");
        userName = sharedPreferences.getString(Constants.USER_NAME, "");
        mobileNumber = sharedPreferences.getString(Constants.MOBILE_NUMBER,"");
        if (!sharedPreferences.getBoolean(Constants.IS_LOGGED_IN, false)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else if (!sharedPreferences.getBoolean(Constants.IS_PROFILE_DONE, false)) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
            finish();
        }
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder()
                .build();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        titleViewModel = new ViewModelProvider(this).get(TitleViewModel.class);
        titleViewModel.getStackSize().observe(this, integer -> setActionBarTitle(titleViewModel.getTopTitle()));
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_cart)
                navView.setVisibility(View.INVISIBLE);
            else navView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}