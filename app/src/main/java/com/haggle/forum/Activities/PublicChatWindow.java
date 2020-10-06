package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haggle.forum.Adapter.PublicList;
import com.haggle.forum.CustomTemplate.CustomListview;
import com.haggle.forum.Holder.PublicListHolder;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.R;


import java.util.ArrayList;
import java.util.Collections;


public class PublicChatWindow extends AppCompatActivity {

    private  Toolbar toolbar;
    private ImageButton imageButton;
    private CustomListview lv;
    private PublicList Adapter;
    private SwipeRefreshLayout refreshLayout;

    private String UID,Chat_ID, Stream;
    private Boolean Admin;
    private Integer Size = 0,RefSize = 0;
    private Boolean IMP = false;
    private long mLastClickTime = 0;
    private int count = 0;

    private FirebaseAuth firebaseAuth;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_chat_window);

        utils = new Utils(getApplicationContext());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();


        Stream = getIntent().getStringExtra("Stream");
        Chat_ID = getIntent().getStringExtra("Chat ID");


        lv = findViewById(R.id.list);

        Adapter = new PublicList(getApplicationContext() ,Chat_ID);
        lv.setSelector(android.R.color.transparent);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DataBase");
        reference.child("Rooms").child(Chat_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()== null){
                    reference.child("Rooms").child(Chat_ID).setValue(null);
                    Intent intent =new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finish();

                }
                else {
                 toolbar.setTitle(dataSnapshot.child("Meta").child("Name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("Rooms").child(Chat_ID).child("Meta").child("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(!Stream.equals("Forum") && UID.equals(dataSnapshot.getValue(String.class))){
                    create();
                }
                if(Stream.equals("Forum")){
                    create();
                }
                if(UID.equals(dataSnapshot.getValue(String.class))){
                    Admin =true;
                }
                else {
                    Admin = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        imageButton = new ImageButton(getApplicationContext());
        imageButton.setBackgroundResource(R.drawable.button_settings);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,Toolbar.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        imageButton.setPadding(25,25,25,0);
        imageButton.setLayoutParams(layoutParams);
        toolbar.addView(imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(utils.isConnected()) {
                    Intent intent = new Intent(getApplicationContext(), ChatSetting.class);
                    intent.putExtra("ChatID", Chat_ID);
                    intent.putExtra("Admin", Admin);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Check network connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        LoadPrev();
        refreshLayout = findViewById(R.id.add);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                LoadSeg();

            }
        });
        LoadCurrent();

    }

    private void create(){
        final RelativeLayout layout = findViewById(R.id.Comment);
        final EditText text = new EditText(getApplicationContext());
        final ImageButton SendButton = new ImageButton(getApplicationContext());
        final ImageButton AttachButton = new ImageButton(getApplicationContext());

        RelativeLayout.LayoutParams Params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Params.addRule(RelativeLayout.ALIGN_PARENT_END);
        Params.addRule(RelativeLayout.CENTER_VERTICAL);
        Params.setMargins(0,0,15,0);

        RelativeLayout.LayoutParams TextParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextParams.setMargins(40,0,90,0);

        text.setMinHeight(70);
        text.setMaxHeight(200);
        text.setLayoutParams(TextParams);

        text.setTextColor(Color.parseColor("#ffffff"));
        text.setHintTextColor(Color.parseColor("#ffffff"));
        text.setHint("Discuss");
        text.setBackgroundResource(R.drawable.template_null);

        SendButton.setLayoutParams(Params);
        AttachButton.setLayoutParams(Params);

        SendButton.setBackgroundResource(R.drawable.button_send);
        AttachButton.setBackgroundResource(R.drawable.button_attach);


        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData(text.getText().toString().trim());
                text.setText("");
            }
        });

        AttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), Gallery.class);
                intent.putExtra("ChatID",Chat_ID);
                intent.putExtra("Stream",Stream);
                startActivity(intent);
            }
        });

        layout.addView(text);
        layout.addView(AttachButton);

        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    SendButton.performClick();

                    return true;
                }
                return false;
            }
        });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    try{
                        layout.removeView(SendButton);
                        layout.addView(AttachButton);
                    }
                    catch (Exception e){

                    }

                }
                else {
                    try{
                        layout.removeView(AttachButton);
                        layout.addView(SendButton);
                    }
                    catch (Exception e){

                    }


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    private void UploadData(final String Data){
        if(!Data.isEmpty()){

            final DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference("DataBase");
            databaseReference.child("Rooms").child(Chat_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String key = databaseReference.push().getKey();
                    String count = String.valueOf(dataSnapshot.child("Public Message").getChildrenCount()+1);

                    databaseReference.child("Rooms").child(Chat_ID).child("Public Message").child(count).child("Message Id").setValue(key);
                    databaseReference.child("Rooms").child(Chat_ID).child("Public Message").child(count).child("Type").setValue("Text");
                    databaseReference.child("Rooms").child(Chat_ID).child("Public Message").child(count).child("Value").setValue(Data);
                    databaseReference.child("Rooms").child(Chat_ID).child("Public Message").child(count).child("Time").setValue(utils.getCurrentTime());
                    databaseReference.child("Rooms").child(Chat_ID).child("Public Message").child(count).child("Date").setValue(utils.getCurrentDate());
                    databaseReference.child("Rooms").child(Chat_ID).child("Public Message").child(count).child("Rand").setValue(UID);

                    databaseReference.child("Rooms").child(Chat_ID).child("Count").child(Chat_ID).setValue(count);

                    databaseReference.child("Rooms").child(Chat_ID).child("Time").child("Time").setValue(utils.getCurrentTime());
                    databaseReference.child("Rooms").child(Chat_ID).child("Time").child("Date").setValue(utils.getCurrentDate());

                    databaseReference.child("Rooms").child(Chat_ID).child("Users").child(UID).setValue(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void LoadPrev(){
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("DataBase").child("Rooms").child(Chat_ID);

        databaseReference.child("Public Message").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Size = (int)dataSnapshot.getChildrenCount();
                RefSize = (int)dataSnapshot.getChildrenCount();
                if(Size> 20) {
                    for (int i = (int) (Size -20);i<=Size;i++) {
                        DataSnapshot snapshot = dataSnapshot.child(String.valueOf(i));
                        String Data, Type, ImageId, PrivateId, Date, SenderID;
                        final String ref = snapshot.getKey();

                        SenderID = snapshot.child("Rand").getValue(String.class);
                        Data = snapshot.child("Value").getValue(String.class);
                        PrivateId = snapshot.child("Message Id").getValue(String.class);
                        Date = snapshot.child("Time").getValue(String.class);
                        Type = snapshot.child("Type").getValue(String.class);

                        if(Type != null && Data != null && Date != null && PrivateId != null && SenderID != null ){
                            if(SenderID.equals(UID)){
                                PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,true);
                                lv.setAdapter(Adapter);
                                Adapter.Add(holder);
                            }
                            else {
                                PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,false);
                                lv.setAdapter(Adapter);
                                Adapter.Add(holder);
                            }
                        }


                    }
                    databaseReference.child("Users").child(UID).setValue(String.valueOf(Size));
                }
                else{
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                        String Data , Type , PrivateId , Date , SenderID;

                            SenderID = snapshot.child("Rand").getValue(String.class);
                            Data = snapshot.child("Value").getValue(String.class);
                            PrivateId = snapshot.child("Message Id").getValue(String.class);
                            Date = snapshot.child("Time").getValue(String.class);
                            Type = snapshot.child("Type").getValue(String.class);
                            if(Type != null && Data != null && Date != null && PrivateId != null && SenderID != null ){
                                if(SenderID.equals(UID)){
                                    PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,true);
                                    lv.setAdapter(Adapter);
                                    Adapter.Add(holder);
                                }
                                else {
                                    PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,false);
                                    lv.setAdapter(Adapter);
                                    Adapter.Add(holder);
                                }
                            }

                    }
                    databaseReference.child("Users").child(UID).setValue(String.valueOf(Size));
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lv.post(new Runnable() {
            @Override
            public void run() {
                lv.setSelection(Adapter.getCount());
            }
        });

        IMP = true;
    }

    private void LoadSeg(){
        final DatabaseReference databaseReference;
        final ArrayList<PublicListHolder> list = new ArrayList<PublicListHolder>();
        databaseReference = FirebaseDatabase.getInstance().getReference("DataBase").child("Rooms").child(Chat_ID).child("Public Message");
        count = count +1;
        if(count == 1){
            Size = Size - 20;
        }
        else {
            Size = Size - 10;
        }
        if(Size>0){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(Size> 10) {
                        for (int i = (int) (Size -10);i<Size;i++) {
                            DataSnapshot snapshot = dataSnapshot.child(String.valueOf(i));
                            String Data, Type, PrivateId, Date, SenderID;

                            SenderID = snapshot.child("Rand").getValue(String.class);
                            Data = snapshot.child("Value").getValue(String.class);
                            PrivateId = snapshot.child("Message Id").getValue(String.class);
                            Date = snapshot.child("Time").getValue(String.class);
                            Type = snapshot.child("Type").getValue(String.class);
                            if(Type != null && Data != null && Date != null && PrivateId != null && SenderID != null ){
                                if(SenderID.equals(UID)){
                                    PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,true);
                                    list.add(holder);
                                }
                                else {
                                    PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,false);
                                    list.add(holder);
                                }
                            }
                        }
                    }
                    else{

                        for (int i = 0;i<Size;i++) {
                            DataSnapshot snapshot = dataSnapshot.child(String.valueOf(i));
                            String Data , Type , PrivateId , Date , SenderID;

                            SenderID = snapshot.child("Rand").getValue(String.class);
                            Data = snapshot.child("Value").getValue(String.class);
                            PrivateId = snapshot.child("Message Id").getValue(String.class);
                            Date = snapshot.child("Time").getValue(String.class);
                            Type = snapshot.child("Type").getValue(String.class);
                            if(Type != null && Data != null && Date != null && PrivateId != null && SenderID != null ){
                                if(SenderID.equals(UID)){
                                    PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,true);
                                    list.add(holder);
                                }
                                else {
                                    PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,false);
                                    list.add(holder);
                                }
                            }

                        }
                    }

                    Collections.reverse(list);
                    for(int i=0; i<list.size(); i++){
                        Adapter.AddTop(lv ,list.get(i));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void LoadCurrent(){
        final DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("DataBase").child("Rooms").child(Chat_ID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                DataSnapshot dataSnapshot = dataSnapshot1.child("Public Message");
                try {
                    Integer temp = Integer.parseInt(dataSnapshot1.child("Count").child(Chat_ID).getValue(String.class));
                    if(IMP && temp > RefSize){
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Integer id = Integer.parseInt(snapshot.getKey());
                            if(id>RefSize){
                                String Data, Type, PrivateId, Date, SenderID;

                                PrivateId = snapshot.child("Message Id").getValue(String.class);
                                SenderID = snapshot.child("Rand").getValue(String.class);
                                Date = snapshot.child("Time").getValue(String.class);
                                Type = snapshot.child("Type").getValue(String.class);
                                Data = snapshot.child("Value").getValue(String.class);

                                if(Type != null && Data != null && Date != null && PrivateId != null && SenderID != null ){
                                    if(SenderID.equals(UID)){
                                        PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,true);
                                        Adapter.Add(holder);

                                    }
                                    else {
                                        PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,false);
                                        Adapter.Add(holder);
                                    }
                                }
                            }
                        }
                        RefSize = temp;
                    }

                }
                catch (Exception e){
                    Integer temp = 0;
                    if(IMP && temp > RefSize){
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Integer id = Integer.parseInt(snapshot.getKey());
                            if(id>RefSize){
                                String Data, Type, PrivateId, Date, SenderID;
                                PrivateId = snapshot.child("Message Id").getValue(String.class);
                                SenderID = snapshot.child("Rand").getValue(String.class);
                                Date = snapshot.child("Time").getValue(String.class);
                                Type = snapshot.child("Type").getValue(String.class);
                                Data = snapshot.child("Value").getValue(String.class);

                                if(Type != null && Data != null && Date != null && PrivateId != null && SenderID != null ){


                                }
                                if(SenderID.equals(UID)){
                                    PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,true);
                                    Adapter.Add(holder);
                                }
                                else {
                                    PublicListHolder holder = new PublicListHolder(Type,Data,PrivateId,Date,false);
                                    Adapter.Add(holder);
                                }
                            }
                        }
                        RefSize = temp;

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
