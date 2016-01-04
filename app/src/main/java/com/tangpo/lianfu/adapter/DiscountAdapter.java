package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Dis;
import com.tangpo.lianfu.entity.Discount;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class DiscountAdapter extends BaseAdapter {

    private Context context;
    private List<Dis> list;
    private LayoutInflater inflater;

    private boolean checked[] = null;

    private static Map<Integer, Boolean> selected;

    public DiscountAdapter(Context context, List<Dis> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        checked = new boolean[list.size()];

        selected = new HashMap<Integer, Boolean>();

        for (int i = 0; i < list.size(); i++) {
            selected.put(i, false);
        }

        for (int i = 0; i < list.size(); i++) {
            checked[i] = false;
        }
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
            convertView = inflater.inflate(R.layout.discount_list, null);
            holder = new ViewHolder();

            holder.check = (RadioButton) convertView.findViewById(R.id.check);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.discount = (TextView) convertView.findViewById(R.id.discount);
            //holder.status = (TextView) convertView.findViewById(R.id.status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.check.setChecked(selected.get(position));

        holder.type.setText(list.get(position).getTypename());
        holder.discount.setText(Float.valueOf(list.get(position).getAgio()) / 10 + "折");
        /*if(list.get(position).getStatus().equals("0")){
            holder.status.setText("未确认");
            holder.status.setTextColor(Color.BLUE);
        }else if(list.get(position).getStatus().equals("1")){
            holder.status.setText("已确认");
            holder.status.setTextColor(Color.GREEN);
        }else{
            holder.status.setText("已拒绝");
            holder.status.setTextColor(Color.RED);
        }*/
        return convertView;
    }

    public void Click(int position) {
        if (selected.get(position)) {
            selected.put(position, false);
        } else {
            selected.put(position, true);
        }
    }

    public void setSelected(int num) {
        for (int i = 0; i < list.size(); i++) {
            selected.put(i, false);
        }
        selected.put(num, true);
    }

    public Map<Integer, Boolean> getSelected() {
        return selected;
    }

    public void setChecked(int position) {
        for (int i = 0; i < list.size(); i++) {
            checked[i] = false;
        }
        checked[position] = true;
    }

    private class ViewHolder {
        public RadioButton check;
        public TextView type;
        public TextView discount;
        //public TextView status;
    }
}