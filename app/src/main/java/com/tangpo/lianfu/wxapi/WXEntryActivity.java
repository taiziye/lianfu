package com.tangpo.lianfu.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.fragment.EmployeeFragment;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.fragment.ManagerFragment;
import com.tangpo.lianfu.fragment.MemFragment;
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

    public static final String ACTION="com.tangpo.lianfu.wxapi.intent.action.WXEntrtyActivity";

    public static final String TOKEN="token";

    public static final String OPENID="openid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if(MainActivity.api!=null){
            MainActivity.api.handleIntent(getIntent(), this);
        }
        if(ManagerFragment.api!=null){
            ManagerFragment.api.handleIntent(getIntent(),this);
        }
        if(EmployeeFragment.api!=null){
            EmployeeFragment.api.handleIntent(getIntent(),this);
        }
        if(MemFragment.api!=null){
            MemFragment.api.handleIntent(getIntent(),this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(MainActivity.api!=null){
            MainActivity.api.handleIntent(getIntent(), this);
        }
        if(ManagerFragment.api!=null){
            ManagerFragment.api.handleIntent(getIntent(),this);
        }
        if(EmployeeFragment.api!=null){
            EmployeeFragment.api.handleIntent(getIntent(),this);
        }
        if(MemFragment.api!=null){
            MemFragment.api.handleIntent(getIntent(),this);
        }
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
                Log.e("tag","code:"+code);
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
                Log.e("tag","openid:"+openid);
                Intent intent=new Intent(WXEntryActivity.ACTION);
                intent.putExtra(WXEntryActivity.TOKEN,access_token);
                intent.putExtra(WXEntryActivity.OPENID,openid);
                sendBroadcast(intent);
                finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WXEntryActivity.this.finish();
    }
}
