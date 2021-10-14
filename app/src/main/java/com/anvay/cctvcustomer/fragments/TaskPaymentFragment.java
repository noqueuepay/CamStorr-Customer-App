package com.anvay.cctvcustomer.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anvay.cctvcustomer.MainActivity;
import com.anvay.cctvcustomer.PaymentActivity;
import com.anvay.cctvcustomer.R;
import com.anvay.cctvcustomer.adapters.DateTimeSelectorAdapter;
import com.anvay.cctvcustomer.databinding.FragmentTaskPaymentBinding;
import com.anvay.cctvcustomer.models.Bid;
import com.anvay.cctvcustomer.models.Date;
import com.anvay.cctvcustomer.models.OngoingTask;
import com.anvay.cctvcustomer.models.Task;
import com.anvay.cctvcustomer.utils.BidViewModel;
import com.anvay.cctvcustomer.utils.Constants;
import com.anvay.cctvcustomer.utils.TaskViewModel;
import com.anvay.cctvcustomer.utils.TitleViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class TaskPaymentFragment extends Fragment implements DateTimeSelectorAdapter.DateTimeAdapterListener {
    private int lastSelectedPosition = 0;
    private List<Date> dateList;
    private DateTimeSelectorAdapter adapter;
    private Task task;
    private Bid acceptedBid;
    private String address, orderId;
    private View loadingView;
    private TaskViewModel taskViewModel;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        String title = "Payment";
        TitleViewModel titleViewModel = new ViewModelProvider(requireActivity()).get(TitleViewModel.class);
        titleViewModel.addToTitleStack(title);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentTaskPaymentBinding binding = FragmentTaskPaymentBinding.inflate(inflater, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        EditText addressDisplay = binding.addressDisplay;
        Checkout.preload(requireActivity().getApplicationContext());
        RecyclerView dateSelectorRecycler = binding.dateSelectorRecycler;
        dateList = new ArrayList<>();
        getDateList();
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        task = taskViewModel.getPostedTask().getValue();
        CheckBox copyProfileAddress = binding.copyProfileAddress;
        addressDisplay.setText(MainActivity.userAddress);
        copyProfileAddress.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b)
                addressDisplay.setText(MainActivity.userAddress);
            else addressDisplay.setText(null);
        });
        dateList.get(0).setSelected(true);
        adapter = new DateTimeSelectorAdapter(dateList, this);
        dateSelectorRecycler.setLayoutManager(layoutManager);
        dateSelectorRecycler.setAdapter(adapter);
        ActivityResultLauncher<Intent> paymentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        assert intent != null;
                        String tid = intent.getExtras().getString(Constants.KEY_TRANSACTION_NUMBER);
                        postData(tid);
                    }
                }
        );
        binding.paymentButton.setOnClickListener(view -> {
            address = addressDisplay.getText().toString();
            if (address.isEmpty())
                Toast.makeText(getContext(), "Fill Address", Toast.LENGTH_SHORT).show();
            else {
                Intent i = new Intent(getContext(), PaymentActivity.class);
                i.putExtra(Constants.KEY_AMOUNT_TO_PAY, acceptedBid.getPaymentAmount());
                paymentLauncher.launch(i);
            }
        });
        loadingView = binding.loadingLayout.getRoot();
        Random rnd = new Random();
        orderId = String.valueOf(100000 + rnd.nextInt(900000));
        BidViewModel bidViewModel = new ViewModelProvider(requireActivity()).get(BidViewModel.class);
        acceptedBid = bidViewModel.getAcceptedBid();
        if (acceptedBid != null) {
            binding.orderIdDisplay.setText(orderId);
            binding.storeNameDisplay.setText(acceptedBid.getStoreName());
            binding.totalCostDisplay.setText("\u20B9 " + acceptedBid.getBidAmount());
            Picasso.get()
                    .load(acceptedBid.getImageUrl())
                    .placeholder(R.drawable.loading_bar)
                    .into(binding.storeImageDisplay);
        }
        return binding.getRoot();
    }

    private void postData(String transactionId) {
        if (task == null || acceptedBid == null)
            return;
        loadingView.setVisibility(View.VISIBLE);
        task.setBids(null);
        OngoingTask ongoingTask = new OngoingTask(task, acceptedBid.getBidAmount(), address,
                dateList.get(lastSelectedPosition).getDate(), acceptedBid.getStoreId(),
                acceptedBid.getStoreName(), orderId, transactionId);
        taskViewModel.setOngoingTask(ongoingTask);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.BASE_ONGOING_TASKS_URL)
                .document()
                .set(ongoingTask)
                .addOnSuccessListener(unused -> {
                    db.collection(Constants.BASE_TASKS_URL)
                            .document(task.getTaskId())
                            .delete()
                            .addOnSuccessListener(unused1 -> {
                                loadingView.setVisibility(View.INVISIBLE);
                                launchFragment();
                            })
                            .addOnFailureListener(e -> {
                                loadingView.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "Transaction Failed. Contact Customer Care", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    loadingView.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Failed. Contact Customer Care", Toast.LENGTH_SHORT).show();
                });
    }

    private void launchFragment() {
        ReceiptFragment fragment = new ReceiptFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        assert getParentFragment() != null;
        int count = fragmentManager.getBackStackEntryCount();
        while (count-- >= 1)
            fragmentManager.popBackStack();
        fragmentManager.beginTransaction()
                .add(getParentFragment().requireView().findViewById(R.id.fragment_container).getId()
                        , fragment)
                .addToBackStack("customerDetails")
                .commit();
    }

    private void getDateList() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.DATE, 3);
        for (int i = 0; i < 7; i++) {
            String date = df.format(calendar.getTime());
            String day = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
            dateList.add(new Date(date, day, false));
            calendar.add(Calendar.DATE, 1);
        }
    }

    private String getDayOfWeek(int d) {
        switch (d) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            default:
                return "Saturday";
        }
    }

    @Override
    public void onItemClicked(int position) {
        dateList.get(position).setSelected(true);
        if (lastSelectedPosition != position) {
            dateList.get(lastSelectedPosition).setSelected(false);
            adapter.notifyItemChanged(lastSelectedPosition);
            lastSelectedPosition = position;
        }
        adapter.notifyItemChanged(position);
    }
}
