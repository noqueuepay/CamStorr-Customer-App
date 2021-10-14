package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.adapters.BidsAdapter;
import com.anvay.cctvcustomer.databinding.FragmentBidsBinding;
import com.anvay.cctvcustomer.models.Bid;
import com.anvay.cctvcustomer.models.Task;
import com.anvay.cctvcustomer.utils.BidViewModel;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TaskViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BidsFragment extends Fragment implements BidsAdapter.BidItemClickListener {
    private View loadingView;
    private List<Bid> bidsList;
    private BidsAdapter adapter;
    private TextView emptyRecyclerText;
    private Task currentTask;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack("All Bids");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBidsBinding binding = FragmentBidsBinding.inflate(inflater, container, false);
        loadingView = binding.loadingLayout.getRoot();
        emptyRecyclerText = binding.emptyRecyclerText;
        RecyclerView bidsRecycler = binding.bidsRecycler;
        TaskViewModel taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        currentTask = taskViewModel.getPostedTask().getValue();
        if (currentTask != null) {
            bidsList = currentTask.getBids();
        }
        adapter = new BidsAdapter(bidsList, this);
        bidsRecycler.setAdapter(adapter);
        checkEmptyRecycler();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bidsRecycler.setLayoutManager(linearLayoutManager);
        checkEmptyRecycler();
        return binding.getRoot();
    }

    private void checkEmptyRecycler() {
        if (bidsList == null || bidsList.isEmpty()) {
            emptyRecyclerText.setVisibility(View.VISIBLE);
        } else emptyRecyclerText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAcceptBidButtonClicked(int position) {
        if (currentTask == null)
            return;
        BidViewModel bidViewModel = new ViewModelProvider(requireActivity()).get(BidViewModel.class);
        bidViewModel.setAcceptedBid(bidsList.get(position));
        TaskPaymentFragment fragment = new TaskPaymentFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        assert getParentFragment() != null;
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId()
                        , fragment)
                .addToBackStack("customerDetails")
                .commit();

    }

    @Override
    public void onIgnoreBidButtonClicked(int position) {
        if (currentTask == null)
            return;
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_TASKS_URL)
                .document(currentTask.getTaskId())
                .update(Constants.KEY_BIDS_ARRAY, FieldValue.arrayRemove(bidsList.get(position)))
                .addOnSuccessListener(unused -> {
                    loadingView.setVisibility(View.INVISIBLE);
                    bidsList.remove(position);
                    adapter.notifyItemRemoved(position);
                    checkEmptyRecycler();
                    Toast.makeText(getContext(), "Bid removed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }
}
