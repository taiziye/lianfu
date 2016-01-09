package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.utils.EaseSmileUtils;
import com.tangpo.lianfu.utils.Tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ConversationAdapter extends BaseAdapter {
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";

    private Context context = null;
    private ArrayList<ChatAccount> list = null;
    private LayoutInflater inflater = null;
    private List<EMConversation> conversationlist = null;
    private String name = "";
    private int unread = 0;

    public ConversationAdapter(Context context, ArrayList<ChatAccount> list, List<EMConversation> conversationlist, String name) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.conversationlist = conversationlist;
        this.name = name;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    public EMConversation getConversation(int position) {
        if (position < conversationlist.size()) {
            return conversationlist.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.conversation_list, parent, false);

            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.latest = (TextView) convertView.findViewById(R.id.latest);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tools.setPhoto(context, list.get(position).getPhoto(), holder.img);

        // 获取与此用户的会话
        EMConversation conversation = getConversation(position);

        holder.name.setText(name);
        unread = conversation.getUnreadMsgCount();

        //设置最后的聊天内容以及时间
        if (conversation.getMsgCount() != 0) {
            EMMessage lastMessage = conversation.getLastMessage();
            holder.latest.setText(EaseSmileUtils.getSmiledText(context, getMessageDigest(lastMessage, (context))), TextView.BufferType.SPANNABLE);
            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
        }

        return convertView;
    }

    public int getUnread() {
        return unread;
    }

    private String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    //从sdk中提到了ui中，使用更简单不犯错的获取string方法
//              digest = EasyUtils.getAppResourceString(context, "location_recv");
                    digest = getString(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
//              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                    digest = getString(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                digest = getString(context, R.string.picture);
                break;
            case VOICE:// 语音消息
                digest = getString(context, R.string.voice_prefix);
                break;
            case VIDEO: // 视频消息
                digest = getString(context, R.string.video);
                break;
            case TXT: // 文本消息
                TextMessageBody txtBody = (TextMessageBody) message.getBody();
            /*if(((DemoHXSDKHelper)HXSDKHelper.getInstance()).isRobotMenuMessage(message)){
                digest = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getRobotMenuMessageDigest(message);
            }else */if(message.getBooleanAttribute(MESSAGE_ATTR_IS_VOICE_CALL, false)){
                digest = getString(context, R.string.voice_call) + txtBody.getMessage();
            }else if(message.getBooleanAttribute(MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
                if(!TextUtils.isEmpty(txtBody.getMessage())){
                    digest = txtBody.getMessage();
                }else{
                    digest = getString(context, R.string.dynamic_expression);
                }
            }else{
                digest = txtBody.getMessage();
            }
                break;
            case FILE: //普通文件消息
                digest = getString(context, R.string.file);
                break;
            default:
                EMLog.e("tag", "error, unknow type");
                return "";
        }

        return digest;
    }

    static String getString(Context context, int resId){
        return context.getResources().getString(resId);
    }

    class ViewHolder{
        public ImageView img;
        public TextView name;
        public TextView latest;
        public TextView time;
    }
}
