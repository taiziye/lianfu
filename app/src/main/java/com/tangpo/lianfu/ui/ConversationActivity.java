package com.tangpo.lianfu.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.HXUser;
import com.tangpo.lianfu.entity.InvitedMessage;
import com.tangpo.lianfu.entity.StoreServer;
import com.tangpo.lianfu.fragment.ContactFragment;
import com.tangpo.lianfu.fragment.ConversationFragment;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetChatAccount;
import com.tangpo.lianfu.parms.GetSpecifyHX;
import com.tangpo.lianfu.utils.EaseNotifier;
import com.tangpo.lianfu.utils.EaseUI;
import com.tangpo.lianfu.utils.InviteMessageDao;
import com.tangpo.lianfu.utils.Tools;
import com.tangpo.lianfu.utils.UserDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by 果冻 on 2015/12/15.
 */
public class ConversationActivity extends FragmentActivity implements View.OnClickListener {
    public static final String ACTION_CONTACT_CHANGED = "action_contact_changed";
    public static final String ACTION_GROUP_CHANGED = "action_group_changed";

    private Button back;
    private TextView name;
    private Button conversation;
    private Button address_list;
    private Button add;

    private Gson gson = new Gson();
    private ArrayList<ChatAccount> accounts = new ArrayList<>();  //客服
    //private ArrayList<ChatAccount> friends = new ArrayList<>();  //好友
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

    private Map<String, ChatAccount> contactList;
    private UserDao userDao = new UserDao();
    private InviteMessageDao inviteMessageDao = new InviteMessageDao(this);
    private Map<String, ChatAccount> localUsers;
    private Map<String, ChatAccount> toAddUsers;
    private List<HXUser> users = new ArrayList<>();
    private FragmentManager fragmentManager = getSupportFragmentManager();

    public String getHxid() {
        return hxid;
    }

    public ArrayList<ChatAccount> getAccounts() {
        return accounts;
    }

    public ArrayList<ChatAccount> getFriendList() {
        ArrayList<ChatAccount> list = new ArrayList<>(localUsers.values());
        return list;
    }

    public void deleteFriend(String userId) {
        if(localUsers.containsKey(userId)) {
            localUsers.remove(userId);
        }
        userDao.deleteContact(userId);
        inviteMessageDao.deleteMessage(userId);
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
        localUsers = new Hashtable<>();
        registerBroadcastReceiver();
        dealRequest();

        getFriends();
        init();
        if (flag != null && "1".equals(flag)) {
            getAccounts(userids, 1);
        }
        /*else {
            name.setText("会话记录");
            conversation.setSelected(true);
            address_list.setSelected(false);
            index = 1;

            if (!fragment[index].isAdded()) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment[index]).show(fragment[index]).commit();
            }
            currentIndex = index;
        }*/
        name.setText("会话记录");
        conversation.setSelected(true);
        address_list.setSelected(false);
        index = 1;

        /*if (!fragment[index].isAdded()) {
            fragmentManager.beginTransaction().add(R.id.fragment_container, fragment[index]).show(fragment[index]).commit();
        }
        currentIndex = index;*/
        replaceFragment(index, currentIndex);
    }

    private void replaceFragment(int index, int currentIndex) {
        if(currentIndex != index || (currentIndex==0 && index == 0)) {
            /*transaction = fragmentManager.beginTransaction();
            *//*transaction.hide(fragment[currentIndex]);
            if (!fragment[index].isAdded()) {
                transaction.add(R.id.frame, fragment[index]);
            }
            transaction.show(fragment[index]);*//*
            if (!fragment[index].isAdded()) {
                transaction.add(R.id.fragment_container, fragment[index]);
            }
            transaction.commit();
            this.currentIndex = index;*/
            transaction = fragmentManager.beginTransaction();
            if (currentIndex != index) {
                transaction.hide(fragment[currentIndex]);
                if (!fragment[index].isAdded()) {
                    transaction.add(R.id.fragment_container, fragment[index]);
                }
                transaction.show(fragment[index]).commit();
                this.currentIndex = index;
            }
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
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);
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
        //transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_conversation:  //会话记录
                name.setText("会话记录");
                conversation.setSelected(true);
                address_list.setSelected(false);
                index = 1;
                break;
            case R.id.address_list:  //客户列表
                name.setText("客服列表");
                conversation.setSelected(false);
                address_list.setSelected(true);
                index = 0;
                break;
            case R.id.add:  //添加好友
                if(account == null) {
                    Tools.showToast(getApplicationContext(), "请登录后操作");
                    return;
                }
                Intent intent = new Intent(this, AddFriendsActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("flag", flag);
                intent.putExtra("name", account.getName());
                startActivity(intent);
                break;
        }
        if (account != null) {
            /*if (currentIndex != index) {
                transaction.hide(fragment[currentIndex]);
                if (!fragment[index].isAdded()) {
                    transaction.add(R.id.fragment_container, fragment[index]);
                }
                transaction.show(fragment[index]).commit();
                currentIndex = index;
            }*/
            replaceFragment(index, currentIndex);
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
                        transaction = fragmentManager.beginTransaction();
                        name.setText("会话记录");
                        conversation.setSelected(true);
                        address_list.setSelected(false);

                        if (!fragment[index].isAdded()) {
                            transaction.add(R.id.fragment_container, fragment[index]).show(fragment[index]).commit();
                        }
                    } else {
                        Tools.showToast(getApplicationContext(), "登录失败，请重新登陆");
                        finish();
                    }
                    break;
                /*case 2: //好友列表
                    friends = (ArrayList<ChatAccount>) msg.obj;
                    *//*if (account != null) {
                        transaction = getSupportFragmentManager().beginTransaction();
                        name.setText("客服列表");
                        conversation.setSelected(false);
                        address_list.setSelected(true);

                        if (!fragment[index].isAdded()) {
                            transaction.add(R.id.fragment_container, fragment[index]).show(fragment[index]).commit();
                        }
                    } else {
                        Tools.showToast(getApplicationContext(), "登录失败，请重新登陆");
                        finish();
                    }*//*
                    toAddUsers = new HashMap<String, ChatAccount>();
                    for (ChatAccount user : friends) {
                        if(!localUsers.containsKey(user.getEasemod_id())){
                            userDao.saveContact(user);
                        }
                        toAddUsers.put(user.getEasemod_id(), user);
                    }
                    localUsers.putAll(toAddUsers);
                    broadcastManager.sendBroadcast(new Intent(ACTION_CONTACT_CHANGED));
                    break;*/
                case 3:
                    String ids = (String) msg.obj;
                    getHX(ids, 4);
                    break;
                case 4:
                    users = (List<HXUser>) msg.obj;
                    for (int i=0; i<users.size(); i++) {
                        ChatAccount user = new ChatAccount(users.get(i).getUser_id(), users.get(i).getUsername(), users.get(i).getName(), users.get(i).getPhone(), users.get(i).getEasemod_id(), users.get(i).getUuid(), "", users.get(i).getPhoto(), "", "");
                        localUsers.put(users.get(i).getEasemod_id(), user);
                    }
                    break;
                case 5:
                    users = (List<HXUser>) msg.obj;
                    for (int i=0; i<users.size(); i++) {
                        if(!localUsers.containsKey(users.get(i).getEasemod_id())) {
                            ChatAccount user = new ChatAccount(users.get(i).getUser_id(), users.get(i).getUsername(), users.get(i).getName(), users.get(i).getPhone(), users.get(i).getEasemod_id(), users.get(i).getUuid(), "", users.get(i).getPhoto(), "", "");
                            //userDao.saveContact(user);
                            localUsers.put(users.get(i).getEasemod_id(), user);
                        }
                    }
                    broadcastManager.sendBroadcast(new Intent(ACTION_CONTACT_CHANGED));
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
    private void getAccounts(final String id, final int flag) {
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
                        accounts.add(account);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = flag;
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

    /**
     * 根据环信账号获取账户信息
     * @param id
     */
    private void getHX(final String id, final int flag) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        if(id.length() <1 ) return;

        String[] kvs = new String[]{id};
        String param = GetSpecifyHX.packagingParam(ConversationActivity.this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray array = result.getJSONArray("param");
                    for (int i = 0; i<array.length(); i++) {
                        HXUser user = gson.fromJson(array.getJSONObject(i).toString(), HXUser.class);
                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = flag;
                msg.obj = users;
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

    /**
     * 获取好友
     */
    private void getFriends() {
        new Thread(){
            public void run(){
                List<String> userNames;
                String ids = "";
                try {
                    userNames = EMContactManager.getInstance().getContactUserNames();
                    for(int i=0; i<userNames.size(); i++){
                        if(i!=0) ids += ',';
                        ids += userNames.get(i);
                    }
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
                Log.e("tag", "getFriends " + ids);
                if(ids.length() !=0 ) {
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = ids;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    public int getUnread() {
        Log.e("tag", "count " + inviteMessageDao.getUnreadMessagesCount());
        return inviteMessageDao.getUnreadMessagesCount();
    }

    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;

    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CONTACT_CHANGED);
        filter.addAction(ACTION_GROUP_CHANGED);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /*if(currentIndex == 1) {
                    if(fragment[currentIndex] != null) {
                        ((ConversationFragment)fragment[currentIndex]).setMsgView(1);
                    }
                } else if(currentIndex == 0) {
                    if (fragment[currentIndex] != null) {
                        ((ContactFragment)fragment[currentIndex]).refresh();
                    }
                }*/
                if(currentIndex == 1) ((ConversationFragment)fragment[currentIndex]).setMsgView(1);
                else ((ContactFragment)fragment[currentIndex]).refresh();
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    private void unregisterBroadcastReceiver() {
        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    public Map<String, ChatAccount> getContactList() {
        if(contactList == null) {
            contactList = userDao.getContactList();
        }
        if (contactList == null) {
            return new Hashtable<String, ChatAccount>();
        }
        return contactList;
    }

    /**
     * 监听好友请求，处理好友请求
     */
    private void dealRequest(){
        EMChatManager.getInstance().getChatOptions().setAcceptInvitationAlways(false);
        EMContactManager.getInstance().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(List<String> list) {
                //添加好友，同意对方的好友请求，被调用
                String ids = "";
                int count = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (!localUsers.containsKey(list.get(i))) {
                        if (count != 0) ids += ",";
                        ids += list.get(i);
                        count++;
                    }
                }
                getHX(ids, 5);
                //broadcastManager.sendBroadcast(new Intent(ACTION_CONTACT_CHANGED));
            }

            @Override
            public void onContactDeleted(List<String> list) {
                //被删除
                //Map<String, ChatAccount> localUsers = userDao.getContactList();
                for (int i = 0; i < list.size(); i++) {
                    if (!localUsers.containsKey(list.get(i))) {
                        localUsers.remove(list.get(i));
                    }
                    userDao.deleteContact(list.get(i));
                    inviteMessageDao.deleteMessage(list.get(i));
                }
                broadcastManager.sendBroadcast(new Intent(ACTION_CONTACT_CHANGED));
            }

            @Override
            public void onContactInvited(String s, String s1) {
                //收到好友请求
                List<InvitedMessage> msgs = inviteMessageDao.getMessageList();
                for (InvitedMessage message : msgs) {
                    if (message.getFrom().equals(s)) {
                        inviteMessageDao.deleteMessage(s);
                    }
                }
                InvitedMessage msg = new InvitedMessage();
                msg.setFrom(s);
                msg.setTime(System.currentTimeMillis());
                msg.setReason(s1);
                msg.setStatus(InvitedMessage.InviteMessageStatus.BEINVITEED);
                inviteMessageDao.saveMessage(msg);
                notifyNewInviteMessage(msg);
                broadcastManager.sendBroadcast(new Intent(ACTION_CONTACT_CHANGED));
            }

            @Override
            public void onContactAgreed(String s) {
                //好友请求被同意
                List<InvitedMessage> msgs = inviteMessageDao.getMessageList();
                for (InvitedMessage msg : msgs) {
                    if (msg.getFrom().equals(s)) {
                        return;
                    }
                }
                InvitedMessage msg = new InvitedMessage();
                msg.setFrom(s);
                msg.setTime(System.currentTimeMillis());
                msg.setStatus(InvitedMessage.InviteMessageStatus.BEAGREED);
                notifyNewInviteMessage(msg);
                broadcastManager.sendBroadcast(new Intent(ACTION_CONTACT_CHANGED));
            }

            @Override
            public void onContactRefused(String s) {
                //被拒绝
                Tools.showToast(ConversationActivity.this, s + "拒绝了你的好友申请");
            }
        });
        EMChat.getInstance().setAppInited();
    }

    private void notifyNewInviteMessage(InvitedMessage msg) {
        if(inviteMessageDao == null) {
            inviteMessageDao = new InviteMessageDao(this);
        }
        inviteMessageDao.saveMessage(msg);
        //saveUnreadMessageCount(1);
        inviteMessageDao.saveUnreadMessageCount(1);
        //
        getNotifier().viberateAndPlayTone(null);
    }

    public void saveUnreadMessageCount(int count) {
        inviteMessageDao.saveUnreadMessageCount(1);
    }

    public EaseNotifier getNotifier() {
        return EaseUI.getInstance().getNotifier();
    }
}
