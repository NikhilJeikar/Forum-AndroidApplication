package com.haggle.forum.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.haggle.forum.R;

public class Login extends AppCompatActivity {

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private EditText MailId, Password;
    private FirebaseAuth firebaseAuth;
    private Button Login;
    private TextView Signup , Forget;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        firebaseAuth = FirebaseAuth.getInstance();

        MailId = findViewById(R.id.edittext1);
        Password = findViewById(R.id.edittext2);
        Login = findViewById(R.id.ok_btn);
        Signup =findViewById(R.id.signup);
        Forget = findViewById(R.id.forget);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = Password.getText().toString().trim();
                final String mailid = MailId.getText().toString().trim();
                if(!mailid.matches(emailPattern)){
                    MailId.setError("Invalid Mail");
                }
                else {
                    run(mailid,password);
                }
            }
        });

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), com.haggle.forum.Activities.Signup.class);
                startActivity(intent);
            }
        });

        Forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    private void run(String  Mail , String password)
    {
        firebaseAuth.signInWithEmailAndPassword(Mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(getApplicationContext(),Home.class);
                    startActivity(intent);
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Password.setError("Invalid password");
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    MailId.setError("Incorrect email address");
                }
            }
        });
    }
}
