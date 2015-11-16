package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

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

        if(convertView == null){
            convertView = inflater.inflate(R.layout.employee_list, null);
            holder = new ViewHolder();

            holder.employee_id = (TextView) convertView.findViewById(R.id.employee_id);
            holder.employee_name = (TextView) convertView.findViewById(R.id.employee_name);
            holder.role = (TextView) convertView.findViewById(R.id.role);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.employee_id.setText(list.get(position).getUser_id());
        holder.employee_name.setText(list.get(position).getZsname());

        /*if(list.get(position).getRank().equals("1")){
            holder.role.setText("管");
        }else {
            holder.role.setText("员");
        }*/
        holder.role.setText(list.get(position).getRank());

        return convertView;
    }

    private class ViewHolder{
        public TextView employee_id;
        public TextView employee_name;
        public TextView role;

        //public Switch isuse;
    }
}
