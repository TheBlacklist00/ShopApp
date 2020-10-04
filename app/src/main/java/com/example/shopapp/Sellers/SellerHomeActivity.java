package com.example.shopapp.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shopapp.Admin.AdminApproveActivity;
import com.example.shopapp.MainActivity;
import com.example.shopapp.Model.Products;
import com.example.shopapp.R;
import com.example.shopapp.ViewHolder.ProductViewHolder;
import com.example.shopapp.ViewHolder.SellerProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerHomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedRef;
private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemReselectedListene
        = new BottomNavigationView.OnNavigationItemSelectedListener()
{
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.navigation_seller_home:
                Intent intent2 = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);
                startActivity(intent2);
                return true;
            case R.id.navigation_seller_add:

                Intent intent = new Intent(SellerHomeActivity.this, SellerCategoryActivity.class);
                startActivity(intent);


            return true;

            case R.id.navigation_seller_logout:

                final FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.signOut();

                Intent i = new Intent(SellerHomeActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            return true;

        }
        return false;
    }

};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) mOnNavigationItemReselectedListene);
        recyclerView = findViewById(R.id.seller_approve_products);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        unverifiedRef = FirebaseDatabase.getInstance().getReference().child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, SellerProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, SellerProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellerProductViewHolder holder, int position, @NonNull final Products model) {
                        holder.txtProductName.setText(String.valueOf(model.getPname()));
                        holder.txtProductDescription.setText(String.valueOf(model.getDescription()));
                        holder.txtProductPrice.setText("Prix :"+ model.getPrice() + "DA");
                        if (model.getProductState().equals("Approved"))
                        {holder.txtProductStatus.setText("Statut du produit :"+ " "+"approuvé");
                        }
                        else
                        {
                            holder.txtProductStatus.setText("Statut du produit :"+" "+"ce produit n'a pas encore été approuvé");
                        }
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String productID = model.getPid();

                                CharSequence options[] = new CharSequence[]
                                        {

                                                "OUI",
                                                "NON"

                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                                builder.setTitle("Voulez vous supprimer ce produit ?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        if (which==0)
                                        {
                                            CheckAndMaintainProductState(productID);
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
                    public SellerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view,parent,false);
                        SellerProductViewHolder holder = new SellerProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckAndMaintainProductState(String productID)
    {
        unverifiedRef.child(productID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SellerHomeActivity.this, "Ce produit a bien été supprimé avec succès", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }
