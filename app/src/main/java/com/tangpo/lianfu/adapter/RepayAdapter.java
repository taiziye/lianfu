package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Repay;

import java.util.List;

/**
 * Created by 果冻 on 2015/11/24.
 */
public class RepayAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Repay> list;

    public RepayAdapter(Context context, List<Repay> list) {
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
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.repay_list, parent, false);
            holder = new ViewHolder();

            holder.user_id = (TextView) convertView.findViewById(R.id.user_id);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.user_id.setText(list.get(position).getId());
        holder.user_name.setText(list.get(position).getUsername());
        holder.money.setText(list.get(position).getProfit());
        holder.time.setText(list.get(position).getPay_date());

        return null;
    }

    public class ViewHolder{
        private TextView user_id;
        private TextView user_name;
        private TextView money;
        private TextView time;
    }
}
