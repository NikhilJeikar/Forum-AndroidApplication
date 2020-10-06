package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.haggle.forum.R;

public class Signup extends AppCompatActivity {

    private EditText MailId, Password,Confirm;
    private Button Signin;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private  FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        MailId = findViewById(R.id.edittext1);
        Password = findViewById(R.id.edittext2);
        Confirm = findViewById(R.id.edittext3);

        Signin = findViewById(R.id.ok_btn);

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = Password.getText().toString().trim();
                final String mailid = MailId.getText().toString().trim();

                if(password.length() <= 6 || !mailid.matches(emailPattern) || !Password.getText().toString().trim().equals(Confirm.getText().toString().trim())){
                    if(password.length() <= 6){
                        Password.setError("Short password");
                    }
                    if(!mailid.matches(emailPattern)){
                        MailId.setError("Invalid Mail");
                    }
                    if(!Password.getText().toString().trim().equals(Confirm.getText().toString().trim())){
                        Confirm.setError("Incorrect password");
                    }

                }
                else {
                    run(mailid,password);
                }
            }
        });

    }

    private void run(final String  Mail , final String password)
    {
        AuthCredential credential = EmailAuthProvider.getCredential(Mail,password);
        firebaseAuth.createUserWithEmailAndPassword(Mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(getApplicationContext(),Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Error Encountered" + task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

                }
                else {
                    Toast.makeText(getApplicationContext(),"Error encountered 1"+ task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
