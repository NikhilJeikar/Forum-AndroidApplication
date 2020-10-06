package com.haggle.forum.Popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.haggle.forum.R;

public class SigninPopup extends AppCompatDialogFragment {
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private EditText MailId, Password;
    private TextView textView;
    private FirebaseAuth firebaseAuth;
    private Button Signin;
    private DatabaseReference reference;
    private Context context;
    private AlertDialog.Builder builder;
    private Dialog dialog;

    public SigninPopup(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.popup_verify,null);

        MailId = view.findViewById(R.id.edittext1);
        Password = view.findViewById(R.id.edittext2);
        Signin = view.findViewById(R.id.ok_btn);

        textView = view.findViewById(R.id.textview1);
        textView.setText("Login");

        builder.setView(view);

        Signin.setOnClickListener(new View.OnClickListener() {
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

        dialog =  builder.create();

        return dialog;
    }

    private void run(String  Mail , String password)
    {
        dialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(Mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful()){
                dismiss();
                }
            else {
                dialog.setCanceledOnTouchOutside(true);
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
