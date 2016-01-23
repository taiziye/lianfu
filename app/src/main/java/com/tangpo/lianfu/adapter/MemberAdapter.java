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
 * Created by 果冻 on 2015/11/9.
 */
public class MemberAdapter extends BaseAdapter {

    private Context context;
    private List<Member> list;
    private LayoutInflater inflater;

    public MemberAdapter(List<Member> list, Context context) {
        this.list = list;
        this.context = context;
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.member_list, null);
            holder = new ViewHolder();

            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.contact = (TextView) convertView.findViewById(R.id.contact);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.user_name.setText(list.get(position).getUsername());
        holder.name.setText(list.get(position).getName());
//        if (list.get(position).getSex().equals("0"))
//            holder.sex.setText("男");
//        else
//            holder.sex.setText("女");
        holder.contact.setText(list.get(position).getPhone());
        holder.time.setText(list.get(position).getRegister_time());
        return convertView;
    }

    private class ViewHolder {
        public TextView user_name;
        public TextView name;
        public TextView contact;
        public TextView time;
    }
}
