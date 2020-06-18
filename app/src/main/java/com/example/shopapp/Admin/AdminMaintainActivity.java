package com.example.shopapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shopapp.R;
import com.example.shopapp.Sellers.SellerCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainActivity extends AppCompatActivity {

    private Button changes,delete;
    private EditText nom,description,prix;
    private ImageView imageView;
    private String productId ="";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain);

        changes = findViewById(R.id.maintain_button);
        nom = findViewById(R.id.maintain_name);
        description = findViewById(R.id.maintain_description);
        prix = findViewById(R.id.maintain_price);
        imageView = findViewById(R.id.maintain_image);
        productId = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);
        delete = findViewById(R.id.delete_button);

        displayCurrentInfos();

        changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });

    }

    private void deleteThisProduct()
    {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent i = new Intent(AdminMaintainActivity.this, SellerCategoryActivity.class);
                startActivity(i);
                Toast.makeText(AdminMaintainActivity.this, "Ce produit a été supprimé avec succès", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyChanges()
    {
        String pName = nom.getText().toString();
        String pDescription = description.getText().toString();
        String pPrice = prix.getText().toString();

        if (pName.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer le nom du produit", Toast.LENGTH_SHORT).show();
        }
        else if (pDescription.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer la déscription du produit", Toast.LENGTH_SHORT).show();
        }
        else if (pPrice.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer le prix du produit", Toast.LENGTH_SHORT).show();
        }
        else
            {
                HashMap<String, Object> productMap = new HashMap<>();
                productMap.put("pid", productId);
                productMap.put("description", pDescription);
                productMap.put("price", pPrice);
                productMap.put("pname", pName);

                productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(AdminMaintainActivity.this, "Changements appliqués avec succès", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminMaintainActivity.this, SellerCategoryActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
    }

    private void displayCurrentInfos()
    {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();

                    nom.setText(pName);
                    description.setText(pDescription);
                    prix.setText(pPrice);
                    Picasso.get().load(pImage).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}