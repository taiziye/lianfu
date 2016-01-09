package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.StoreServer;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetChatAccount;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2015/12/15.
 */
public class ConversationActivity extends Activity implements View.OnClickListener {
    private Button back;
    private TextView name;
    private Button conversation;
    private Button address_list;

    private Gson gson = new Gson();
    //private ArrayList<ChatAccount> accounts = new ArrayList<>();
    private String acStr = "";
    private String serverStr = "";
    private ArrayList<StoreServer> servers = new ArrayList<>();
    private String userids = "";
    private String userid = "";
    private ChatAccount account = null;

    private ConversationFragment conversationFragment = null;
    private ContactFragment contactFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_conversation);
        userid = getIntent().getStringExtra("userid");
        getAccounts(userid);
        //EMGroupManager.getInstance().loadAllGroups();
        //EMChatManager.getInstance().loadAllConversations();
        conversationFragment = new ConversationFragment();
        contactFragment = new ContactFragment();
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        conversation = (Button) findViewById(R.id.btn_conversation);
        conversation.setOnClickListener(this);
        address_list = (Button) findViewById(R.id.address_list);
        address_list.setOnClickListener(this);
        name.setText("客服列表");
        address_list.setSelected(true);

        String str = getIntent().getStringExtra("servers");
        try {
            JSONArray array = new JSONArray(str);
            JSONObject object = null;
            for (int i=0; i<array.length(); i++) {
                object = array.getJSONObject(i);
                StoreServer server = gson.fromJson(object.toString(), StoreServer.class);
                servers.add(server);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i=0; i<servers.size(); i++) {
            if (i != 0)  userids += ",";
            userids += servers.get(i).getUser_id();
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.back:
                //退出环信登陆
                //EMChatManager.getInstance().logout();
                finish();
                break;
            case R.id.btn_conversation:
                transaction.hide(contactFragment);
                if (!conversationFragment.isAdded())transaction.add(0, conversationFragment);
                bundle.putString("acstr", acStr);
                transaction.show(conversationFragment).commit();
                name.setText("会话记录");
                conversation.setSelected(true);
                break;
            case R.id.address_list:
                transaction.hide(conversationFragment);
                if (!contactFragment.isAdded())transaction.add(0, contactFragment);
                bundle.putString("acstr", acStr);
                transaction.show(contactFragment).commit();
                name.setText("客服列表");
                address_list.setSelected(true);
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:  //客户列表
                    //accounts = (ArrayList<ChatAccount>) msg.obj;
                    acStr = (String) msg.obj;
                    /*FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    if (!conversationFragment.isAdded())transaction.add(0, conversationFragment);
                    bundle.putString("acstr", acStr);
                    transaction.show(conversationFragment).commit();*/
                    break;
                case 2:
                    account = ((ArrayList<ChatAccount>) msg.obj).get(0);
                    if (!EMChat.getInstance().isLoggedIn()) {
                        //未登录
                        login();
                    }
                    break;
            }
        }
    };

    private void login() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(ConversationActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        final long start = System.currentTimeMillis();
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(account.getEasemod_id(), account.getPwd(), new EMCallBack() {
            @Override
            public void onSuccess() {
                //
                //EMGroupManager.getInstance().loadAllGroups();
                dialog.dismiss();
                Log.e("tag", "登陆成功");
                getAccounts(userids);
                EMChatManager.getInstance().loadAllConversations();
            }

            @Override
            public void onError(int i, String s) {
                //
                dialog.dismiss();
                Log.e("tag", "登陆失败 " + s);
            }

            @Override
            public void onProgress(int i, String s) {
                //
            }
        });
    }

    private void getAccounts(final String id) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs = new String[]{id};
        String param = GetChatAccount.packagingParam(getApplicationContext(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                String tmp = "";
                try {
                    tmp = result.getJSONArray("param").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                if (userids.equals(id)) msg.what = 1;
                else msg.what = 2;
                msg.obj = tmp;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
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
