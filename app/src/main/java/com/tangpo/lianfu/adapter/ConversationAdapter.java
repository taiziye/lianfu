package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.ChatUser;
import com.tangpo.lianfu.entity.HXUser;
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
    private List<EMConversation> list = null;
    private List<EMConversation> copylist = null;
    private LayoutInflater inflater = null;
    private List<EMConversation> mOriginalValues = null;
    public static int unread = 0;
    private boolean notiyfyByFilter;
    private List<HXUser> users = null;
    private List<String> names = new ArrayList<>();

    public ConversationAdapter(Context context, List<EMConversation> list, List<HXUser> users) {
        this.context = context;
        this.list = new ArrayList<>();
        this.list.addAll(list);
        copylist = new ArrayList<>(list);
        inflater = LayoutInflater.from(context);
        this.users = users;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public EMConversation getItem(int position) {
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
        EMConversation conversation = getItem(position);
        String username = null;
        for (int i=0; i<users.size(); i++) {
            Log.e("tag", users.get(i).getEasemod_id().toLowerCase() + " " + conversation.getUserName().toLowerCase());
            if (users.get(i).getEasemod_id().toLowerCase().equals(conversation.getUserName().toLowerCase())) {
                username = users.get(i).getName();
                names.add(username);
                break;
            }
        }

        Tools.setPhoto(context, ChatAccount.getInstance().getPhoto(), holder.img);
        // 获取与此用户的会话
        if (username == null || username.length() == 0) {
            holder.name.setText(conversation.getUserName());
        } else {
            holder.name.setText(username);
        }
        //Log.e("tag", " " + conversation.getUserName() + " " + conversation.getMessage(position).getFrom());
        //holder.latest.setText(list.get(position).getMsg());
        handleTextMessage(conversation, holder.latest);
        handleTimeTextView(conversation, holder.time);

        if (conversation.getUnreadMsgCount() > 0) {
            holder.unread.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unread.setVisibility(View.VISIBLE);
            unread++;
        } else {
            holder.unread.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public String getUserName(int position) {
        if (names.size() == 0 || names.get(position) == null) {
            return list.get(position).getUserName();
        }
        return names.get(position);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            list.clear();
            list.addAll(copylist);
            notiyfyByFilter = false;
        }
    }

    /**
     * 文本消息
     *
     * @param conversation
     * @param txt
     */
    private void handleTextMessage(EMConversation conversation, TextView txt) {
        EMMessage message = conversation.getLastMessage();
        Spannable span = SmileUtils.getSmiledText(context, getMessageDigest(message, context));
        // 设置内容
        txt.setText(span, TextView.BufferType.SPANNABLE);
    }

    private void handleTimeTextView(EMConversation conversation, TextView time) {
        EMMessage message = conversation.getLastMessage();
        time.setText(Tools.long2DateString(message.getMsgTime()));
    }

    private String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    //从sdk中提到了ui中，使用更简单不犯错的获取string方法
                    digest = getString(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
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
                if(message.getBooleanAttribute(MESSAGE_ATTR_IS_VOICE_CALL, false)){
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
                List<EMConversation> filterlist = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(list);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i=0; i<mOriginalValues.size(); i++) {
                        EMConversation data = mOriginalValues.get(i);
                        if (names.get(i).toLowerCase().startsWith(constraint.toString().toLowerCase())) {
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
                list.clear();
                list.addAll((List<EMConversation>)results.values);
                if (results.count > 0) {
                    notiyfyByFilter = true;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetChanged();
                }
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
