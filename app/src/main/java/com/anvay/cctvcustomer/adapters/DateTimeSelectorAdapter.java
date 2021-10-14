package com.anvay.cctvcustomer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.databinding.ListItemDateSelectorBinding;
import com.anvay.cctvcustomer.models.Date;

import java.util.List;

public class DateTimeSelectorAdapter extends RecyclerView.Adapter<DateTimeSelectorAdapter.ViewHolder> {
    private final List<Date> dateList;
    private final DateTimeAdapterListener listener;
    private ListItemDateSelectorBinding binding;

    public DateTimeSelectorAdapter(List<Date> dateList, DateTimeAdapterListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListItemDateSelectorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return dateList == null ? 0 : dateList.size();
    }

    public interface DateTimeAdapterListener {
        void onItemClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMain, textSecondary;
        View selectedOverlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMain = binding.textMain;
            textSecondary = binding.textSecondary;
            selectedOverlay = binding.selectedOverlay;
            itemView.setOnClickListener(view -> listener.onItemClicked(getAdapterPosition()));
        }

        public void bind(int position) {
            Date date = dateList.get(position);
            textMain.setText(date.getDate());
            textSecondary.setText(date.getDay());
            if (date.isSelected())
                selectedOverlay.setVisibility(View.VISIBLE);
            else selectedOverlay.setVisibility(View.INVISIBLE);
        }
    }
}
