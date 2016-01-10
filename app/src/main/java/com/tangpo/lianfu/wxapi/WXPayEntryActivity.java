package com.tangpo.lianfu.wxapi;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.ui.OnlinePayActivity;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "WXPayEntryActivity";

	public static final String ACTION="com.tangpo.lianfu.wxapi.intent.action.WXPayEntrtyActivity";
    private IWXAPI api;

	private int COMMAND_PAY_BY_WX = 5;

	private ProgressDialog dialog=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp resp) {
		Intent intent=new Intent(WXPayEntryActivity.ACTION);
		intent.putExtra("errCode",resp.errCode);
		sendBroadcast(intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
}