package com.tangpo.lianfu.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.QQ.Util;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
//import com.tencent.mm.sdk.openapi.SendAuth;

/**
 * Created by shengshoubo on 2015/11/30.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        MainActivity.api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {
// TODO Auto-generated method stub
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                System.out.println("errcode_success");
                Log.e("tag", "errcode_success");
                String code = ((SendAuth.Resp) resp).token;
                getOpenidAndToken(code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            default:
                break;
        }
        //finish();
    }
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                String access_token=((JSONObject)msg.obj).getString("access_token");
                String openid=((JSONObject)msg.obj).getString("openid");
                getUserInfo(access_token,openid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 获取微信用户的openid和access token
    public void getOpenidAndToken(String code) {
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("appid", Constants.APP_ID);
        params.put("secret", Constants.APP_KEY);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        String httpurl = "https://api.weixin.qq.com/sns/oauth2/access_token";
        httpClient.get(httpurl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Message msg=mHandler.obtainMessage();
                msg.obj=response;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ToastUtils.showToast(getApplicationContext(),getString(R.string.invalid_code),Toast.LENGTH_SHORT);
            }
        });
    }

    public void getUserInfo(String access_token,String openid){
        AsyncHttpClient httpClient=new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("access_token",access_token);
        params.put("openid",openid);
        String httpurl = "https://api.weixin.qq.com/sns/userinfo";
        httpClient.get(httpurl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("tag", response.toString());
                Intent intent=new Intent(WXEntryActivity.this,MainActivity.class);
                intent.putExtra("user",response.toString());
                setResult(RESULT_OK,intent);
                startActivity(intent);
                finish();
                //ToastUtils.showToast(getApplicationContext(),response.toString(),Toast.LENGTH_LONG);
                //Util.showResultDialog(getApplicationContext(), response.toString(), getString(R.string.login_success));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ToastUtils.showToast(getApplicationContext(), getString(R.string.invalid_openid), Toast.LENGTH_SHORT);
            }
        });
    }
}
