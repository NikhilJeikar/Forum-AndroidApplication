package com.haggle.forum.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haggle.forum.Activities.ImagePost;
import com.haggle.forum.R;

import java.io.File;
import java.util.ArrayList;

public class ImageSubGroupList extends RecyclerView.Adapter<ImageSubGroupList.ViewHolder> {

    private ArrayList<String> imgURLs;
    private Context context;
    private String ChatID = "";
    private String Stream = "";

    public ImageSubGroupList(ArrayList<String> imgURLs, Context context, String chatID, String stream) {
        this.imgURLs = imgURLs;
        this.context = context;
        ChatID = chatID;
        Stream = stream;
    }

    @NonNull
    @Override
    public ImageSubGroupList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_image_sub_group, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSubGroupList.ViewHolder holder, final int position) {
        Glide.with(context).load(new File(imgURLs.get(position))).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = imgURLs.get(position);
                Intent intent = new Intent(context, ImagePost.class);
                intent.putExtra("File",path);
                intent.putExtra("ChatID",ChatID);
                intent.putExtra("Source",true);
                intent.putExtra("Stream",Stream);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgURLs.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.TemplateImage);
        }
    }
}
