package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
            holder.confirm = (TextView) convertView.findViewById(R.id.confirm);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.contact = (Button) convertView.findViewById(R.id.contact);
            holder.profit = (TextView) convertView.findViewById(R.id.profit);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.shop_name.setText(list.get(position).getStore());
        if ("已确认".equals(list.get(position).getRecord_status())) {
            holder.confirm.setText("已确认");
            holder.confirm.setTextColor(Color.GRAY);
        } else {
            holder.confirm.setText("未确认");
            holder.confirm.setTextColor(Color.RED);
        }
        holder.money.setText(list.get(position).getFee() + "");
        holder.time.setText(list.get(position).getDatetime());

        /**
         * 隐藏联系客服按钮
         */
        holder.contact.setVisibility(View.GONE);

        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //联系客服
            }
        });

        return convertView;
    }

    private class ViewHolder {
        public TextView shop_name;
        public TextView confirm;
        public TextView money;
        public TextView time;
        public TextView profit;
        public Button contact;
    }
}
