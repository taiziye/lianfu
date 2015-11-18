package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.entity.UserConsumRecord;

import java.util.List;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class ConsumRecordAdapter extends BaseAdapter {

    private Context context;
    private List<EmployeeConsumeRecord> list;
    private LayoutInflater container;

    private String store_id = "";

    private String employeename = "";

    public ConsumRecordAdapter(List<EmployeeConsumeRecord> list, Context context, String store_id, String employeename) {
        this.context = context;
        this.list = list;
        container = LayoutInflater.from(context);
        this.store_id = store_id;
        this.employeename = employeename;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = container.inflate(R.layout.consum_record_list, null);
            holder = new ViewHolder();

            holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.profit = (TextView) convertView.findViewById(R.id.profit);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.compute = (TextView) convertView.findViewById(R.id.compute);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.name = (TextView) convertView.findViewById(R.id.employee_name);
            holder.level = (TextView) convertView.findViewById(R.id.level);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.shop_name.setText(store_id);
        holder.user_name.setText(list.get(position).getUsername());
        holder.money.setText("消费" + list.get(position).getFee() + "元");
        holder.profit.setText("(利润" + list.get(position).getProfit() + "元)");
        if (list.get(position).getPay_status().equals("1")) {
            holder.compute.setText("已结算");
        } else {
            holder.compute.setText("未结算");
        }

        holder.time.setText(list.get(position).getConsume_date());
        if(employeename == null) {
            holder.name.setText("");
        }else
            holder.name.setText(employeename);
        return convertView;
    }

    private class ViewHolder {
        public TextView shop_name;
        public TextView user_name;
        public TextView profit;
        public TextView money;
        public TextView compute;
        public TextView time;
        public TextView name;
        public TextView level;
    }
}
