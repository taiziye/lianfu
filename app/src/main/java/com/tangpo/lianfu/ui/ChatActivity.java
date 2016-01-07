package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

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
public class ChatActivity extends Activity implements View.OnClickListener {
    private Button back;
    private TextView name;
    private Button conversation;
    private Button address_list;

    private ProgressDialog dialog = null;
    private Gson gson = new Gson();
    private ArrayList<ChatAccount> accounts = new ArrayList<>();
    private ArrayList<StoreServer> servers = new ArrayList<>();
    private String userids = "";
    private String userid = "";
    private ChatAccount account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        userid = getIntent().getStringExtra("userid");
        getAccounts(userid);
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
        conversation.setSelected(true);

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
        getAccounts(userids);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    accounts = (ArrayList<ChatAccount>) msg.obj;
                    break;
                case 2:
                    account = ((ArrayList<ChatAccount>) msg.obj).get(0);
                    break;
            }
        }
    };

    private void getAccounts(final String id) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs = new String[]{id};
        String param = GetChatAccount.packagingParam(getApplicationContext(), kvs);
        if (userids.equals(id)) dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                if (userids.equals(id)) dialog.dismiss();
                JSONObject object = null;
                try {
                    JSONArray array = result.getJSONArray("param");
                    for (int i=0; i<array.length(); i++) {
                        object = array.getJSONObject(i);
                        Log.e("tag", object.toString());
                        ChatAccount account = gson.fromJson(object.toString(), ChatAccount.class);
                        accounts.add(account);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                if (userids.equals(id)) msg.what = 1;
                else msg.what = 2;
                msg.obj = accounts;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                if (userids.equals(id)) dialog.dismiss();
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
