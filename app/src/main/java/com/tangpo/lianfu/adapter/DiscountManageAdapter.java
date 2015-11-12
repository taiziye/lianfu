package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Discount;

import java.util.List;

/**
 * Created by 果冻 on 2015/11/12.
 */
public class DiscountManageAdapter extends BaseAdapter {
    private List<Discount> list = null;
    private LayoutInflater inflater = null;

    public DiscountManageAdapter(Context context, List<Discount> list) {
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
        if(convertView == null){
            convertView = inflater.inflate(R.layout.discount_manage_list, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.discount = (TextView) convertView.findViewById(R.id.discount);
            holder.check = (TextView) convertView.findViewById(R.id.check);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(list.get(position).getDesc());
        holder.discount.setText(list.get(position).getDesc());
        if(list.get(position).getStatus().equals("0")) {
            holder.check.setText("未确认");
        } else if(list.get(position).getStatus().equals("1")) {
            holder.check.setText("已确认");
        } else {
            holder.check.setText("已拒绝");
        }
        return convertView;
    }

    private class ViewHolder{
        public TextView name;
        public TextView discount;
        public TextView check;
    }
}
