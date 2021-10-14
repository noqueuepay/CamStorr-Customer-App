package com.anvay.cctvcustomer.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.databinding.ListItemBidBinding;
import com.anvay.cctvcustomer.models.Bid;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.ViewHolder> {
    private final BidItemClickListener clickListener;
    private final List<Bid> bidsList;
    private ListItemBidBinding binding;

    public BidsAdapter(List<Bid> bidsList, BidItemClickListener clickListener) {
        this.clickListener = clickListener;
        this.bidsList = bidsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListItemBidBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return bidsList == null ? 0 : bidsList.size();
    }

    public interface BidItemClickListener {
        void onAcceptBidButtonClicked(int position);

        void onIgnoreBidButtonClicked(int position);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeNameDisplay, dateDisplay, bidAmountDisplay;
        Button acceptBidButton, ignoreBidButton;
        ShapeableImageView storeImageDisplay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeNameDisplay = binding.storeNameDisplay;
            dateDisplay = binding.postedDateDisplay;
            storeImageDisplay = binding.storeImageDisplay;
            bidAmountDisplay = binding.bidAmountDisplay;
            acceptBidButton = binding.acceptBidButton;
            ignoreBidButton = binding.ignoreBidButton;
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {
            Bid bid = bidsList.get(position);
            storeNameDisplay.setText(bid.getStoreName());
            dateDisplay.setText(bid.getDate());
            Picasso.get()
                    .load(bid.getImageUrl())
                    .placeholder(R.drawable.loading_bar)
                    .into(storeImageDisplay);
            bidAmountDisplay.setText("\u20B9 " + bid.getBidAmount());
            acceptBidButton.setOnClickListener(view -> clickListener.onAcceptBidButtonClicked(position));
            ignoreBidButton.setOnClickListener(view -> clickListener.onIgnoreBidButtonClicked(position));
        }
    }
}
