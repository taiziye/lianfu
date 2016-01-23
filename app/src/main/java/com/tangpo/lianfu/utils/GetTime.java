package com.tangpo.lianfu.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shengshoubo on 2015/11/3.
 */
public class GetTime {

    private static String time;
    private static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                time= (String) msg.obj;
            }
        }
    };
    public static final String getTime() {
        getNetworkTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_time = formatter.format(System.currentTimeMillis());
        if(time!=null){
            return time;
        }else{
            return current_time;
        }
    }

    private static void getNetworkTime(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    URL url=new URL("http://www.baidu.com");
                    URLConnection uc=url.openConnection();
                    uc.connect();
                    long ld=uc.getDate();
                    Date date=new Date(ld);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = formatter.format(date);
                    Message msg=new Message();
                    msg.what=1;
                    msg.obj=time;
                    handler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}