package com.tangpo.lianfu.config;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.Set;

/**
 * Created by shengshoubo on 2015/8/30.
 */
public class Configs {

    public static final String APP_ID = "com.tangpo.lianfu";

    //request
    public static final String KEY_TOKEN = "token";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_USER = "user";
    public static final String KEY_THIRDUSER = "thirduser";
    public static final String KEY_STORE = "store";
    public static final String KEY_MEMBERS = "member";
    public static final String KEY_EMPLOYEES = "employee";
    public static final String KEY_MANAGER = "manager";
    public static final String KEY_APPJSONKEY = "959d081c0196468b81b306d648835072";
    public static final String KEY_PHONE_NUM = "phone";
    public static final String KEY_OPENID = "openid";
    public static final String KEY_LOGINTYPE = "logintype";
    public static final String KEY_PARAM = "param";

    public static final String SERVER_URL = "http://www.51xfzf.com/clientserver/fshopserver.aspx";
//    public static   String SERVER_URL = "http://182.92.191.236:10000/clientserver/fshopserver.aspx";

    //获取访问服务器的Token
    public static String getCatchedToken(Context context) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).getString(KEY_TOKEN, null);
    }

    //保存服务器返回的Token
    public static void cacheToken(Context context, String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    //获取电话号码
    public static String getCatchedPhoneNum(Context context) {
        return context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).getString(KEY_PHONE_NUM, null);
    }

    //保存电话号码
    public static void cachePhoneNum(Context context, String phoneNum) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_PHONE_NUM, phoneNum);
        editor.commit();
    }

    //保存当前的位置信息
    public static void cacheCurLocation(Context context, double lat, double lng) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_LATITUDE, (float) lat);
        editor.putFloat(KEY_LONGITUDE, (float) lng);
        editor.commit();
    }

    //保存当前登陆的用户
    public static void cacheUser(Context context, String user) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USER, user);
        editor.commit();
    }

    //保存当前登陆的用户
    public static void cacheThirdUser(Context context, String user) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_THIRDUSER, user);
        editor.commit();
    }

    /**
     * 保存管理的所有会员
     *
     * @param context
     * @param members
     */
    public static void cacheMember(Context context, Set<String> members) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_APPEND).edit();
        editor.putStringSet(KEY_MEMBERS, members);
        editor.commit();
    }

    /**
     * 保存管理的所有员工
     *
     * @param context
     * @param employees
     */
    public static void cacheEmployee(Context context, Set<String> employees) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putStringSet(KEY_EMPLOYEES, employees);
        editor.commit();
    }

    public static void cacheManager(Context context, String manager) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_MANAGER, manager);
        editor.commit();
    }

    public static void cacheOpenIdAndLoginType(Context context, String OpenId,String LoginType) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_OPENID, OpenId);
        editor.putString(KEY_LOGINTYPE, LoginType);
        editor.commit();
    }

    public static void cacheStore(Context context,String store){
        SharedPreferences.Editor editor=context.getSharedPreferences(APP_ID,Context.MODE_APPEND).edit();
        editor.putString(KEY_STORE,store);
        editor.commit();
    }

    public static void cleanData(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.remove(Configs.KEY_TOKEN);
        editor.remove(Configs.KEY_LOGINTYPE);
        editor.remove(Configs.KEY_USER);
        editor.remove(Configs.KEY_THIRDUSER);
        editor.remove(Configs.KEY_OPENID);
        editor.remove(Configs.KEY_PHONE_NUM);
        editor.remove(Configs.KEY_MANAGER);
        editor.remove(Configs.KEY_EMPLOYEES);
        editor.remove(Configs.KEY_MEMBERS);
        editor.remove(Configs.KEY_STORE);
        editor.clear();
        editor.commit();
    }
}
