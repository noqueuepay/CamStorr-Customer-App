package com.anvay.cctvcustomer.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.databinding.FragmentProductDetailsBinding;
import com.anvay.cctvcustomer.models.CartItem;
import com.anvay.cctvcustomer.models.ProductItem;
import com.anvay.cctvcustomer.utils.AppDatabase;
import com.anvay.cctvcustomer.utils.CartViewModel;
import com.anvay.cctvcustomer.utils.ProductItemViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsFragment extends Fragment {
    private FragmentProductDetailsBinding binding;
    private View loadingView;
    private ProductItem currentItem;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack("Product Details");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);
        loadingView = binding.loading.getRoot();
        ProductItemViewModel productItemViewModel = new ViewModelProvider(requireActivity()).get(ProductItemViewModel.class);
        currentItem = productItemViewModel.getProductItem();
        displayData();
        binding.addToCartButton.setOnClickListener(view -> addToCart());
        binding.buyNowButton.setOnClickListener(view -> buyNow());
        return binding.getRoot();
    }

    private void addToCart() {
        if (currentItem == null)
            return;
        AppDatabase database = AppDatabase.getInstance(getContext());
        database.appDao().addCartItem(currentItem.getCartItem());
        Toast.makeText(getContext(), "Added to your cart", Toast.LENGTH_SHORT).show();
    }

    private void buyNow() {
        CartViewModel cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(new CartItem(currentItem.getId(), currentItem.getPrice(), 1,
                currentItem.getName(), currentItem.getStoreId(), currentItem.getStoreName(), currentItem.getImageUrl()));
        cartViewModel.setCartItemList(cartItemList);
        cartViewModel.setAmountToPay(currentItem.getPrice());
        CartPaymentFragment fragment = new CartPaymentFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        assert getParentFragment() != null;
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @SuppressLint("SetTextI18n")
    private void displayData() {
        if (currentItem == null)
            return;
        Picasso.get()
                .load(currentItem.getImageUrl())
                .placeholder(R.drawable.loading_bar)
                .into(binding.imageDisplay);
        binding.productName.setText(currentItem.getName());
        binding.productBrand.setText(currentItem.getBrand());
        binding.productPrice.setText("\u20B9 " + currentItem.getPrice());
        binding.productPriceOriginal.setText("\u20B9 " + currentItem.getOriginalPrice());
        binding.productDiscount.setText(currentItem.getDiscount() + "% Off");
        binding.productDetails.setText(currentItem.getDetails());
        binding.gstDisplay.setText(currentItem.getGst());
        binding.storeNameDisplay.setText(currentItem.getStoreName());
        binding.skuDisplay.setText(currentItem.getSku());
        binding.slaDisplay.setText(currentItem.getSla());
        binding.mNameDisplay.setText(currentItem.getmName());
        binding.mAddressDisplay.setText(currentItem.getmAddress());
    }

}
