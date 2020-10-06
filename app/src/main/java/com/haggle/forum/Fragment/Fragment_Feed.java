package com.haggle.forum.Fragment;

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
import com.haggle.forum.Adapter.FeedList;
import com.haggle.forum.Holder.FeedListHolder;
import com.haggle.forum.Popup.ConformationPopup;
import com.haggle.forum.R;

import java.util.ArrayList;
import java.util.Random;


public class Fragment_Feed extends Fragment {

    private FeedList Adapter;
    private ListView lv;
    private ProgressBar progressBar;

    private String UID;
    private ArrayList<FeedListHolder> list;
    private long mLastClickTime = 0;

    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        list =new ArrayList<FeedListHolder>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();

        View view = inflater.inflate(R.layout.fragment__feed, container, false);

        Adapter = new FeedList(getContext(), UID ,getActivity());

        lv = view.findViewById(R.id.list);
        progressBar = view.findViewById(R.id.loading);
        lv.setAdapter(Adapter);

        lv.setSelector(android.R.color.transparent);

        final DatabaseReference db;

        db = FirebaseDatabase.getInstance().getReference("DataBase");


        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot Snapshot = dataSnapshot.child("Public List");
                int count = (int) Snapshot.getChildrenCount();

                for (DataSnapshot snapshot : Snapshot.getChildren()) {
                    String RoomID = snapshot.getValue(String.class);
                    String Name, Count, Stream;
                    Name = dataSnapshot.child("Rooms").child(RoomID).child("Meta").child("Name").getValue(String.class);
                    Count = String.valueOf(dataSnapshot.child("Rooms").child(RoomID).child("Meta").child("Users").getChildrenCount() + 1);
                    Stream = dataSnapshot.child("Rooms").child(RoomID).child("Meta").child("Stream").getValue(String.class);
                    String DpID =  dataSnapshot.child("Rooms").child(RoomID).child("Meta").child("DP").getValue(String.class);
                    FeedListHolder holder = new FeedListHolder(Name, Count, Stream, RoomID,DpID);
                    list.add(holder);
                }
                progressBar.setVisibility(View.INVISIBLE);

                if(count>10){
                    for (int i = 0;i<10;i++){
                        int randomNumber = new Random().nextInt(count);
                        Adapter.Add(list.get(randomNumber));
                        Adapter.notifyDataSetChanged();
                    }
                }
                else {
                    for (DataSnapshot snapshot : Snapshot.getChildren()) {
                        String RoomID = snapshot.getValue(String.class);
                        String Name, Count, Stream;
                        Name = dataSnapshot.child("Rooms").child(RoomID).child("Meta").child("Name").getValue(String.class);
                        Count = String.valueOf(dataSnapshot.child("Rooms").child(RoomID).child("Meta").child("Users").getChildrenCount() + 1);
                        Stream = dataSnapshot.child("Rooms").child(RoomID).child("Meta").child("Stream").getValue(String.class);
                        String DpID =  dataSnapshot.child("Rooms").child(RoomID).child("Meta").child("Dp").getValue(String.class);
                        FeedListHolder holder = new FeedListHolder(Name, Count, Stream, RoomID,DpID);
                        Adapter.Add(holder);
                        Adapter.notifyDataSetChanged();
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Conformation(Adapter.getItem(position).getName(),Adapter.getItem(position).getRandomId());
            }
        });
        return view;
    }

    private void Conformation(String Text , String Id ){
        ConformationPopup popup = new ConformationPopup(Text,Id,null,null);
        popup.show(getActivity().getSupportFragmentManager(),"Confirmation");
    }
}
