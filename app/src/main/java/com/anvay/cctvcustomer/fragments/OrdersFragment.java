package com.anvay.cctvcustomer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import com.anvay.cctvcustomer.MainActivity;
import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.adapters.OrderItemsAdapter;
import com.anvay.cctvcustomer.databinding.FragmentOrdersBinding;
import com.anvay.cctvcustomer.models.OrderItem;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.OrderViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements OrderItemsAdapter.OrderItemsClickListener {
    private List<OrderItem> orderItemList;
    private OrderItemsAdapter adapter;
    private View loadingView;
    private OrderViewModel orderViewModel;
    private int clickedPosition;
    private TextView emptyRecyclerText;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        String title = "My Orders";
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack(title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentOrdersBinding binding = FragmentOrdersBinding.inflate(inflater, container, false);
        loadingView = binding.loadingLayout.getRoot();
        emptyRecyclerText = binding.emptyRecyclerText;
        orderItemList = new ArrayList<>();
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        RecyclerView ordersRecycler = binding.ordersRecycler;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        ordersRecycler.setLayoutManager(linearLayoutManager);
        adapter = new OrderItemsAdapter(orderItemList, this);
        ordersRecycler.setAdapter(adapter);
        getData();
        orderViewModel.getLiveOrderItem().observe(requireActivity(), orderItem -> {
            adapter.notifyItemChanged(clickedPosition, orderItem);
        });
        return binding.getRoot();
    }

    private void getData() {
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.ORDERS_URL)
                .whereEqualTo(Constants.KEY_CUSTOMER_ID, MainActivity.userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        OrderItem item = doc.toObject(OrderItem.class);
                        if (item != null) {
                            item.setDocumentId(doc.getId());
                            orderItemList.add(item);
                        }
                    }
                    adapter.notifyItemInserted(0);
                    checkForEmptyRecycler();
                    loadingView.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
                    Log.e("orders", e.getMessage() + e.getCause());
                    loadingView.setVisibility(View.INVISIBLE);
                });
    }

    private void checkForEmptyRecycler() {
        if (orderItemList.isEmpty())
            emptyRecyclerText.setVisibility(View.VISIBLE);
        else emptyRecyclerText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClicked(int position) {
        clickedPosition = position;
        orderViewModel.setOrderItem(orderItemList.get(position));
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        assert getParentFragment() != null;
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId(), fragment)
                .addToBackStack(null)
                .commit();
    }
}
