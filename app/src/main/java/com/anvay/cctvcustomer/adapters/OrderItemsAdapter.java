package com.anvay.cctvcustomer.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.models.OrderItem;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {

    private final List<OrderItem> list;
    private final OrderItemsClickListener listener;

    public OrderItemsAdapter(List<OrderItem> list, OrderItemsClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public interface OrderItemsClickListener {
        void onItemClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, date, status, price;
        private final ShapeableImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            date = itemView.findViewById(R.id.date_of_purchase);
            status = itemView.findViewById(R.id.order_status);
            price = itemView.findViewById(R.id.product_price);
            imageView = itemView.findViewById(R.id.product_image);
            itemView.setOnClickListener(view -> listener.onItemClicked(getAdapterPosition()));
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {
            OrderItem orderItem = list.get(position);
            Picasso.get()
                    .load(orderItem.getImageUrl())
                    .placeholder(R.drawable.loading_bar)
                    .into(imageView);
            name.setText(orderItem.getProductName());
            status.setText(orderItem.getStatus().name);
            status.setTextColor(orderItem.getStatus().color);
            price.setText("\u20B9 " + orderItem.getPrice());
            date.setText(orderItem.getDate());
        }
    }
}
