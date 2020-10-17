package com.example.shopapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shopapp.Interface.ItemClickListner;
import com.example.shopapp.Model.Products;
import com.example.shopapp.R;
import com.example.shopapp.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminApproveActivity extends AppCompatActivity {
private RecyclerView recyclerView;
RecyclerView.LayoutManager layoutManager;
private DatabaseReference unverifiedRef;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approve);

        recyclerView = findViewById(R.id.approve_products);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        unverifiedRef = FirebaseDatabase.getInstance().getReference().child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedRef.orderByChild("productState").equalTo("Not Approved"),Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(String.valueOf(model.getPname()));
                        holder.txtProductDescription.setText(String.valueOf(model.getDescription()));
                        holder.txtProductPrice.setText("Prix :"+ model.getPrice() + "DA");
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               final String productID = model.getPid();

                               CharSequence options[] = new CharSequence[]
                                       {

                                               "Approuver",
                                               "Rejeter"

                                       };
                               AlertDialog.Builder builder = new AlertDialog.Builder(AdminApproveActivity.this);
                               builder.setTitle("Voulez vous approuver la vente de ce produit sur votre application?");
                               builder.setItems(options, new DialogInterface.OnClickListener() {

                                   @Override
                                   public void onClick(DialogInterface dialog, int which)
                                   {
                                       if (which==0)
                                       {
                                           ChangeProductState(productID);
                                       }
                                       if (which==1)
                                       {



                                       }
                                   }
                               });

                               builder.show();
                           }
                       });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

recyclerView.setAdapter(adapter);
adapter.startListening();
}

    private void ChangeProductState(String productID)
    {
        unverifiedRef.child(productID).child("productState").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminApproveActivity.this, "Vous avez approuvé le produit\nIl sera mis en vente pour les acheteurs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}