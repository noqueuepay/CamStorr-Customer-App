package com.anvay.cctvcustomer.utils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.anvay.cctvcustomer.models.ProductItem;

public class ProductItemViewModel extends ViewModel {

    private final MutableLiveData<ProductItem> productItem = new MutableLiveData<>();

    public ProductItem getProductItem() {
        return productItem.getValue();
    }

    public void setProductItem(ProductItem productItem) {
        this.productItem.setValue(productItem);
    }

}
