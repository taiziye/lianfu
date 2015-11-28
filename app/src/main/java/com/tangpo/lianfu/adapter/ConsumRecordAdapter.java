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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tangpo.lianfu.BuildConfig;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.entity.UserConsumRecord;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.DeleteConsumeRecord;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

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

    private boolean isEdit = false;

    public ConsumRecordAdapter(List<EmployeeConsumeRecord> list, Context context, String store_id, String employeename, String userid) {
        this.context = context;
        this.list = list;
        container = LayoutInflater.from(context);
        this.store_id = store_id;
        this.employeename = employeename;
        this.userid = userid;
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

            holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
            holder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            holder.profit = (TextView) convertView.findViewById(R.id.profit);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.compute = (TextView) convertView.findViewById(R.id.compute);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.name = (TextView) convertView.findViewById(R.id.employee_name);
            holder.level = (TextView) convertView.findViewById(R.id.level);

            holder.frame = (RelativeLayout) convertView.findViewById(R.id.frame);
            holder.delete = (Button) convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(!isEdit){
            holder.frame.setVisibility(View.GONE);
        } else {
            holder.frame.setVisibility(View.VISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(list.get(position).getId(), position);
            }
        });

        holder.shop_name.setText(list.get(position).getId());
        holder.user_name.setText(list.get(position).getUsername());
        holder.money.setText("消费" + list.get(position).getFee() + "元");
        holder.profit.setText("(利润" + list.get(position).getDiscount() + "元)");
        if (list.get(position).getPay_status().equals("1")) {
            holder.compute.setText("已结算");
            holder.compute.setTextColor(Color.RED);
        } else {
            holder.compute.setText("未结算");
            holder.compute.setTextColor(Color.GRAY);
        }

        holder.time.setText(list.get(position).getConsume_date());
        if(employeename == null) {
            holder.name.setText("");
        }else
            holder.name.setText(employeename);
        return convertView;
    }

    public void setEdit(boolean flag){
        isEdit = flag;
    }

    private ProgressDialog dialog = null;

    private void deleteRecord(String pay_record_id, final int position) {
        if(!Tools.checkLAN()) {
            Log.e("tag", "check");
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
        public TextView shop_name;
        public TextView user_name;
        public TextView profit;
        public TextView money;
        public TextView compute;
        public TextView time;
        public TextView name;
        public TextView level;

        public RelativeLayout frame;
        public Button delete;
    }
}
