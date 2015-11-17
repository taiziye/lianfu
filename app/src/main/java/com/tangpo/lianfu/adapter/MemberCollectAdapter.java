package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.MemberCollect;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CollectStore;
import com.tangpo.lianfu.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by 果冻 on 2015/11/15.
 */
public class MemberCollectAdapter extends BaseAdapter {

    private Context context;
    private List<MemberCollect> list;
    private LayoutInflater inflater;
    private ViewHolder holder = null;

    private String userid = null;

    private boolean flag = false;

    public MemberCollectAdapter(Context context, List<MemberCollect> list) {
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

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.member_collect_list, null);

            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
            holder.commodity = (TextView) convertView.findViewById(R.id.commodity);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.cancel = (Button) convertView.findViewById(R.id.cancel);
            holder.contact = (Button) convertView.findViewById(R.id.contact);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        holder.img.setImageURI(Uri.parse(list.get(position).getPhoto()));
        holder.shop_name.setText(list.get(position).getStore());
        //holder.commodity.setText(list.get(position).getBusiness());
        holder.address.setText(list.get(position).getAddress());

        /*holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    //
                } else {
                    String kvs[] = new String[]{list.get(position).getId(), userid};
                    String params = CollectStore.packagingParam(context, kvs);

                    new NetConnection(new NetConnection.SuccessCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            *//**
         *
         *//*
                        }
                    }, new NetConnection.FailCallback() {
                        @Override
                        public void onFail(JSONObject result) {
                            try {
                                if (result.getString("status").equals("9")) {
                                    ToastUtils.showToast(context, context.getString(R.string.login_timeout), Toast.LENGTH_SHORT);
                                } else if (result.getString("status").equals("10")) {
                                    ToastUtils.showToast(context, context.getString(R.string.server_exception), Toast.LENGTH_SHORT);
                                } else {
                                    ToastUtils.showToast(context, context.getString(R.string.collect_failed), Toast.LENGTH_SHORT);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, params);
                }
            }
        });*/

        /*holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });*/
        return convertView;
    }

    private class ViewHolder {
        public ImageView img;
        public TextView shop_name;
        public TextView commodity;
        public TextView address;
        public Button cancel;
        public Button contact;
    }
}
