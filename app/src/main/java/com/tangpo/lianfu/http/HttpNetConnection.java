package com.tangpo.lianfu.http;

import android.os.AsyncTask;

import com.tangpo.lianfu.config.Configs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shengshoubo on 2015/8/30.
 */

public class HttpNetConnection {

    //直接将加密封装好的json字符串作为参数
    public HttpNetConnection(final SuccessCallback successCallback, final FailCallback failCallback, final String param) {


        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                URL url = null;
                try {
                    url = new URL(Configs.SERVER_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    OutputStream os = connection.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os,"UTF-8");
                    BufferedWriter bw = new BufferedWriter(osw);
                    bw.write(param);
                    bw.flush();
                    bw.close();
                    osw.close();
                    os.close();

                    InputStream is=connection.getInputStream();
                    InputStreamReader isr=new InputStreamReader(is,Configs.CHARSET);
                    BufferedReader br=new BufferedReader(isr);
                    String line;
                    StringBuffer result=new StringBuffer();
                    while((line=br.readLine())!=null){
                        result.append(line);
                    }
                    br.close();
                    isr.close();
                    is.close();
                    return result.toString();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute (String s){
            super.onPostExecute(s);
            if (s != null) {
                if (successCallback != null) {
                    successCallback.onSuccess(s);
                }
            } else {
                if (failCallback != null) {
                    failCallback.onFail(s);
                }
            }
        }
    }.execute();
}

//通知调用者
public static interface SuccessCallback {
    void onSuccess(String result);
}

public static interface FailCallback {
    void onFail(String result);
}
}
