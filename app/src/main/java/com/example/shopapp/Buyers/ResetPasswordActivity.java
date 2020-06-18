package com.example.shopapp.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopapp.Prevalent.Prevalent;
import com.example.shopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {
private String check = "";
private TextView title,questions;
private EditText userName,qst1,qst2;
private Button verifier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");
        title = (TextView) findViewById(R.id.page_title);
        userName = (EditText) findViewById(R.id.security_id);
        qst1 = (EditText) findViewById(R.id.question1);
        qst2 = (EditText) findViewById(R.id.question2);
        questions = (TextView) findViewById(R.id.listes_questions);
        verifier = (Button) findViewById(R.id.verify_button);
    }

    @Override
    protected void onStart() {
        super.onStart();

        userName.setVisibility(View.GONE);


        if (check.equals("settings"))
        {
            title.setText("Modifier les questions");

            questions.setText("Veuillez Répondre au questions de sécurité suivantes");


            verifier.setText("Modifier");

            displayAnswers();

            verifier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

            setAnswers();


                }
            });
        }
        else  if (check.equals("login"))
        {
            userName.setVisibility(View.VISIBLE);

            verifier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });
        }
    }

    private void verifyUser()
    {
        final String un = userName.getText().toString();
        final String reponse1 = qst1.getText().toString().toLowerCase();
        final String reponse2 = qst2.getText().toString().toLowerCase();

        if (un.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer votre nom d'utilisateur", Toast.LENGTH_SHORT).show();
        }
        else
        if (reponse1.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer la réponse de la première question", Toast.LENGTH_SHORT).show();
        }
        else
        if (reponse2.equals(""))
        {
            Toast.makeText(this, "Veuillez entrer la réponse de la deuxième question", Toast.LENGTH_SHORT).show();
        }
        else{

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(un);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                    {
                        String mun = dataSnapshot.child("username").getValue().toString();
                        if (dataSnapshot.hasChild("Security Questions"))
                        {
                            String answer1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String answer2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                            if(!answer1.equals(reponse1))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "La réponse 1 est invalide", Toast.LENGTH_SHORT).show();
                            }
                            else if(!answer2.equals(reponse2))
                            {
                                Toast.makeText(ResetPasswordActivity.this, "La réponse 2 est invalide", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("Nouveau mot de passe");

                                final EditText newpassword = new EditText(ResetPasswordActivity.this);
                                newpassword.setHint("Veuillez entrer votre nouveau mot de passe ici");
                                builder.setView(newpassword);

                                builder.setPositiveButton("Changer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (!newpassword.getText().toString().equals(""))
                                        {
                                            ref.child("password").setValue(newpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(ResetPasswordActivity.this, "Votre mot de passe a été changé avec succès", Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                                        startActivity(i);
                                                    }
                                                }
                                            });
                                        }


                                    }

                                });
                                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder.show();
                            }
                        }
                        else
                        {
                            Toast.makeText(ResetPasswordActivity.this, "Vous n'avez pas modifier les questions de sécurité.\n Veuillez contacter le service client", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Il n'éxiste aucun utilisateur avec ce nom d'utilisateur", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }

    private void setAnswers()
    {
        String reponse1 = qst1.getText().toString().toLowerCase();
        String reponse2 = qst2.getText().toString().toLowerCase();

        if (qst1.equals("") || qst2.equals(""))
        {
            Toast.makeText(ResetPasswordActivity.this, "Veuillez répondre aux deux questions", Toast.LENGTH_SHORT).show();
        }
        else
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(Prevalent.currentOnlineUser.getUsername());
            HashMap<String,Object> userdataMap = new HashMap<>();

            userdataMap.put("answer1",reponse1);

            userdataMap.put("answer2",reponse2);

            ref.child("Security Questions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ResetPasswordActivity.this, "Les réponses à vos questions de sécurité on été mise à jour avec succès", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
    private void displayAnswers()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getUsername());
        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String answer1 = dataSnapshot.child("answer1").getValue().toString();
                    String answer2 = dataSnapshot.child("answer2").getValue().toString();

                    qst1.setText(answer1);
                    qst2.setText(answer2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}