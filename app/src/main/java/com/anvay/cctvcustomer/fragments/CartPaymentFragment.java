package com.anvay.cctvcustomer.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.MainActivity;
import com.anvay.cctvcustomer.PaymentActivity;
import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.databinding.FragmentCartPaymentBinding;
import com.anvay.cctvcustomer.models.CartItem;
import com.anvay.cctvcustomer.models.OrderItem;
import com.anvay.cctvcustomer.utils.AppDatabase;
import com.anvay.cctvcustomer.utils.CartViewModel;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CartPaymentFragment extends Fragment {
    private final List<OrderItem> orderItemList = new ArrayList<>();
    private String address;
    private View loadingView;
    private double amountToPay;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        String title = "Payment";
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack(title);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentCartPaymentBinding binding = FragmentCartPaymentBinding.inflate(inflater, container, false);
        EditText addressDisplay = binding.addressDisplay;
        CheckBox copyProfileAddress = binding.copyProfileAddress;
        Checkout.preload(requireActivity().getApplicationContext());
        addressDisplay.setText(MainActivity.userAddress);
        loadingView = binding.loadingLayout.getRoot();
        loadingView.setVisibility(View.VISIBLE);
        ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        String tid = intent.getExtras().getString(Constants.KEY_TRANSACTION_NUMBER);
                        postData(tid);
                    }
                }
        );
        copyProfileAddress.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                addressDisplay.setText(MainActivity.userAddress);
            else addressDisplay.setText(null);
        });
        binding.paymentButton.setOnClickListener(view -> {
            address = addressDisplay.getText().toString();
            if (address.isEmpty())
                Toast.makeText(getContext(), "Fill Address", Toast.LENGTH_SHORT).show();
            else {
                Intent i = new Intent(getContext(), PaymentActivity.class);
                i.putExtra(Constants.KEY_AMOUNT_TO_PAY, String.valueOf(amountToPay * 100));
                paymentLauncher.launch(i);
            }
        });
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        String customerId = MainActivity.userId;
        String customerName = MainActivity.userName;
        binding.dateDisplay.setText(Constants.getDate(Constants.TIMESTAMP_DATE_TIME));
        amountToPay = cartViewModel.getAmountToPay();
        binding.totalCostDisplay.setText("\u20B9 " + amountToPay);
        List<CartItem> itemList = cartViewModel.getCartItemList();
        for (CartItem cartItem : itemList) {
            OrderItem item = new OrderItem(cartItem, null, customerId, customerName, null);
            orderItemList.add(item);
        }
        loadingView.setVisibility(View.INVISIBLE);
        return binding.getRoot();
    }

    private void postData(String transactionId) {
        if (orderItemList.isEmpty())
            return;
        loadingView.setVisibility(View.VISIBLE);
        int n = orderItemList.size();
        for (int i = 0; i < n; i++) {
            OrderItem item = orderItemList.get(i);
            item.setTransactionId(transactionId);
            item.setCustomerAddress(address);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            int finalI = i;
            db.collection(Constants.ORDERS_URL)
                    .document()
                    .set(item)
                    .addOnSuccessListener(unused -> {
                        if (finalI == n - 1)
                            launchFragment(transactionId);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show());
        }
        AppDatabase.getInstance(getContext()).appDao().deleteAllCartItems();
    }

    private void launchFragment(String transactionId) {
        CartReceiptFragment fragment = new CartReceiptFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_TRANSACTION_NUMBER, transactionId);
        bundle.putBoolean(Constants.KEY_FROM_ORDERS_SCREEN, false);
        bundle.putString(Constants.KEY_AMOUNT_TO_PAY, "\u20b9 " + amountToPay);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        assert getParentFragment() != null;
        int count = fragmentManager.getBackStackEntryCount();
        while (count-- > 0)
            fragmentManager.popBackStack();
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId()
                        , fragment)
                .addToBackStack(null)
                .commit();
    }
}
