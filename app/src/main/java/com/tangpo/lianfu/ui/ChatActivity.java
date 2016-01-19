package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ChatAdapter;
import com.tangpo.lianfu.adapter.ExpressionAdapter;
import com.tangpo.lianfu.adapter.ExpressionPagerAdapter;
import com.tangpo.lianfu.broadcast.NewMessageBroadcastReceiver;
import com.tangpo.lianfu.entity.Chat;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.utils.ExpandGridView;
import com.tangpo.lianfu.utils.SmileUtils;
import com.tangpo.lianfu.utils.Tools;

import java.io.File;
import java.lang.reflect.Field;
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
    //private String photo;
    //private ChatAccount account;
    //private ChatAccount ac;
    private List<String> reslist;

    private InputMethodManager inputMethodManager = null;
    private PullToRefreshListView listView;
    private EMConversation conversation = null;

    private String latestmsg;
    private String time;
    //private List<Chat> list = new ArrayList<Chat>();
    private ChatAdapter adapter = null;
    private boolean isloading;
    private boolean haveMoreData = true;
    //表情
    private boolean flag = false;
    private LinearLayout more;
    private ViewPager vPager;
    private int pageSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        //注销外部广播
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //account = getIntent().getExtras().getParcelable("account");
        //photo = getIntent().getStringExtra("photo");
        username = getIntent().getStringExtra("username");
        hxid = getIntent().getStringExtra("hxid");
        my_id = ChatAccount.getInstance().getEasemod_id();
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /*account = intent.getExtras().getParcelable("account");
        photo = intent.getStringExtra("photo");
        username = intent.getStringExtra("username");
        hxid = intent.getStringExtra("hxid").toLowerCase();
        my_id = intent.getStringExtra("myid").toLowerCase();
        list.clear();
        initView(hxid);*/
        username = intent.getStringExtra("username");
        hxid = intent.getStringExtra("hxid");
        name.setText(username);

        adapter = new ChatAdapter(ChatActivity.this, hxid);
        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        onConversationInit();
        /*Log.e("tag", "onNewIntent " + username);
        if (username.equals(name)) {
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage});
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        //finish();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (latestmsg != null && latestmsg.length() != 0) {
            //account.setMsg(latestmsg);
            //account.setTime(time);
            //Tools.saveAccount(account);
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

        reslist = getExpressionRes(35);
        List<View> views = new ArrayList<>();
        View gv1 = getGridChildView(1);
        View gv2 = getGridChildView(2);
        views.add(gv1);
        views.add(gv2);
        vPager.setAdapter(new ExpressionPagerAdapter(views));

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
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("tag", "refresh");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isloading && haveMoreData) {
                            List<EMMessage> messages;
                            messages = conversation.loadMoreMsgFromDB(adapter.getItem(0).getMsgId(), pageSize);
                            if (messages.size() > 0) {
                                adapter.notifyDataSetChanged();
                                adapter.refreshSeekTo(messages.size() - 1);
                                if (messages.size() != pageSize) {
                                    haveMoreData = false;
                                }
                            } else {
                                haveMoreData = false;
                            }
                            isloading = false;
                        } else {
                            Tools.showToast(ChatActivity.this, "没有更多记录");
                        }
                        listView.onRefreshComplete();
                    }
                }, 1000);
            }
        });
    }

    private void initView(String hxid) {
        name.setText(username);
        adapter = new ChatAdapter(ChatActivity.this, hxid);
        listView.setAdapter(adapter);
        adapter.refreshSelectLast();
        onConversationInit();
        //listView.getRefreshableView().setSelection(list.size() - 1);
        //NewMessageBroadcastReceiver.unregister(ChatActivity.this);
        /*msgReceiver = new MessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);*/
        //NewMessageBroadcastReceiver.unregister(ChatActivity.this);
        EMChat.getInstance().setAppInited();
    }

    public PullToRefreshListView getListView() {
        return listView;
    }

    protected void onConversationInit() {
        conversation = EMChatManager.getInstance().getConversation(hxid);
        conversation.markAllMessagesAsRead();

        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pageSize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pageSize);
        }

        if (conversation.getMsgCount() < 20) {
            Tools.showToast(ChatActivity.this, "聊天记录已全部加载");
            haveMoreData = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(msgReceiver);
        //NewMessageBroadcastReceiver.register(ChatActivity.this);
        finish();
    }

    private void notifier(EMMessage message, ChatAccount ac){
        //NewMessageBroadcastReceiver.notifier(ChatActivity.this, message, ac);
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        CharSequence title = "来自" + message.getUserName() + "的信息";
        String msg = "";
        if (message.getType() == EMMessage.Type.TXT) {
            msg = ((TextMessageBody)message.getBody()).getMessage();
        } else {
            msg = "图片消息";
        }
        /*builder.setContentTitle(title);
        builder.setContentText(msg);
        builder.setSmallIcon(R.drawable.chat);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        Notification notification = builder.build();
        manager.notify((int) System.currentTimeMillis(), notification);

        Intent intent = new Intent(this, UpdatePasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 6, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);*/
        Long when = System.currentTimeMillis();
        Notification notification = new Notification(R.drawable.chat, title, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        long[] vibrate = {0,100,200,300};
        notification.vibrate = vibrate ;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        Intent i = new Intent(ChatActivity.this, ChatActivity.class);
        i.putExtra("username", message.getUserName());
        i.putExtra("hxid", message.getFrom());
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pd = PendingIntent.getActivity(ChatActivity.this, 0, i, 0);  //PendingIntent.FLAG_UPDATE_CURRENT

        notification.setLatestEventInfo(ChatActivity.this, title, msg, pd);
        manager.notify(3, notification);
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
                    hideSoftKeyBoard();
                } else {
                    flag = !flag;
                    expression.setImageResource(R.drawable.biaoqing_btn_normal);
                    more.setVisibility(View.GONE);
                }
                break;
            case R.id.add_img:
                Intent intent = new Intent(ChatActivity.this, SelectPicActivity.class);
                intent.putExtra("flag", "flag");
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
            String picPath = data.getStringExtra(SelectPicActivity.SMALL_KEY_PHOTO_PATH);
            sendImg(picPath);
        }
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        EMMessage message;
        EMConversation conversation;
        Chat chat;
        NewMessageBroadcastReceiver.unread += 1;
        switch (event.getEvent()){
            case EventNewMessage:
                message = (EMMessage) event.getData();
                String user = message.getUserName();
                if (user.equals(hxid)) {
                    refreshUIWithNewMessage();
                } else {
                    ChatAccount ac = new ChatAccount("", username, message.getUserName(), "", message.getFrom().toLowerCase(), "", "", ChatAccount.getInstance().getPhoto(), latestmsg, time);
                    Tools.saveAccount(ac);
                    notifier(message, ac);
                }
                break;
            case EventOfflineMessage:
                refreshUI();
                break;
        }
    }

    private void refreshUI() {
        if(adapter == null){
            return;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refresh();
            }
        });
    }

    private void refreshUIWithNewMessage(){
        if(adapter == null){
            return;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.refreshSelectLast();
            }
        });
    }

    /*private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            NewMessageBroadcastReceiver.unregister(ChatActivity.this);
            abortBroadcast();
            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String user = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation	conversation = EMChatManager.getInstance().getConversation(username);
            // 如果是群聊消息，获取到group id
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                user = message.getTo();
            }
            if (!user.equals(username)) {
                // 消息不是发给当前会话，return
                notifier(message);
                return;
            }
            latestmsg = message.getBody().toString().substring(5, message.getBody().toString().length() - 1);
            time = Tools.long2DateString(message.getMsgTime());

            if (message.getFrom().toLowerCase().equals(hxid)) {
                Chat chat = new Chat(message.getFrom().toLowerCase(), message.getUserName(), photo,  time);
                list.add(chat);
                conversation.addMessage(message);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);
                listView.getRefreshableView().setSelection(list.size() - 1);
            } else {
                ac = new ChatAccount("", username, message.getUserName(), "", message.getFrom().toLowerCase(), "", "", photo, latestmsg, time);
                latestmsg = "";
            }
            Chat chat = new Chat();
            chat.setHxid(message.getFrom().toLowerCase());
            chat.setUsername(message.getUserName());
            if (message.getFrom().equals(my_id)) {
                chat.setImg(photo);
            } else {
                chat.setImg(account.getPhoto());
            }
            chat.setMessage(message);
            chat.setTime(Tools.long2DateString(message.getMsgTime()));
            list.add(chat);
            adapter.notifyDataSetChanged();
            //notifier(message);
        }
    }*/

    /**
     * 消息通知
     * @param message
     */
    /*private void notifier(EMMessage message) {
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
        //i.putExtra("account", ac);
        //i.putExtra("username", ac.getName());
        //i.putExtra("hxid", ac.getEasemod_id());
        //i.putExtra("myid", my_id);
        //i.putExtra("photo", photo);
        PendingIntent pd = PendingIntent.getActivity(this, 0, i, 0);

        CharSequence msg = message.getUserName();
        CharSequence text = latestmsg;
        notification.setLatestEventInfo(ChatActivity.this, title, msg, pd);
        manager.notify(2, notification);
    }*/

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
        adapter.refreshSelectLast();
        chat.getText().clear();
        /*Chat chat = new Chat(my_id, username, photo, new SimpleDateFormat("dd号 HH:mm").format(new Date()));
        chat.setMessage(message);
        list.add(chat);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.getRefreshableView().setSelection(list.size() - 1);*/
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
        EMChatManager.getInstance().updateCurrentUserNick(username);
        //如果是群聊，设置chattype,默认是单聊
        //message.setChatType(EMMessage.ChatType.GroupChat);
        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        // 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
        // body.setSendOriginalImage(true);
        message.addBody(body);
        message.setReceipt(hxid);
        conversation.addMessage(message);
        adapter.refreshSelectLast();
        chat.getText().clear();
        /*Chat chat = new Chat(my_id, username, photo, new SimpleDateFormat("dd号 HH:mm").format(new Date()));
        chat.setMessage(message);
        list.add(chat);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.getRefreshableView().setSelection(list.size() - 1);*/

        EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
            @Override
            public void onSuccess() {
                   //conversation.addMessage(message);
                latestmsg = "[图片]";
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
    /*protected void loadCoversation(String hxid) {
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
            *//*String tmp = msg.getBody().toString();
            int length = tmp.length();
            if (length <= 6) {
                chat.setMsg("");
            } else {
                chat.setMsg(tmp.substring(5, length-1));
            }*//*
            chat.setMessage(msg);
            chat.setTime(Tools.long2DateString(msg.getMsgTime()));
            list.add(chat);
        }
    }*/

    public List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "ee_" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(this, R.layout.expression_gridview, null);
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, reslist.size()));
        }
        list.add("delete_expression");
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = expressionAdapter.getItem(position);
                try {
                    if (filename != "delete_expression") { // 不是删除键，显示表情
                        // 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
                        Class clz = Class.forName("com.tangpo.lianfu.utils.SmileUtils");
                        Field field = clz.getField(filename);
                        chat.append(SmileUtils.getSmiledText(ChatActivity.this,
                                (String) field.get(null)));
                    } else { // 删除文字或者表情
                        if (!TextUtils.isEmpty(chat.getText())) {

                            int selectionStart = chat.getSelectionStart();// 获取光标的位置
                            if (selectionStart > 0) {
                                String body = chat.getText().toString();
                                String tempStr = body.substring(0, selectionStart);
                                int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
                                if (i != -1) {
                                    CharSequence cs = tempStr.substring(i, selectionStart);
                                    if (SmileUtils.containsKey(cs.toString()))
                                        chat.getEditableText().delete(i, selectionStart);
                                    else
                                        chat.getEditableText().delete(selectionStart - 1,
                                                selectionStart);
                                } else {
                                    chat.getEditableText().delete(selectionStart - 1, selectionStart);
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                }
            }
        });
        return view;
    }
}
