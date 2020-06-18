package com.example.shopapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.shopapp.Model.Products;
import com.example.shopapp.Prevalent.Prevalent;
import com.example.shopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity
{
    private FloatingActionButton addToCart;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,productName,productDescription;
    private String productId ="",state="Normal";
    private String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);



    addToCart = (FloatingActionButton) findViewById(R.id.add_product_to_cart);
    numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
    productImage = (ImageView) findViewById(R.id.product_image_details);
    productPrice = (TextView) findViewById(R.id.product_price_details);
    productName = (TextView) findViewById(R.id.product_name_details);
    productDescription = (TextView) findViewById(R.id.product_description_details);
    productId = getIntent().getStringExtra("pid");

    getProductDetails(productId);

    addToCart.setOnClickListener(new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

            if (state.equals("Order Placed") || state.equals("Oder Shipped"))
            {
                Toast.makeText(ProductDetailsActivity.this, "Vous pouvez acheter d'autres produits une fois votre commande précédente livrée.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                addingToCartList();
            }

        }
    });
    }

    @Override
    protected void onStart() {
        super.onStart();

            CheckOrderState();


    }

    private void addingToCartList()
    {
        String date,time;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        date = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a ");
        time = currentDate.format(calForDate.getTime());

        final DatabaseReference carListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap  = new HashMap<>();
        cartMap.put("pid",productId);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",date);
        cartMap.put("time",time);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");

        carListRef.child("User View").child(Prevalent.currentOnlineUser.getUsername())
                .child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    carListRef.child("Admin View ").child(Prevalent.currentOnlineUser.getUsername())
                            .child("Products").child(productId)
                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ProductDetailsActivity.this, "Les produits ont été ajoutés dans votre panier", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                startActivity(i);
                            }
                        }
                    });
                }
            }
        });

        }

    private void getProductDetails(String productId)
    {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    Products product = dataSnapshot.getValue(Products.class);
                    productName.setText(product.getPname());
                    productDescription.setText(product.getDescription());
                    productPrice.setText("Prix :"+product.getPrice()+"DA");
                    Picasso.get().load(product.getImage()).into(productImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void CheckOrderState()
    {



        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getUsername());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("shipped"))
                    {
                        state = "Order Shipped";
                    }
                    else if (shippingState.equals("not shipped"))
                    {

                        state = "Order Placed";

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
