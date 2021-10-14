package com.anvay.cctvcustomer.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_cart")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private int ID;

    @ColumnInfo(name = "product_id")
    private String productId;

    @ColumnInfo(name = "product_price")
    private double productPrice;

    @ColumnInfo(name = "product_quantity")
    private int productQuantity;

    @ColumnInfo(name = "product_name")
    private String productName;

    @ColumnInfo(name = "store_id")
    private String storeId;

    @ColumnInfo(name = "store_name")
    private String storeName;

    @ColumnInfo(name = "product_image_url")
    private String imageUrl;

    public CartItem(String productId, double productPrice, int productQuantity, String productName,
                    String storeId, String storeName, String imageUrl) {
        this.productId = productId;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.productName = productName;
        this.storeId = storeId;
        this.storeName = storeName;
        this.imageUrl = imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void incrementQuantity() {
        this.productQuantity++;
    }

    public void decrementQuantity() {
        this.productQuantity--;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}