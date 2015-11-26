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
public class PayBill {
    public static final String packagingParam(Context context, String... kvs) {
        JSONObject jsonObject = new JSONObject();
        String action = "4";
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
            paramJsonObject.put("store_id", Escape.escape(kvs[1]));
            paramJsonObject.put("fee", Escape.escape(kvs[2]));
            paramJsonObject.put("phone", Escape.escape(kvs[3]));
            paramJsonObject.put("receipt _no", Escape.escape(kvs[4]));
            paramJsonObject.put("receipt_photo", kvs[5]);
            paramJsonObject.put("online", Escape.escape(kvs[6]));
            paramJsonObject.put("pay_way", Escape.escape(kvs[7]));


            jsonObject.put("param", paramJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
