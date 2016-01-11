package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Cost;

import java.util.List;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class CostRepayAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Cost> list;

    public CostRepayAdapter(Context context, List<Cost> list) {
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
            convertView = inflater.inflate(R.layout.cost_repay_list, parent, false);

            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.msg = (TextView) convertView.findViewById(R.id.msg);
            holder.desc = (TextView) convertView.findViewById(R.id.desc);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.money.setText(list.get(position).getCost()+"元");
        holder.time.setText(list.get(position).getBackdate());
        holder.msg.setText(list.get(position).getBank_account() + "/" + list.get(position).getBank_name());
        holder.desc.setText(list.get(position).getBackinfo());
        holder.status.setText(list.get(position).getBackstate());
        if ("返还失败".equals(list.get(position).getBackstate())) {
            holder.desc.setVisibility(View.VISIBLE);
        } else {
            holder.desc.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder{
        public TextView money;
        public TextView status;
        public TextView time;
        public TextView msg;
        public TextView desc;
    }
}
