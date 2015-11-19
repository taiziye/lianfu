package com.tangpo.lianfu.parms;

import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.GetMD5Vec;
import com.tangpo.lianfu.utils.GetTime;
import com.tangpo.lianfu.utils.RandomNum;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shengshoubo on 2015/8/30.
 */
public class GetCode {
    public static final String packagingParam(String... kvs) {
        JSONObject jsonObject = new JSONObject();
        String action = "25";
        String time = GetTime.getTime();
        String rannum = RandomNum.randomString(32);
        String key = Configs.KEY_APPJSONKEY;
        String md5vec = GetMD5Vec.getMD5Vec(action, rannum, time, key);
        try {
            jsonObject.put("action", Escape.escape(action));
            jsonObject.put("time", Escape.escape(time));
            jsonObject.put("rannum", Escape.escape(rannum));
            jsonObject.put("md5ver", Escape.escape(md5vec));

            JSONObject paramJsonObject = new JSONObject();
            paramJsonObject.put("phone", Escape.escape(kvs[0]));
            jsonObject.put("param", paramJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
