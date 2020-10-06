package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.haggle.forum.Fragment.Fragment_Feed;
import com.haggle.forum.Fragment.Fragment_Home;
import com.haggle.forum.Fragment.Fragment_InAppNotification;
import com.haggle.forum.Popup.GroupPopup;
import com.haggle.forum.R;

public class Home extends AppCompatActivity {

    private  Toolbar toolbar;
    private ImageButton imageButton;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 125);
        }
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 125);
        }
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Forums");

        setSupportActionBar(toolbar);

        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;

        Toolbar.LayoutParams layoutParams1 = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.RIGHT;
        layoutParams1.setMarginEnd(50);

        imageButton2 = new ImageButton(getApplicationContext());
        imageButton2.setBackgroundResource(R.drawable.button_settings);
        imageButton2.setPadding(25,25,25,0);
        imageButton2.setLayoutParams(layoutParams);

        imageButton1 = new ImageButton(getApplicationContext());
        imageButton1.setBackgroundResource(R.drawable.button_join);
        imageButton1.setPadding(25,25,25,0);
        imageButton1.setLayoutParams(layoutParams);

        imageButton = new ImageButton(getApplicationContext());
        imageButton.setBackgroundResource(R.drawable.button_search);
        imageButton.setPadding(25,25,25,0);
        imageButton.setLayoutParams(layoutParams1);

        final Fragment[] fragment = {null};
        final Class[] fragmentclass = {null};
        final FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.e_feed:

                        fragmentclass[0] = Fragment_Home.class;

                        try {
                            fragment[0] = (Fragment) fragmentclass[0].newInstance();
                        }
                        catch (Exception e){

                        }
                        try {
                            toolbar.removeView(imageButton2);
                        }
                        catch (Exception e){

                        }
                        toolbar.removeView(imageButton);
                        toolbar.removeView(imageButton1);
                        toolbar.addView(imageButton2);

                        fragmentManager.beginTransaction().replace(R.id.mainpage_activity_fragment, fragment[0]).commit();

                        return true;
                    case R.id.feed:
                        fragmentclass[0] = Fragment_Feed.class;

                        try {
                            fragment[0] = (Fragment) fragmentclass[0].newInstance();
                        }
                        catch (Exception e){

                        }

                        fragmentManager.beginTransaction().replace(R.id.mainpage_activity_fragment, fragment[0]).commit();

                        try{
                            toolbar.removeView(imageButton);
                            toolbar.removeView(imageButton1);
                        }
                        catch (Exception e){

                        }
                        toolbar.removeView(imageButton2);
                        toolbar.addView(imageButton1);
                        toolbar.addView(imageButton);


                        return true;
                    case R.id.settings:
                        fragmentclass[0] = Fragment_InAppNotification.class;

                        try {
                            fragment[0] = (Fragment) fragmentclass[0].newInstance();
                        }
                        catch (Exception e){

                        }

                        fragmentManager.beginTransaction().replace(R.id.mainpage_activity_fragment, fragment[0]).commit();
                        toolbar.removeView(imageButton);
                        toolbar.removeView(imageButton2);
                        toolbar.removeView(imageButton1);
                        return true;
                }
                return false;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), Search.class);
                startActivity(intent);


            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupPopup group = new GroupPopup(getSupportFragmentManager());
                group.show(getSupportFragmentManager(), "GroupManager");

            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        fragmentclass[0] = Fragment_Home.class;

        try {
            fragment[0] = (Fragment) fragmentclass[0].newInstance();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.mainpage_activity_fragment, fragment[0]).commit();

        toolbar.removeView(imageButton);
        toolbar.removeView(imageButton1);
        toolbar.addView(imageButton2);

        FirebaseFunctions.getInstance() // Optional region: .getInstance("europe-west1")
                .getHttpsCallable("myCoolFunction")
                .call()
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
            @Override
            public void onSuccess(HttpsCallableResult httpsCallableResult) {

            }
        });


    }

}
