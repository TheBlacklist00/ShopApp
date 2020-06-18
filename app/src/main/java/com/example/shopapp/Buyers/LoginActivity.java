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
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopapp.Admin.AdminHomeActivity;
import com.example.shopapp.Sellers.SellerCategoryActivity;
import com.example.shopapp.Model.Users;
import com.example.shopapp.Prevalent.Prevalent;
import com.example.shopapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private EditText username,mdp ;
    private TextView AdminLink, NotAdminLink,forgot;
    private ProgressDialog loadingbar;
    private String dbname = "Users";
    private CheckBox check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgot = (TextView) findViewById(R.id.forgot_password);
    login = (Button) findViewById(R.id.login);
    username = (EditText) findViewById(R.id.login_username_input);
    mdp = (EditText) findViewById(R.id.login_password_input);
        loadingbar =new ProgressDialog(this);
        check = (CheckBox) findViewById(R.id.remember_me);
        Paper.init(this);
        AdminLink = (TextView) findViewById(R.id.admin_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_link);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                i.putExtra("check","login");
                startActivity(i);
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                login.setText("Se connecter");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                dbname = "Users";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                login.setText("Se connecter comme Admin");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                dbname = "Admins";
            }
        });

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });
    }

    private void LoginUser()
    {
        String un = username.getText().toString();
        String pass = mdp.getText().toString();
        if(TextUtils.isEmpty(un))
        {
            Toast.makeText(this,"Veuillez entrer votre nom d'utilisateur",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass))
        {
            Toast.makeText(this,"Veuillez entrer votre mot de passe",Toast.LENGTH_SHORT).show();
        }
        else
            {
                loadingbar.setTitle("Connexion à votre compte en cours");
                loadingbar.setMessage("Veuillez patienter pendant l'accès à votre compte");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                AllowAccess(un,pass);
            }
    }

    private void AllowAccess(final String un, final String pass)
    {
        if (check.isChecked())
        {
            Paper.book().write(Prevalent.UserName,un);
            Paper.book().write(Prevalent.UserPasswordKey,pass);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.child(dbname).child(un).exists())
                {
                    Users usersData = dataSnapshot.child(dbname).child(un).getValue(Users.class);
                    if (usersData.getUsername().equals(un))
                    {
                        if (usersData.getPassword().equals(pass))
                        {
                            if (dbname.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Bienvenue Admin", Toast.LENGTH_SHORT).show();

                                loadingbar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                            }
                            else if (dbname.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "Authentifié avec succès", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }

                    }
                }
                else
                    {
                        Toast.makeText(LoginActivity.this, "Il n'existe aucun compte avec le nom d'utilisateur entré !", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                        Toast.makeText(LoginActivity.this, "Vérifiez vos informations ou céez un nouveau compte !", Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {



            }
        });
    }


}
