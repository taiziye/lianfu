package com.tangpo.lianfu.http;

import android.os.AsyncTask;

import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.utils.Escape;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by shengshoubo on 2015/8/30.
 */

public class NetConnection {

    //直接将加密封装好的json字符串作为参数
    public NetConnection(final SuccessCallback successCallback, final FailCallback failCallback, final String param) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                HttpClient client=new DefaultHttpClient();
                String result=null;
                try {
                    String urlString=Configs.SERVER_URL;
                    HttpPost post=new HttpPost(urlString);
                    StringEntity entity=new StringEntity(param,"UTF-8");
                    entity.setContentEncoding("UTF-8");
                    entity.setContentType("application/json");
                    post.setEntity(entity);

                    HttpResponse response=client.execute(post);
                    //将返回的结果解码
                    result= Escape.unescape(EntityUtils.toString(response.getEntity()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
            @Override
            protected void onPostExecute (String s){
                super.onPostExecute(s);
                if (s != null) {
                    if (successCallback != null) {
                        try {
                            //将解码后的字符串封装成JSONObject
                            JSONObject resJson=new JSONObject(s);
                            successCallback.onSuccess(resJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (failCallback != null) {
                        try {
                            //将解码后的字符串封装成JSONObject
                            JSONObject resJson=new JSONObject(s);
                            failCallback.onFail(resJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.execute();
    }

    //通知调用者
    public static interface SuccessCallback {
        void onSuccess(JSONObject result);
    }

    public static interface FailCallback {
        void onFail(JSONObject result);
    }
}
