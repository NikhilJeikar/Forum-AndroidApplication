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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.haggle.forum.R;


public class SignupPopup extends AppCompatDialogFragment {

    private EditText MailId, Password;
    private TextView textView;
    private Button Signin;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private  FirebaseAuth firebaseAuth;

    private Context context;

    public SignupPopup(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.popup_verify,null);

        MailId = view.findViewById(R.id.edittext1);
        Password = view.findViewById(R.id.edittext2);
        Signin = view.findViewById(R.id.ok_btn);

        textView = view.findViewById(R.id.textview1);
        textView.setText("Sign up");

        builder.setView(view);

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = Password.getText().toString().trim();
                final String mailid = MailId.getText().toString().trim();
                if(password.length() <= 6 || !mailid.matches(emailPattern)){
                    if(password.length() <= 6){
                        Password.setError("Short password");
                    }
                    if(!mailid.matches(emailPattern)){
                        MailId.setError("Invalid Mail");
                    }
                }
                else {
                    run(mailid,password);
                }
            }
        });


        return builder.create();
    }

    private void run(final String  Mail , final String password)
    {

        firebaseAuth.createUserWithEmailAndPassword(Mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    AuthCredential credential = EmailAuthProvider.getCredential(Mail, password);

                    firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dismiss();
                        }
                    });

                }
                else {
                    Toast.makeText(getContext(),"Error encountered",Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}
