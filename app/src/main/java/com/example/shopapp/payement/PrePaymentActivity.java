package com.example.shopapp.payement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopapp.Prevalent.Prevalent;
import com.example.shopapp.R;
import com.example.shopapp.config.Config;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class PrePaymentActivity extends AppCompatActivity
{
    public static final int PAYPAL_REQUEST_CODE = 7171;

    public static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    private Button PayNow;
    private TextView Amount;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepayment);

        Intent i = new Intent(this,PayPalService.class);
        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(i);


        PayNow = (Button) findViewById(R.id.payement);
        Amount = (TextView) findViewById(R.id.display_total_price_payement);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getUsername());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String total = snapshot.child("totalAmount").getValue().toString();
                Amount.setText(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        PayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayement();
            }
        });
    }

    private void processPayement()
    {
        Amount.getText().toString();
        PayPalPayment payPalPayment =  new PayPalPayment(new BigDecimal(String.valueOf(Amount)),"DZD","Payer Shop+",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent i = new Intent(PrePaymentActivity.this,PaymentActivity.class);
        i.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        i.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(i,PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation !=null)
                {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this,PaymentDetailsActivity.class)
                        .putExtra("PaymentDetails",paymentDetails)
                                .putExtra("PaymentAmount", (Parcelable) Amount)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            else if (resultCode== Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Annuler", Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultCode==PaymentActivity.RESULT_EXTRAS_INVALID)
        {
            Toast.makeText(this, "Invalide", Toast.LENGTH_SHORT).show();
        }
    }
}