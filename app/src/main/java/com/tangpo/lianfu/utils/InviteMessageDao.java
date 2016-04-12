package com.tangpo.lianfu.utils;

import android.content.ContentValues;
import android.content.Context;

import com.tangpo.lianfu.entity.InvitedMessage;

import java.util.List;

/**
 * Created by 果冻 on 2016/4/10.
 */
public class InviteMessageDao {
    public static final String TABLE_NAME = "new_friends_msgs";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_FROM = "username";
    public static final String COLUMN_NAME_GROUP_ID = "groupid";
    public static final String COLUMN_NAME_GROUP_NAME = "groupname";

    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";
    public static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
    public static final String COLUMN_NAME_GROUPINVITER = "groupinviter";

    public static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";

    public InviteMessageDao(Context context) {}

    public Integer saveMessage(InvitedMessage message) {
        return DBManager.getInstance().saveMessage(message);
    }

    public void updateMessage(int msgId, ContentValues values) {
        DBManager.getInstance().updateMessage(msgId, values);
    }

    public List<InvitedMessage> getMessageList() {
        return DBManager.getInstance().getMessageList();
    }

    public void deleteMessage(String from) {
        DBManager.getInstance().deleteMessage(from);
    }

    public int getUnreadMessagesCount() {
        return DBManager.getInstance().getUnreadNotifyCount();
    }

    public void saveUnreadMessageCount(int count) {
        DBManager.getInstance().setUnreadNotifyCount(count);
    }
}
