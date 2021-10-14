package com.anvay.cctvcustomer.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.anvay.cctvcustomer.models.OrderItem;

public class OrderViewModel extends ViewModel {


    private final MutableLiveData<OrderItem> orderItem = new MutableLiveData<>();

    public LiveData<OrderItem> getLiveOrderItem() {
        return orderItem;
    }

    public OrderItem getOrderItem() {
        return orderItem.getValue();
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem.setValue(orderItem);
    }

    public void setStatusReceived() {
        if (orderItem.getValue() != null) {
            this.orderItem.getValue().setStatus(Constants.OrderStatus.RECEIVED);
            orderItem.setValue(orderItem.getValue());
        }
    }

    public void setStatusCancelled() {
        if (orderItem.getValue() != null) {
            this.orderItem.getValue().setStatus(Constants.OrderStatus.CANCELLED);
            orderItem.setValue(orderItem.getValue());
        }
    }
}
