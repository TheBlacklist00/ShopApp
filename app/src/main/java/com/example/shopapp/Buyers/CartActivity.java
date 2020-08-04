package com.example.shopapp.Buyers;

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
import android.widget.Toast;

import com.example.shopapp.Model.Cart;
import com.example.shopapp.Prevalent.Prevalent;
import com.example.shopapp.R;
import com.example.shopapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button next ;
    private TextView total,msg;

    public int  overTotalPrice = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        next = (Button) findViewById(R.id.suivant);
        total = (TextView) findViewById(R.id.total_price);
        msg = (TextView) findViewById(R.id.msg1);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                i.putExtra("Total Price",String.valueOf(overTotalPrice));
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        CheckOrderState();

final DatabaseReference cartListRef = FirebaseDatabase .getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getUsername()).child("Products"),Cart.class).build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                        holder.txtProductQuantity.setText("Quantité :"+model.getQuantity());
                    holder.txtProductPrice.setText("Prix unitaire :"+model.getPrice()+"$");
                    holder.txtProductName.setText(model.getPname());

                int oneTypeProductTPrice=(Integer.parseInt(model.getPrice().replaceAll("\\D+",""))) * Integer.parseInt(model.getQuantity());

                overTotalPrice = overTotalPrice + oneTypeProductTPrice;

                total.setText("Prix Total :"+String.valueOf(overTotalPrice)+"DA");



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        CharSequence options[] = new CharSequence[]
                                {
                                   "Modifier",
                                        "Retirer"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                 if(which==0)
                                 {
                                     Intent i = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                     i.putExtra("pid",model.getPid());
                                     startActivity(i);
                                 }
                                 if (which==1)
                                 {
                                     cartListRef.child("User View").child(Prevalent.currentOnlineUser.getUsername()).child("Products")
                                             .child(model.getPid())
                                             .removeValue()
                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     if(task.isSuccessful())
                                                     {
                                                         Toast.makeText(CartActivity.this, "L'élément a été retiré de votre panier avec succès", Toast.LENGTH_SHORT).show();
                                                         Intent i = new Intent(CartActivity.this, HomeActivity.class);
                                                         startActivity(i);
                                                     }
                                                 }
                                             });
                                 }
                            }
                        });

                        builder.show();

                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                        total.setText("Statut de la commande : livrée ");
                        recyclerView.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        msg.setText("Félicitations\\nVotre commande a été livré avec succès\\nVOUS LA RECEVREZ BIENTOT");
                        next.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Vous pouvez acheter d'autres produits une fois votre précédente commande livrée.", Toast.LENGTH_SHORT).show();
                    }
                    else if (shippingState.equals("not shipped"))
                    {

                        total.setText("Statut de la commande : Pas encore livrée");
                        recyclerView.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        msg.setText("Votre commande n'a pas encore été livré \nElle le sera très bientot.");
                        next.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "Vous pouvez acheter d'autres produits une fois votre précédente commande livrée.", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        overTotalPrice = 0;
    }
}
