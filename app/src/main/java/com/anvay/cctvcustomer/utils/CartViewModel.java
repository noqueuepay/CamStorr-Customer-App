package com.anvay.cctvcustomer.utils;

import androidx.lifecycle.ViewModel;

import com.anvay.cctvcustomer.models.CartItem;

import java.util.List;

public class CartViewModel extends ViewModel {

    private List<CartItem> cartItemList;
    private double amountToPay;

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public double getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(double amountToPay) {
        this.amountToPay = amountToPay;
    }
}
