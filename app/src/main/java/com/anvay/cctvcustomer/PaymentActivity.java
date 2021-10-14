package com.anvay.cctvcustomer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anvay.cctvcustomer.utils.Constants;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent i = getIntent();
        if (!i.hasExtra(Constants.KEY_AMOUNT_TO_PAY)) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        } else startPayment(i.getExtras().getString(Constants.KEY_AMOUNT_TO_PAY));
    }

    public void startPayment(String amount) {
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.icon_app);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Anvay Technosolutions Pvt. Ltd");
            options.put("currency", "INR");
            options.put("amount", amount);
            options.put("prefill.email", "p.test@example.com");
            options.put("prefill.name", MainActivity.userName);
            options.put("prefill.contact", MainActivity.mobileNumber);
            options.put("theme.color", getResources().getColor(R.color.colorPrimary));
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("error", "Error in starting Razorpay Checkout", e);
            Toast.makeText(this, "Payment failed due to error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String transactionId) {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_TRANSACTION_NUMBER, transactionId);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("payment error", s);
        if (i == Checkout.NETWORK_ERROR) {
            Log.e("error payment failed", "Checkout.NETWORK_ERROR");
            Toast.makeText(this, "Poor Network, Payment Failed", Toast.LENGTH_SHORT).show();
        }
        if (i == Checkout.INVALID_OPTIONS) {
            Log.e("error payment failed", "Checkout.INVALID_OPTIONS");
            Toast.makeText(this, "Payment Failed, Invalid Options", Toast.LENGTH_SHORT).show();

        }
        if (i == Checkout.PAYMENT_CANCELED) {
            Log.e("error payment failed", "Checkout.PAYMENT_CANCELED");
            Toast.makeText(this, "Payment Canceled by user", Toast.LENGTH_SHORT).show();

        }
        if (i == Checkout.TLS_ERROR) {
            Log.e("error payment failed", "Checkout.TLS_ERROR");
            Toast.makeText(this, "Payment Not supported", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}