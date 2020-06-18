package com.example.shopapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopapp.MainActivity;
import com.example.shopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class JoinActivity extends AppCompatActivity {

    private Button join;
    private EditText nom,usernam,mdp;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        join = (Button) findViewById(R.id.join);
        nom = (EditText) findViewById(R.id.join_name_input);
        usernam = (EditText) findViewById(R.id.join_username_input);
        mdp = (EditText) findViewById(R.id.join_password_input);

        loadingbar =new ProgressDialog(this);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateAccount();
            }
        });
    }

    private void CreateAccount()
    {
        String name = nom.getText().toString();
        String username = usernam.getText().toString();
        String pass = mdp.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Veuillez entrer votre email",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass))
        {
        Toast.makeText(this,"Veuillez entrer votre mot de passe",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this,"Veuillez entrer votre nom",Toast.LENGTH_SHORT).show();
        }
        else
            {
                loadingbar.setTitle("Creation de votre compte en cours");
                loadingbar.setMessage("Veuillez patienter pendant la création de votre compte ");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                ValidateEmail(name,username,pass);
            }
    }

    private void ValidateEmail(final String name, final String username, final String pass)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!(dataSnapshot.child("Users").child(username).exists()))
                {
                    HashMap<String,Object> userdataMap = new HashMap<>();

                    userdataMap.put("username",username);

                    userdataMap.put("password",pass);

                    userdataMap.put("name",name);

                    RootRef.child("Users").child(username).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(JoinActivity.this,"Félicitations,votre compte a été créé avec succès.",Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent i = new Intent(JoinActivity.this,LoginActivity.class);
                                startActivity(i);
                            }
                            else
                                {
                                    loadingbar.dismiss();
                                    Toast.makeText(JoinActivity.this,"Il y a eu un problème lors de la création de votre compte.Veuillez réessayer.",Toast.LENGTH_SHORT).show();
                                }

                        }
                    });

                }
                else
                    {
                        Toast.makeText(JoinActivity.this,"Ce nom d'utilisateur éxiste déja !",Toast.LENGTH_SHORT).show();

                        loadingbar.dismiss();

                        Toast.makeText(JoinActivity.this,"Veuillez réessayer avec une autre adresse.",Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(JoinActivity.this, MainActivity.class);

                        startActivity(i);

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


}
