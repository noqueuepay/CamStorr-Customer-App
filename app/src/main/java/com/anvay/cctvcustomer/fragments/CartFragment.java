package com.anvay.cctvcustomer.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.adapters.CartItemsAdapter;
import com.anvay.cctvcustomer.databinding.FragmentCartBinding;
import com.anvay.cctvcustomer.models.CartItem;
import com.anvay.cctvcustomer.utils.AppDatabase;
import com.anvay.cctvcustomer.utils.CartViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CartFragment extends Fragment implements CartItemsAdapter.CartItemsClickListener {
    private FragmentCartBinding binding;
    private List<CartItem> cartItemList;
    private CartItemsAdapter adapter;
    private View loadingView;
    private AppDatabase appDatabase;
    private double totalPrice;
    private View summaryView;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        String title = "Your Cart";
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.setCartPurchased(false);
        titleViewModel.addToTitleStack(title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        loadingView = binding.loadingLayout.getRoot();
        appDatabase = AppDatabase.getInstance(getContext());
        cartItemList = appDatabase.appDao().getCartItems();
        RecyclerView cartItemsRecycler = binding.cartItemsRecycler;
        summaryView = binding.cartSummaryView;
        calculateSummary();
        checkForEmptyRecycler();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new CartItemsAdapter(cartItemList, this);
        cartItemsRecycler.setLayoutManager(layoutManager);
        cartItemsRecycler.setAdapter(adapter);
        binding.purchaseButton.setOnClickListener(view -> launchPaymentFragment());
        return binding.getRoot();
    }

    private void launchPaymentFragment() {
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.setCartItemList(cartItemList);
        cartViewModel.setAmountToPay(totalPrice);
        CartPaymentFragment fragment = new CartPaymentFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .add(binding.fragmentContainer.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @SuppressLint("SetTextI18n")
    private void updateSummary() {
        binding.totalCostDisplay.setText("\u20B9 " + totalPrice);
    }

    private void calculateSummary() {
        for (CartItem item : cartItemList) {
            int quantity = item.getProductQuantity();
            totalPrice += item.getProductPrice() * quantity;
        }
        updateSummary();
    }

    @Override
    public void onIncrementClicked(int position) {
        CartItem cartItem = cartItemList.get(position);
        appDatabase.appDao().incrementQuantity(cartItem.getID());
        cartItem.incrementQuantity();
        adapter.notifyItemChanged(position);
        totalPrice += cartItem.getProductPrice();
        updateSummary();
    }

    @Override
    public void onDecrementClicked(int position) {
        CartItem cartItem = cartItemList.get(position);
        if (cartItem.getProductQuantity() == 1)
            return;
        cartItem.decrementQuantity();
        appDatabase.appDao().decrementQuantity(cartItem.getID());
        totalPrice -= cartItem.getProductPrice();
        adapter.notifyItemChanged(position);
        updateSummary();
    }

    @Override
    public void onDeleteClicked(int position) {
        CartItem cartItem = cartItemList.get(position);
        appDatabase.appDao().deleteCartItem(cartItem.getID());
        totalPrice -= cartItem.getProductQuantity() * cartItem.getProductPrice();
        updateSummary();
        cartItemList.remove(position);
        checkForEmptyRecycler();
        adapter.notifyItemRemoved(position);
        adapter.notifyItemChanged(position, cartItemList.size());
    }

    private void checkForEmptyRecycler() {
        if (cartItemList.isEmpty()) {
            summaryView.setVisibility(View.INVISIBLE);
            binding.emptyRecyclerText.setVisibility(View.VISIBLE);
        } else {
            summaryView.setVisibility(View.VISIBLE);
            binding.emptyRecyclerText.setVisibility(View.INVISIBLE);
        }
    }
}
