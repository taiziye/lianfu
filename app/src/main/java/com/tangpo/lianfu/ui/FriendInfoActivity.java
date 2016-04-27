package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.HXUser;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetSpecifyHX;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GHD on 2016/4/27.
 */
public class FriendInfoActivity extends Activity implements View.OnClickListener {
    private Button back;
    private Button delete;

    private TextView user_name;
    private TextView contact_tel;
    private TextView rel_name;

    private String hxid;
    private HXUser user;
    private Gson gson;
    private ChatAccount account = ChatAccount.getInstance();

    private int flag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friend_info_activity);
        hxid = getIntent().getStringExtra("hxid");
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        user_name = (TextView) findViewById(R.id.user_name);
        contact_tel = (TextView) findViewById(R.id.contact_tel);
        rel_name = (TextView) findViewById(R.id.rel_name);

        gson = new Gson();
        getUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.delete:
                if(flag == 1) {
                    deleteFriend(hxid);
                }else{
                    addFriend(hxid);
                }
                break;
        }
    }

    private void deleteFriend(final String id){
        new Thread(){
            @Override
            public void run() {
                try {
                    EMContactManager.getInstance().deleteContact(id);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 2;
                msg.obj = delete;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    user = (HXUser) msg.obj;
                    user_name.setText(user.getUsername());
                    contact_tel.setText(user.getPhone());
                    rel_name.setText(user.getName());
                    break;
                case 2:
                    delete = (Button) msg.obj;
                    Tools.showToast(FriendInfoActivity.this, "好友已删除");
                    delete.setText("加为好友");
                    flag = 0;
                    break;
                case 3:
                    delete = (Button) msg.obj;
                    delete.setClickable(false);
                    delete.setBackgroundColor(Color.GRAY);
                    Tools.showToast(FriendInfoActivity.this, "请求已发送");
                    break;
            }
        }
    };

    private void addFriend(final String userID){
        new Thread(){
            @Override
            public void run() {
                try {
                    EMContactManager.getInstance().addContact(userID, account.getName() + "申请添加你为好友");
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = delete;
                    handler.sendMessage(msg);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getUserInfo() {
        if (!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs = new String[]{hxid};
        String param = GetSpecifyHX.packagingParam(getApplicationContext(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray array = result.getJSONArray("param");
                    for (int i = 0; i<array.length(); i++) {
                        user = gson.fromJson(array.getJSONObject(i).toString(), HXUser.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = user;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                try {
                    if ("3".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "用户不存在");
                    } else if ("10".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else {
                        Tools.showToast(getApplicationContext(), result.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
