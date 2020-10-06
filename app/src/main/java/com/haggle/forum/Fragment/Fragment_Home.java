package com.haggle.forum.Fragment;

import android.content.Intent;
import android.nfc.tech.NfcA;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.haggle.forum.Adapter.InList;
import com.haggle.forum.Holder.InListHolder;
import com.haggle.forum.Activities.PublicChatWindow;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.R;

import java.util.ArrayList;



public class Fragment_Home extends Fragment {

    private InList Adapter;
    private ListView lv;
    private ProgressBar progressBar;

    private ArrayList<InListHolder> list;
    private String UID ;
    private long mLastClickTime = 0;

    private FirebaseAuth firebaseAuth;

    private Utils utils;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        utils =new Utils(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Adapter = new InList(getContext(), UID ,getActivity());

        lv = view.findViewById(R.id.list);
        progressBar = view.findViewById(R.id.loading);
        lv.setAdapter(Adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DataBase");
        reference.child("User table").child(UID).child("Notification").setValue(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot snapshot = dataSnapshot.child("User table").child(UID).child("Group").child("list");
                for (DataSnapshot Snapshot :snapshot.getChildren()){
                    String id = Snapshot.getKey();
                    String Name = dataSnapshot.child("Rooms").child(id).child("Meta").child("Name").getValue(String.class);
                    String Stream = dataSnapshot.child("Rooms").child(id).child("Meta").child("Stream").getValue(String.class);
                    String DpID =  dataSnapshot.child("Rooms").child(id).child("Meta").child("Dp").getValue(String.class);

                    InListHolder holder = new InListHolder(Name,id,Stream,DpID);
                    Adapter.Add(holder);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        lv.setSelector(android.R.color.transparent);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(utils.isConnected()){
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(getContext(), PublicChatWindow.class);
                intent.putExtra("Chat ID", Adapter.getItem(position).getRandomId());
                intent.putExtra("Stream",Adapter.getItem(position).getStream());
                intent.putExtra("Pos", position);
                startActivity(intent);
                }
            }
        });

        return view;
    }



}
