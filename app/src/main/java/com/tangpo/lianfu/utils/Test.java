package com.tangpo.lianfu.utils;

import com.google.gson.JsonObject;

/**
 * Created by shengshoubo on 2015/11/3.
 */
public class Test {

    public static String getJson() {
        String time = GetTime.getTime();
        String rannum = RandomNum.randomString(32);
        String md5vec = GetMD5Vec.getMD5Vec("13", rannum, time);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", Escape.escape("13"));
        jsonObject.addProperty("time", Escape.escape(time));
        jsonObject.addProperty("rannum", Escape.escape(rannum));
        jsonObject.addProperty("md5vec", Escape.escape(md5vec));
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("user_id", Escape.escape("1"));
        paramObject.addProperty("store_id", Escape.escape("1"));
        paramObject.addProperty("pay_date", Escape.escape("2015-10-16"));
        paramObject.addProperty("pay_way", Escape.escape(""));
        paramObject.addProperty("pay_status", Escape.escape("0"));
        paramObject.addProperty("page_index", Escape.escape(""));
        paramObject.addProperty("page_size", Escape.escape(""));
        jsonObject.add("param", paramObject);
        return jsonObject.toString();
    }

    public static final String param = "{\"rannum\":\"ADAETNBasdfe23456\",\"param\":{\"store_id\":\"8\",\"user_id\":\"1\",\"page_index\":\"1\",\"page_size\":\"15\"},\"action\":\"13\",\"time\":\"2015-11-03%2020%3A25%3A31\",\"md5ver\":\"4bb4955e99bfdb61c6af794fb50593f8\"}";

}
