package com.tangpo.lianfu.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.PathUtil;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.ui.ChatActivity;
import com.tangpo.lianfu.ui.HomePageActivity;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2016/1/11.
 */
public class NewMessageBroadcastReceiver extends BroadcastReceiver {
    private String my_id;
    public static String latestmsg;
    public static String time;
    public static ChatAccount account = new ChatAccount();

    public NewMessageBroadcastReceiver() {
    }

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
        EMConversation conversation = EMChatManager.getInstance().getConversation(username);
        // 如果是群聊消息，获取到group id
        if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            username = message.getTo();
        }
        if (!username.equals(username)) {
            // 消息不是发给当前会话，return
            return;
        }
        my_id = HomePageActivity.account.getEasemod_id();
        latestmsg = message.getBody().toString().substring(5, message.getBody().toString().length() - 1);
        time = Tools.long2DateString(message.getMsgTime());
        if (message.getType() == EMMessage.Type.IMAGE) {
            latestmsg = "[图片]";
            /*ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
            String filename = imgBody.getFileName();
            Log.e("tag", "filename " + filename + " sub " + filename.substring(filename.lastIndexOf("\\") + 1) + " index " + filename.lastIndexOf("\\"));
            imgBody.setLocalUrl("storage/emulated/0/data/" + filename.substring(filename.lastIndexOf("\\") + 1));
            message.addBody(imgBody);*/
        } else {
            TextMessageBody txtBody = (TextMessageBody) message.getBody();
            latestmsg = txtBody.getMessage();
        }

        conversation.addMessage(message);
        ChatAccount ac = new ChatAccount("", username, message.getUserName(), "", message.getFrom().toLowerCase(), "", "", HomePageActivity.account.getPhoto(), latestmsg, time);
        Tools.saveAccount(ac);
        ac.setType(message.getType());
        account.copy(ac);
        latestmsg = "";
        notifier(context, message, ac);
    }

    /**
     * 消息通知
     * @param message
     */
    private void notifier(Context context, EMMessage message, ChatAccount ac) {
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        CharSequence title = "来自" + message.getUserName() + "的信息";
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
        notification.sound = Uri.parse("file:/ sdcard /notification/ringer.mp3");
        notification.sound = Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "6");
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        Intent i = new Intent(context.getApplicationContext(), ChatActivity.class);
        i.putExtra("account", ac);
        i.putExtra("username", ac.getName());
        i.putExtra("hxid", ac.getEasemod_id());
        i.putExtra("myid", my_id);
        i.putExtra("photo", ac.getPhoto());
        PendingIntent pd = PendingIntent.getActivity(context, 0, i, 0);

        CharSequence msg = message.getUserName();
        notification.setLatestEventInfo(context, title, msg, pd);
        manager.notify(2, notification);
    }
}