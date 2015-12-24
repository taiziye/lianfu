package com.tangpo.lianfu.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.ui.PayBillActivity;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;

	private int COMMAND_PAY_BY_WX = 5;
	
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
		if(resp.errCode== BaseResp.ErrCode.ERR_OK){
			ToastUtils.showToast(WXPayEntryActivity.this, getString(R.string.pay_success), Toast.LENGTH_SHORT);
		}else if(resp.errCode== BaseResp.ErrCode.ERR_AUTH_DENIED){
			ToastUtils.showToast(WXPayEntryActivity.this,getString(R.string.auth_denied),Toast.LENGTH_SHORT);
		}else if(resp.errCode== BaseResp.ErrCode.ERR_USER_CANCEL){
			ToastUtils.showToast(WXPayEntryActivity.this,getString(R.string.user_cacel),Toast.LENGTH_SHORT);
		}else if(resp.errCode== BaseResp.ErrCode.ERR_SENT_FAILED){
			ToastUtils.showToast(WXPayEntryActivity.this,getString(R.string.sent_failed),Toast.LENGTH_SHORT);
		}else if(resp.errCode== BaseResp.ErrCode.ERR_COMM){
			ToastUtils.showToast(WXPayEntryActivity.this,getString(R.string.comm_err),Toast.LENGTH_SHORT);
		}else{
			ToastUtils.showToast(WXPayEntryActivity.this,getString(R.string.unsupport),Toast.LENGTH_SHORT);
		}
		Intent intent=new Intent(WXPayEntryActivity.this, PayBillActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
}