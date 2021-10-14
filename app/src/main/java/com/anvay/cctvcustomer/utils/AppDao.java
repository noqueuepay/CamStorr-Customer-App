package com.anvay.cctvcustomer.utils;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.anvay.cctvcustomer.models.CartItem;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AppDao {
    @Insert(onConflict = REPLACE)
    void addCartItem(CartItem cartItem);

    @Query("DELETE FROM user_cart WHERE ID = :id")
    void deleteCartItem(int id);

    @Query("UPDATE user_cart SET product_quantity = product_quantity+1 WHERE ID = :id")
    void incrementQuantity(int id);

    @Query("UPDATE user_cart SET product_quantity = product_quantity-1 WHERE ID = :id")
    void decrementQuantity(int id);

    @Query("SELECT * FROM user_cart")
    List<CartItem> getCartItems();

    @Query("DELETE FROM user_cart")
    void deleteAllCartItems();
}
