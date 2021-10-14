package com.anvay.cctvcustomer.models;

import com.google.firebase.firestore.Exclude;

public class ProductItem {
    @Exclude
    private String documentId;
    private String id, name, brand, details, category, subCategory, imageUrl, storeName, storeId,
            sku, sla, mName, mAddress, gst;
    private int stockQuantity;
    private double price, discount;

    public ProductItem() {
    }

    public ProductItem(String id, String name, String brand, String details, String category, String
            subCategory, String imageUrl, String storeName, String storeId, String sku, String sla, String mName,
                       String mAddress, int stockQuantity, double price, double discount, String gst) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.details = details;
        this.category = category;
        this.subCategory = subCategory;
        this.imageUrl = imageUrl;
        this.storeId = storeId;
        this.sku = sku;
        this.sla = sla;
        this.storeName = storeName;
        this.mName = mName;
        this.mAddress = mAddress;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.discount = discount;
        this.gst = gst;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSla() {
        return sla;
    }

    public void setSla(String sla) {
        this.sla = sla;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
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
    public int getOriginalPrice() {
        return (int) ((this.price * 100) / (100 - this.discount));
    }

    @Exclude
    public CartItem getCartItem() {
        return new CartItem(id, price, 1, name, storeId, storeName, imageUrl);
    }
}
