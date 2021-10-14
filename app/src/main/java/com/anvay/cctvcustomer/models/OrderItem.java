package com.anvay.cctvcustomer.models;

import android.text.format.DateFormat;

import com.anvay.cctvcustomer.utils.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Calendar;

@IgnoreExtraProperties
public class OrderItem {
    @ServerTimestamp
    private Timestamp timestamp;
    @Exclude
    private String documentId;
    private String productName, productId, storeName, storeId, transactionId, imageUrl, customerId,
            customerName, customerAddress;
    private int quantity;
    private double price;
    private Constants.OrderStatus status;

    public OrderItem() {
    }

    public OrderItem(CartItem cartItem, String transactionId, String customerId, String customerName,
                     String customerAddress) {
        this.productName = cartItem.getProductName();
        this.customerAddress = customerAddress;
        this.productId = cartItem.getProductId();
        this.customerName = customerName;
        this.storeName = cartItem.getStoreName();
        this.storeId = cartItem.getStoreId();
        this.transactionId = transactionId;
        this.imageUrl = cartItem.getImageUrl();
        this.customerId = customerId;
        this.quantity = cartItem.getProductQuantity();
        this.price = cartItem.getProductPrice();
        this.status = Constants.OrderStatus.PENDING;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Constants.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Constants.OrderStatus status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    @Exclude
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Exclude
    public String getDate() {
        if (timestamp == null)
            return "Not available";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getSeconds() * 1000);
        return DateFormat.format("dd-MMM", calendar).toString();
    }
}
