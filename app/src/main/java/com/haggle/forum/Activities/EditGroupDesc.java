package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haggle.forum.R;

public class EditGroupDesc extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText editText;
    private Button Save;

    private String ChatID;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_desc);

        ChatID = getIntent().getStringExtra("ChatId");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit topic");

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

        editText = findViewById(R.id.edittext);
        Save = findViewById(R.id.SaveButton);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("DataBase").child("Rooms").child(ChatID).child("Meta").child("Desc").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editText.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("DataBase").child("Rooms").child(ChatID).child("Meta").child("Desc").setValue(editText.getText().toString().trim());
                onBackPressed();
            }
        });
    }
}
