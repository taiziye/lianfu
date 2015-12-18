package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Member;

import java.util.List;

/**
 * Created by 果冻 on 2015/12/18.
 */
public class UserAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Member> list;

    public UserAdapter(Context context, List<Member> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.user_list, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.tel = (TextView) convertView.findViewById(R.id.tel);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(list.get(position).getName());
        holder.tel.setText(list.get(position).getPhone());
        return convertView;
    }

    class ViewHolder{
        public TextView name;
        public TextView tel;
    }
}
