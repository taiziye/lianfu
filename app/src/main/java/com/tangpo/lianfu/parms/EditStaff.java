package com.tangpo.lianfu.parms;

import android.content.Context;

import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.GetMD5Vec;
import com.tangpo.lianfu.utils.GetTime;
import com.tangpo.lianfu.utils.RandomNum;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shengshoubo on 2015/11/2.
 */
public class EditStaff {
    public static final String packagingParam(Context context, String... kvs) {
        JSONObject jsonObject = new JSONObject();
        String action = "20";
        String time = GetTime.getTime();
        String rannum = RandomNum.randomString(32);
        String key = Configs.KEY_APPJSONKEY;
        String sessid = Configs.getCatchedToken(context);
        String md5vec = GetMD5Vec.getMD5Vec(action, rannum, time, key, sessid);
        try {
            jsonObject.put("action", Escape.escape(action));
            jsonObject.put("time", Escape.escape(time));
            jsonObject.put("rannum", Escape.escape(rannum));
            jsonObject.put("md5ver", Escape.escape(md5vec));
            jsonObject.put("sessid", Escape.escape(sessid));

            JSONObject paramJsonObject = new JSONObject();
            paramJsonObject.put("user_id", Escape.escape(kvs[0]));
            paramJsonObject.put("employee_id", Escape.escape(kvs[1]));
            paramJsonObject.put("rank", Escape.escape(kvs[2]));
            paramJsonObject.put("username", Escape.escape(kvs[3]));
            paramJsonObject.put("name", Escape.escape(kvs[4]));
            paramJsonObject.put("id_number", Escape.escape(kvs[5]));
            paramJsonObject.put("upgrade", Escape.escape(kvs[6]));
            paramJsonObject.put("phone", Escape.escape(kvs[7]));
            paramJsonObject.put("bank_account", Escape.escape(kvs[8]));
            paramJsonObject.put("bank", Escape.escape(kvs[9]));
            paramJsonObject.put("bank_name", Escape.escape(kvs[10]));
            paramJsonObject.put("sex", Escape.escape(kvs[11]));
            paramJsonObject.put("isServer", Escape.escape(kvs[12]));
            paramJsonObject.put("isstop", Escape.escape(kvs[13]));


            jsonObject.put("param", paramJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
