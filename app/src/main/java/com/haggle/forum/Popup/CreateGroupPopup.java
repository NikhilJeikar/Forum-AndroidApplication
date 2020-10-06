package com.haggle.forum.Popup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haggle.forum.Holder.InListHolder;
import com.haggle.forum.Utilty.FileSave;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class CreateGroupPopup extends AppCompatDialogFragment {

    private Button join;
    private Button cancel;
    private TextView textView;
    private EditText Name;
    private RadioGroup Stream;
    private RadioGroup Stream_Type;

    private String  UID;

    private DatabaseReference reference;

    private FirebaseAuth firebaseAuth;

    private Utils utils;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        utils =new Utils(getContext());

        UID = firebaseAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("DataBase");

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.popup_create_group,null);

        Name = view.findViewById(R.id.edittext1);
        Stream = view.findViewById(R.id.Radio_Group_Stream);
        Stream_Type = view.findViewById(R.id.Radio_Group);
        join = view.findViewById(R.id.ok_btn);
        cancel = view.findViewById(R.id.cancel_btn);

        textView = view.findViewById(R.id.textview1);
        textView.setText("Create");

        builder.setView(view);
        Name.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z0-9_.]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = Name.getText().toString().trim();
                if(Stream.getCheckedRadioButtonId() == R.id.Forum_Radiobutton){
                    if (name.length() >= 5) {
                        reference.child("Public List").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean log = false;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.getKey().equals(name)) {
                                        Name.setError("Name is already taken");
                                        log = true;
                                        break;
                                    }
                                }

                                if (!log) {
                                    Run();

                                    dismiss();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        Name.setError("Name is short ");
                    }
                }
                else {
                    if(firebaseAuth.getCurrentUser().isAnonymous()){
                        Name.setError("Sign in");
                    }
                    if(!firebaseAuth.getCurrentUser().isAnonymous() &&
                            !firebaseAuth.getCurrentUser().isEmailVerified()){
                        Name.setError("Verify mail address");
                    }
                    if(!firebaseAuth.getCurrentUser().isAnonymous() &&
                            firebaseAuth.getCurrentUser().isEmailVerified()){
                        if (name.length() >= 5) {
                            reference.child("Public List").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean log = false;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (snapshot.getKey().equals(name)) {
                                            Name.setError("Name is already taken");
                                            log = true;
                                            break;
                                        }
                                    }

                                    if (!log) {
                                        Run();
                                        dismiss();

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            Name.setError("Name is short ");
                        }
                    }
                }
            }
        });
        return builder.create();
    }

    private void Add(DataSnapshot dataSnapshot, String UID ,String Stream){
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("DataBase");

        Random random = new Random();
        String GenId =String.valueOf(random.nextInt(89999999) + 10000000);

        String Rid = reference.push().getKey();
        String DpID = reference.push().getKey();

        if(dataSnapshot.child(GenId).getValue(String.class) == null){
            String pass =String.valueOf(random.nextInt(899999) + 100000);
            reference.child("Private").child(GenId).child("Name").setValue(Name.getText().toString().trim());
            reference.child("Private").child(GenId).child("Key").setValue(pass);
            reference.child("Private").child(GenId).child("Room Id").setValue(Rid);

            reference.child("Rooms").child(Rid).child("Meta").child("Name").setValue(Name.getText().toString().trim());
            reference.child("Rooms").child(Rid).child("Meta").child("Type").setValue("Private");
            reference.child("Rooms").child(Rid).child("Meta").child("Stream").setValue(Stream);
            reference.child("Rooms").child(Rid).child("Meta").child("Admin").setValue(UID);
            reference.child("Rooms").child(Rid).child("Meta").child("Desc").setValue("We is power full than I ....");
            reference.child("Rooms").child(Rid).child("Meta").child("C Date").setValue(utils.getCurrentDate());
            reference.child("Rooms").child(Rid).child("Meta").child("C Time").setValue(utils.getCurrentTime());
            reference.child("Rooms").child(Rid).child("Meta").child("Dp").setValue(DpID);

            reference.child("Rooms").child(Rid).child("Users").child(UID).setValue("0");

            reference.child("User table").child(UID).child("Group").child("PrivateKey").child(Rid).setValue(pass);
            reference.child("User table").child(UID).child("Group").child("list").child(Rid).setValue("0");
            reference.child("User table").child(UID).child("Group").child("Own").child(Rid).setValue("0");

            reference.child("User table").child(UID).child("Group").child("CreateData").child(Rid).child("Date").setValue(utils.getCurrentDate());
            reference.child("User table").child(UID).child("Group").child("CreateData").child(Rid).child("Time").setValue(utils.getCurrentTime());


            StorageReference mstorage = FirebaseStorage.getInstance().getReference("DP").child(DpID);

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.template_default_dp);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            bmp.recycle();

            UploadTask uploadTask = mstorage.putBytes(byteArray);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });

            return;
        }
        else {
            Add(dataSnapshot, UID, Stream);
            return;
        }
    }

    private void Run(){
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("DataBase");

        String Rid = reference.push().getKey();
        String DpID = reference.push().getKey();

        if(Stream.getCheckedRadioButtonId() == R.id.Forum_Radiobutton){
            if(Stream_Type.getCheckedRadioButtonId() == R.id.Private_Radiobutton){
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Add(dataSnapshot,UID, "Forum");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            else {
                reference.child("Rooms").child(Rid).child("Meta").child("Name").setValue(Name.getText().toString().trim());
                reference.child("Rooms").child(Rid).child("Meta").child("Type").setValue("Public");
                reference.child("Rooms").child(Rid).child("Meta").child("Stream").setValue("Forum");
                reference.child("Rooms").child(Rid).child("Meta").child("Admin").setValue(UID);
                reference.child("Rooms").child(Rid).child("Meta").child("Desc").setValue("We is power full than I ....");
                reference.child("Rooms").child(Rid).child("Meta").child("C Date").setValue(utils.getCurrentDate());
                reference.child("Rooms").child(Rid).child("Meta").child("C Time").setValue(utils.getCurrentTime());
                reference.child("Rooms").child(Rid).child("Meta").child("Dp").setValue(DpID);

                reference.child("Rooms").child(Rid).child("Users").child(UID).setValue("0");

                reference.child("Public List").child(Name.getText().toString().trim()).setValue(Rid);

                reference.child("User table").child(UID).child("Group").child("list").child(Rid).setValue("0");
                reference.child("User table").child(UID).child("Group").child("Own").child(Rid).setValue("0");

                reference.child("User table").child(UID).child("Group").child("CreateData").child(Rid).child("Date").setValue(utils.getCurrentDate());
                reference.child("User table").child(UID).child("Group").child("CreateData").child(Rid).child("Time").setValue(utils.getCurrentTime());

                StorageReference mstorage = FirebaseStorage.getInstance().getReference("DP").child(DpID);

                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.template_default_dp);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bmp.recycle();

                UploadTask uploadTask = mstorage.putBytes(byteArray);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });

            }

        }
        else {
            if(Stream_Type.getCheckedRadioButtonId() == R.id.Private_Radiobutton){
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Add(dataSnapshot,UID, "Channel");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            else {
                reference.child("Rooms").child(Rid).child("Meta").child("Name").setValue(Name.getText().toString().trim());
                reference.child("Rooms").child(Rid).child("Meta").child("Type").setValue("Public");
                reference.child("Rooms").child(Rid).child("Meta").child("Stream").setValue("Channel");
                reference.child("Rooms").child(Rid).child("Meta").child("Desc").setValue("We is power full than I ....");
                reference.child("Rooms").child(Rid).child("Meta").child("Admin").setValue(UID);
                reference.child("Rooms").child(Rid).child("Users").child(UID).setValue("0");
                reference.child("Rooms").child(Rid).child("Meta").child("C Date").setValue(utils.getCurrentDate());
                reference.child("Rooms").child(Rid).child("Meta").child("C Time").setValue(utils.getCurrentTime());
                reference.child("Rooms").child(Rid).child("Meta").child("Dp").setValue(DpID);

                reference.child("Public List").child(Name.getText().toString().trim()).setValue(Rid);

                reference.child("User table").child(UID).child("Group").child("list").child(Rid).setValue("0");
                reference.child("User table").child(UID).child("Group").child("Own").child(Rid).setValue("0");

                reference.child("User table").child(UID).child("Group").child("Own").child(Rid).setValue("0");
            }
        }




    }

}
