package com.tangpo.lianfu.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.DeleteDiscount;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 果冻 on 2015/11/12.
 */
public class DiscountManageAdapter extends BaseAdapter {
    private List<Discount> list = null;
    private LayoutInflater inflater = null;
    private Context context;

    private boolean isEdit = false;
    private String userid;

    public DiscountManageAdapter(Context context, List<Discount> list, String userid) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.discount_manage_list, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.discount = (TextView) convertView.findViewById(R.id.discount);
            holder.check = (TextView) convertView.findViewById(R.id.check);
            holder.delete = (Button) convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(!isEdit){
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiscount(position);
            }
        });

        holder.name.setText(list.get(position).getDesc());
        holder.discount.setText(Float.valueOf(list.get(position).getDiscount())/10+"折");
        holder.check.setText(list.get(position).getStatus());
        /*if (list.get(position).getStatus().equals("0")) {
            holder.check.setText(list.get(position).getStatus());
        } else if (list.get(position).getStatus().equals("1")) {
            holder.check.setText("已确认");
        } else {
            holder.check.setText("已拒绝");
        }*/
        return convertView;
    }

    private void deleteDiscount(final int position) {
        if(!Tools.checkLAN()) {
            Tools.showToast(context, "网络未连接，请联网后重试");
            return;
        }

        String kvs[] = new String[]{userid, list.get(position).getId()};
        String param = DeleteDiscount.packagingParam(context, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                list.remove(list.get(position));
                DiscountManageAdapter.this.notifyDataSetInvalidated();
                Tools.showToast(context, context.getString(R.string.delete_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                try {
                    if("404".equals(result.getString("status")) || "300".equals(result.getString("status"))) {
                        Tools.showToast(context, "删除失败！该折扣已审核不能删除！");
                    } else {
                        Tools.handleResult(context, result.getString("status"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    public void setEdit(boolean flag) {
        isEdit = flag;
    }

    private class ViewHolder {
        public TextView name;
        public TextView discount;
        public TextView check;
        public Button delete;
    }
}
