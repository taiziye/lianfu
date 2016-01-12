package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ChatAdapter;
import com.tangpo.lianfu.entity.Chat;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.utils.Tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ChatActivity extends Activity implements View.OnClickListener, EMEventListener {
    public static final int CHAT = 9;
    private Button back;
    private TextView name;
    private ImageView expression;
    private ImageView add_img;
    private EditText chat;
    private Button send;
    private String username;
    //private String userid;
    private String hxid;
    private String my_id;
    private String photo;
    private ChatAccount account;
    private ChatAccount ac;

    private InputMethodManager inputMethodManager = null;
    private PullToRefreshListView listView;
    private EMConversation conversation = null;

    private String latestmsg;
    private String time;
    private List<Chat> list = new ArrayList<>();
    private ChatAdapter adapter = null;

    //表情
    private boolean flag = false;
    private LinearLayout more;
    private ViewPager vPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        account = getIntent().getExtras().getParcelable("account");
        //userid = getIntent().getStringExtra("userid");
        photo = getIntent().getStringExtra("photo");
        username = getIntent().getStringExtra("username");
        hxid = getIntent().getStringExtra("hxid").toLowerCase();
        my_id = getIntent().getStringExtra("myid").toLowerCase();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventOfflineMessage});
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        account = intent.getExtras().getParcelable("account");
        photo = intent.getStringExtra("photo");
        username = intent.getStringExtra("username");
        hxid = intent.getStringExtra("hxid").toLowerCase();
        my_id = intent.getStringExtra("myid").toLowerCase();
        list.clear();
        initView(hxid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (latestmsg != null && latestmsg.length() != 0) {
            account.setMsg(latestmsg);
            account.setTime(time);
            Tools.saveAccount(account);
        }
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);

        expression = (ImageView) findViewById(R.id.expression);
        expression.setOnClickListener(this);
        add_img = (ImageView) findViewById(R.id.add_img);
        add_img.setOnClickListener(this);
        chat = (EditText) findViewById(R.id.chat);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
        more = (LinearLayout) findViewById(R.id.more);
        vPager = (ViewPager) findViewById(R.id.vPager);

        chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    send.setVisibility(View.VISIBLE);
                    add_img.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    add_img.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView = (PullToRefreshListView) findViewById(R.id.list);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard();
                return false;
            }
        });
        initView(hxid);
    }

    private void initView(String hxid) {
        name.setText(username);

        loadCoversation(hxid);
        adapter = new ChatAdapter(ChatActivity.this, list, my_id);
        listView.setAdapter(adapter);
        listView.getRefreshableView().setSelection(list.size() - 1);

        /*msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);*/
        EMChat.getInstance().setAppInited();
    }

    private NewMessageBroadcastReceiver msgReceiver;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(msgReceiver);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.expression:
                if (!flag) {
                    flag = !flag;
                    expression.setImageResource(R.drawable.biaoqing_btn_enable);
                    more.setVisibility(View.VISIBLE);
                } else {
                    flag = !flag;
                    expression.setImageResource(R.drawable.biaoqing_btn_normal);
                    more.setVisibility(View.GONE);
                }
                break;
            case R.id.add_img:
                Intent intent = new Intent(ChatActivity.this, SelectPicActivity.class);
                startActivityForResult(intent, CHAT);
                break;
            case R.id.send:
                if (chat.getText().toString().trim().length() == 0) {
                    Tools.showToast(ChatActivity.this, "发送消息不能为空，请重新输入");
                } else {
                    sendMsg(chat.getText().toString());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            sendImg(picPath);
        }
    }

    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        if (emNotifierEvent.getEvent() == EMNotifierEvent.Event.EventOfflineMessage) {
            EMMessage message = (EMMessage) emNotifierEvent.getData();
            Log.e("tag", "msg " + message.toString());
        }
        Log.e("tag", "message " + ((EMMessage)emNotifierEvent.getData()).toString() + " event " + emNotifierEvent.getEvent().toString());
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();

            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation	conversation = EMChatManager.getInstance().getConversation(username);
            // 如果是群聊消息，获取到group id
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                username = message.getTo();
            }
            if (!username.equals(username)) {
                // 消息不是发给当前会话，return
                return;
            }
            Log.e("tag", message.toString());
            Log.e("tag", "myid " + my_id + " hxid " + hxid);
            latestmsg = message.getBody().toString().substring(5, message.getBody().toString().length() - 1);
            time = Tools.long2DateString(message.getMsgTime());

            if (message.getFrom().toLowerCase().equals(hxid)) {
                Chat chat = new Chat(message.getFrom().toLowerCase(), message.getUserName(), photo, latestmsg, time);
                list.add(chat);
                conversation.addMessage(message);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                listView.getRefreshableView().setSelection(list.size() - 1);
            } else {
                ac = new ChatAccount("", username, message.getUserName(), "", message.getFrom().toLowerCase(), "", "", photo, latestmsg, time);
                latestmsg = "";
            }
            notifier(message);
        }
    }

    /**
     * 消息通知
     * @param message
     */
    private void notifier(EMMessage message) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        CharSequence title = "来自" + message.getUserName() + "的信息";
        Long when = System.currentTimeMillis();
        Notification notification = new Notification(R.drawable.chat, title, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.ledARGB = 0xff00ff00;

        notification.ledOnMS = 300;

        notification.ledOffMS = 1000;

        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        Intent i = new Intent(ChatActivity.this, ChatActivity.class);
        i.putExtra("account", ac);
        Log.e("tag", "ac " + ac.toString());
        i.putExtra("username", ac.getName());
        i.putExtra("hxid", ac.getEasemod_id());
        i.putExtra("myid", my_id);
        i.putExtra("photo", photo);
        PendingIntent pd = PendingIntent.getActivity(this, 0, i, 0);

        CharSequence msg = message.getUserName();
        CharSequence text = latestmsg;
        notification.setLatestEventInfo(ChatActivity.this, title, msg, pd);
        manager.notify(2, notification);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //conversation.addMessage((EMMessage) msg.obj);
                    time = new SimpleDateFormat("dd号 HH:mm").format(new Date());
                    break;
                case 2:
                    Tools.showToast(getApplicationContext(), "发送失败，请重新发送");
                    break;
                case 3:
                    time = new SimpleDateFormat("dd号 HH:mm").format(new Date());
                    break;
            }
        }
    };

    /**
     * 发送文本消息
     * @param msg
     */
    private void sendMsg(final String msg) {
        //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance().getConversation(hxid);
        EMChatManager.getInstance().updateCurrentUserNick(username);
        //创建一条文本消息
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //如果是群聊，设置chattype,默认是单聊
        //message.setChatType(EMMessage.ChatType.GroupChat);
        //设置消息body
        TextMessageBody txtBody = new TextMessageBody(msg);
        message.addBody(txtBody);
        //设置接收人
        message.setReceipt(hxid);
        //把消息加入到此会话对象中
        conversation.addMessage(message);
        chat.getText().clear();
        Chat chat = new Chat(my_id, username, photo, msg, new SimpleDateFormat("dd号 HH:mm").format(new Date()));
        list.add(chat);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.getRefreshableView().setSelection(list.size() - 1);
        //发送消息
        EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
            @Override
            public void onSuccess() {
                //把消息加入到此会话对象中
                latestmsg = msg;
                Message msg = new Message();
                msg.what = 1;
                msg.obj = message;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(int i, String s) {
                //Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_LONG).show();
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    /**
     * 发送图片
     * @param filePath  图片路径
     */
    private void sendImg(final String filePath) {
        final EMConversation conversation = EMChatManager.getInstance().getConversation(hxid);
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);

        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        message.setReceipt(hxid);
        conversation.addMessage(message);

        conversation.addMessage(message);
        chat.getText().clear();
        Chat chat = new Chat(my_id, username, photo, filePath, new SimpleDateFormat("dd号 HH:mm").format(new Date()));
        list.add(chat);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.getRefreshableView().setSelection(list.size() - 1);

        EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
            @Override
            public void onSuccess() {
                   //conversation.addMessage(message);
                latestmsg = filePath;
                Message msg = new Message();
                msg.what = 3;
                handler.sendMessage(msg);
            }

            @Override
            public void onError(int i, String s) {
                //Tools.showToast(ChatActivity.this, "发送失败");
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    private void hideSoftKeyBoard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 重发消息
     */
    /*private void resendMessage() {
        EMMessage msg = null;
        msg = conversation.getMessage(resendPos);
        // msg.setBackSend(true);
        msg.status = EMMessage.Status.CREATE;

        adapter.refreshSeekTo(resendPos);
    }*/

    /**
     * 加载聊天记录
     * @return
     */
    protected void loadCoversation(String hxid) {
        conversation = EMChatManager.getInstance().getConversationByType(hxid, EMConversation.EMConversationType.Chat);
        conversation.markAllMessagesAsRead();

        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs!=null ? msgs.size():0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < 20) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, 20);
        }

        int count = conversation.getAllMessages().size();
        for (int i=0; i<count; i++) {
            EMMessage msg = conversation.getMessage(i);
            Chat chat = new Chat();
            chat.setHxid(msg.getFrom().toLowerCase());
            chat.setUsername(msg.getUserName());
            if (msg.getFrom().equals(my_id)) {
                chat.setImg(photo);
            } else {
                chat.setImg(account.getPhoto());
            }
            String tmp = msg.getBody().toString();
            int length = tmp.length();
            if (length <= 6) {
                chat.setMsg("");
            } else {
                chat.setMsg(tmp.substring(5, length-1));
            }
            chat.setTime(Tools.long2DateString(msg.getMsgTime()));
            list.add(chat);
        }
    }
}
