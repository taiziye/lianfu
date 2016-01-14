package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.entity.StoreInfo;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.BindAccount;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.parms.UnbindAccount;
import com.tangpo.lianfu.utils.CircularImage;
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

/**
 * Created by 果冻 on 2015/11/3.
 */
public class ManagerFragment extends Fragment implements OnClickListener {

    private final static int SCANNIN_STORE_INFO = 1;
    private final static int GET_STORE_INFO = 2;
    private final static int GET_OPENID = 3;
    private Button double_code;
    private Button chat;
    private Button login_out;

    private Button bind_weibo;
    private Button bind_weixin;
    private Button bind_qq;

    private TextView weibo_nick;
    private TextView weixin_nick;
    private TextView qq_nick;

    private CircularImage img;
    private ImageView next;
    private TextView remainder;

    private TextView power;
    private TextView name;
    private TextView user_name;
    private LinearLayout shop_info;
    private LinearLayout personal_info;
    private LinearLayout discount_manage;
    private LinearLayout update_type;
    private LinearLayout modify_pass;
    private SharedPreferences preferences = null;
    private Gson gson = null;
    private UserEntity user = null;
    private String user_id=null;
    private Intent intent = null;

    private ProgressDialog dialog=null;

    private FindStore store=null;

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

    private String isbindwb;
    private String isbindwx;
    private String isbindqq;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_fragment, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        gson = new Gson();

        bind_weibo= (Button) view.findViewById(R.id.bind_weibo);
        bind_weixin= (Button) view.findViewById(R.id.bind_wexin);
        bind_qq= (Button) view.findViewById(R.id.bind_qq);

        bind_weibo.setOnClickListener(this);
        bind_weixin.setOnClickListener(this);
        bind_qq.setOnClickListener(this);

        weibo_nick= (TextView) view.findViewById(R.id.weibo_nick);
        weixin_nick= (TextView) view.findViewById(R.id.weixin_nick);
        qq_nick= (TextView) view.findViewById(R.id.qq_nick);

        double_code = (Button) view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        chat = (Button) view.findViewById(R.id.chat);
        chat.setOnClickListener(this);
        login_out = (Button) view.findViewById(R.id.login_out);
        login_out.setOnClickListener(this);

        img = (CircularImage) view.findViewById(R.id.img);
        remainder = (TextView) view.findViewById(R.id.remainder);

        power = (TextView) view.findViewById(R.id.power);
        name = (TextView) view.findViewById(R.id.name);
        user_name = (TextView) view.findViewById(R.id.user_name);
        shop_info = (LinearLayout) view.findViewById(R.id.shop_info);
        shop_info.setOnClickListener(this);
        personal_info = (LinearLayout) view.findViewById(R.id.personal_info);
        personal_info.setOnClickListener(this);
        discount_manage = (LinearLayout) view.findViewById(R.id.discount_manage);
        discount_manage.setOnClickListener(this);
        update_type = (LinearLayout) view.findViewById(R.id.update_type);
        update_type.setOnClickListener(this);
        modify_pass = (LinearLayout) view.findViewById(R.id.modify_pass);
        modify_pass.setOnClickListener(this);

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String str = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(str);
            user = gson.fromJson(jsonObject.toString(), UserEntity.class);
            user_id=user.getUser_id();
            isbindwb=user.getBindwb();
            isbindwx=user.getBindwx();
            isbindqq=user.getBindqq();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * 获取头像
         */
        Tools.setPhoto(getActivity(), user.getPhoto(), img);

        user_name.setText("");
        if("3".equals(user.getUser_type())){
            power.setText(getString(R.string.manager));
        }else{
            power.setText(getString(R.string.shop_ownner));
        }
        name.setText(user.getName());
        remainder.setText(user.getMoney());

        if("1".equals(user.getBindwb())){
            bind_weibo.setText(getString(R.string.unbind));
            bind_weibo.setBackgroundResource(R.drawable.unbind);
        }
        if("1".equals(user.getBindwx())){
            bind_weixin.setText(getString(R.string.unbind));
            bind_weixin.setBackgroundResource(R.drawable.unbind);
        }
        if("1".equals(user.getBindqq())){
            bind_qq.setText(getString(R.string.unbind));
            bind_qq.setBackgroundResource(R.drawable.unbind);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.double_code:
                //扫描二维码
                intent=new Intent();
                intent.setClass(getActivity(),MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,SCANNIN_STORE_INFO);
                break;
            case R.id.chat:
                break;
            case R.id.shop_info:
                intent = new Intent(getActivity(), ShopInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.personal_info:
                intent = new Intent(getActivity(), PersonalInfoActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("flag", "1");
                startActivity(intent);
                //Tools.showToast(getActivity(), "请期待下一个版本");
                break;
            case R.id.discount_manage:
                intent = new Intent(getActivity(), DiscountManageActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.update_type:
                /**
                 * 无接口
                 */
                intent = new Intent(getActivity(), MemberUpdateTypeActivity.class);
                startActivity(intent);
                break;
            case R.id.modify_pass:
                intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.login_out:
                Configs.cleanData(getActivity());
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

            case R.id.bind_weibo:
                Tools.showToast(getActivity(),getString(R.string.new_function_has_not_online));
//                if("0".equals(isbindwb)){
//                    Weibo();
//                }
//                else{
//                    unBind("1");
//                }
                break;
            case R.id.bind_wexin:
                Tools.showToast(getActivity(),getString(R.string.new_function_has_not_online));
//                if("0".equals(isbindwx)){
//                    Weixin();
//                }
//                else{
//                    unBind("0");
//                }
                break;
            case R.id.bind_qq:
                Tools.showToast(getActivity(),getString(R.string.new_function_has_not_online));
//                if("0".equals(isbindqq)){
//                    QQ();
//                }
//                else{
//                    unBind("2");
//                }
                break;
        }
    }

    private void unBind(final String logintype) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }
        String[] kvs=new String[]{user_id,logintype};
        String param= UnbindAccount.packagingParam(getActivity(),kvs);
        dialog=ProgressDialog.show(getActivity(),getString(R.string.connecting),getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                if(logintype.equals("1")){
                    bind_weibo.setText(getString(R.string.bind));
                    bind_weibo.setBackgroundResource(R.drawable.bind);
                    isbindwb="0";
                }else if(logintype.equals("0")){
                    bind_weixin.setText(getString(R.string.bind));
                    bind_weixin.setBackgroundResource(R.drawable.bind);
                    isbindwx="0";
                }else{
                    bind_qq.setText(getString(R.string.bind));
                    bind_qq.setBackgroundResource(R.drawable.bind);
                    isbindqq="0";
                }
                Tools.showToast(getActivity(), getString(R.string.unbind_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if ("3".equals(status)){
                        Tools.showToast(getActivity(),getString(R.string.this_account_has_bind_other_lianfu_account));
                    }else if("10".equals(status)){
                        Tools.showToast(getActivity(),getString(R.string.server_exception));
                    }else{
                        Tools.showToast(getActivity(),getString(R.string.input_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },param);
    }

    public void Weixin() {
        // 注册微信
        api= WXAPIFactory.createWXAPI(getActivity(), com.tangpo.lianfu.config.WeiXin.Constants.APP_ID, true);
        api.registerApp(com.tangpo.lianfu.config.WeiXin.Constants.APP_ID);
        SendAuth.Req req=new SendAuth.Req();
        req.scope= com.tangpo.lianfu.config.WeiXin.Constants.SCOPE;
        req.state= com.tangpo.lianfu.config.WeiXin.Constants.STATE;
        api.sendReq(req);
        getActivity().registerReceiver(mBrocastReceiver,new IntentFilter(WXEntryActivity.ACTION));
    }

    private BroadcastReceiver mBrocastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String openid=intent.getStringExtra("openid");
            Message msg=new Message();
            msg.what=GET_OPENID;
            msg.obj=openid;
            handler.sendMessage(msg);

            if(mBrocastReceiver!=null){
                getActivity().unregisterReceiver(mBrocastReceiver);
                mBrocastReceiver=null;
            }
        }
    };

    public void Weibo() {
        //以下是微博授权实例
        mAuthInfo=new AuthInfo(getActivity(),Constants.APP_KEY,Constants.REDIRECT_URL,Constants.SCOPE);
        mSsoHandler=new SsoHandler(getActivity(),mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
    }

    public void QQ() {
        //以下是QQ授权实例
        mAppid = AppConstants.APP_ID;
        mTencent= Tencent.createInstance(mAppid, getActivity());
        if (!mTencent.isSessionValid()) {
            mTencent.login(getActivity(), "all", loginListener);
        }
        mTencent.logout(getActivity());
    }

    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            String openid=mAccessToken.getToken();
            Log.e("tag",">>>>>>>>>>>>>>>>>>>"+openid);
            if (mAccessToken.isSessionValid()) {
                lianfuBindThirdAccount(user_id,openid,"1");
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
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(getActivity(),
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getActivity(),
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
                    Configs.cacheThirdUser(getActivity(),response.toString());
                    //第三方登录
                } else {
                    Util.showResultDialog(getActivity(), getString(R.string.return_is_null), getString(R.string.login_fail));
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(getActivity(), info.toString(), Toast.LENGTH_LONG).show();
        }
    };


    BaseUiListener loginListener=new BaseUiListener();
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(getActivity(), getString(R.string.return_is_null), getString(R.string.login_fail));
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(getActivity(), getString(R.string.return_is_null), getString(R.string.login_fail));
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
                    lianfuBindThirdAccount(user_id,openId,"2");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError e) {
            Util.toastMessage(getActivity(), "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(getActivity(), "onCancel: ");
            Util.dismissDialog();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SCANNIN_STORE_INFO:
                if(resultCode==getActivity().RESULT_OK){
                    Bundle bundle=data.getExtras();
                    String result=bundle.getString("result");
                    //在这里处理返回来的store_id、service_center、referrer
                    String store_id= Uri.parse(result).getQueryParameter("store_id");
                    String service_center=Uri.parse(result).getQueryParameter("service_center");
                    String referrer=Uri.parse(result).getQueryParameter("referrer");

                    if(store_id!=null&&service_center!=null&&referrer!=null){
                        getStoreDetail(store_id,user_id);
                    }
                }
                break;
            case '胍':{
                if (mSsoHandler != null) {
                    mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
                    //获取用户信息接口
                    mUsersAPI=new UsersAPI(getActivity(),Constants.APP_KEY,mAccessToken);
                    if(mAccessToken!=null && mAccessToken.isSessionValid()){
                        long uid=Long.parseLong(mAccessToken.getUid());
                        mUsersAPI.show(uid,mListener);
                    }
                }
            }
            break;
            case com.tencent.connect.common.Constants.REQUEST_LOGIN:
            case  com.tencent.connect.common.Constants.REQUEST_APPBAR:
                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                mInfo=new UserInfo(getActivity(),MainActivity.mTencent.getQQToken());
                mInfo.getUserInfo(new BaseUIListener(getActivity(), "get_simple_userinfo"));
            break;
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_STORE_INFO:
                    FindStore store= (FindStore) msg.obj;
                    String favoriate="0";
                    Intent intent=new Intent(getActivity(),ShopActivity.class);
                    intent.putExtra("store",store);
                    intent.putExtra("userid",user_id);
                    intent.putExtra("favorite",favoriate);
                    startActivity(intent);
                    break;

                case GET_OPENID:
                    String openid= (String) msg.obj;
                    lianfuBindThirdAccount(user_id,openid,"0");
                    break;
            }
        }
    };
    private void getStoreDetail(String store_id,String user_id){
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{store_id, user_id};
        String param = StoreDetail.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    store = gson.fromJson(result.getJSONObject("param").toString(),FindStore.class);
                    Message msg=new Message();
                    msg.what=GET_STORE_INFO;
                    msg.obj=store;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(getActivity(), result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },param);
    }

    private void lianfuBindThirdAccount(String user_id,String openid, final String logintype){
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs=new String[]{user_id,openid,logintype};
        String params= BindAccount.packagingParam(getActivity(),kvs);

        dialog=ProgressDialog.show(getActivity(),getString(R.string.connecting),getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                if(logintype.equals("1")){
                    bind_weibo.setText(getString(R.string.unbind));
                    bind_weibo.setBackgroundResource(R.drawable.unbind);
                    isbindwb="0";
                }else if(logintype.equals("0")){
                    bind_weixin.setText(getString(R.string.unbind));
                    bind_weixin.setBackgroundResource(R.drawable.unbind);
                    isbindwx="1";
                }else{
                    bind_qq.setText(getString(R.string.unbind));
                    bind_qq.setBackgroundResource(R.drawable.unbind);
                    isbindqq="2";
                }
                Tools.showToast(getActivity(), getString(R.string.bind_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if ("3".equals(status)){
                        Tools.showToast(getActivity(),getString(R.string.this_account_has_bind_other_lianfu_account));
                    }else if("10".equals(status)){
                        Tools.showToast(getActivity(),getString(R.string.server_exception));
                    }else{
                        Tools.showToast(getActivity(),getString(R.string.input_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }
}
