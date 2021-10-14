package com.anvay.cctvcustomer.models;

import android.text.format.DateFormat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@IgnoreExtraProperties
public class Task {
    @ServerTimestamp
    private Timestamp timestamp;

    private String customerId, customerName, typeOfService, cameraType, cameraBrand, hardDiskType,
            zipcode, description;
    private int numberOfCameras, wireLength;
    private List<Bid> bids;
    @Exclude
    private String taskId;

    public Task() {
    }

    public Task(String customerId, String customerName, String typeOfService, String cameraBrand,
                String cameraType, String hardDiskType, String zipcode, String description,
                int numberOfCameras, int wireLength) {
        this.customerName = customerName;
        this.typeOfService = typeOfService;
        this.cameraBrand = cameraBrand;
        this.cameraType = cameraType;
        this.hardDiskType = hardDiskType;
        this.zipcode = zipcode;
        this.numberOfCameras = numberOfCameras;
        this.wireLength = wireLength;
        this.description = description;
        this.customerId = customerId;
        bids = new ArrayList<>();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTypeOfService() {
        return typeOfService;
    }

    public void setTypeOfService(String typeOfService) {
        this.typeOfService = typeOfService;
    }

    public String getCameraType() {
        return cameraType;
    }

    public void setCameraType(String cameraType) {
        this.cameraType = cameraType;
    }

    public String getCameraBrand() {
        return cameraBrand;
    }

    public void setCameraBrand(String cameraBrand) {
        this.cameraBrand = cameraBrand;
    }

    public String getHardDiskType() {
        return hardDiskType;
    }

    public void setHardDiskType(String hardDiskType) {
        this.hardDiskType = hardDiskType;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfCameras() {
        return numberOfCameras;
    }

    public void setNumberOfCameras(int numberOfCameras) {
        this.numberOfCameras = numberOfCameras;
    }

    public int getWireLength() {
        return wireLength;
    }

    public void setWireLength(int wireLength) {
        this.wireLength = wireLength;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    @Exclude
    public String getDate() {
        if (timestamp == null)
            return "Now";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getSeconds() * 1000);
        return DateFormat.format("dd-MMM", calendar).toString();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Exclude
    public String getTaskId() {
        return taskId;
    }

    @Exclude
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
