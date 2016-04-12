package com.tangpo.lianfu.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.InvitedMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 果冻 on 2016/4/10.
 */
public class DBManager {
    static private DBManager dbManager = new DBManager();
    private DBOpenHelper dbHelper;

    private DBManager() {
        dbHelper = DBOpenHelper.getInstance();
    }

    public static synchronized DBManager getInstance() {
        if(dbManager == null) {
            dbManager = new DBManager();
        }
        return dbManager;
    }

    synchronized public void saveContactList(List<ChatAccount> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (ChatAccount account : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.ID, account.getUser_id());
                values.put(UserDao.USERNAME, account.getUsername());
                values.put(UserDao.NAME, account.getName());
                values.put(UserDao.PHONE, account.getPhone());
                values.put(UserDao.EASEMOD_ID, account.getEasemod_id());
                values.put(UserDao.UUID, account.getUuid());
                values.put(UserDao.PHOTO, account.getPhoto());
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    synchronized public Map<String, ChatAccount> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, ChatAccount> users = new HashMap<String, ChatAccount>();
        if(db.isOpen()) {
           Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME + " desc", null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(UserDao.ID));
                String username = cursor.getString(cursor.getColumnIndex(UserDao.USERNAME));
                String name = cursor.getString(cursor.getColumnIndex(UserDao.NAME));
                String phone = cursor.getString(cursor.getColumnIndex(UserDao.PHONE));
                String easemod_id = cursor.getString(cursor.getColumnIndex(UserDao.EASEMOD_ID));
                String uuid = cursor.getString(cursor.getColumnIndex(UserDao.UUID));
                String photo = cursor.getString(cursor.getColumnIndex(UserDao.PHOTO));
                ChatAccount user = new ChatAccount();
                user.setUser_id(id);
                user.setUsername(username);
                user.setName(name);
                user.setPhone(phone);
                user.setEasemod_id(easemod_id);
                user.setUuid(uuid);
                user.setPhoto(photo);
                users.put(easemod_id, user);
            }
            cursor.close();
        }
        return users;
    }

    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.EASEMOD_ID + " = ?", new String[]{username});
        }
    }

    synchronized public void saveContact(ChatAccount user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.ID, user.getUser_id());
        values.put(UserDao.USERNAME, user.getUsername());
        values.put(UserDao.NAME, user.getName());
        values.put(UserDao.PHONE, user.getPhone());
        values.put(UserDao.EASEMOD_ID, user.getEasemod_id());
        values.put(UserDao.UUID, user.getUuid());
        values.put(UserDao.PHOTO, user.getPhoto());
        if(db.isOpen()) {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    public synchronized Integer saveMessage(InvitedMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if(db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessageDao.COLUMN_NAME_GROUP_NAME, message.getGroupName());
            values.put(InviteMessageDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessageDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessageDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(InviteMessageDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            db.insert(InviteMessageDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessageDao.TABLE_NAME, null);
            if(cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }
            cursor.close();
        }
        return id;
    }

    public synchronized void updateMessage(int msgId, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()) {
            db.update(InviteMessageDao.TABLE_NAME, values, InviteMessageDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    public synchronized List<InvitedMessage> getMessageList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InvitedMessage> msgs = new ArrayList<InvitedMessage>();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select * from " + InviteMessageDao.TABLE_NAME + " desc", null);
            while (cursor.moveToNext()){
                InvitedMessage msg = new InvitedMessage();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUP_NAME));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_STATUS));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessageDao.COLUMN_NAME_GROUPINVITER));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setGroupInviter(groupInviter);

                if(status == InvitedMessage.InviteMessageStatus.BEINVITEED.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.BEINVITEED);
                } else if (status == InvitedMessage.InviteMessageStatus.BEAGREED.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.BEAGREED);
                } else if (status == InvitedMessage.InviteMessageStatus.BEREFUSED.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.BEREFUSED);
                } else if (status == InvitedMessage.InviteMessageStatus.AGREED.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.AGREED);
                } else if (status == InvitedMessage.InviteMessageStatus.REFUSED.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.REFUSED);
                } else if (status == InvitedMessage.InviteMessageStatus.BEAPPLYED.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.BEAPPLYED);
                } else if (status == InvitedMessage.InviteMessageStatus.GROUPINVITATION.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.GROUPINVITATION);
                } else if (status == InvitedMessage.InviteMessageStatus.GROUPINVITATION_ACCEPTED.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.GROUPINVITATION_ACCEPTED);
                } else if (status == InvitedMessage.InviteMessageStatus.GROUPINVITATION_DECLINED.ordinal()) {
                    msg.setStatus(InvitedMessage.InviteMessageStatus.GROUPINVITATION_DECLINED);
                }

                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    synchronized public void deleteMessage(String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            db.delete(InviteMessageDao.TABLE_NAME, InviteMessageDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    synchronized public int getUnreadNotifyCount() {
        int count =0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessageDao.TABLE_NAME, null);
            if (cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    synchronized public void setUnreadNotifyCount(int count) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessageDao.TABLE_NAME, values, null, null);
        }
    }

    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbManager = null;
    }
}
