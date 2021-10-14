package com.anvay.cctvcustomer.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.databinding.ListItemCartBinding;
import com.anvay.cctvcustomer.models.CartItem;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder> {

    private final List<CartItem> cartItems;
    private final CartItemsClickListener listener;

    public CartItemsAdapter(List<CartItem> cartItems, CartItemsClickListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemCartBinding binding = ListItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cartItems == null ? 0 : cartItems.size();
    }

    public interface CartItemsClickListener {
        void onIncrementClicked(int position);

        void onDecrementClicked(int position);

        void onDeleteClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView productQuantity, productQuantityIncrement, productQuantityDecrement,
                productName, productPrice;
        private final ImageView productDelete;
        private final ShapeableImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productQuantityIncrement = itemView.findViewById(R.id.product_quantity_increment);
            productQuantityDecrement = itemView.findViewById(R.id.product_quantity_decrement);
            productDelete = itemView.findViewById(R.id.product_delete);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {
            CartItem item = cartItems.get(position);
            productName.setText(item.getProductName());
            productPrice.setText("\u20B9 " + item.getProductPrice());
            Picasso.get()
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.loading_bar)
                    .into(productImage);
            productQuantity.setText(String.valueOf(item.getProductQuantity()));
            productQuantityIncrement.setOnClickListener(view -> listener.onIncrementClicked(position));
            productQuantityDecrement.setOnClickListener(view -> listener.onDecrementClicked(position));
            productDelete.setOnClickListener(view -> listener.onDeleteClicked(position));
        }
    }
}
