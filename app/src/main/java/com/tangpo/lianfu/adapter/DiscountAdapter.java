package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Discount;

import java.util.List;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class DiscountAdapter extends BaseAdapter {

    private Context context;
    private List<Discount> list;
    private LayoutInflater inflater;

    private boolean checked[] = null;

    public DiscountAdapter(Context context, List<Discount> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        checked = new boolean[list.size()];

        for (int i=0; i<list.size(); i++){
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
        if(convertView == null){
            convertView = inflater.inflate(R.layout.discount_list, null);
            holder = new ViewHolder();

            holder.check = (RadioButton) convertView.findViewById(R.id.check);
            holder.type = (TextView) convertView.findViewById(R.id.type);
            holder.discount = (TextView) convertView.findViewById(R.id.discount);
                    //holder.money = (TextView) convertView.findViewById(R.id.money);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.check.setChecked(checked[position]);

        holder.type.setText(list.get(position).getDesc());
        holder.discount.setText(list.get(position).getDiscount());
        //holder.money.setText(list.get(position).getMoney() + "");
        return convertView;
    }

    public void setChecked(int position){
        checked[position] = true;
    }

    private class ViewHolder{
        public RadioButton check;
        public TextView type;
        public TextView discount;
    }
}