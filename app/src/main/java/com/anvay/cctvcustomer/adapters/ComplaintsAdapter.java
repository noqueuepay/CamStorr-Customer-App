package com.anvay.cctvcustomer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.models.Complaint;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ViewHolder> {
    private final List<Complaint> complaintList;
    private final ComplaintsClickListener complaintsClickListener;

    public ComplaintsAdapter(List<Complaint> complaintList, ComplaintsClickListener complaintsClickListener) {
        this.complaintList = complaintList;
        this.complaintsClickListener = complaintsClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public ComplaintsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ComplaintsAdapter.ViewHolder holder, int position) {
        holder.bind(complaintList.get(position));
    }

    @Override
    public int getItemCount() {
        return complaintList == null ? 0 : complaintList.size();
    }

    public interface ComplaintsClickListener {
        void onComplaintClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView ticketNumber;
        private final TextView complaintDate;
        private final TextView complaintCategory;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ticketNumber = itemView.findViewById(R.id.ticket_number);
            complaintDate = itemView.findViewById(R.id.complaint_date);
            complaintCategory = itemView.findViewById(R.id.complaint_category);
            itemView.setOnClickListener(this);
        }

        public void bind(Complaint complaint) {
            ticketNumber.setText(complaint.getTicketNumber());
            complaintDate.setText(complaint.getDate());
            complaintCategory.setText(complaint.getComplaintCategory());
        }

        @Override
        public void onClick(View view) {
            complaintsClickListener.onComplaintClicked(getAdapterPosition());
        }
    }
}
