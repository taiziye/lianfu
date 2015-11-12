package com.tangpo.lianfu.utils;

import com.tangpo.lianfu.config.Configs;

/**
 * Created by shengshoubo on 2015/11/3.
 */
public class GetMD5Vec {
    public static final String getMD5Vec(String action,String rannum,String time){
        String key= Configs.KEY_APPJSONKEY;
        return MD5Tool.md5(action+rannum+time+key);
    }
}
