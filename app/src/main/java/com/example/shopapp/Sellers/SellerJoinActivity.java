package com.example.shopapp.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerJoinActivity extends AppCompatActivity {
private EditText nom,nt,mdp,email,adres;
private Button login,join;
private FirebaseAuth auth ;
private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_join);

        auth = FirebaseAuth.getInstance();
        loadingbar =new ProgressDialog(this);
        nom = (EditText) findViewById(R.id.name_seller);
        nt = (EditText) findViewById(R.id.phone_seller);
        mdp = (EditText) findViewById(R.id.password_seller);
        email = (EditText) findViewById(R.id.mail_seller);
        adres = (EditText) findViewById(R.id.address_seller);

        login = (Button) findViewById(R.id.seller_login);
        join = (Button) findViewById(R.id.join_seller_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellerJoinActivity.this,SellerLoginActivity.class);
                startActivity(i);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinSeller();
            }
        });
    }

    private void joinSeller()
    {
        final String name = nom.getText().toString();
        final String phone = nt.getText().toString();
        final String pw = mdp.getText().toString();
        final String mail = email.getText().toString();
        final String address = adres.getText().toString();

        if (name.equals(""))
        {
            Toast.makeText(this, "Veuilleez entrer votre nom", Toast.LENGTH_SHORT).show();
        }
        else if (phone.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer votre numéro de téléphone", Toast.LENGTH_SHORT).show();
        }
        else if (pw.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer votre mot de passe", Toast.LENGTH_SHORT).show();
        }
        else if (mail.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer votre adresse mail", Toast.LENGTH_SHORT).show();
        }
        else if (address.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer l'adresse de votre magasin", Toast.LENGTH_SHORT).show();
        }
        else
        {
            auth.createUserWithEmailAndPassword(mail,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        loadingbar.setTitle("Creation de votre compte vendeur");
                        loadingbar.setMessage("Veuillez patienter pendant la création de votre compte de vendeur ");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();


                        final DatabaseReference sellerRef;
                        sellerRef = FirebaseDatabase.getInstance().getReference();
                        String sid = auth.getCurrentUser().getUid();

                        HashMap<String,Object> sellerMap = new HashMap<>();
                        sellerMap.put("sid",sid);
                        sellerMap.put("phone",phone);
                        sellerMap.put("email",mail);
                        sellerMap.put("password",pw);
                        sellerMap.put("address",address);
                        sellerMap.put("name",name);

                        sellerRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loadingbar.dismiss();
                                        Toast.makeText(SellerJoinActivity.this, "Votre compte vendeur a été créé avec succès", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(SellerJoinActivity.this, SellerLoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                    }
                }
            });
        }

    }
}