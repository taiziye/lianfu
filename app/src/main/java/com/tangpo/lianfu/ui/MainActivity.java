package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.config.QQ.AppConstants;
import com.tangpo.lianfu.config.QQ.BaseUIListener;
import com.tangpo.lianfu.config.QQ.Util;
import com.tangpo.lianfu.config.WeiBo.AccessTokenKeeper;
import com.tangpo.lianfu.config.WeiBo.Constants;
import com.tangpo.lianfu.config.WeiBo.ErrorInfo;
import com.tangpo.lianfu.config.WeiBo.User;
import com.tangpo.lianfu.config.WeiBo.UsersAPI;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.Login;
import com.tangpo.lianfu.parms.OAuth;
import com.tangpo.lianfu.utils.CircularImage;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;
import com.tangpo.lianfu.wxapi.WXEntryActivity;
import com.tencent.connect.UserInfo;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends Activity implements View.OnClickListener {

    private EditText user_name;
    private EditText user_pass;
    private Button login;

    private TextView forget;
    private TextView register;

    private CircularImage weibo;
    private CircularImage weixin;
    private CircularImage qq;

    private ProgressDialog pd = null;

    private Intent intent = null;

    //这里是微博授权的实例对象
    private AuthInfo mAuthInfo;

    /**
     * 封装了"access_token","expires_in","refresh_token",并提供了它们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    /**
     * 注意：SsoHandler仅当SDK支持SSO时有效
     */
    private SsoHandler mSsoHandler;

    private UsersAPI mUsersAPI;

    //这里是QQ授权的实例的对象
    private static final String TAG = MainActivity.class.getName();
    public static String mAppid;
    private UserInfo mInfo;
    public static Tencent mTencent;

    //这里是微信授权的实例的对象
    public static IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        String token = Configs.getCatchedToken(this);
        //判断用户是否登录，如果已登录，则跳过该页面
        if (token != null) {  //如果已登录
            //根据登录身份跳转到相应的界面
            Tools.gotoActivity(MainActivity.this, HomePageActivity.class);
            finish();
        }
        init();
    }

    private void init() {
        user_name = (EditText) findViewById(R.id.user_name);
        user_pass = (EditText) findViewById(R.id.user_pass);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        forget = (TextView) findViewById(R.id.forget);
        forget.setOnClickListener(this);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        weibo = (CircularImage) findViewById(R.id.weibo);
        weibo.setOnClickListener(this);
        weixin = (CircularImage) findViewById(R.id.weixin);
        weixin.setOnClickListener(this);
        qq = (CircularImage) findViewById(R.id.qq);
        qq.setOnClickListener(this);

    }

    private void login() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String name = user_name.getText().toString();
        if (name.equals("")) {
            ToastUtils.showToast(this, getString(R.string.username_cannot_be_null), Toast.LENGTH_SHORT);
            return;
        }
        String pass = user_pass.getText().toString();
        if (pass.equals("")) {
            ToastUtils.showToast(this, getString(R.string.password_cannot_be_null), Toast.LENGTH_SHORT);
            return;
        }
        //String openId="";
        String kvs[] = new String[]{name, pass};

        String params = Login.packagingParam(kvs);

        System.out.println(Escape.unescape(params));

        pd = ProgressDialog.show(MainActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                try {
                    JSONObject jsonObject = result.getJSONObject("param");
                    String sessid = jsonObject.getString("session_id");
                    Configs.cacheToken(getApplicationContext(), sessid);
                    Configs.cacheUser(getApplicationContext(), jsonObject.toString());
                    intent = new Intent(MainActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                System.out.println(Escape.unescape(result.toString()));
                try {
                    if("2".equals(result.getString("status"))) {
                        Tools.showToast(MainActivity.this, getString(R.string.username_or_pwd_error));
                    }else{
                        Tools.showToast(MainActivity.this, getString(R.string.fail_to_login));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    public void register() {
        intent = new Intent(MainActivity.this, RegisterActivity.class);
        Tools.gatherActivity(this);
        startActivity(intent);
        finish();
    }

    public void forgetPassword() {
        intent=new Intent(MainActivity.this,ForgetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                Configs.cleanData(this);
                login();
                break;
            case R.id.register:
                register();
                break;
            case R.id.forget:
                forgetPassword();
                break;
            case R.id.weixin:
                //微信第三方登录
                Weixin();
                break;
            case R.id.weibo:
                //微博第三方登录
                Weibo();
                break;
            case R.id.qq:
                //qq第三方登录
                QQ();
                break;
        }
    }



    public void Weixin() {
        // 注册微信
        api= WXAPIFactory.createWXAPI(this, com.tangpo.lianfu.config.WeiXin.Constants.APP_ID, true);
        api.registerApp(com.tangpo.lianfu.config.WeiXin.Constants.APP_ID);
        SendAuth.Req req=new SendAuth.Req();
        req.scope= com.tangpo.lianfu.config.WeiXin.Constants.SCOPE;
        req.state= com.tangpo.lianfu.config.WeiXin.Constants.STATE;
        api.sendReq(req);
        registerReceiver(mBrocastReceiver,new IntentFilter(WXEntryActivity.ACTION));
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle= (Bundle) msg.obj;
            String access_token=bundle.getString("access_token");
            String openid=bundle.getString("openid");
            getUserInfo(access_token, openid);
            OAuthLogin(openid,"0");
        }
    };
    private BroadcastReceiver mBrocastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WXEntryActivity.ACTION)){
                String access_token=intent.getStringExtra("access_token");
                String openid=intent.getStringExtra("openid");
                Configs.cacheOpenIdAndLoginType(MainActivity.this,openid,"0");
                Message msg=new Message();
                Bundle bundle=new Bundle();
                bundle.putString("access_token",access_token);
                bundle.putString("openid",openid);

                msg.obj=bundle;
                mHandler.sendMessage(msg);
            }

            if (mBrocastReceiver!=null){
                unregisterReceiver(mBrocastReceiver);
                mBrocastReceiver=null;
            }

        }
    };

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
                Configs.cacheThirdUser(MainActivity.this,response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                ToastUtils.showToast(getApplicationContext(), getString(R.string.invalid_openid), Toast.LENGTH_SHORT);
            }
        });
    }

    public void Weibo() {
        //以下是微博授权实例
        mAuthInfo=new AuthInfo(this, Constants.APP_KEY,Constants.REDIRECT_URL,Constants.SCOPE);
        mSsoHandler=new SsoHandler(MainActivity.this,mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

    public void QQ() {
        //以下是QQ授权实例
        mAppid = AppConstants.APP_ID;
        mTencent=Tencent.createInstance(mAppid,this);
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", loginListener);
        }
        mTencent.logout(this);
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
//            //从这里获取用户输入的 电话号码信息
//            String  uid =  mAccessToken.getUid();
            String openid=mAccessToken.getToken();
            Configs.cacheOpenIdAndLoginType(MainActivity.this,openid,"1");
            OAuthLogin(openid,"1");
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                //updateTokenView(false);

                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(MainActivity.this, mAccessToken);
                Toast.makeText(MainActivity.this,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(MainActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(MainActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                    //Util.showResultDialog(MainActivity.this, response.toString(), getString(R.string.login_success));
                    Configs.cacheThirdUser(MainActivity.this,response.toString());
                    //第三方登录
                } else {
                    Util.showResultDialog(MainActivity.this, getString(R.string.return_is_null), getString(R.string.login_fail));
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(MainActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if(requestCode=='胍'){
            if (mSsoHandler != null) {
                mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
                mAccessToken=AccessTokenKeeper.readAccessToken(this);
                //获取用户信息接口
                mUsersAPI=new UsersAPI(this,Constants.APP_KEY,mAccessToken);
                if(mAccessToken!=null && mAccessToken.isSessionValid()){
                    long uid=Long.parseLong(mAccessToken.getUid());
                    mUsersAPI.show(uid,mListener);
                }
            }
        }
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN ||
                requestCode == com.tencent.connect.common.Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
            mInfo=new UserInfo(this,MainActivity.mTencent.getQQToken());
            mInfo.getUserInfo(new BaseUIListener(this, "get_simple_userinfo"));
        }
    }
    BaseUiListener loginListener=new BaseUiListener();

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(MainActivity.this, getString(R.string.return_is_null), getString(R.string.login_fail));
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(MainActivity.this, getString(R.string.return_is_null), getString(R.string.login_fail));
                return;
            }
            //这里的value缓存了openid的信息
            try {
                String token = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
                String expires = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                String openId = jsonResponse.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)){
                    //将openid和token保存起来
                    mTencent.setAccessToken(token, expires);
                    mTencent.setOpenId(openId);
                    Configs.cacheOpenIdAndLoginType(MainActivity.this,openId,"2");
                    //第三方登录
                    OAuthLogin(openId,"2");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
            Util.toastMessage(MainActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(MainActivity.this, "onCancel: ");
            Util.dismissDialog();
        }
    }


    private void OAuthLogin(final String openid, final String logintype){
        pd = ProgressDialog.show(MainActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[]=new String[]{openid,logintype};
        String params= OAuth.packagingParam(kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                Configs.cleanData(MainActivity.this);
                try {
                    JSONObject jsonObject = result.getJSONObject("param");
                    String sessid = jsonObject.getString("session_id");
                    Configs.cacheToken(getApplicationContext(), sessid);
                    Configs.cacheUser(getApplicationContext(), jsonObject.toString());
                    intent = new Intent(MainActivity.this, HomePageActivity.class);
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

                        Intent intent=new Intent(MainActivity.this,RelationActivity.class);
                        startActivity(intent);
                        //finish();
                    }
                    if(status.equals("10")){
                        ToastUtils.showToast(MainActivity.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBrocastReceiver!=null){
            mBrocastReceiver=null;
        }
    }
}
