package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Employee;

import java.util.List;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class EmployeeAdapter extends BaseAdapter {

    private Context context;
    private List<Employee> list;
    private LayoutInflater inflater;

    public EmployeeAdapter(Context context, List<Employee> list) {
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
            convertView = inflater.inflate(R.layout.employee_list, null);
            holder = new ViewHolder();

            holder.employee_name = (TextView) convertView.findViewById(R.id.employee_name);
            holder.role = (TextView) convertView.findViewById(R.id.role);
            holder.service = (TextView) convertView.findViewById(R.id.service);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            //holder.change = (ToggleButton) convertView.findViewById(R.id.change);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.employee_name.setText(list.get(position).getName());
        holder.role.setText(list.get(position).getRank());
        if("0".equals(list.get(position).getIsstop())) {
            holder.status.setText(context.getString(R.string.start_use));
        } else {
            holder.status.setText(context.getString(R.string.stop_use));
        }

        if ("0".equals(list.get(position).getIsServer())) {
            //holder.change.setChecked(false);
            holder.service.setText(context.getString(R.string.not_server));
        } else {
            //holder.change.setChecked(true);
            holder.service.setText(context.getString(R.string.server));
        }
        return convertView;
    }

    private class ViewHolder {
        public TextView employee_name;
        public TextView role;
        //是否启用客服
        //public ToggleButton change;
        public TextView status;
        public TextView service;
        //public Switch isuse;
    }
}
