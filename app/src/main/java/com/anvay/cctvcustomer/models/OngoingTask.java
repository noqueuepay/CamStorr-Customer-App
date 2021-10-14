package com.anvay.cctvcustomer.models;

import android.text.format.DateFormat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Calendar;

@IgnoreExtraProperties
public class OngoingTask {
    @ServerTimestamp
    private Timestamp paymentTimestamp;
    private Task task;
    private double acceptedBidAmount;
    private String customerId, customerAddress, dateOfTask, storeId, storeName;
    private String transactionId, orderId;
    private boolean isCompleted;
    @Exclude
    private String ongoingTaskId;

    public OngoingTask() {
    }

    public OngoingTask(Task task, double acceptedBidAmount, String customerAddress, String dateOfTask,
                       String storeId, String storeName, String orderId, String transactionId) {
        this.task = task;
        this.orderId = orderId;
        this.acceptedBidAmount = acceptedBidAmount;
        this.customerAddress = customerAddress;
        this.isCompleted = false;
        this.dateOfTask = dateOfTask;
        this.storeId = storeId;
        this.customerId = task.getCustomerId();
        this.storeName = storeName;
        this.transactionId = transactionId;
    }

    @Exclude
    public String getPaymentDate() {
        if (paymentTimestamp == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(paymentTimestamp.getSeconds() * 1000);
        return DateFormat.format("dd-MMM-yyyy HH:mm", calendar).toString();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public double getAcceptedBidAmount() {
        return acceptedBidAmount;
    }

    public void setAcceptedBidAmount(double acceptedBidAmount) {
        this.acceptedBidAmount = acceptedBidAmount;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getDateOfTask() {
        return dateOfTask;
    }

    public void setDateOfTask(String dateOfTask) {
        this.dateOfTask = dateOfTask;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Timestamp getPaymentTimestamp() {
        return paymentTimestamp;
    }

    public void setPaymentTimestamp(Timestamp paymentTimestamp) {
        this.paymentTimestamp = paymentTimestamp;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Exclude
    public String getOngoingTaskId() {
        return ongoingTaskId;
    }

    @Exclude
    public void setOngoingTaskId(String ongoingTaskId) {
        this.ongoingTaskId = ongoingTaskId;
    }
}
