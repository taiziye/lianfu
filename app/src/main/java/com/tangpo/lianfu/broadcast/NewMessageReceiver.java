package com.tangpo.lianfu.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.ui.ChatActivity;

/**
 * Created by 果冻 on 2016/1/11.
 */
public class NewMessageReceiver {
    //private static int unread = EMChatManager.getInstance().getUnreadMsgsCount();
    private static String my_id = ChatAccount.getInstance().getEasemod_id();
    public static String time;
    public static ChatAccount account = new ChatAccount();
    private String packageName = null;

    public NewMessageReceiver(Context context) {
        packageName = context.getApplicationInfo().packageName;
    }

    /*public static void setUnread(int num) {
        unread += num;
    }*/

    public static int getUnread() {
        Log.e("tag", "count " + EMChatManager.getInstance().getUnreadMsgsCount() + " id " + ChatAccount.getInstance().getEasemod_id());
        return EMChatManager.getInstance().getUnreadMsgsCount();
    }

    /**
     * 消息通知
     * @param message
     */
    public static void notifier(Context context, EMMessage message, ChatAccount ac) {
        Log.e("tag", "notifier ");
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

        /*PackageManager packageManager = context.getPackageManager();
        String appname = (String) packageManager.getApplicationLabel(context.getApplicationInfo());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
        Intent msgIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);*/

        Intent i = new Intent(context.getApplicationContext(), ChatActivity.class);
        i.putExtra("account", ac);
        i.putExtra("username", ac.getName());
        i.putExtra("hxid", ac.getEasemod_id());
        i.putExtra("myid", my_id);
        i.putExtra("photo", ac.getPhoto());
        PendingIntent pd = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        String msg = "";
        if (message.getType() == EMMessage.Type.TXT) {
            msg = ((TextMessageBody)message.getBody()).getMessage();
        } else {
            msg = "图片消息";
        }
        notification.setLatestEventInfo(context, title, msg, pd);
        manager.notify((int) System.currentTimeMillis(), notification);
    }
}
