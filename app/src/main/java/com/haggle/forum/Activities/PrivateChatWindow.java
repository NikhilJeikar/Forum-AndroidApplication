package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haggle.forum.Adapter.PrivateList;
import com.haggle.forum.CustomTemplate.CustomListview;
import com.haggle.forum.Holder.PrivateListHolder;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.Popup.ImagePopup;
import com.haggle.forum.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class PrivateChatWindow extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private CustomListview lv;
    private PrivateList Adapter;
    private  Toolbar toolbar;

    private String UID;
    private String ChatId;
    private String ParentId;
    private String Text;
    private String Time;
    private String Type;
    private Boolean Belong;
    private Long Size;
    private Integer RefSize;
    private Boolean Theju = false;
    private int count = 0;

    private FirebaseAuth firebaseAuth;

    private Utils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat_window);

        utils = new Utils(getApplicationContext());

        ChatId = getIntent().getStringExtra("ChatID" );
        Type = getIntent().getStringExtra("Type" );
        Text = getIntent().getStringExtra("Text" );
        Time = getIntent().getStringExtra("Time" );
        ParentId = getIntent().getStringExtra("ParentID");
        Belong = getIntent().getBooleanExtra("Belong",false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Forum");

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
        lv = findViewById(R.id.list);
        Adapter = new PrivateList(getApplicationContext());
        lv.setAdapter(Adapter);
        lv.setSelector(android.R.color.transparent);

        PreviewCreate();
        create();

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


    private void PreviewCreate(){
        if(Type.equals("Text")){
            RelativeLayout layout = findViewById(R.id.Preview);
            if (Belong){
                RelativeLayout parent = (RelativeLayout)View.inflate(getApplicationContext(),R.layout.layout_message_text_send,null);
                layout.addView(parent);

                TextView time = findViewById(R.id.message_time_1);
                TextView text = findViewById(R.id.message_body_1);

                time.setText(Time);
                text.setText(Text);
            }
            else {
                RelativeLayout parent = (RelativeLayout)View.inflate(getApplicationContext(),R.layout.layout_message_text_receive,null);
                layout.addView(parent);

                TextView time = findViewById(R.id.message_time_1);
                TextView text = findViewById(R.id.message_body_1);

                time.setText(Time);
                text.setText(Text);

            }
        }
        else if(Type.equals("Image")){
            RelativeLayout layout = findViewById(R.id.Preview);

            final File file = new  File (getCacheDir(),ParentId);


            if (Belong){
                RelativeLayout parent = (RelativeLayout)View.inflate(getApplicationContext(),R.layout.layout_message_image_send,null);
                layout.addView(parent);

                ProgressBar progress = findViewById(R.id.loading);

                TextView time = findViewById(R.id.message_time_1);
                TextView text = findViewById(R.id.message_body_2);
                ImageView imageView = findViewById(R.id.message_body_1);

                imageView.setImageURI(Uri.fromFile(file));
                progress.setVisibility(View.GONE);

                time.setText(Time);
                text.setText(Text);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImagePopup(getApplicationContext(), v, Uri.fromFile(file), getWindow());
                    }
                });
            }
            else {
                RelativeLayout parent = (RelativeLayout)View.inflate(getApplicationContext(),R.layout.layout_message_image_receive,null);
                layout.addView(parent);

                ProgressBar progress = findViewById(R.id.loading);

                TextView time = findViewById(R.id.message_time_1);
                TextView text = findViewById(R.id.message_body_2);
                ImageView imageView = findViewById(R.id.message_body_1);

                imageView.setImageURI(Uri.fromFile(file));
                progress.setVisibility(View.GONE);

                time.setText(Time);
                text.setText(Text);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImagePopup(getApplicationContext(), v, Uri.fromFile(file), getWindow());
                    }
                });

            }




        }
        else if(Type.equals("Report")){
            RelativeLayout layout = findViewById(R.id.Preview);

            RelativeLayout parent = (RelativeLayout)View.inflate(getApplicationContext(),R.layout.layout_message_text_receive,null);
            layout.addView(parent);

            TextView time = findViewById(R.id.message_time_1);
            TextView text = findViewById(R.id.message_body_1);

            time.setText("This has been removed");
            text.setText(Text);

        }
        else {
            RelativeLayout layout = findViewById(R.id.Preview);

            RelativeLayout parent = (RelativeLayout)View.inflate(getApplicationContext(),R.layout.layout_message_text_receive,null);
            layout.addView(parent);

            TextView time = findViewById(R.id.message_time_1);
            TextView text = findViewById(R.id.message_body_1);

            time.setText("Update the app to view ");
            text.setText(Text);
        }

        final TextView UpCount ,DownCount;
        final ImageView Upcount ,Downcount;

        UpCount = findViewById(R.id.upvote_count);
        DownCount = findViewById(R.id.downvote_count);

        Upcount = findViewById(R.id.upvote);
        Downcount = findViewById(R.id.downvote);

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DataBase").child("Rooms").child(ChatId).child("Vote").child(ParentId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Downvote").child(UID).getValue() == null){
                    Downcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#979797")));
                }
                if(snapshot.child("Upvote").child(UID).getValue() == null){
                    Upcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#979797")));
                }
                DownCount.setText(String.valueOf(snapshot.child("Downvote").getChildrenCount()));
                UpCount.setText(String.valueOf(snapshot.child("Upvote").getChildrenCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Downcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                reference.child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);

                Upcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#979797")));
                Downcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285f4")));

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("Downvote").child(UID).getValue() == null){
                            Downcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#979797")));
                        }
                        if(snapshot.child("Upvote").child(UID).getValue() == null){
                            Upcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#979797")));
                        }
                        DownCount.setText(String.valueOf(snapshot.child("Downvote").getChildrenCount()));
                        UpCount.setText(String.valueOf(snapshot.child("Upvote").getChildrenCount()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Upcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                reference.child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);

                Upcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4285f4")));
                Downcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#979797")));

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("Downvote").child(UID).getValue() == null){
                            Downcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#979797")));
                        }
                        if(snapshot.child("Upvote").child(UID).getValue() == null){
                            Upcount.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#979797")));
                        }
                        DownCount.setText(String.valueOf(snapshot.child("Downvote").getChildrenCount()));
                        UpCount.setText(String.valueOf(snapshot.child("Upvote").getChildrenCount()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }


    private void create(){
        final EditText myEditText = findViewById(R.id.Text);
        final ImageButton imageButton = findViewById(R.id.button);

        myEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        myEditText.setHint("Comment...");
        myEditText.setHintTextColor(Color.parseColor("#999999"));
        myEditText.setTextColor(Color.parseColor("#999999"));


        myEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    imageButton.performClick();
                    myEditText.setText("");
                    return true;
                }
                return false;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData(myEditText.getText().toString().trim());
                myEditText.setText("");
            }
        });
    }


    private void UploadData(final String Data){
        if(!Data.isEmpty()){
            final DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference("DataBase");
            databaseReference.child("Rooms").child(ChatId).child("Private Message").child(ParentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String count = String.valueOf(dataSnapshot.getChildrenCount()+1);
                    databaseReference.child("Rooms").child(ChatId).child("Private Message").child(ParentId).child(count).child("Rand").setValue(UID);
                    databaseReference.child("Rooms").child(ChatId).child("Private Message").child(ParentId).child(count).child("Value").setValue(Data);
                    databaseReference.child("Rooms").child(ChatId).child("Private Message").child(count).child("Type").setValue("Text");
                    databaseReference.child("Rooms").child(ChatId).child("Private Message").child(ParentId).child(count).child("Time").setValue(utils.getCurrentTime());
                    databaseReference.child("Rooms").child(ChatId).child("Private Message").child(ParentId).child(count).child("Date").setValue(utils.getCurrentDate());

                    databaseReference.child("Rooms").child(ChatId).child("Count").child(ParentId).setValue(count);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }


    private void LoadPrev(){
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("DataBase").child("Rooms").child(ChatId).child("Private Message").child(ParentId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Size = dataSnapshot.getChildrenCount();
                RefSize = (int)dataSnapshot.getChildrenCount();

                if(Size> 20) {
                    for (int i = (int) (Size -20);i<=Size;i++) {
                        DataSnapshot snapshot = dataSnapshot.child(String.valueOf(i));
                        String Data, Date, SenderID , junk,Type;

                        SenderID = snapshot.child("Rand").getValue(String.class);
                        Data = snapshot.child("Value").getValue(String.class);
                        Date = snapshot.child("Time").getValue(String.class);
                        junk = snapshot.child("Date").getValue(String.class);
                        Type = snapshot.child("Type").getValue(String.class);

                        if(Data != null && Date != null && SenderID != null && junk != null ){
                                if(SenderID.equals(UID)){
                                    PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,true);
                                    lv.setAdapter(Adapter);
                                    Adapter.Add(holder);
                                }
                                else {
                                    PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,false);
                                    lv.setAdapter(Adapter);
                                    Adapter.Add(holder);
                                }
                        }
                    }
                }
                else{
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        String Data, Date, SenderID,junk,Type;

                        SenderID = snapshot.child("Rand").getValue(String.class);
                        Data = snapshot.child("Value").getValue(String.class);
                        Date = snapshot.child("Time").getValue(String.class);
                        junk = snapshot.child("Date").getValue(String.class);
                        Type = snapshot.child("Type").getValue(String.class);

                        if(Data != null && Date != null && SenderID != null && junk != null ){
                            if(SenderID.equals(UID)){
                                PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,true);
                                Adapter.Add(holder);
                            }
                            else {
                                PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,false);
                                Adapter.Add(holder);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lv.post(new Runnable() {
            @Override
            public void run() {
                lv.setSelection(Adapter.getCount() - 1);
            }
        });

        Theju = true;
    }


    private void LoadSeg(){
        final DatabaseReference databaseReference;
        final ArrayList<PrivateListHolder> list = new ArrayList<PrivateListHolder>();
        databaseReference = FirebaseDatabase.getInstance().getReference("DataBase").child("Rooms").child(ChatId).child("Private Message").child(ParentId);
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
                            String Data, Date, SenderID,Type;

                            SenderID = snapshot.child("Rand").getValue(String.class);
                            Data = snapshot.child("Value").getValue(String.class);
                            Date = snapshot.child("Time").getValue(String.class);
                            Type = snapshot.child("Type").getValue(String.class);

                            if(Data != null && Date != null && SenderID != null ){
                                if(SenderID.equals(UID)){
                                    PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,true);
                                    list.add(holder);
                                }
                                else {
                                    PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,false);
                                    list.add(holder);
                                }
                            }


                        }
                    }
                    else{
                        for (int i = 0;i<Size;i++) {
                            DataSnapshot snapshot = dataSnapshot.child(String.valueOf(i));
                            String Data, Date, SenderID,Type;

                            SenderID = snapshot.child("Rand").getValue(String.class);
                            Data = snapshot.child("Value").getValue(String.class);
                            Date = snapshot.child("Time").getValue(String.class);
                            Type = snapshot.child("Type").getValue(String.class);

                            if(Data != null && Date != null && SenderID != null ){
                                if(SenderID.equals(UID)){
                                    PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,true);
                                    list.add(holder);
                                }
                                else {
                                    PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,false);
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
        databaseReference = FirebaseDatabase.getInstance().getReference("DataBase").child("Rooms").child(ChatId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                DataSnapshot dataSnapshot = dataSnapshot1.child("Private Message").child(ParentId);
                try{
                    Integer temp = Integer.parseInt(dataSnapshot1.child("Count").child(ParentId).getValue(String.class));
                    if(Theju && temp > RefSize){
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Integer id = Integer.parseInt(snapshot.getKey());
                            if(id>RefSize) {
                                String Data, Date, SenderID,Type;

                                SenderID = snapshot.child("Rand").getValue(String.class);
                                Data = snapshot.child("Value").getValue(String.class);
                                Date = snapshot.child("Time").getValue(String.class);
                                Type = snapshot.child("Type").getValue(String.class);

                                if (Data != null && Date != null && SenderID != null) {
                                    if (SenderID.equals(UID)) {
                                        PrivateListHolder holder = new PrivateListHolder(Data, Date, Type,true);
                                        Adapter.Add(holder);
                                    } else {
                                        PrivateListHolder holder = new PrivateListHolder(Data, Date, Type,false);
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
                    if(Theju && temp > RefSize){
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Integer id = Integer.parseInt(snapshot.getKey());
                            if(id>RefSize){
                                String Data, Date, SenderID,Type;

                                SenderID = snapshot.child("Rand").getValue(String.class);
                                Data = snapshot.child("Value").getValue(String.class);
                                Date = snapshot.child("Time").getValue(String.class);
                                Type = snapshot.child("Type").getValue(String.class);

                                if(Data != null && Date != null && SenderID != null ){
                                    if(SenderID.equals(UID)){
                                        PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,true);
                                        Adapter.Add(holder);
                                    }
                                    else {
                                        PrivateListHolder holder = new PrivateListHolder(Data,Date,Type,false);
                                        Adapter.Add(holder);
                                    }
                                }
                            }
                            RefSize = temp;
                            }
                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
