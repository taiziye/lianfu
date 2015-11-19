package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Profit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 果冻 on 2015/11/17.
 */
public class ComputeProfitAdapter extends BaseAdapter {
    private List<Profit> list = null;
    private LayoutInflater inflater = null;

    private static Map<Integer, Boolean> selected;
    private static int count = 0;

    public ComputeProfitAdapter(Context context, List<Profit> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);

        selected = new HashMap<Integer, Boolean>();

        for (int i = 0; i < list.size(); i++) {
            selected.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.compute_list, null);
            holder = new ViewHolder();

            holder.check = (CheckBox) convertView.findViewById(R.id.check);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.bank = (TextView) convertView.findViewById(R.id.bank);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.profit = (TextView) convertView.findViewById(R.id.profit);
            holder.status = (TextView) convertView.findViewById(R.id.status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.check.setChecked(false);
        if (selected.size() > 0) {
            holder.check.setChecked(selected.get(position));
        }

        holder.name.setText(list.get(position).getUsername());
        holder.bank.setText(list.get(position).getPay_account());
        holder.date.setText(list.get(position).getConsume_date());
        holder.profit.setText(list.get(position).getProfit());
        holder.status.setText(list.get(position).getPay_status());
        return convertView;
    }

    public Map<Integer, Boolean> getSelected() {
        return selected;
    }

    //处于管理状态下单击item将取消或选中item
    public void Click(int position) {
        //Log.e("TAG", " " + delSet.toString());
        if (selected.get(position)) {
            selected.put(position, false);
            count--;
        } else {
            selected.put(position, true);
            count++;
        }
    }

    public boolean isAll() {
        if (count == selected.size()) {
            return true;
        } else {
            return false;
        }
    }

    public void setSelected(int num) {
        //清空delSet
        for (int i = 0; i < num; i++) {
            selected.put(i, false);
        }
    }

    //选中所有的item
    public void SelectAll() {
        for (int i = 0; i < list.size(); i++) {
            selected.put(i, true);
            //delSet.add(data.get(i).getTmId());
        }
    }

    //取消全选
    public void SelecttEmpty() {
        for (int i = 0; i < list.size(); i++) {
            selected.put(i, false);
        }
        //delSet.clear();
    }

    private class ViewHolder {
        public CheckBox check;
        private TextView name;
        private TextView bank;
        private TextView date;
        private TextView profit;
        private TextView status;
    }
}
