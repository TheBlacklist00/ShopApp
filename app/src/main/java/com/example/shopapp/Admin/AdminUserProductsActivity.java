package com.example.shopapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shopapp.Model.Cart;
import com.example.shopapp.R;
import com.example.shopapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProductsActivity extends AppCompatActivity {

    private RecyclerView productsList = findViewById(R.id.products_list);

    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

    private DatabaseReference cartRef;

    String ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_user_products);

        ID = getIntent().getStringExtra("uid");


        productsList.setLayoutManager(layoutManager);

        cartRef = FirebaseDatabase
                .getInstance()

                .getReference()

                .child("Cart List")

                .child("Admin View")

                .child(ID)

                .child("Products");

    }

    @Override

    protected void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Cart> options

                = new FirebaseRecyclerOptions.Builder<Cart>()

                .setQuery(cartRef,Cart.class)

                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

                holder.txtProductQuantity.setText("Quantité :"+model.getQuantity());

                holder.txtProductPrice.setText("Prix unitaire :"+model.getPrice()+"DA");

                holder.txtProductName.setText(model.getPname());

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        productsList.setAdapter(adapter);

        adapter.startListening();
    }
}