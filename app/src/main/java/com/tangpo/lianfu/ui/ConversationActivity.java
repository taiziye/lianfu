package com.tangpo.lianfu.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.StoreServer;
import com.tangpo.lianfu.fragment.ContactFragment;
import com.tangpo.lianfu.fragment.ConversationFragment;
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
public class ConversationActivity extends FragmentActivity implements View.OnClickListener {
    private Button back;
    private TextView name;
    private Button conversation;
    private Button address_list;

    private Gson gson = new Gson();
    private ArrayList<ChatAccount> accounts = new ArrayList<>();
    private ArrayList<StoreServer> servers = new ArrayList<>();
    private String userids = "";
    private String userid = "";
    private String hxid = "";
    private String flag;
    private ChatAccount account = ChatAccount.getInstance();
    private Bundle bundle = new Bundle();

    private Fragment[] fragment = null;
    private int index = 0;
    private int currentIndex = 0;
    private FragmentTransaction transaction = null;

    public String getHxid() {
        return hxid;
    }

    public ArrayList<ChatAccount> getAccounts() {
        return accounts;
    }

    public String getPhoto(){
        return account.getPhoto();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_conversation);
        userid = getIntent().getStringExtra("userid");
        flag = getIntent().getStringExtra("flag");
        fragment = new Fragment[]{new ContactFragment(), new ConversationFragment()};
        init();
        if (flag != null && "1".equals(flag)) {
            getAccounts(userids);
        } else {
            name.setText("会话记录");
            conversation.setSelected(true);
            address_list.setSelected(false);
            index = 1;

            if (!fragment[index].isAdded()) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment[index]).show(fragment[index]).commit();
            }
            currentIndex = index;
        }
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        name = (TextView) findViewById(R.id.name);
        conversation = (Button) findViewById(R.id.btn_conversation);
        conversation.setOnClickListener(this);
        address_list = (Button) findViewById(R.id.address_list);
        address_list.setOnClickListener(this);
        name.setText("客服列表");

        String str = getIntent().getStringExtra("servers");
        if (str != null && str.length() > 0) {
            try {
                JSONArray array = new JSONArray(str);
                JSONObject object;
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
    }

    @Override
    public void onClick(View v) {
        transaction = getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_conversation:  //会话记录
                /*if (account != null) {
                    *//*bundle.putString("photo", account.getPhoto());
                    bundle.putString("hxid", hxid);
                    fragment = new ConversationFragment();
                    name.setText("会话记录");
                    fragment.setArguments(bundle);
                    //conversationListFragment = new EaseConversationListFragment();
                    conversation.setSelected(true);
                    address_list.setSelected(false);*//*
                    name.setText("会话记录");
                    conversation.setSelected(true);
                    address_list.setSelected(false);
                    index = 1;
                } else {
                    Tools.showToast(getApplicationContext(), "登录失败，请重新登陆");
                    finish();
                }*/
                name.setText("会话记录");
                conversation.setSelected(true);
                address_list.setSelected(false);
                index = 1;
                break;
            case R.id.address_list:  //客户列表
                /*if (account != null) {
                    *//*bundle.putString("photo", account.getPhoto());
                    bundle.putString("hxid", hxid);
                    bundle.putSerializable("acstr", accounts);
                    fragment = new ContactFragment();
                    fragment.setArguments(bundle);*//*
                    name.setText("客服列表");
                    conversation.setSelected(false);
                    address_list.setSelected(true);
                    index = 0;
                } else {
                    Tools.showToast(getApplicationContext(), "登录失败，请重新登陆");
                    finish();
                }*/
                name.setText("客服列表");
                conversation.setSelected(false);
                address_list.setSelected(true);
                index = 0;
                break;
        }
        if (account != null) {
            /*transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();*/
            if (currentIndex != index) {
                transaction.hide(fragment[currentIndex]);
                if (!fragment[index].isAdded()) {
                    transaction.add(R.id.fragment_container, fragment[index]);
                }
                transaction.show(fragment[index]).commit();
                currentIndex = index;
            }
        } else {
            Tools.showToast(getApplicationContext(), "登录失败，请重新登陆");
            finish();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:  //客户列表
                    accounts = (ArrayList<ChatAccount>) msg.obj;
                    if (account != null) {
                        transaction = getSupportFragmentManager().beginTransaction();
                        /*fragment = new ContactFragment();
                        bundle.putString("hxid", hxid);
                        bundle.putSerializable("acstr", accounts);
                        bundle.putString("photo", account.getPhoto());
                        fragment.setArguments(bundle);*/
                        name.setText("客服列表");
                        conversation.setSelected(false);
                        address_list.setSelected(true);

                        /*transaction.replace(R.id.fragment_container, fragment);
                        transaction.commit();*/
                        if (!fragment[index].isAdded()) {
                            transaction.add(R.id.fragment_container, fragment[index]).show(fragment[index]).commit();
                        }
                    } else {
                        Tools.showToast(getApplicationContext(), "登录失败，请重新登陆");
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //关闭数据库
        finish();
    }

    /**
     * 获取环信账号
     * @param id
     */
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
                try {
                    JSONArray array = result.getJSONArray("param");
                    for (int i = 0; i<array.length(); i++) {
                        ChatAccount account = gson.fromJson(array.getJSONObject(i).toString(), ChatAccount.class);
                        Log.e("tag", " " + account.toString());
                        accounts.add(account);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = accounts;
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
