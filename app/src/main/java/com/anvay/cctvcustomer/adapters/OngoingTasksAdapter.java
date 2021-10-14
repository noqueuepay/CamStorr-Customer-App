package com.anvay.cctvcustomer.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.databinding.ListItemOngoingTaskBinding;
import com.anvay.cctvcustomer.models.OngoingTask;
import com.anvay.cctvcustomer.models.Task;

import java.util.List;

public class OngoingTasksAdapter extends RecyclerView.Adapter<OngoingTasksAdapter.ViewHolder> {
    private final List<OngoingTask> ongoingTaskList;
    private final OngoingTasksItemClickListener listener;
    private ListItemOngoingTaskBinding binding;

    public OngoingTasksAdapter(List<OngoingTask> ongoingTaskList, OngoingTasksItemClickListener listener) {
        this.ongoingTaskList = ongoingTaskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OngoingTasksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListItemOngoingTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull OngoingTasksAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ongoingTaskList == null ? 0 : ongoingTaskList.size();
    }

    public interface OngoingTasksItemClickListener {
        void onItemClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(view -> listener.onItemClicked(getAdapterPosition()));
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {
            OngoingTask ongoingTask = ongoingTaskList.get(position);
            Task task = ongoingTask.getTask();
            binding.bidAmountDisplay.setText("\u20B9 " + ongoingTask.getAcceptedBidAmount());
            binding.titleDisplay.setText(task.getCameraBrand() + "-" + task.getCameraType());
            binding.storeNameDisplay.setText("Store Name: " + ongoingTask.getStoreName());
            binding.orderIdDisplay.setText("Order Id: " + ongoingTask.getOrderId());
            binding.dateOfTaskDisplay.setText("Date of task: " + ongoingTask.getDateOfTask());
        }
    }
}

