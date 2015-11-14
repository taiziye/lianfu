package com.tangpo.lianfu.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.tangpo.lianfu.entity.UserEntity;

import java.util.Set;

/**
 * Created by shengshoubo on 2015/8/30.
 */
public class Configs {

	public static final String APP_ID="com.tangpo.lianfu";

	//request
	public static final String KEY_TOKEN="token";
	public static final String KEY_LATITUDE="latitude";
	public static final String KEY_LONGITUDE="longitude";
	public static final String KEY_USER="user_type";
	public static final String KEY_STORE="store";
	public static final String KEY_MEMBERS="member";
	public static final String KEY_EMPLOYEES="employee";
	public static final String KEY_ACTION="action";
	public static final String KEY_RANDOM_NUM="rannum";
	public static final String KEY_TIME="time";
	public static final String KEY_APPJSONKEY="959d081c0196468b81b306d648835072";
	public static final String KEY_MD5VER="md5ver";
	public static final String KEY_PARAM="param";
	public static final String KEY_PHONE_NUM="phone";
	public static final String KEY_CODE = "code";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_SERVICE_CENTER = "service_center";
	public static final String KEY_SERVICE_ADDRESS = "service_address";
	public static final String KEY_REFERRER = "referrer";
	public static final String KEY_SEX = "sex";
	public static final String KEY_BIRTH = "birth";
	public static final String KEY_QQ = "qq";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_BANK_ACCOUNT = "bank_account";
	public static final String KEY_BANK_NAME = "bank_name";
	public static final String KEY_BANK = "bank";
	public static final String KEY_BANK_ADDRESS = "bank_address";
	public static final String KEY_OPENID = "openid";
	public static final String KEY_LNG = "lng";
	public static final String KEY_LAT = "lat";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_STORE_ID = "store_id";
	public static final String KEY_DISCOUNT = "discount";
	public static final String KEY_RECEIPT_NO = "receipt _no";
	public static final String KEY_RECEIPT_PHOTO = "receipt_photo";
	public static final String KEY_ONLINE = "online";
	public static final String KEY_PAGE_SIZE = "page_size";
	public static final String KEY_PAGE_INDEX = "page_index";
	public static final String KEY_PW = "pw";
	public static final String KEY_INCOME = "income";
	public static final String KEY_MEM_NUM = "mem_num";
	public static final String KEY_PROFIT = "profit";
	public static final String KEY_NEED_PAY = "need_pay";
	public static final String KEY_ADMIN_NUM = "admin_num";
	public static final String KEY_STAFF_NUM = "staff_num";
	public static final String KEY_PAYBACK = "payback";
	public static final String KEY_REGISTER_DATE = "register_date";
	public static final String KEY_NAME = "name";
	public static final String KEY_FEE = "fee";
	public static final String KEY_MEM_ID = "mem_id";
	public static final String KEY_PAY_DATE = "pay_date";
	public static final String KEY_PAY_WAY = "pay_way";
	public static final String KEY_PAY_STATUS = "pay_status";
	public static final String KEY_PAY_RECORD_ID = "pay_record_id";
	public static final String KEY_TRAD_NO = "trad_no";
	public static final String KEY_PAY_ACCOUNT = "pay_account";
	public static final String KEY_TOTAL_FEE = "total_fee";
	public static final String KEY_LIST = "list";
	public static final String KEY_COMSUME_ID = "consume_id";
	public static final String KEY_ID = "id";
	public static final String KEY_ID_NUMBER = "id_number";
	public static final String KEY_INFO = "info";
	public static final String KEY_EMPLOYEE_ID = "employee_id";
	public static final String KEY_RANK = "rank";
	public static final String KEY_UPGRADE = "upgrade";
	public static final String KEY_DISCOUNT_ID = "discount_id";
	public static final String KEY_DESC = "desc";

	//response
	public static final String KEY_STATUS="status";


	public static final int RESULT_STATUS_SUCCESS=0;
	public static final int RESULT_STATUS_INVALID_FORMAT=2;
	public static final int RESULT_STATUS_TIMEOUT=9;
	public static final int RESULT_STATUS_SERVER_ECEPTION=10;

	public static final String CHARSET="UTF-8";
	public static final String SERVER_URL = "http://182.92.191.236:10000/clientserver/fshopserver.aspx";


	public static final String ACTION_REGISTER_MEMBER="0";
	public static final String ACTION_LOGIN = "1";
	public static final String ACTION_FIND_STORE = "2";
	public static final String ACTION_FIND_DETAIL = "3";
	public static final String ACTION_PAY_BILL = "4";
	public static final String ACTION_COLLECT_STORE = "5";
	public static final String ACTION_CHECK_COLLECTED_STORE = "6";
	public static final String ACTION_CHECK_CONSUME_RECORD = "7";
	public static final String ACTION_EDIT_MATERIAL = "8";
	public static final String ACTION_HOME_PAGE = "9";
	public static final String ACTION_MEMBER_MANAGEMENT = "10";
	public static final String ACTION_GET_ALTERNATIVE_DISCOUNT = "11";
	public static final String ACTION_COMMIT_CONSUME_RECORD = "12";
	public static final String ACTION_PROFIT_MANAGEMENT = "13";
	public static final String ACTION_DELETE_CONSUME_RECORD = "14";
	public static final String ACTION_PROFIT_ACCOUNT = "15";
	public static final String ACTION_ADD_MEMBER = "16";
	public static final String ACTION_PLATFORM_REBATE_RECORD = "17";
	public static final String ACTION_EDIT_MEMBER = "18";
	public static final String ACTION_STAFF_MANAGEMENT = "19";
	public static final String ACTION_EDIT_STAFF = "20";
	public static final String ACTION_MANAGE_DISCOUNT = "21";
	public static final String ACTION_DELETE_DISCOUNT = "22";
	public static final String ACTION_EDIT_DISCOUNT = "23";
	public static final String ACTION_NEW_DISCOUNT = "24";
	public static final String ACTION_GET_CODE="25";
	public static final String ACTION_CHECK_CODE="26";

	//这里关于Activity返回的请求码和结果码为了防止在不同的Activity中会有冲突，最好定义在配置文件当中
	public static final int ACTIVITY_RESULT_NEED_REFRESH=10000;

	//获取访问服务器的Token
	public static String getCatchedToken(Context context){
		return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_TOKEN,null);
	}

	//保存服务器返回的Token
	public static void cacheToken(Context context,String token){
		SharedPreferences.Editor editor=context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
		editor.putString(KEY_TOKEN,token);
		editor.commit();
	}

	//获取电话号码
	public static String getCatchedPhoneNum(Context context){
		return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_PHONE_NUM,null);
	}

	//保存电话号码
	public static void cachePhoneNum(Context context,String phoneNum){
		SharedPreferences.Editor editor=context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
		editor.putString(KEY_PHONE_NUM,phoneNum);
		editor.commit();
	}

	//保存当前的位置信息
	public static void cacheCurLocation(Context context,double lat,double lng){
		SharedPreferences.Editor editor=context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
		editor.putFloat(KEY_LATITUDE,(float)lat);
		editor.putFloat(KEY_LONGITUDE, (float) lng);
		editor.commit();
	}

	//保存当前的用户的类别
	public static void cacheUser(Context context,String user){
		SharedPreferences.Editor editor=context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
		editor.putString(KEY_USER, user);
		editor.commit();
	}

	/**
	 * 保存用户收藏店铺列表
	 * @param context
	 * @param stores  店铺id集
	 */
	public static void cacheCollectedStore(Context context, Set<String> stores){
		SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_APPEND).edit();
		editor.putStringSet(KEY_STORE, stores);
		editor.commit();
	}

	/**
	 * 保存管理的所有会员
	 * @param context
	 * @param members
	 */
	public static void cacheMember(Context context, Set<String> members){
		SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID, Context.MODE_APPEND).edit();
		editor.putStringSet(KEY_MEMBERS, members);
		editor.commit();
	}

	/**
	 * 保存管理的所有员工
	 * @param context
	 * @param employees
	 */
	public static void cacheEmployee(Context context,Set<String> employees){
		SharedPreferences.Editor editor=context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
		editor.putStringSet(KEY_EMPLOYEES,employees);
		editor.commit();
	}
}
