package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.CostRepayDetail;

import java.util.List;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class CostRepayDetailAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<CostRepayDetail> list;

    public CostRepayDetailAdapter(Context context, List<CostRepayDetail> list) {
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
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.repay_detail_list, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.backmoney = (TextView) convertView.findViewById(R.id.backmoney);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(list.get(position).getName());
        holder.money.setText(list.get(position).getCost());
        holder.backmoney.setText(list.get(position).getBackcost());
        holder.time.setText(list.get(position).getConsume_date());
        return convertView;
    }

    class ViewHolder{
        public TextView name;
        public TextView money;
        public TextView backmoney;
        public TextView time;
    }
}
