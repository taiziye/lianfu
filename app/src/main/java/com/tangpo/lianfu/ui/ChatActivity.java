package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.tangpo.lianfu.utils.DataHelper;
import com.tangpo.lianfu.utils.Tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ChatActivity extends Activity implements View.OnClickListener {
    public static ChatActivity activityInstance;
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

    private InputMethodManager inputMethodManager = null;
    private PullToRefreshListView listView;
    private EMConversation conversation = null;
    private DataHelper helper = null;

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
        hxid = getIntent().getStringExtra("hxid");
        my_id = getIntent().getStringExtra("myid");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper = new DataHelper(ChatActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (latestmsg != null) {
            account.setMsg(latestmsg);
            account.setTime(time);
            helper.saveChatAccount(account);
        }
        helper.close();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        name.setText(username);
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
        loadCoversation();
        adapter = new ChatAdapter(ChatActivity.this, list, my_id);
        listView.setAdapter(adapter);
        listView.getRefreshableView().setSelection(list.size() - 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        Chat chat = new Chat(hxid, username, photo, msg, new SimpleDateFormat("dd号 HH:mm").format(new Date()));
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
    private void senImg(final String filePath) {
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
     * 加载聊天记录
     * @return
     */
    protected void loadCoversation() {
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
            chat.setHxid(msg.getFrom());
            chat.setUsername(username);
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
