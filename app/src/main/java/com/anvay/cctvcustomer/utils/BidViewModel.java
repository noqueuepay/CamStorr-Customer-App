package com.anvay.cctvcustomer.utils;

import androidx.lifecycle.ViewModel;

import com.anvay.cctvcustomer.models.Bid;

public class BidViewModel extends ViewModel {

    private Bid acceptedBid;

    public Bid getAcceptedBid() {
        return acceptedBid;
    }

    public void setAcceptedBid(Bid acceptedBid) {
        this.acceptedBid = acceptedBid;
    }
}
