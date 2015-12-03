package com.tangpo.lianfu.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tangpo.lianfu.MyApplication;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
//import com.tencent.mm.sdk.openapi.SendAuth;

/**
 * Created by shengshoubo on 2015/11/30.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private void handleIntent(Intent paramIntent) {
        MainActivity.api.handleIntent(paramIntent,this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
// TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
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
//                String code = ((SendAuth.Resp) resp).code;
                System.out.println("errcode_success");
                Log.e("tag","errcode_success");
                String code = ((SendAuth.Resp) resp).token;
                ToastUtils.showToast(this,"code", Toast.LENGTH_LONG);
                Log.e("tag",code);
                String url = "https://api.weixin.qq.com/sns/oauth2/access_token" +

                        "?appid=" + Constants.APP_ID +

                        "&secret=" + Constants.APP_KEY +

                        "&code=" + code +

                        "&grant_type=authorization_code";

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
}
