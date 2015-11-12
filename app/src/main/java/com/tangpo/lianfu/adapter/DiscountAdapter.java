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
<<<<<<< HEAD
            //holder.money = (TextView) convertView.findViewById(R.id.money);
=======
            holder.money = (TextView) convertView.findViewById(R.id.money);
>>>>>>> 69f03d035a55c98022a3f9ebc9db36ec3dba40c4

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.check.setChecked(checked[position]);

<<<<<<< HEAD
        holder.type.setText(list.get(position).getDesc());
        holder.discount.setText(list.get(position).getDiscount());
        //holder.money.setText(list.get(position).getMoney() + "");
=======
        holder.type.setText(list.get(position).getType());
        holder.discount.setText(list.get(position).getDicount());
        holder.money.setText(list.get(position).getMoney() + "");
>>>>>>> 69f03d035a55c98022a3f9ebc9db36ec3dba40c4
        return convertView;
    }

    public void setChecked(int position){
        checked[position] = true;
    }

    private class ViewHolder{
        public RadioButton check;
        public TextView type;
        public TextView discount;
<<<<<<< HEAD
        //public TextView money;
=======
        public TextView money;
>>>>>>> 69f03d035a55c98022a3f9ebc9db36ec3dba40c4
    }
}
