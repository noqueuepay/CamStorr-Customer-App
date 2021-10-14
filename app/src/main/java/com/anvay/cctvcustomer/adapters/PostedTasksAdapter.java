package com.anvay.cctvcustomer.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.databinding.ListItemTaskBinding;
import com.anvay.cctvcustomer.models.Task;

import java.util.List;

public class PostedTasksAdapter extends RecyclerView.Adapter<PostedTasksAdapter.ViewHolder> {
    private final List<Task> postedTasks;
    private final PostedTaskClickListener clickListener;
    private ListItemTaskBinding binding;

    public PostedTasksAdapter(List<Task> postedTasks, PostedTaskClickListener clickListener) {
        this.postedTasks = postedTasks;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ListItemTaskBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return postedTasks == null ? 0 : postedTasks.size();
    }

    public interface PostedTaskClickListener {
        void onTaskItemClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleDisplay, descriptionDisplay, dateDisplay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleDisplay = binding.titleDisplay;
            descriptionDisplay = binding.descriptionDisplay;
            dateDisplay = binding.postedDateDisplay;
        }

        @SuppressLint("SetTextI18n")
        public void bind(int position) {
            Task task = postedTasks.get(position);
            titleDisplay.setText(task.getCameraBrand() + "-" + task.getCameraType());
            descriptionDisplay.setText(task.getDescription());
            dateDisplay.setText(task.getDate());
            itemView.setOnClickListener(view -> clickListener.onTaskItemClicked(position));
        }
    }
}
