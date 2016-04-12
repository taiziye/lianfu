package com.tangpo.lianfu.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
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

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.HXUser;
import com.tangpo.lianfu.entity.InvitedMessage;
import com.tangpo.lianfu.utils.InviteMessageDao;
import com.tangpo.lianfu.utils.Tools;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2016/4/10.
 */
public class RequestAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<HXUser> list;
    private InviteMessageDao messageDao;

    public RequestAdapter(Context context, ArrayList<HXUser> list) {
        this.context = context;
        this.list = list;
        messageDao = new InviteMessageDao(context);
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
        Holder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.request_list, parent, false);
            holder = new Holder();

            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.reason = (TextView) convertView.findViewById(R.id.reason);
            holder.agree = (Button) convertView.findViewById(R.id.agree);
            holder.refuse = (Button) convertView.findViewById(R.id.refuse);

            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        Log.e("tag", "status " + list.get(position).getMessage().getStatus() + " id " + list.get(position).getMessage().getId());

        if(list.get(position).getMessage().getStatus() == InvitedMessage.InviteMessageStatus.AGREED ||
                list.get(position).getMessage().getStatus() == InvitedMessage.InviteMessageStatus.REFUSED) {
            holder.agree.setClickable(false);
            holder.agree.setBackgroundColor(Color.GRAY);
            holder.refuse.setClickable(false);
            holder.refuse.setBackgroundColor(Color.GRAY);
        }
        Tools.setPhoto(context, list.get(position).getPhoto(), holder.img);
        holder.name.setText(list.get(position).getName());
        holder.reason.setText(list.get(position).getMessage().getReason());
        final MSGOBJ obj = new MSGOBJ();
        obj.agree = holder.agree;
        obj.refuse = holder.refuse;
        holder.agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;
                        obj.easemod_id = list.get(position).getEasemod_id();
                        obj.id = list.get(position).getMessage().getId();
                        msg.obj = obj;
                        handler.sendMessage(msg);
                    }
                }.start();
            }
        });

        holder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 2;
                        obj.easemod_id = list.get(position).getEasemod_id();
                        obj.id = list.get(position).getMessage().getId();
                        msg.obj = obj;
                        handler.sendMessage(msg);
                    }
                }.start();
            }
        });

        return convertView;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ContentValues values = new ContentValues();
            MSGOBJ obj = (MSGOBJ) msg.obj;
            switch (msg.what) {
                case 1:
                    try {
                        EMChatManager.getInstance().acceptInvitation(obj.easemod_id);
                        values.put(InviteMessageDao.COLUMN_NAME_STATUS, InvitedMessage.InviteMessageStatus.AGREED.ordinal());
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        EMChatManager.getInstance().refuseInvitation(obj.easemod_id);
                        values.put(InviteMessageDao.COLUMN_NAME_STATUS, InvitedMessage.InviteMessageStatus.REFUSED.ordinal());
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            messageDao.updateMessage(obj.id, values);
            obj.agree.setClickable(false);
            obj.agree.setBackgroundColor(Color.GRAY);
            obj.refuse.setClickable(false);
            obj.refuse.setBackgroundColor(Color.GRAY);
        }
    };

    class Holder{
        public ImageView img;
        public TextView name;
        public TextView reason;
        public Button agree;
        public Button refuse;
    }

    class MSGOBJ{
        public Button agree;
        public Button refuse;
        public int id;
        public String easemod_id;
    }
}
