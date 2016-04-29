package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.HXUser;
import com.tangpo.lianfu.utils.Tools;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2016/4/9.
 */
public class HXUserAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<HXUser> userList;
    private String name;
    private String hxid;

    public HXUserAdapter(Context context, ArrayList<HXUser> userList, String name, String hxid) {
        this.context = context;
        this.userList = userList;
        this.name = name;
        this.hxid = hxid;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView==null) {
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_user, parent, false);

            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.id = (TextView) convertView.findViewById(R.id.id);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.send = (Button) convertView.findViewById(R.id.send);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Tools.setPhoto(context, userList.get(position).getPhoto(), holder.img);
        holder.id.setText(userList.get(position).getUsername());
        holder.name.setText(userList.get(position).getName());
        final Button btn = holder.send;
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hxid.equalsIgnoreCase(userList.get(position).getEasemod_id())) {
                    Tools.showToast(context, "不能添加自己为好友");
                } else {
                    sendRequest(userList.get(position).getEasemod_id(), btn);
                }
            }
        });

        return convertView;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Button btn = (Button) msg.obj;
                    Tools.showToast(context, "请求已发送");
                    btn.setClickable(false);
                    btn.setBackgroundColor(Color.GRAY);
                    break;
            }
        }
    };

    private void sendRequest(final String userID, final Button btn){
        new Thread(){
            @Override
            public void run() {
                try {
                    EMContactManager.getInstance().addContact(userID, name + "申请添加你为好友");
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = btn;
                    handler.sendMessage(msg);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class Holder{
        public ImageView img;
        public TextView id;
        public TextView name;
        public Button send;
    }
}
