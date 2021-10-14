package com.anvay.cctvcustomer.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.databinding.ListItemProductBinding;
import com.anvay.cctvcustomer.models.ProductItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private final List<ProductItem> productItems;
    private final ProductItemClickListener listener;
    private ListItemProductBinding binding;

    public ProductsAdapter(List<ProductItem> productItems, ProductItemClickListener listener) {
        this.productItems = productItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListItemProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return productItems == null ? 0 : productItems.size();
    }

    public interface ProductItemClickListener {
        void onProductItemClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> listener.onProductItemClicked(getAdapterPosition()));
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {
            ProductItem item = productItems.get(position);
            binding.productBrand.setText(item.getBrand());
            binding.productPrice.setText("\u20B9 " + item.getPrice());
            binding.productDiscount.setText(item.getDiscount() + "% off");
            binding.productPriceOriginal.setText("\u20B9 " + item.getOriginalPrice());
            binding.productName.setText(item.getName());
            Picasso.get()
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.loading_bar)
                    .into(binding.productImage);
        }
    }
}
