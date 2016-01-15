package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.tangpo.lianfu.utils.SmileUtils;
import com.tangpo.lianfu.utils.Tools;

import org.jivesoftware.smack.Chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ConversationAdapter extends BaseAdapter implements Filterable {
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";

    private Context context = null;
    private List<ChatAccount> list = null;
    private LayoutInflater inflater = null;
    private List<ChatAccount> mOriginalValues = null;
    public static int unread = 0;

    public ConversationAdapter(Context context, List<ChatAccount> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            holder.unread = (TextView) convertView.findViewById(R.id.unread);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tools.setPhoto(context, list.get(position).getPhoto(), holder.img);
        // 获取与此用户的会话
        holder.name.setText(list.get(position).getName());
        //holder.latest.setText(list.get(position).getMsg());
        handleTextMessage(list.get(position).getMsg(), holder.latest);
        holder.time.setText(list.get(position).getTime());
        if (list.get(position).getUnread() != 0) {
            holder.unread.setText(list.get(position).getUnread());
            holder.unread.setVisibility(View.VISIBLE);
            unread++;
        } else {
            holder.unread.setVisibility(View.INVISIBLE);
        }
        /*if (conversation.getUnreadMsgCount() > 0) {
            holder.unread.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unread.setVisibility(View.VISIBLE);
            unread += Integer.parseInt(String.valueOf(conversation.getUnreadMsgCount()));
        } else {
            holder.unread.setVisibility(View.INVISIBLE);
        }
        unread = conversation.getUnreadMsgCount();*/

        //设置最后的聊天内容以及时间
        /*if (conversation.getMsgCount() != 0) {
            EMMessage lastMessage = conversation.getLastMessage();
            holder.latest.setText(EaseSmileUtils.getSmiledText(context, getMessageDigest(lastMessage, (context))), TextView.BufferType.SPANNABLE);
            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
        }*/

        return convertView;
    }

    /**
     * 文本消息
     *
     * @param message
     * @param txt
     */
    private void handleTextMessage(String message, TextView txt) {
        Spannable span = SmileUtils.getSmiledText(context, message);
        // 设置内容
        txt.setText(span, TextView.BufferType.SPANNABLE);
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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<ChatAccount> filterlist = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(list);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i=0; i<mOriginalValues.size(); i++) {
                        ChatAccount data = mOriginalValues.get(i);
                        if (data.getName().startsWith(constraint.toString())) {
                            filterlist.add(data);
                        }
                    }
                    results.count = filterlist.size();
                    results.values = filterlist;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<ChatAccount>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    class ViewHolder{
        public ImageView img;
        public TextView name;
        public TextView latest;
        public TextView time;
        public TextView unread;
    }
}
