package com.tangpo.lianfu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.broadcast.NewMessageBroadcastReceiver;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.ui.ChatActivity;
import com.tangpo.lianfu.ui.PictureActivity;
import com.tangpo.lianfu.utils.CircularImage;
import com.tangpo.lianfu.utils.ImageCache;
import com.tangpo.lianfu.utils.ImageUtils;
import com.tangpo.lianfu.utils.LoadImageTask;
import com.tangpo.lianfu.utils.SmileUtils;
import com.tangpo.lianfu.utils.Tools;

import java.io.File;

/**
 * Created by 果冻 on 2015/12/15.
 */
public class ChatAdapter extends BaseAdapter {
    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private LayoutInflater inflater;
    private Activity context;
    private ImageMessageBody imgBody;
    private TextMessageBody txtBody;
    private Bitmap bm;
    private String username;
    private EMMessage[] messages = null;
    private EMConversation conversation;
    //private String url;

    public ChatAdapter(Context context, String username) {
        this.context = (Activity)context;
        this.username = username;
        this.inflater = LayoutInflater.from(context);
        this.conversation = EMChatManager.getInstance().getConversation(username);
        NewMessageBroadcastReceiver.unread -= this.conversation.getUnreadMsgCount();
        this.conversation.markAllMessagesAsRead();
    }

    Handler handler = new Handler(){
        private void refreshList() {
            messages = (EMMessage[])conversation.getAllMessages().toArray(new EMMessage[conversation.getAllMessages().size()]);
            for (int i=0; i<messages.length; i++) {
                conversation.getMessage(i);
            }
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (context instanceof ChatActivity) {
                        PullToRefreshListView listView = ((ChatActivity)context).getListView();
                        if (messages.length > 0) {
                            listView.getRefreshableView().setSelection(messages.length - 1);
                        }
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = msg.arg1;
                    if (context instanceof ChatActivity) {
                        PullToRefreshListView listView = ((ChatActivity)context).getListView();
                        listView.getRefreshableView().setSelection(position);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getCount() {
        return conversation.getAllMessages().size();
    }

    @Override
    public EMMessage getItem(int position) {
        return conversation.getMessage(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final EMMessage message = getItem(position);
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_list, parent, false);
            holder.he = (LinearLayout) convertView.findViewById(R.id.he);
            holder.me = (LinearLayout) convertView.findViewById(R.id.me);
            holder.heimg = (CircularImage) convertView.findViewById(R.id.heimg);
            holder.meimg = (CircularImage) convertView.findViewById(R.id.meimg);
            holder.he_text = (TextView) convertView.findViewById(R.id.he_text);
            holder.me_text = (TextView) convertView.findViewById(R.id.me_text);
            holder.time1 = (TextView) convertView.findViewById(R.id.time1);
            holder.time2 = (TextView) convertView.findViewById(R.id.time2);
            holder.img1 = (ImageView) convertView.findViewById(R.id.img1);
            holder.img2 = (ImageView) convertView.findViewById(R.id.img2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.me.setVisibility(View.VISIBLE);
        holder.he.setVisibility(View.VISIBLE);
        //String my_id = hxid.toLowerCase();
        //String he_id = list.get(position).getHxid().toLowerCase();
        //EMMessage message = list.get(position).getMessage();
        //根据数据设置holder要显示的frame
        if ( message.direct == EMMessage.Direct.SEND ) {  //根据情形是否需要显示
            holder.he.setVisibility(View.GONE);
            if (message.getType() == EMMessage.Type.IMAGE) { //图片
                //imgBody = list.get(position).getImgBody();
                holder.me_text.setVisibility(View.GONE);
                holder.img2.setVisibility(View.VISIBLE);
                holder.img2.setClickable(false);
                //setImage(imgBody, context, holder.img2);
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    handleImageMessage(message, holder.img2, position, convertView);
                } else {
                    imgBody = (ImageMessageBody) message.getBody();
                    bm = BitmapFactory.decodeFile(imgBody.getLocalUrl());
                    holder.img2.setImageBitmap(bm);
                    setOnClickable(holder.img2, imgBody.getLocalUrl());
                }
            } else {
                holder.me_text.setVisibility(View.VISIBLE);
                holder.img2.setVisibility(View.GONE);
                handleTextMessage(message, holder.me_text);
            }
            holder.time2.setText(Tools.long2DateString(message.getMsgTime()));
            Tools.setPhoto(context, ChatAccount.getInstance().getPhoto(), holder.meimg);
        } else {
            holder.me.setVisibility(View.GONE);
            if (message.getType() == EMMessage.Type.IMAGE) { //图片
                //imgBody = list.get(position).getImgBody();
                holder.he_text.setVisibility(View.GONE);
                holder.img1.setVisibility(View.VISIBLE);
                holder.img1.setClickable(false);
                //setImage(imgBody, context, holder.img1);
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    handleImageMessage(message, holder.img1, position, convertView);
                } else {
                    imgBody = (ImageMessageBody) message.getBody();
                    bm = BitmapFactory.decodeFile(imgBody.getLocalUrl());
                    holder.img1.setImageBitmap(bm);
                    setOnClickable(holder.img1, imgBody.getLocalUrl());
                }
            } else {
                holder.he_text.setVisibility(View.VISIBLE);
                holder.img1.setVisibility(View.GONE);
                handleTextMessage(message, holder.he_text);
            }
            holder.time1.setText(Tools.long2DateString(message.getMsgTime()));
            Tools.setPhoto(context, ChatAccount.getInstance().getPhoto(), holder.heimg);
        }
        return convertView;
    }

    class ViewHolder{
        LinearLayout he;
        LinearLayout me;
        CircularImage heimg;
        CircularImage meimg;
        TextView he_text;
        TextView me_text;
        TextView time1;
        TextView time2;
        ImageView img1;
        ImageView img2;
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * 刷新页面, 选择最后一个
     */
    public void refreshSelectLast() {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
    }

    private void setOnClickable(ImageView view, final String url) {
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PictureActivity.class);
                intent.putExtra("flag", "url");
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });
    }

    /**
     * 文本消息
     *
     * @param message
     * @param txt
     */
    private void handleTextMessage(EMMessage message, TextView txt) {
        txtBody = (TextMessageBody) message.getBody();
        Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
        // 设置内容
        txt.setText(span, TextView.BufferType.SPANNABLE);
    }

    /**
     * 图片消息
     *
     * @param message
     * @param view
     * @param position
     * @param convertView
     */
    private void handleImageMessage(final EMMessage message, final ImageView view, final int position, View convertView) {
        //
        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.status == EMMessage.Status.INPROGRESS) {
                view.setImageResource(R.drawable.camera);
                showDownloadImageProgress(message);
            }  else {
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                if (imgBody.getLocalUrl() != null) {
                    String remotePath = imgBody.getRemoteUrl();
                    String filePath = ImageUtils.getImagePath(remotePath);
                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
                    if(TextUtils.isEmpty(thumbRemoteUrl)&&!TextUtils.isEmpty(remotePath)){
                        thumbRemoteUrl = remotePath;
                    }
                    String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
                    showImageView(thumbnailPath, view, filePath, imgBody.getRemoteUrl(), message);
                }
            }
        }
    }

    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
                                  final EMMessage message) {
        final String remote = remoteDir;
        bm = ImageCache.getInstance().get(thumbernailPath);
        if (bm != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bm);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PictureActivity.class);
                    File file = new File(localFullSizePath);
                    if (file.exists()) {
                        Uri uri = Uri.fromFile(file);
                        Log.e("tag", "uri chat " + uri.getPath());
                        intent.putExtra("flag", "url");
                        intent.putExtra("url", localFullSizePath);
                    } else {
                        ImageMessageBody body = (ImageMessageBody) message.getBody();
                        intent.putExtra("secret", body.getSecret());
                        intent.putExtra("remotepath", remote);
                    }
                    if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
                            && message.getChatType() != EMMessage.ChatType.GroupChat && message.getChatType() != EMMessage.ChatType.ChatRoom) {
                        try {
                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    context.startActivity(intent);
                }
            });
            return true;
        } else {
            new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, (Activity)context, message);
            return true;
        }
    }

    private void showDownloadImageProgress(final EMMessage message) {
        EMLog.d("TAG", "!!! show download image progress");
        // final ImageMessageBody msgbody = (ImageMessageBody)
        // message.getBody();
        final FileMessageBody msgbody = (FileMessageBody) message.getBody();
        /*if(holder.pb!=null)
            holder.pb.setVisibility(View.VISIBLE);
        if(holder.tv!=null)
            holder.tv.setVisibility(View.VISIBLE);*/

        msgbody.setDownloadCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // message.setBackReceive(false);
                        /*if (message.getType() == EMMessage.Type.IMAGE) {
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                        }*/
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
            }

            @Override
            public void onProgress(final int progress, String status) {
                /*if (message.getType() == EMMessage.Type.IMAGE) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tv.setText(progress + "%");

                        }
                    });
                }*/
            }
        });
    }

}
