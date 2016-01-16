package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ProfitPay;
import com.tangpo.lianfu.ui.OfflineProfitPayActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/17.
 */
public class ComputeProfitAdapter extends BaseAdapter {
    private List<ProfitPay> list = null;
    private LayoutInflater inflater = null;

    private static int count = 1;

    private Context context;

    private static HashMap<Integer,Boolean> isSelected;

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int size) {
        for(int i=0;i<size;i++){
            if(OfflineProfitPayActivity.checkedItems.get(i)!=null){
                getIsSelected().put(i,OfflineProfitPayActivity.checkedItems.get(i));
            }else{
                getIsSelected().put(i,false);
            }
        }
    }

    public ComputeProfitAdapter(Context context, List<ProfitPay> list) {
        this.context=context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected=new HashMap<Integer,Boolean>();
        initData();
        //boxList = new ArrayList<>();

    }

    private void initData(){
        for(int i=0;i<list.size();i++){
            if(OfflineProfitPayActivity.checkedItems.get(i)!=null){
                getIsSelected().put(i,OfflineProfitPayActivity.checkedItems.get(i));
            }else{
                getIsSelected().put(i,false);
            }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.compute_list, null);
            holder = new ViewHolder();

            holder.check = (CheckBox) convertView.findViewById(R.id.check);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.profit = (TextView) convertView.findViewById(R.id.profit);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(list.get(position).getName());
        holder.money.setText(String.format("%.2f", Float.parseFloat(list.get(position).getFee())));
        holder.profit.setText(String.format("%.2f", Float.parseFloat(list.get(position).getProfit())));
        if ("0".equals(list.get(position).getPay_status())) {
            holder.status.setText("未支付");
            holder.time.setVisibility(View.GONE);
            holder.status.setTextColor(Color.RED);
        } else {
            holder.status.setText("已支付");
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(parseDate(list.get(position).getPay_date()));
            holder.status.setTextColor(Color.parseColor("#008B00"));
        }
        holder.check.setChecked(getIsSelected().get(position));

        return convertView;
    }

    private String parseDate(String str) {
        String[] tmp1 = null;
        String[] tmp2 = null;
        String date = "";
        if (str.length() > 0) {
            tmp1 = str.split(" ");
            tmp2 = tmp1[0].split("\\/");
            date = tmp2[0] + "年" + tmp2[1] + "月" + tmp2[2] + "日 " + tmp1[1];
        }
        return date;
    }

//    public boolean getSelected(int position) {
//        if(boxList.get(position).isChecked())
//            return true;
//        else
//            return false;
//    }

//    //处于管理状态下单击item将取消或选中item
//    public boolean Click(int position) {
//        Log.e("TAG", boxList.get(position) + " " + boxList.get(position).isChecked());
//        if (!boxList.get(position).isChecked()) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }

//    public boolean isAll() {
//        if (count == list.size()) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    //选中所有的item
//    public void SelectAll() {
//        for (int i = 0; i < list.size(); i++) {
////            selected.put(i, true);
//            boxList.get(i).setChecked(true);
//            //delSet.add(data.get(i).getTmId());
//        }
//        count = list.size();
//    }
//
//    //取消全选
//    public void SelecttEmpty() {
//        for (int i = 0; i < list.size(); i++) {
//            boxList.get(i).setChecked(false);
//        }
//        count = 0;
//        //delSet.clear();
//    }

    public class ViewHolder {
        public CheckBox check;
        private TextView name;
        //private TextView bank;
        private TextView money;
        private TextView profit;
        private TextView status;
        private TextView time;
    }
}
