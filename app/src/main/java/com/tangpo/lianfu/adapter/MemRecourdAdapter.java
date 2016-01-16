package com.tangpo.lianfu.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.MemRecord;
import com.tangpo.lianfu.entity.Store;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetSpecifyServer;
import com.tangpo.lianfu.ui.ConversationActivity;
import com.tangpo.lianfu.ui.MemRecordFragment;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class MemRecourdAdapter extends BaseAdapter {

    private Context context;
    private List<MemRecord> list;
    private LayoutInflater inflater;

    private ProgressDialog dialog;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mem_record_list, null);
            holder = new ViewHolder();

            holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
            holder.confirm = (TextView) convertView.findViewById(R.id.confirm);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.contact = (Button) convertView.findViewById(R.id.contact);
            //holder.profit = (TextView) convertView.findViewById(R.id.profit);

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
        float consume_money=Float.parseFloat(list.get(position).getFee());
        DecimalFormat formatter=new DecimalFormat("##0.00");
        holder.money.setText(formatter.format(consume_money) + "元");
        holder.time.setText(list.get(position).getDatetime());

        /**
         * 隐藏联系客服按钮
         */
        holder.contact.setVisibility(View.VISIBLE);

        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //联系客服
                getServer(list.get(position).getStore_id());
            }
        });

        return convertView;
    }


    private void getServer(String store_id) {
        if(!Tools.checkLAN()) {
            Tools.showToast(context, "网络未连接，请联网后重试");
            return;
        }
        String[] kvs = new String[]{store_id};
        String param = GetSpecifyServer.packagingParam(context, kvs);
        dialog = ProgressDialog.show(context, context.getString(R.string.connecting), context.getString(R.string.please_wait));

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                dialog.dismiss();
                Log.e("tag", "result:" + result);
                JSONArray array = null;
                try {
                    array = result.getJSONArray("param");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.obj = array;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    if ("3".equals(result.getString("status"))) {
                        Tools.showToast(context, "店铺不存在客服");
                    } else if ("10".equals(result.getString("status"))) {
                        Tools.showToast(context, context.getString(R.string.server_exception));
                    } else {
                        Tools.showToast(context, result.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                JSONArray array = (JSONArray) msg.obj;
                Intent intent = new Intent(context, ConversationActivity.class);
                intent.putExtra("servers", array.toString());
                intent.putExtra("userid", MemRecordFragment.user_id);
                context.startActivity(intent);
             }
    };
    private class ViewHolder {
        public TextView shop_name;
        public TextView confirm;
        public TextView money;
        public TextView time;
        //public TextView profit;
        public Button contact;
    }
}
