package com.haggle.forum.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.haggle.forum.Holder.FeedListHolder;
import com.haggle.forum.Utilty.FileSave;
import com.haggle.forum.R;
import com.haggle.forum.ViewHolder.InListViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FeedList extends BaseAdapter {
    private ArrayList<FeedListHolder> list = new ArrayList<FeedListHolder>();
    private Context context;
    private String UID;
    private android.app.Activity Activity;


    public FeedList(Context context,String UID ,Activity Activity) {
        this.context = context;
        this.UID = UID;
        this.Activity = Activity;
    }

    public void Add(FeedListHolder item){
        list.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FeedListHolder getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final InListViewHolder Holder = new InListViewHolder();
        LayoutInflater Inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = Inflater.inflate(R.layout.layout_item_inlist, null);
        Holder.Name = convertView.findViewById(R.id.Name);
        Holder.Count = convertView.findViewById(R.id.Count);
        Holder.Type = convertView.findViewById(R.id.Type);
        Holder.Dp = convertView.findViewById(R.id.dp);

        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference("DataBase");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot DataSnapshot) {
                DataSnapshot = DataSnapshot.child("Rooms").child(list.get(position).getRandomId());
                Holder.Count.setText(String.valueOf(DataSnapshot.child("Users").getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final FileSave fileSave = new FileSave();
        StorageReference mstorage = FirebaseStorage.getInstance().getReference("DP");

        File file = new  File (context.getCacheDir(),list.get(position).getDpID());

        if (file.exists()) {
            Glide.with(context).load(Uri.fromFile(file)).circleCrop().into(Holder.Dp);
        }
        else {
            fileSave.createCustomFile(context,list.get(position).getDpID(),mstorage.child(list.get(position).getDpID()));

            mstorage.child(list.get(position).getDpID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    Glide.with(context).load(uri).circleCrop().into(Holder.Dp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                }
            });
        }

        Holder.Name.setText(list.get(position).getName());
        Holder.Type.setText(list.get(position).getType());
        return convertView;
    }
}
