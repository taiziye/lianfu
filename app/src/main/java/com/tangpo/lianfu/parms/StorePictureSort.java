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
 * Created by shengshoubo on 2015/12/25.
 */
public class StorePictureSort {
    public static final String packagingParam(Context context, String... kvs) {
        JSONObject jsonObject = new JSONObject();
        String action = "44";
        String time = GetTime.getTime();
        String rannum = RandomNum.randomString(32);
        String key = Configs.KEY_APPJSONKEY;
        String sessid = Configs.getCatchedToken(context)+"";
        String md5vec = GetMD5Vec.getMD5Vec(action, rannum, time, key, sessid);
        try {
            jsonObject.put("action", Escape.escape(action));
            jsonObject.put("time", Escape.escape(time));
            jsonObject.put("rannum", Escape.escape(rannum));
            jsonObject.put("md5ver", Escape.escape(md5vec));
            jsonObject.put("sessid", Escape.escape(sessid));

            JSONObject paramJsonObject = new JSONObject();
            paramJsonObject.put("store_id", Escape.escape(kvs[0]));
            paramJsonObject.put("user_id", Escape.escape(kvs[1]));
            paramJsonObject.put("picindex1", Escape.escape(kvs[2]));
            paramJsonObject.put("picindex2", Escape.escape(kvs[3]));

            jsonObject.put("param", paramJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
