package com.example.shopapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopapp.Prevalent.Prevalent;
import com.example.shopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity
{
    private EditText name , phone , address ,  city;
    private Button confirm;
    private String Total = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        Total = getIntent().getStringExtra("Total Price");

        confirm =(Button) findViewById(R.id.confirm_order);
        name= (EditText) findViewById(R.id.order_name);
        phone = (EditText) findViewById(R.id.order_phone);
        address = (EditText) findViewById(R.id.order_address);
        city = (EditText) findViewById(R.id.order_city);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });
    }

    private void Check()
    {
        if (TextUtils.isEmpty(name.getText().toString()))
        {
            Toast.makeText(this, "Veuillez entrer votre nom COMPLET", Toast.LENGTH_SHORT).show();
        }
        else        if (TextUtils.isEmpty(phone.getText().toString()))
        {
            Toast.makeText(this, "Veuillez entrer votre numéro de téléphone", Toast.LENGTH_SHORT).show();
        }
        else        if (TextUtils.isEmpty(address.getText().toString()))
        {
            Toast.makeText(this, "Veuillez entrer votre adresse de domicile", Toast.LENGTH_SHORT).show();
        }
        else        if (TextUtils.isEmpty(city.getText().toString()))
        {
            Toast.makeText(this, "Veuillez entrer le nom de la ville ou vous résidez", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder()
    {
        String date,time;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        date = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a ");
        time = currentDate.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getUsername());
        HashMap<String,Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount",Total);
        ordersMap.put("name",name.getText().toString());
        ordersMap.put("phone",phone.getText().toString());
        ordersMap.put("address",address.getText().toString());
        ordersMap.put("city",city.getText().toString());
        ordersMap.put("date",date);
        ordersMap.put("time",time);
        ordersMap.put("state","not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                 FirebaseDatabase.getInstance().getReference().child("Cart List")
                         .child("User View")
                         .child(Prevalent.currentOnlineUser.getUsername())
                         .removeValue()
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(ConfirmFinalOrderActivity.this, "Votre commande a bien été soumise avec succès", Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                             }
                         });
            }
        });
    }
}