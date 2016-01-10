package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.utils.Tools;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2016/1/9.
 */
public class ContactAdapter extends BaseAdapter implements Filterable {
    private Context context = null;
    private ArrayList<ChatAccount> list = null;
    private LayoutInflater inflater = null;
    private ArrayList<ChatAccount> mOriginalValues;

    public ContactAdapter(Context context, ArrayList<ChatAccount> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ChatAccount getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.conversation_list, parent, false);

            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.latest = (TextView) convertView.findViewById(R.id.latest);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.unread = (TextView) convertView.findViewById(R.id.unread);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.unread.setVisibility(View.GONE);
        holder.latest.setVisibility(View.GONE);
        if (list.get(position).getPhoto() != null) {
            Tools.setPhoto(context, list.get(position).getPhoto(), holder.img);
        }
        holder.name.setText(list.get(position).getName());
        holder.time.setText(list.get(position).getTime());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<ChatAccount> filterlist = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(list);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i=0; i<mOriginalValues.size(); i++) {
                        ChatAccount data = mOriginalValues.get(i);
                        if (data.getName().startsWith(constraint.toString())) {
                            filterlist.add(data);
                        }
                    }
                    results.count = filterlist.size();
                    results.values = filterlist;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<ChatAccount>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    class ViewHolder{
        public ImageView img;
        public TextView name;
        public TextView latest;
        public TextView time;
        public TextView unread;
    }
}
