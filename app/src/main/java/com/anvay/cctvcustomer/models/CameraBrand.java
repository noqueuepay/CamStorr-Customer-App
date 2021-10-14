package com.anvay.cctvcustomer.models;

import java.util.ArrayList;
import java.util.List;

public class CameraBrand {
    private String brandName;
    private List<String> cameraTypes = new ArrayList<>();
    private List<String> hardDiskTypes = new ArrayList<>();

    public CameraBrand() {
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public List<String> getCameraTypes() {
        return cameraTypes;
    }

    public void setCameraTypes(List<String> cameraTypes) {
        this.cameraTypes = cameraTypes;
    }

    public List<String> getHardDiskTypes() {
        return hardDiskTypes;
    }

    public void setHardDiskTypes(List<String> hardDiskTypes) {
        this.hardDiskTypes = hardDiskTypes;
    }
}

