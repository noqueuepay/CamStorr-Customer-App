package com.anvay.cctvcustomer.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.databinding.FragmentOrderDetailsBinding;
import com.anvay.cctvcustomer.models.OrderItem;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.OrderViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderDetailsFragment extends Fragment {
    private OrderItem orderItem;
    private FragmentOrderDetailsBinding binding;
    private View loadingView;
    private OrderViewModel orderViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false);
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        loadingView = binding.loadingLayout.getRoot();
        orderItem = orderViewModel.getOrderItem();
        Constants.OrderStatus status = orderItem.getStatus();
        if (status == Constants.OrderStatus.RECEIVED)
            binding.receivedButton.setVisibility(View.GONE);
        if (status == Constants.OrderStatus.CANCELLED) {
            binding.receivedButton.setVisibility(View.GONE);
            binding.cancelButton.setVisibility(View.INVISIBLE);
        }
        binding.cancelButton.setOnClickListener(view -> openConfirmationDialog());
        binding.receivedButton.setOnClickListener(view -> setReceived());
        binding.receiptButton.setOnClickListener(view -> launchReceiptFragment());
        initUI();
        return binding.getRoot();
    }

    private void openConfirmationDialog() {
        Dialog dialog = new Dialog(getContext());
        loadingView.setVisibility(View.INVISIBLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(width, height);
        dialog.show();
        TextView infoText = dialog.findViewById(R.id.info_text);
        Button confirm = dialog.findViewById(R.id.confirm_button);
        Button cancel = dialog.findViewById(R.id.cancel_button);
        infoText.setText("Are you sure, you want to cancel this order?\nRead refund policy before cancelling");
        confirm.setOnClickListener(view -> {
            setCancelled();
            dialog.dismiss();
        });
        cancel.setOnClickListener(view -> dialog.dismiss());
    }

    private void launchReceiptFragment() {
        CartReceiptFragment fragment = new CartReceiptFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_FROM_ORDERS_SCREEN, true);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        assert getParentFragment() != null;
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setReceived() {
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.ORDERS_URL)
                .document(orderItem.getDocumentId())
                .update(Constants.KEY_ORDER_STATUS, Constants.OrderStatus.RECEIVED)
                .addOnSuccessListener(unused -> {
                    binding.receivedButton.setVisibility(View.GONE);
                    binding.statusDisplay.setText(Constants.OrderStatus.RECEIVED.name);
                    binding.statusDisplay.setTextColor(Constants.OrderStatus.RECEIVED.color);
                    orderViewModel.setStatusReceived();
                    Toast.makeText(getContext(), "Order marked as received", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }

    private void setCancelled() {
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.ORDERS_URL)
                .document(orderItem.getDocumentId())
                .update(Constants.KEY_ORDER_STATUS, Constants.OrderStatus.CANCELLED)
                .addOnSuccessListener(unused -> {
                    binding.receivedButton.setVisibility(View.GONE);
                    binding.cancelButton.setVisibility(View.INVISIBLE);
                    binding.statusDisplay.setText(Constants.OrderStatus.CANCELLED.name);
                    binding.statusDisplay.setTextColor(Constants.OrderStatus.CANCELLED.color);
                    orderViewModel.setStatusCancelled();
                    Toast.makeText(getContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        if (orderItem == null)
            return;
        binding.nameDisplay.setText(orderItem.getProductName());
        binding.storeNameDisplay.setText(orderItem.getStoreName());
        binding.transactionNoDisplay.setText(orderItem.getTransactionId());
        double price = orderItem.getPrice();
        int quantity = orderItem.getQuantity();
        binding.priceDisplay.setText("\u20B9 " + price);
        binding.quantityDisplay.setText(String.valueOf(quantity));
        binding.paidAmountDisplay.setText("\u20B9 " + (price * quantity));
        binding.statusDisplay.setText(orderItem.getStatus().name);
        binding.statusDisplay.setTextColor(orderItem.getStatus().color);
        binding.addressDisplay.setText(orderItem.getCustomerAddress());
        binding.paymentDateDisplay.setText(orderItem.getDate());
    }
}
