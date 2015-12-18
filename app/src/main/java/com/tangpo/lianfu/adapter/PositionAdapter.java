package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CollectStore;
import com.tangpo.lianfu.ui.BoundOrRegister;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        collect = new ImageView[list.size() + 10];
        text = new TextView[list.size() + 10];

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
            holder.collect = (LinearLayout) convertView.findViewById(R.id.collect);
            holder.s_img = (ImageView) convertView.findViewById(R.id.s_img);
            holder.text = (TextView) convertView.findViewById(R.id.text);

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
        holder.s_img.setImageResource(R.drawable.s_collect);

        collect[position] = holder.s_img;
        text[position] = holder.text;

        if(list.get(position).getFavorite().equals("1")) {
            holder.s_img.setImageResource(R.drawable.s_collect_r);
            holder.text.setText(R.string.has_been_collected);
        } else {
            holder.s_img.setImageResource(R.drawable.s_collect);
            holder.text.setText(R.string.collect);
        }

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 2:
                        collect[position].setImageResource(R.drawable.s_collect_r);
                        text[position].setText(R.string.has_been_collected);
                        list.get(position).setFavorite("1");
                        break;
                    case 3:
                        collect[position].setImageResource(R.drawable.s_collect);
                        text[position].setText(R.string.collect);
                        list.get(position).setFavorite("0");
                        break;
                }
            }
        };

        holder.collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences=context.getSharedPreferences(Configs.APP_ID,Context.MODE_PRIVATE);
                String logintype=preferences.getString(Configs.KEY_LOGINTYPE, "");
                if(logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
                    Intent intent=new Intent(context, BoundOrRegister.class);
                    context.startActivity(intent);
                    return;
                }
                cur = position;

                if(!Tools.checkLAN()) {
                    Tools.showToast(context, "网络未连接，请联网后重试");
                    return;
                }

                if (list.get(position).getFavorite().equals("1")) {
                    //取消收藏
                    /*String kvs[] = new String[]{list.get(position).getId(), userid};
                    String param = CancelCollectedStore.packagingParam(context, kvs);

                    new NetConnection(new NetConnection.SuccessCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            //
                            Message msg = new Message();
                            msg.what = 3;
                            handler.sendMessage(msg);
                        }
                    }, new NetConnection.FailCallback() {
                        @Override
                        public void onFail(JSONObject result) {
                            //
                        }
                    }, param);*/
                } else {
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

    private ImageView[] collect;
    private TextView[] text;

    private class ViewHolder {
        public ImageView img;
        public TextView shop_name;
        public TextView commodity;
        public TextView address;
        public LinearLayout collect;
        public int position = 0;
        public TextView text;
        private ImageView s_img;
    }
}
