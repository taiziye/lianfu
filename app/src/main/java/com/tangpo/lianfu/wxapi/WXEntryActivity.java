package com.tangpo.lianfu.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.OAuth;
import com.tangpo.lianfu.ui.HomePageActivity;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.ui.RelationActivity;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
//import com.tencent.mm.sdk.openapi.SendAuth;

/**
 * Created by shengshoubo on 2015/11/30.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private ProgressDialog pd=null;
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
                String code = ((SendAuth.Resp) resp).code;
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
                Configs.cacheOpenIdAndLoginType(WXEntryActivity.this,openid,"0");
                getUserInfo(access_token, openid);
                OAuthLogin(openid,"0");
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

    public void getUserInfo(String access_token, final String openid){
        AsyncHttpClient httpClient=new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("access_token",access_token);
        params.put("openid",openid);
        String httpurl = "https://api.weixin.qq.com/sns/userinfo";
        httpClient.get(httpurl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Configs.cacheThirdUser(WXEntryActivity.this,response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ToastUtils.showToast(getApplicationContext(), getString(R.string.invalid_openid), Toast.LENGTH_SHORT);
            }
        });
    }

    private void OAuthLogin(final String openid, final String logintype){
        pd = ProgressDialog.show(WXEntryActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[]=new String[]{openid,logintype};
        String params= OAuth.packagingParam(kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                Configs.cleanData(getApplicationContext());
                try {
                    JSONObject jsonObject = result.getJSONObject("param");
                    String sessid = jsonObject.getString("session_id");
                    Configs.cacheToken(getApplicationContext(), sessid);
                    Configs.cacheUser(getApplicationContext(), jsonObject.toString());
                    Intent intent = new Intent(WXEntryActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                try {
                    String status=result.getString("status");
                    if(status.equals("3")){
                        Configs.cacheOpenIdAndLoginType(getApplicationContext(), openid, logintype);
                        Intent intent=new Intent(WXEntryActivity.this,RelationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    if(status.equals("10")){
                        ToastUtils.showToast(WXEntryActivity.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }
}
