package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Profit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 果冻 on 2015/11/17.
 */
public class ComputeProfitAdapter extends BaseAdapter {
    private List<Profit> list = null;
    private LayoutInflater inflater = null;

//    private static Map<Integer, Boolean> selected;
    private static int count = 1;

    private List<CheckBox>  boxList;

    public ComputeProfitAdapter(Context context, List<Profit> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);

//        selected = new HashMap<Integer, Boolean>();

        boxList = new ArrayList<>();

//        for (int i = 0; i < list.size(); i++) {
//            selected.put(i, false);
//        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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

        holder.name.setText(list.get(position).getUsername());
        holder.bank.setText(list.get(position).getPay_account());
        holder.date.setText(list.get(position).getConsume_date());
        holder.profit.setText(list.get(position).getProfit());
        holder.status.setText(list.get(position).getPay_status());

        boxList.add(holder.check);

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", position + " " + count);
                if(!boxList.get(position).isChecked()){
                    //
                    boxList.get(position).setChecked(false);
                    count --;
                } else {
                    //
                    boxList.get(position).setChecked(true);
                    count ++;
                }
            }
        });

        return convertView;
    }

    public boolean getSelected(int position) {
        if(boxList.get(position).isChecked())
            return true;
        else
            return false;
    }

    //处于管理状态下单击item将取消或选中item
    public boolean Click(int position) {
        Log.e("TAG", boxList.get(position) + " " + boxList.get(position).isChecked());
        if (!boxList.get(position).isChecked()) {
            return true;
        } else {
            return false;
        }

    }

    public boolean isAll() {
        if (count == list.size()) {
            return true;
        } else {
            return false;
        }
    }

    //选中所有的item
    public void SelectAll() {
        for (int i = 0; i < list.size(); i++) {
//            selected.put(i, true);
            boxList.get(i).setChecked(true);
            //delSet.add(data.get(i).getTmId());
        }
        count = list.size();
    }

    //取消全选
    public void SelecttEmpty() {
        for (int i = 0; i < list.size(); i++) {
            boxList.get(i).setChecked(false);
        }
        count = 0;
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
