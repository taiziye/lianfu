package com.tangpo.lianfu.utils;

import com.tangpo.lianfu.config.Configs;

/**
 * Created by shengshoubo on 2015/11/3.
 */
public class GetMD5Vec {
    public static final String getMD5Vec(String... kvs) {
//        String key= Configs.KEY_APPJSONKEY;
        String md5str = "";
        for (int i = 0; i < kvs.length; i++) {
            md5str += kvs[i];
        }
        return MD5Tool.md5(md5str);
    }
}
