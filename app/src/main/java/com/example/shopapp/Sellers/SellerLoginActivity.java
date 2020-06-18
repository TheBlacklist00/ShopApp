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

public class SellerLoginActivity extends AppCompatActivity {
    private EditText email,mdp;
    private Button login;
    private ProgressDialog loadingbar;
    private FirebaseAuth auth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
    email = (EditText) findViewById(R.id.login_mail_seller);
    mdp = (EditText) findViewById(R.id.login_password_seller);
    login = (Button) findViewById(R.id.login_seller_button);
        auth = FirebaseAuth.getInstance();

        loadingbar =new ProgressDialog(this);

    login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loginSeller();
        }
    });

    }

    private void loginSeller()
    {
        final String password = mdp.getText().toString();
        final String mail = email.getText().toString();

        if (password.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer votre mot de passe", Toast.LENGTH_SHORT).show();
        }
        else if (mail.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer votre adresse mail", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Connexion à votre compte en cours");
            loadingbar.setMessage("Veuillez patienter pendant la connexion à votre compte vendeur");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful())
               {
                   Intent i = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                   i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   startActivity(i);
                   finish();
               }
               else
               {
                   Toast.makeText(SellerLoginActivity.this, "Il n'existe aucun compte avec le nom d'utilisateur entré !", Toast.LENGTH_SHORT).show();
                   loadingbar.dismiss();
                   Toast.makeText(SellerLoginActivity.this, "Vérifiez vos informations ou céez un nouveau compte !", Toast.LENGTH_SHORT).show();
               }
                }
            });

        }
    }
}