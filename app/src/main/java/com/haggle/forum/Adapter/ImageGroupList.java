package com.haggle.forum.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haggle.forum.Holder.ImageGroupListHolder;
import com.haggle.forum.R;
import com.haggle.forum.ViewHolder.ImageGroupListViewHolder;

import java.util.ArrayList;

public class ImageGroupList extends BaseAdapter {
    Context context;
    ArrayList<ImageGroupListHolder> list = new ArrayList<ImageGroupListHolder>() ;
    private String ChatID = "";
    private String Stream = "";

    public ImageGroupList(Context context, ArrayList<ImageGroupListHolder> list, String chatID, String stream) {
        this.context = context;
        this.list = list;
        ChatID = chatID;
        Stream = stream;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ImageGroupListHolder getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater Inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = Inflater.inflate(R.layout.layout_item_image_group, null);

        ImageGroupListViewHolder holder = new ImageGroupListViewHolder();
        holder.recyclerView = convertView.findViewById(R.id.recyclerView);
        holder.text = convertView.findViewById(R.id.Loc);

        ImageSubGroupList adapter = new ImageSubGroupList(list.get(position).getSource(),context,ChatID,Stream);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView.setLayoutManager(mLayoutManager);
        holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
        convertView.setTag(holder);


        holder.text.setText(list.get(position).getName());
        holder.recyclerView.setAdapter(adapter);


        return convertView;
    }
}
