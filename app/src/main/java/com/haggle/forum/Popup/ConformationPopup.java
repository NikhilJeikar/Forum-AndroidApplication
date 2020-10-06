package com.haggle.forum.Popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.haggle.forum.Holder.InListHolder;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.R;


public class ConformationPopup extends AppCompatDialogFragment {

    private TextView text;
    private Button confirm;

    private String Text;
    private String Id;
    private String UID;
    private String Ref;
    private String Key;

    private DatabaseReference reference;

    private FirebaseAuth firebaseAuth;

    private Utils utils;

    public ConformationPopup(String text, String id, String ref, String key) {
        Text = text;
        Id = id;
        Ref = ref;
        Key = key;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        utils = new Utils(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
         View view = layoutInflater.inflate(R.layout.popup_confirmation,null);

        reference = FirebaseDatabase.getInstance().getReference("DataBase");

        builder.setView(view);
        confirm = view.findViewById(R.id.Button);
        text = view.findViewById(R.id.text);
        text.setText("Do you want to join " + Text+" ?");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child("User table").child(UID).child("Group").child("list").child(Id).setValue(0);
                reference.child("User table").child(UID).child("Group").child("join").child(Id).setValue(0);

                reference.child("Rooms").child(Id).child("Users").child(UID).setValue("0");
                reference.child("Rooms").child(Id).child("Reference").child(UID).setValue(Ref);

                reference.child("User table").child(UID).child("Group").child("joinData").child(Id).child("Date").setValue(utils.getCurrentDate());
                reference.child("User table").child(UID).child("Group").child("joinData").child(Id).child("Time").setValue(utils.getCurrentTime());
                reference.child("User table").child(UID).child("Group").child("PrivateKey").child(Id).setValue(Key);

                /*
                reference.child("Rooms").child(Id).child("Meta").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Name , Count , Stream;
                        Name = dataSnapshot.child("Name").getValue(String.class);
                        Count = String.valueOf(dataSnapshot.child("Users").getChildrenCount() + 1);
                        Stream = dataSnapshot.child("Stream").getValue(String.class);

                        if(dataSnapshot.child("Users").child(UID).getValue(String.class) == null){
                            reference.child("Rooms").child("Metadata").child(Id).child("Users").child(UID).child(Count);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
*/
                dismiss();

            }
        });
        AlertDialog dialog = builder.show();
        dialog.getWindow().setLayout(600, 450);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return dialog;
    }
}
