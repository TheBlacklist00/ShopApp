package com.example.shopapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.shopapp.Model.AdminOrders;
import com.example.shopapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminOrdersActivity extends AppCompatActivity
{
    private RecyclerView ordersList ;

    private DatabaseReference ordersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.orders_list);

        ordersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override

    protected void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef,AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter
                = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model)
            {
                holder.userName.setText(model.getName());
                holder.userPhone.setText(model.getPhone());
                holder.total.setText("Prix Total : "+model.getTotalAmount());
                holder.datetime.setText("Date : "+model.getDate()+"\n"+"Heure : "+model.getTime());
                holder.adresscity.setText("Adresse : "+model.getAddress()+ "\n" +"Ville : "+model.getCity());

                holder.ShowDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String UID = (String) getRef(position).getKey();

                        Intent intent = new Intent(AdminOrdersActivity.this,AdminUserProductsActivity.class);

                        intent.putExtra("uid",UID);

                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options [] = new CharSequence[]
                                {
                                        "OUI",
                                        "NON"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminOrdersActivity.this);
                        builder.setTitle("Avez vous dèja livré cette commande");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    if (which==0)
                                    {
                                        String UID = (String) getRef(position).getKey();
                                        RemoveOrder(UID);
                                    }
                                    else
                                    {
                                        finish();
                                    }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent,false);
                return new AdminOrdersViewHolder(view);
            }
        };

        ordersList.setAdapter(adapter);

        adapter.startListening();

    }

    private void RemoveOrder(String uid)
    {
        ordersRef.child(uid)
                .removeValue();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder

    {

        public TextView userName,userPhone,total,datetime,adresscity;

        public Button ShowDetails;

        public AdminOrdersViewHolder(@NonNull View itemView) {

            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);

            userPhone = itemView.findViewById(R.id.order_user_phone);

            total = itemView.findViewById(R.id.order_total_price);

            datetime = itemView.findViewById(R.id.order_date_time);

            adresscity = itemView.findViewById(R.id.order_address_city);

            ShowDetails = (Button) itemView.findViewById(R.id.admin_order_details);
        }
    }
}