package com.tangpo.lianfu.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shengshoubo on 2015/11/3.
 */
public class GetTime {
    public static final String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(System.currentTimeMillis());
        return time;
    }
}