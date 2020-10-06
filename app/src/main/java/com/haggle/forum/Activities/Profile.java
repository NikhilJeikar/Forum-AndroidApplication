package com.haggle.forum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.haggle.forum.R;

import java.io.File;

public class Profile extends AppCompatActivity {

    private RelativeLayout layout;
    private  Toolbar toolbar;
    private TextView MailID;
    private TextView Cmail;
    private TextView Cpassword;
    private TextView CChache;
    private TextView AdSense;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");

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

        layout = findViewById(R.id.parent);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ABOVE ,R.id.AdSense);
        params.addRule(RelativeLayout.BELOW, R.id.Signout);

        final TextView textView = new TextView(getApplicationContext());
        textView.setGravity(Gravity.CENTER);
        textView.setText("Verify");
        textView.setTextSize(20);
        textView.setTextColor(ColorStateList.valueOf(Color.parseColor("#979797")));
        textView.setLayoutParams(params);
        if(!firebaseAuth.getCurrentUser().isEmailVerified()){
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    textView.setClickable(false);
                }
            });
        }

        MailID = findViewById(R.id.sub_text_1);
        Cmail = findViewById(R.id.Cmail);
        Cpassword = findViewById(R.id.Cpassorword);

        AdSense = findViewById(R.id.AdSense);

        MailID.setText(firebaseAuth.getCurrentUser().getEmail());

        /*CChache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCache();
            }
        });*/

    }

    public void deleteCache() {
        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
