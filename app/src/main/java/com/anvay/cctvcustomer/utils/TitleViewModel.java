package com.anvay.cctvcustomer.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayDeque;
import java.util.Deque;

public class TitleViewModel extends ViewModel {
    private final MutableLiveData<Integer> stackSize = new MutableLiveData<>();
    private Deque<String> titleStack = new ArrayDeque<>();
    private Boolean cartPurchased;

    public LiveData<Integer> getStackSize() {
        return stackSize;
    }

    public void setTitleStack(String firstTitle) {
        titleStack = new ArrayDeque<>();
        titleStack.push(firstTitle);
        stackSize.setValue(1);
    }

    public void addToTitleStack(String title) {
        if (titleStack != null) {
            titleStack.push(title);
            if (stackSize.getValue() != null)
                stackSize.setValue(stackSize.getValue() + 1);
        }
    }

    public void popTitleStack() {
        if (titleStack != null && titleStack.size() > 1) {
            titleStack.pop();
            if (stackSize.getValue() != null)
                stackSize.setValue(stackSize.getValue() - 1);
        }
    }

    public String getTopTitle() {
        if (titleStack != null)
            return titleStack.peek();
        return "Home";
    }

    public Boolean isCartPurchased() {
        return cartPurchased;
    }

    public void setCartPurchased(Boolean cartPurchased) {
        this.cartPurchased = cartPurchased;
    }
}
