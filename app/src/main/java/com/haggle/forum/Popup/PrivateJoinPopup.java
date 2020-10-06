package com.haggle.forum.Popup;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haggle.forum.Activities.QR_Scanner;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.R;

public class PrivateJoinPopup extends AppCompatDialogFragment {

    private EditText ID;
    private EditText KEY;
    private TextView textView;
    private Button join;
    private Button cancel;
    private ImageButton scan;

    private String UID;

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    private DatabaseReference reference;

    private FirebaseAuth firebaseAuth;

    private Utils utils;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        utils = new Utils(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();


        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.popup_private_join,null);

        reference = FirebaseDatabase.getInstance().getReference("DataBase");

        ID = view.findViewById(R.id.edittext1);
        KEY = view.findViewById(R.id.edittext2);
        join = view.findViewById(R.id.ok_btn);
        cancel = view.findViewById(R.id.cancel_btn);
        scan = view.findViewById(R.id.scan_button);

        textView = view.findViewById(R.id.textview1);
        textView.setText("PRIVATE STREAM");

        builder.setView(view);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String key = KEY.getText().toString().trim();
                final String id = ID.getText().toString().trim();
                if(key.length() != 6 || id.length() != 8){
                    if(key.length() != 6){
                        KEY.setError("Invalid key");
                    }
                    if(id.length() != 8){
                        ID.setError("Invalid ID");
                    }
                }
                else {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot DataSnapshot) {
                            boolean log1 = false, log2 = false;
                            String Rid = "", RoomName = "";
                            DataSnapshot dataSnapshot = DataSnapshot.child("Private");
                            if(dataSnapshot.child(id).getValue(Object.class) != null){
                                log1 = true;
                                if(dataSnapshot.child(id).child("Key").getValue(String.class).equals(key)){
                                    log2 = true;
                                    Rid = dataSnapshot.child(id).child("Room Id").getValue(String.class);
                                    RoomName = dataSnapshot.child(id).child("Name").getValue(String.class);
                                }
                            }
                            if(!log1){
                                ID.setError("No such id exist");
                            }
                            if(log1 && !log2){
                                KEY.setError("Invalid Key");
                            }
                            if(log1 && log2){
                                ConformationPopup conformationPopup =new ConformationPopup(RoomName ,   Rid,null,key);
                                conformationPopup.show(getActivity().getSupportFragmentManager(),"Confirmation");
                                dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getContext(), QR_Scanner.class);
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new
                            String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }


            }
        });

        return builder.create();
    }


}
