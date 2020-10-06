package com.haggle.forum.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.firebase.auth.FirebaseAuth;

public class Precheck extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;

    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.haggle.forum.R.layout.activity_precheck);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else {
            UID = firebaseAuth.getCurrentUser().getUid();
            firebaseAuth.getCurrentUser().reload();
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        }


    }

}
