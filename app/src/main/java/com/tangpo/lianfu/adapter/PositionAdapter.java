package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.internal.IndicatorLayout;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CollectStore;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by 果冻 on 2015/11/11.
 */
public class PositionAdapter extends BaseAdapter {

    private Context context;
    private List<FindStore> list;
    private LayoutInflater inflater;
    private ViewHolder holder = null;
    private List<String> collectedStore = new ArrayList<>();
    private SharedPreferences preferences = null;

    private String userid = null;

    private boolean[] collected;

    public PositionAdapter(Context context, List<FindStore> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        preferences = context.getSharedPreferences(Configs.APP_ID, Context.MODE_APPEND);
        String user = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(user);
            userid = jsonObject.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn = new Button[list.size()];

        collected = new boolean[list.size()];

        Set<String> store = preferences.getStringSet(Configs.KEY_STORE, null);
        if (store != null) {
            Iterator<String> it = store.iterator();
            while (it.hasNext()) {
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

    private int cur = 0;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
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
        holder.position = position;
        Tools.setPhoto(context, list.get(position).getPhoto(), holder.img);
        holder.shop_name.setText(list.get(position).getStore());
        holder.commodity.setText(list.get(position).getBusiness());
        holder.address.setText(list.get(position).getAddress());

        holder.collect.setText(context.getString(R.string.collect));

        btn[position] = holder.collect;

        /*if (collectedStore.contains(list.get(position).getId())) {
            holder.collect.setText(context.getString(R.string.cancel_collect));
            collected[position] = true;
        } else {
            holder.collect.setText(context.getString(R.string.collect));
        }*/

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                /*if(msg.what == 1){
                    ViewHolder tmp = (ViewHolder) msg.obj;
                    Log.e("tag", "postion = " + tmp.position);
                    collected[tmp.position] = true;
                    tmp.collect.setText(context.getString(R.string.cancel_collect));
                }*/
                Log.e("tag", "cur " + cur);
                switch (msg.what){
                    case 2:
                        collected[cur] = true;
                        btn[cur].setText(context.getString(R.string.cancel_collect));
                        break;
                    case 3:
                        collected[cur] = false;
                        btn[cur].setText(context.getString(R.string.collect));
                        break;
                }
            }
        };

        holder.collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cur = position;

                if (collected[position]) {
                    //取消收藏
                    Message msg = new Message();
                    msg.what = 3;
                    handler.sendMessage(msg);
                } else {
                    Log.e("tag", "collect");

                    String kvs[] = new String[]{list.get(position).getId(), userid};
                    String param = CollectStore.packagingParam(context, kvs);
                    new NetConnection(new NetConnection.SuccessCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {

                            ViewHolder tmp = holder;

                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);

                            ToastUtils.showToast(context, context.getString(R.string.collect_success), Toast.LENGTH_SHORT);
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
                    }, param);
                }
            }
        });
        return convertView;
    }

    private Button[] btn;

    private class ViewHolder {
        public ImageView img;
        public TextView shop_name;
        public TextView commodity;
        public TextView address;
        public Button collect;
        public int position = 0;
    }
}
