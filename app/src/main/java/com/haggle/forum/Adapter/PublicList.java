package com.haggle.forum.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.haggle.forum.Activities.PrivateChatWindow;
import com.haggle.forum.CustomTemplate.CustomListview;
import com.haggle.forum.Holder.PublicListHolder;
import com.haggle.forum.Utilty.FilePaths;
import com.haggle.forum.Utilty.FileSave;
import com.haggle.forum.CustomTemplate.SwipeOnTouchListener;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.R;
import com.haggle.forum.ViewHolder.PublicListViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PublicList extends BaseAdapter {
    private String Chat_ID;
    private ArrayList<PublicListHolder> list = new ArrayList<PublicListHolder>();
    private long mLastClickTime = 0;
    private Context context ;
    private DatabaseReference reference;

    private FirebaseAuth firebaseAuth;

    private Utils utils;

    public PublicList(Context context ,String ChatId) {
        this.context = context;
        utils = new Utils(context);
        Chat_ID = ChatId;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();
        reference = FirebaseDatabase.getInstance().getReference("DataBase").child("Rooms").child(Chat_ID).child("Vote");
    }

    public  void Add(PublicListHolder item){
        list.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PublicListHolder getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PublicListViewHolder Holder = new PublicListViewHolder();

        StorageReference mstorage = FirebaseStorage.getInstance().getReference("ChatImages");

        LayoutInflater Inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final PublicListHolder holder = list.get(position);

        if(holder.getDataType().equals("Image")){

            final FilePaths filePaths = new FilePaths();
            final FileSave fileSave = new FileSave();

            if(holder.getBelong()){

                convertView = Inflater.inflate(R.layout.layout_message_image_send, null);

                Holder.imageView = convertView.findViewById(R.id.message_body_1);
                Holder.textView = convertView.findViewById(R.id.message_body_2);
                Holder.time = convertView.findViewById(R.id.message_time_1);
                Holder.progress = convertView.findViewById(R.id.loading);
                Holder.layout = convertView.findViewById(R.id.line1);

                convertView.setTag(Holder);

                File file = new  File (context.getCacheDir(),list.get(position).getPrivateID());
                if(file.exists()){
                    Holder.imageView.setImageURI(Uri.fromFile(file));
                    Holder.progress.setVisibility(View.GONE);
                }
                else {

                    fileSave.createCustomFile(context,list.get(position).getPrivateID(),mstorage.child(list.get(position).getPrivateID()));
                    mstorage.child(list.get(position).getPrivateID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(context).load(uri).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Holder.progress.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(Holder.imageView);
                        }
                    });
                }
                Holder.textView.setText(list.get(position).getText());
                Holder.imageView.setOnTouchListener( new SwipeOnTouchListener(context){
                    @Override
                    public void onSwipeLeft() {
                        super.onSwipeLeft();
                        reference.child(list.get(position).getPrivateID()).child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                        reference.child(list.get(position).getPrivateID()).child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                        Toast.makeText(context, "Swipe Left gesture detected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeRight() {
                        super.onSwipeRight();
                        reference.child(list.get(position).getPrivateID()).child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                        reference.child(list.get(position).getPrivateID()).child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                        Toast.makeText(context, "Swipe Right gesture detected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClick() {
                        super.onClick();
                        if(utils.isConnected()){
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent intent = new Intent(context, PrivateChatWindow.class);
                            intent.putExtra("ChatID" , Chat_ID);
                            intent.putExtra("ParentID" , list.get(position).getPrivateID());
                            intent.putExtra("Time",list.get(position).getTime());
                            intent.putExtra("Text",list.get(position).getText());
                            intent.putExtra("Belong",list.get(position).getBelong());
                            intent.putExtra("Type",list.get(position).getDataType());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else {
                            Toast.makeText(context,"Check network connection",Toast.LENGTH_SHORT).show();
                        }


                    }

                });
                Holder.time.setText(holder.getTime());
            }
            else {
                convertView = Inflater.inflate(R.layout.layout_message_image_receive, null);
                Holder.imageView = convertView.findViewById(R.id.message_body_1);
                Holder.textView = convertView.findViewById(R.id.message_body_2);
                Holder.time = convertView.findViewById(R.id.message_time_1);
                Holder.progress = convertView.findViewById(R.id.loading);
                Holder.layout = convertView.findViewById(R.id.line1);

                convertView.setTag(Holder);

                File file = new  File (context.getCacheDir(),list.get(position).getPrivateID());
                if(file.exists()){
                    Holder.imageView.setImageURI(Uri.fromFile(file));
                    Holder.progress.setVisibility(View.GONE);
                }
                else {
                    fileSave.createCustomFile(context,list.get(position).getPrivateID(),mstorage.child(list.get(position).getPrivateID()));
                    mstorage.child(list.get(position).getPrivateID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(context).load(uri).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Holder.progress.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(Holder.imageView);
                        }
                    });

                }

                Holder.time.setText(holder.getTime());
                Holder.textView.setText(list.get(position).getText());
                Holder.imageView.setOnTouchListener( new SwipeOnTouchListener(context){

                    @Override
                    public void onSwipeLeft() {
                        super.onSwipeLeft();
                        reference.child(list.get(position).getPrivateID()).child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                        reference.child(list.get(position).getPrivateID()).child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                        Toast.makeText(context, "Swipe Left gesture detected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeRight() {
                        super.onSwipeRight();
                        reference.child(list.get(position).getPrivateID()).child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                        reference.child(list.get(position).getPrivateID()).child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                        Toast.makeText(context, "Swipe Right gesture detected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClick() {
                        super.onClick();
                        if(utils.isConnected()){
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent intent = new Intent(context, PrivateChatWindow.class);
                            intent.putExtra("ChatID" , Chat_ID);
                            intent.putExtra("ParentID" , list.get(position).getPrivateID());
                            intent.putExtra("Time",list.get(position).getTime());
                            intent.putExtra("Text",list.get(position).getText());
                            intent.putExtra("Belong",list.get(position).getBelong());
                            intent.putExtra("Type",list.get(position).getDataType());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else {
                            Toast.makeText(context,"Check network connection",Toast.LENGTH_SHORT).show();
                        }


                    }

                });
            }
        }

        else if(holder.getDataType().equals("Text")) {
            if(holder.getBelong()){
                convertView = Inflater.inflate(R.layout.layout_message_text_send, null);
                Holder.textView = convertView.findViewById(R.id.message_body_1);
                Holder.time = convertView.findViewById(R.id.message_time_1);
                Holder.layout = convertView.findViewById(R.id.line1);

                convertView.setTag(Holder);

                Holder.textView.setText(holder.getText());
                Holder.layout.setOnTouchListener( new SwipeOnTouchListener(context){

                    @Override
                    public void onSwipeLeft() {
                        super.onSwipeLeft();
                        reference.child(list.get(position).getPrivateID()).child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                        reference.child(list.get(position).getPrivateID()).child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                        Toast.makeText(context, "Swipe Left gesture detected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeRight() {
                        super.onSwipeRight();
                        reference.child(list.get(position).getPrivateID()).child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                        reference.child(list.get(position).getPrivateID()).child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                        Toast.makeText(context, "Swipe Right gesture detected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClick() {
                        super.onClick();
                        if(utils.isConnected()){
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent intent = new Intent(context, PrivateChatWindow.class);
                            intent.putExtra("ChatID" , Chat_ID);
                            intent.putExtra("ParentID" , list.get(position).getPrivateID());
                            intent.putExtra("Time",list.get(position).getTime());
                            intent.putExtra("Text",list.get(position).getText());
                            intent.putExtra("Belong",list.get(position).getBelong());
                            intent.putExtra("Type",list.get(position).getDataType());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else {
                            Toast.makeText(context,"Check network connection",Toast.LENGTH_SHORT).show();
                        }


                    }

                });
                Holder.time.setText(holder.getTime());
            }
            else {
                convertView = Inflater.inflate(R.layout.layout_message_text_receive, null);
                Holder.textView = convertView.findViewById(R.id.message_body_1);
                Holder.time = convertView.findViewById(R.id.message_time_1);
                Holder.layout = convertView.findViewById(R.id.line1);

                convertView.setTag(Holder);

                Holder.textView.setText(holder.getText());
                Holder.layout.setOnTouchListener( new SwipeOnTouchListener(context){

                    @Override
                    public void onSwipeLeft() {
                        super.onSwipeLeft();
                        reference.child(list.get(position).getPrivateID()).child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                        reference.child(list.get(position).getPrivateID()).child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                        Toast.makeText(context, "Swipe Left gesture detected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeRight() {
                        super.onSwipeRight();
                        reference.child(list.get(position).getPrivateID()).child("Upvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(1);
                        reference.child(list.get(position).getPrivateID()).child("Downvote").child(firebaseAuth.getCurrentUser().getUid()).setValue(null);
                        Toast.makeText(context, "Swipe Right gesture detected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClick() {
                        super.onClick();
                        if(utils.isConnected()){
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Intent intent = new Intent(context, PrivateChatWindow.class);
                            intent.putExtra("ChatID" , Chat_ID);
                            intent.putExtra("ParentID" , list.get(position).getPrivateID());
                            intent.putExtra("Time",list.get(position).getTime());
                            intent.putExtra("Text",list.get(position).getText());
                            intent.putExtra("Belong",list.get(position).getBelong());
                            intent.putExtra("Type",list.get(position).getDataType());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        else {
                            Toast.makeText(context,"Check network connection",Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                Holder.time.setText(holder.getTime());
            }
        }

        else if(holder.getDataType().equals("Report")) {
            convertView = Inflater.inflate(R.layout.layout_message_text_receive, null);

            Holder.textView = convertView.findViewById(R.id.message_body_1);
            Holder.time = convertView.findViewById(R.id.message_time_1);
            Holder.layout = convertView.findViewById(R.id.line1);

            convertView.setTag(Holder);

            Holder.textView.setText("This has been removed ");
            Holder.time.setText(holder.getTime());
        }

        else {
            convertView = Inflater.inflate(R.layout.layout_message_text_receive, null);

            Holder.textView = convertView.findViewById(R.id.message_body_1);
            Holder.time = convertView.findViewById(R.id.message_time_1);
            Holder.layout = convertView.findViewById(R.id.line1);

            convertView.setTag(Holder);

            Holder.textView.setText("Update the app to view ");

        }

        return convertView;
    }

    public void AddTop(CustomListview listView , PublicListHolder item){
        int firstVisPos = listView.getFirstVisiblePosition();
        View firstVisView = listView.getChildAt(0);
        int top = firstVisView != null ? firstVisView.getTop() : 0;
        listView.setBlockLayoutChildren(true);
        list.add(0 , item);
        int itemsAddedBeforeFirstVisible = 1;   //  no. of stories added in list
        notifyDataSetChanged();
        listView.setBlockLayoutChildren(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setSelectionFromTop(firstVisPos + itemsAddedBeforeFirstVisible, top);
        } else {
            listView.setSelection(firstVisPos + itemsAddedBeforeFirstVisible);
        }

    }
}
