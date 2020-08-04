package com.example.shopapp.payement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.shopapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetailsActivity extends AppCompatActivity {
  private TextView txtID,txtAmount,txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        txtID = (TextView) findViewById(R.id.details_payment_ID);
        txtAmount = (TextView) findViewById(R.id.details_payment_Amount);
        txtStatus = (TextView) findViewById(R.id.details_payment_Status);

        Intent i = getIntent();

        try {

            JSONObject jsonObject = new JSONObject(i.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"),i.getStringExtra("PaymentAmount"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String paymentAmount)
    {
        try {
            txtID.setText(response.getString("id"));
            txtAmount.setText("DZD"+paymentAmount);
            txtStatus.setText(response.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}