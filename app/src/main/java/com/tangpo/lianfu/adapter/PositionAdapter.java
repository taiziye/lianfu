package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Store;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CollectStore;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class PositionAdapter extends BaseAdapter{

    private Context context;
    private List<Store> list;
    private LayoutInflater inflater;
    private ViewHolder holder = null;
    private List<String> collectedStore = new ArrayList<>();
    private SharedPreferences preferences = null;

    private String userid = null;

    private boolean flag = false;

    public PositionAdapter(Context context, List<Store> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        preferences = context.getSharedPreferences(Configs.APP_ID, Context.MODE_APPEND);
        String user=preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject=new JSONObject(user);
            userid = jsonObject.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Set<String> store = preferences.getStringSet(Configs.KEY_STORE, null);
        if(store != null){
            Iterator<String> it = store.iterator();
            while(it.hasNext()){
                try {
                    JSONObject object = new JSONObject(it.next());
                    collectedStore.add(object.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.position_list, null);

            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
            holder.commodity = (TextView) convertView.findViewById(R.id.commodity);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            holder.collect = (Button) convertView.findViewById(R.id.collect);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //
        holder.img.setImageURI(Uri.parse(list.get(position).getBanner()));
        holder.shop_name.setText(list.get(position).getStore());
        holder.commodity.setText(list.get(position).getBusiness());
        holder.address.setText(list.get(position).getAddress());
        if(collectedStore.contains(list.get(position).getId())){
            holder.collect.setText(context.getString(R.string.cancel_collect));
            flag = true;
        }else {
            holder.collect.setText(context.getString(R.string.collect));
            flag = false;
        }
        holder.collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    //
                }else {
                    String kvs[] = new String []{list.get(position).getId(), userid};
                    String params = CollectStore.packagingParam(context, kvs);

                    new NetConnection(new NetConnection.SuccessCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            holder.collect.setText(context.getString(R.string.cancel_collect));
                            flag = true;
                            ToastUtils.showToast(context, context.getString(R.string.collect_success), Toast.LENGTH_SHORT);
                        }
                    }, new NetConnection.FailCallback() {
                        @Override
                        public void onFail(JSONObject result) {
                            try {
                                if(result.getString("status").equals("9")){
                                    ToastUtils.showToast(context, context.getString(R.string.login_timeout), Toast.LENGTH_SHORT);
                                } else if(result.getString("status").equals("10")){
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
        });
        return convertView;
    }

    private class ViewHolder{
        public ImageView img;
        public TextView shop_name;
        public TextView commodity;
        public TextView address;
        public Button collect;
    }
}
