package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haggle.forum.Adapter.SearchAdapter;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.Popup.ConformationPopup;
import com.haggle.forum.R;

import java.util.Objects;

public class Search extends AppCompatActivity {

    private  Toolbar toolbar;

    private String Id ;
    private String UID;

    private FirebaseAuth firebaseAuth;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        utils = new Utils(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.button_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 onBackPressed();
            }
        });

        final EditText search = findViewById(R.id.Search);
        final SearchAdapter adapter = new SearchAdapter(getApplicationContext());
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        final DatabaseReference db;

        db = FirebaseDatabase.getInstance().getReference("DataBase").child("Public List");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    adapter.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter.notifyDataSetChanged();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                final String string = adapter.getItem(position);

                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Id = dataSnapshot.child(string).getValue(String.class);

                        search.setText(string);
                        Conformation(string,Id);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void Conformation(String Text , String Id ){
        ConformationPopup popup = new ConformationPopup(Text,Id,null,null);
        popup.show(getSupportFragmentManager(),"Confirmation");
    }
}
