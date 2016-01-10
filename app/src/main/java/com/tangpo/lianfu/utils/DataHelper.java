package com.tangpo.lianfu.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tangpo.lianfu.entity.ChatAccount;

import org.jivesoftware.smack.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2016/1/10.
 */
public class DataHelper {
    //数据库名称
    private static String DB_NAME = "hx.db";
    //数据库版本
    private static int DB_VERSION = 4;
    private SQLiteDatabase db = null;
    private SqliteHelper dbHelper = null;

    public DataHelper(Context context) {
        dbHelper = SqliteHelper.getInstance(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public void close(){
        db.close();
        dbHelper.close();
    }

    /**
     * 获取users表中的username name easemod_id photo字段
     * @return 返回账户列表
     */
    public List<ChatAccount> getChatAccountList() {
        List<ChatAccount> list = new ArrayList<>();
        Cursor cursor = db.query(SqliteHelper.TABLE_NAME, null, null, null, null, null, ChatAccount.ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
            ChatAccount account = new ChatAccount();
            account.setUsername(cursor.getString(cursor.getColumnIndex(ChatAccount.USERNAME)));
            account.setName(cursor.getString(cursor.getColumnIndex(ChatAccount.NAME)));
            account.setEasemod_id(cursor.getString(cursor.getColumnIndex(ChatAccount.EASEMOD_ID)));
            account.setPhoto(cursor.getString(cursor.getColumnIndex(ChatAccount.PHOTO)));
            account.setMsg(cursor.getString(cursor.getColumnIndex(ChatAccount.MSG)));
            account.setTime(cursor.getString(cursor.getColumnIndex(ChatAccount.TIME)));
            list.add(account);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    /**
     * 添加users表的记录
     * @param account
     * @return
     */
    public Long saveChatAccount(ChatAccount account) {
        ContentValues values = new ContentValues();
        values.put(ChatAccount.USERNAME, account.getUsername());
        values.put(ChatAccount.NAME, account.getName());
        values.put(ChatAccount.EASEMOD_ID, account.getEasemod_id());
        values.put(ChatAccount.PHOTO, account.getPhoto());
        values.put(ChatAccount.MSG, account.getMsg());
        values.put(ChatAccount.TIME, account.getTime());
        Long uid = db.insert(SqliteHelper.TABLE_NAME, ChatAccount.ID, values);
        return uid;
    }

    /**
     * 删除记录
     * @param easemod_id  用户的环信id
     * @return
     */
    public int delChatAccount(String easemod_id) {
        int id = db.delete(SqliteHelper.TABLE_NAME, ChatAccount.EASEMOD_ID + "=?", new String[]{easemod_id});
        return id;
    }
}
