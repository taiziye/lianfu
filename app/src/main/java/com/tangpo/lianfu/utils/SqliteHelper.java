package com.tangpo.lianfu.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tangpo.lianfu.entity.ChatAccount;

/**
 * Created by 果冻 on 2016/1/9.
 */
public class SqliteHelper extends SQLiteOpenHelper{
    public static final String TABLE_NAME= "users";
    private static SqliteHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + " ("
            + ChatAccount.ID + " integer primary key, "
            + ChatAccount.USERNAME + " TEXT, "
            + ChatAccount.NAME + " TEXT, "
            + ChatAccount.EASEMOD_ID + " TEXT, "
            + ChatAccount.MSG + " TEXT, "
            + ChatAccount.TIME + " TEXT, "
            + ChatAccount.PHOTO + " TEXT);";

    /**
     *
     * @param context
     * @param name  数据库名称
     * @param factory
     * @param version  数据库版本
     */
    private SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SqliteHelper getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        if (instance == null) {
            instance = new SqliteHelper(context, name, factory, version);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2){
            db.execSQL("ALTER TABLE "+ TABLE_NAME +" ADD COLUMN "+
                    ChatAccount.PHOTO + " TEXT ;");
        }

        /*if(oldVersion < 3){
            db.execSQL(CREATE_PREF_TABLE);
        }
        if(oldVersion < 4){
            db.execSQL(ROBOT_TABLE_CREATE);
        }*/
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}