package com.haggle.forum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.haggle.forum.R;

public class Settings extends AppCompatActivity {
    private TextView Profile;
    private TextView Signout;
    private TextView AboutUs;
    private TextView Help;
    private TextView Invite;
    private Toolbar toolbar;

    private static final int REQUEST_INVITE = 100;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationIcon(R.drawable.button_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        Profile = findViewById(R.id.login);
        Signout = findViewById(R.id.Signout);
        AboutUs = findViewById(R.id.AboutUs);
        Help = findViewById(R.id.Help);
        Invite = findViewById(R.id.Invite);


        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });

        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Help.class);
                startActivity(intent);
            }
        });

        AboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AboutUs.class);
                startActivityForResult(intent, REQUEST_INVITE);
            }
        });

        Invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = generateContentLink();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, link.toString());

                startActivity(Intent.createChooser(intent, "Share Link"));

            }
        });

        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), Precheck.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finishAndRemoveTask();

            }
        });

    }
    public static Uri generateContentLink () {
        Uri baseUrl = Uri.parse("https://your-custom-name.page.link");
        String domain = "https://your-app.page.link";

        DynamicLink link = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(baseUrl)
                .setDomainUriPrefix(domain)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.your.packageName").build())
                .buildDynamicLink();

        return link.getUri();
    }

}


