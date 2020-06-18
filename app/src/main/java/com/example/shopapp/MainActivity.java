package com.example.shopapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shopapp.Buyers.HomeActivity;
import com.example.shopapp.Buyers.JoinActivity;
import com.example.shopapp.Buyers.LoginActivity;
import com.example.shopapp.Model.Users;
import com.example.shopapp.Prevalent.Prevalent;
import com.example.shopapp.Sellers.SellerHomeActivity;
import com.example.shopapp.Sellers.SellerJoinActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button join,login,seller;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        join = (Button) findViewById(R.id.join_btn);
        login = (Button) findViewById(R.id.login_btn);
        seller = (Button) findViewById(R.id.seller_btn);
        loadingbar =new ProgressDialog(this);
        Paper.init(this);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, JoinActivity.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SellerJoinActivity.class);
                startActivity(i);
            }
        });

        String username =Paper.book().read(Prevalent.UserName);

        String pass = Paper.book().read(Prevalent.UserPasswordKey);

        if (username != "" && pass !="")
        {
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pass))
            {
                AllowAccess(username,pass);

                loadingbar.setTitle("Déja Authentifié");
                loadingbar.setMessage("Veuillez patienter...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser !=null)
        {
            Intent i = new Intent(MainActivity.this, SellerHomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    private void AllowAccess(final String un, final String pass)
    {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.child("Users").child(un).exists())
                {
                    Users usersData = dataSnapshot.child("Users").child(un).getValue(Users.class);
                    if (usersData.getUsername().equals(un))
                    {
                        if (usersData.getPassword().equals(pass))
                        {
                            loadingbar.dismiss();
                            Intent i = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(i);
                        }

                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Il n'existe aucun compte avec le nom d'utilisateur entré !", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(MainActivity.this, "Vérifiez vos informations ou céez un nouveau compte !", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {



            }
        });
    }
}
