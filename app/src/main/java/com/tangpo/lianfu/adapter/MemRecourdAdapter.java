package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.MemRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class MemRecourdAdapter extends BaseAdapter {

    private Context context;
    private List<MemRecord> list;
    private LayoutInflater inflater;

    public MemRecourdAdapter(Context context, List<MemRecord> list) {
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mem_record_list, null);
            holder = new ViewHolder();

            holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
            holder.compute = (TextView) convertView.findViewById(R.id.compute);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.shop_name.setText(list.get(position).getShop_name());
        if (list.get(position).isCompute()) {
            holder.compute.setText("已确认");
            holder.compute.setBackgroundColor(Color.RED);
        } else {
            holder.compute.setText("未确认");
            holder.compute.setBackgroundColor(Color.GRAY);
        }
        holder.money.setText(list.get(position).getMoney() + "");
        holder.time.setText((new SimpleDateFormat("yyyy-MM-dd hh:mm")).format(new Date()));

        return convertView;
    }

    private class ViewHolder {
        public TextView shop_name;
        public TextView compute;
        public TextView money;
        public TextView time;
    }
}
