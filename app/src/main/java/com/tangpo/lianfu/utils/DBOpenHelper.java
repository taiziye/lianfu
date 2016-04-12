package com.tangpo.lianfu.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tangpo.lianfu.MyApplication;
import com.tangpo.lianfu.entity.ChatAccount;

/**
 * Created by 果冻 on 2016/4/10.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static DBOpenHelper instance;

    private static final String USER_TABLE_CREATE = "create table "
            + UserDao.TABLE_NAME + " ("
            + UserDao.TABLE_ID + " integer primary key autoincrement, "
            + UserDao.ID + " text, "
            + UserDao.USERNAME + " text, "
            + UserDao.NAME + " text, "
            + UserDao.PHONE + " text, "
            + UserDao.EASEMOD_ID + " text, "
            + UserDao.UUID + " text, "
            + UserDao.PHOTO + " text);";

    private static final String INVITE_MESSAGE_TABLE_CREATE = "create table " +
            InviteMessageDao.TABLE_NAME + " (" +
            InviteMessageDao.COLUMN_NAME_ID + " integer primary key autoincrement, " +
            InviteMessageDao.COLUMN_NAME_FROM + " text, " +
            InviteMessageDao.COLUMN_NAME_GROUP_ID + " text, " +
            InviteMessageDao.COLUMN_NAME_GROUP_NAME + " text, " +
            InviteMessageDao.COLUMN_NAME_REASON + " text, " +
            InviteMessageDao.COLUMN_NAME_STATUS + " integer, " +
            InviteMessageDao.COLUMN_NAME_ISINVITEFROMME + " integer, " +
            InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT + " integer, " +
            InviteMessageDao.COLUMN_NAME_TIME + " text, " +
            InviteMessageDao.COLUMN_NAME_GROUPINVITER + " text);";

    private DBOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DBOpenHelper getInstance() {
        if(instance == null) {
            instance = new DBOpenHelper(MyApplication.getContext().getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return ChatAccount.getInstance().getName();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(INVITE_MESSAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDB() {
        if(instance != null) {
            SQLiteDatabase db = instance.getWritableDatabase();
            db.close();
        }
    }
}
