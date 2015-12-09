package com.tangpo.lianfu.http;

import android.os.AsyncTask;
import android.widget.Toast;

import com.tangpo.lianfu.MyApplication;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.ToastUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
                HttpClient client = new DefaultHttpClient();
                HttpParams httpParams=null;
                httpParams=client.getParams();
                String result = null;
                try {
                    httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
                    httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT,35000);
                    String urlString = Configs.SERVER_URL;
                    HttpPost post = new HttpPost(urlString);
                    StringEntity entity = new StringEntity(param, "UTF-8");
                    entity.setContentEncoding("UTF-8");
                    entity.setContentType("application/json");
                    post.setEntity(entity);

                    HttpResponse response = client.execute(post);
                    //将返回的结果解码
                    result = Escape.unescape(EntityUtils.toString(response.getEntity()));
                    return result;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    if (successCallback != null) {
                        try {
                            //将解码后的字符串封装成JSONObject
                            JSONObject resJson = new JSONObject(s);
                            if("0".equals(resJson.getString("status"))) {
                                successCallback.onSuccess(resJson);
                            } else {
                                failCallback.onFail(resJson);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    JSONObject resJson=new JSONObject();
                    try {
                        resJson.put("status","999999");
                        failCallback.onFail(resJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ToastUtils.showToast(MyApplication.getContext(), MyApplication.getContext().getString(R.string.internet_request_timeout), Toast.LENGTH_SHORT);
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
