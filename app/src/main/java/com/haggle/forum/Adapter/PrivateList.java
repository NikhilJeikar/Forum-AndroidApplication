package com.haggle.forum.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haggle.forum.CustomTemplate.CustomListview;
import com.haggle.forum.Holder.PrivateListHolder;
import com.haggle.forum.R;
import com.haggle.forum.ViewHolder.PublicListViewHolder;

import java.util.ArrayList;

public class PrivateList extends BaseAdapter {

    ArrayList<PrivateListHolder> list = new ArrayList<PrivateListHolder>();
    Context context ;

    public PrivateList(Context context) {
        this.context = context;
    }

    public  void Add(PrivateListHolder item){
        list.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public PrivateListHolder getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PublicListViewHolder Holder = new PublicListViewHolder();

        LayoutInflater Inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final PrivateListHolder holder = list.get(position);
        if(holder.getType().equals("Text")){
            if(holder.getBelong()){
                convertView = Inflater.inflate(R.layout.layout_message_text_send, null);
                Holder.textView = convertView.findViewById(R.id.message_body_1);
                Holder.time = convertView.findViewById(R.id.message_time_1);
                convertView.setTag(Holder);
                Holder.textView.setText(holder.getText());
                Holder.time.setText(holder.getTime());
            }
            else {
                convertView = Inflater.inflate(R.layout.layout_message_text_receive, null);
                Holder.textView = convertView.findViewById(R.id.message_body_1);
                Holder.time = convertView.findViewById(R.id.message_time_1);
                convertView.setTag(Holder);
                Holder.textView.setText(holder.getText());
                Holder.time.setText(holder.getTime());
            }
        }
        else if(holder.getType().equals("Report"))  {
            convertView = Inflater.inflate(R.layout.layout_message_text_receive, null);
            Holder.textView = convertView.findViewById(R.id.message_body_1);
            Holder.time = convertView.findViewById(R.id.message_time_1);
            convertView.setTag(Holder);
            Holder.textView.setText("This has been removed ");
        }
         else {
            convertView = Inflater.inflate(R.layout.layout_message_text_receive, null);
            Holder.textView = convertView.findViewById(R.id.message_body_1);
            Holder.time = convertView.findViewById(R.id.message_time_1);
            convertView.setTag(Holder);
            Holder.textView.setText("Update the app to view ");
        }

        return convertView;
    }

    public void AddTop(CustomListview listView , PrivateListHolder item){
        int firstVisPos = listView.getFirstVisiblePosition();
        View firstVisView = listView.getChildAt(0);
        int top = firstVisView != null ? firstVisView.getTop() : 0;
        listView.setBlockLayoutChildren(true);
        list.add(0 , item);
        int itemsAddedBeforeFirstVisible = 1;   //  no. of stories added in list
        notifyDataSetChanged();
        listView.setBlockLayoutChildren(false);
        listView.setSelectionFromTop(firstVisPos + itemsAddedBeforeFirstVisible, top);

    }
}
