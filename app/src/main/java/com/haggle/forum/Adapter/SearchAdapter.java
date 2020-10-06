package com.haggle.forum.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.haggle.forum.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends BaseAdapter implements Filterable {

    private ArrayList<String> originalData = new ArrayList<String>();
    private ArrayList<String>filteredData = new ArrayList<String>();
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();
    private Context context;

    public SearchAdapter(Context context) {

        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void add(String data){
        this.filteredData.add(data);
        this.originalData .add(data) ;
    }

    public int getCount() {
        return filteredData.size();
    }

    public String getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_search, null);

            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText(filteredData.get(position));

        return convertView;
    }

    static class ViewHolder {
        TextView text;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<String> list = originalData;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>();

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)){
                    nlist.add(filterableString);
                }
            }
            results.values = nlist;
            results.count = nlist.size();

            if (nlist.size() == 0){
                Toast.makeText(context,"Be the first",Toast.LENGTH_LONG).show();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }
}
