package com.tangpo.lianfu.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.DeleteConsumeRecord;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/9.
 */
public class ConsumRecordAdapter extends BaseAdapter {

    private Context context;
    private List<EmployeeConsumeRecord> list;
    private LayoutInflater container;

    private String store_id = "";

    private String employeename = "";

    private String userid = "";
    private String store_name = "";

    private boolean isEdit = false;

    public ConsumRecordAdapter(List<EmployeeConsumeRecord> list, Context context,
                               String store_id, String employeename, String userid, String store_name) {
        this.context = context;
        this.list = list;
        container = LayoutInflater.from(context);
        this.store_id = store_id;
        this.employeename = employeename;
        this.userid = userid;
        this.store_name = store_name;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = container.inflate(R.layout.consum_record_list, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.profit = (TextView) convertView.findViewById(R.id.profit);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.phone= (TextView) convertView.findViewById(R.id.phone_num);

            holder.frame = (RelativeLayout) convertView.findViewById(R.id.frame);
            holder.delete = (Button) convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(!isEdit){
            holder.frame.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
        } else {
            holder.frame.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.INVISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(list.get(position).getId(), position);
            }
        });

        holder.name.setText(list.get(position).getName());
        DecimalFormat formatter=new DecimalFormat("##0.00");
        holder.money.setText(formatter.format(Float.valueOf(list.get(position).getFee())));
        if(list.get(position).getGains().length() >= 1) holder.profit.setText(formatter.format(Float.valueOf(list.get(position).getGains())));
        else holder.profit.setText( 0 );
        if (list.get(position).getIsPass().equals("0")) {
            holder.status.setText("未确认");
        } else if(list.get(position).getIsPass().equals("1")){
            holder.status.setText("已拒绝");
        }else{
            holder.status.setText("已确认");
        }

        holder.time.setText(parseDate(list.get(position).getConsume_date()));
        holder.phone.setText(list.get(position).getPhone());
        /*if(employeename == null) {
            holder.name.setText("");
        }else
            holder.name.setText(employeename);*/
        return convertView;
    }

    private String parseDate(String str) {
        String[] tmp1 = new String[2];
        String[] tmp2 = new String[3];
        /*try {
            Date date = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss").parse(str);
            dateStr = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        tmp1 = str.split(" ");
        tmp2 = tmp1[0].split("/");
        String date = tmp2[0] + "年" + tmp2[1] + "月" + tmp2[2] + "日 " + tmp1[1];
        return date;
    }

    public void setEdit(boolean flag){
        isEdit = flag;
    }

    private ProgressDialog dialog = null;

    private void deleteRecord(String pay_record_id, final int position) {
        if(!Tools.checkLAN()) {
            Tools.showToast(context, "网络未连接，请联网后重试");
            return;
        }
        dialog = ProgressDialog.show(context, context.getString(R.string.connecting), context.getString(R.string.please_wait));
        String kvs[] = new String[]{userid, pay_record_id, store_id};
        String param = DeleteConsumeRecord.packagingParam(context, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                list.remove(list.get(position));
                ConsumRecordAdapter.this.notifyDataSetInvalidated();
                Tools.showToast(context, context.getString(R.string.delete_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();

                try {
                    if("1".equals(result.getString("status"))){
                        Tools.showToast(context, context.getString(R.string.delete_failed));
                    } else if("9".equals(result.getString("status"))) {
                        Tools.showToast(context, context.getString(R.string.login_timeout));
                        SharedPreferences preferences = context.getSharedPreferences(Configs.APP_ID, context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(Configs.KEY_TOKEN);
                        editor.commit();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                    } else {
                        Tools.showToast(context, context.getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    private class ViewHolder {
        public TextView name;
        public TextView money;
        public TextView profit;
        public TextView status;
        public TextView time;
        public TextView phone;

        public RelativeLayout frame;
        public Button delete;
    }
}
