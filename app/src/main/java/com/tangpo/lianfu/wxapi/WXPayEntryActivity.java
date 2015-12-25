package com.tangpo.lianfu.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.ProfitAccount;
import com.tangpo.lianfu.ui.OnlinePayActivity;
import com.tangpo.lianfu.ui.PayBillActivity;
import com.tangpo.lianfu.ui.PayByWechat;
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
		if(resp.errCode== BaseResp.ErrCode.ERR_OK){
			ToastUtils.showToast(WXPayEntryActivity.this, getString(R.string.pay_success), Toast.LENGTH_SHORT);
			SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
			String key_param=preferences.getString(Configs.KEY_PARAM, "");
			try {
				if(key_param!=""){
					JSONObject param=new JSONObject(key_param);
					ProfitAccount(param);
				}else{
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
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

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
	private void ProfitAccount(JSONObject object) throws JSONException {
		if(!Tools.checkLAN()) {
			Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
			return;
		}
		dialog= ProgressDialog.show(WXPayEntryActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
		String user_id=object.getString("user_id");
		String store_id=object.getString("store_id");
		String pay_way=object.getString("pay_way");
		final String total_fee=object.getString("total_fee");
		String consume_id=object.getString("consume_id");
		String out_trade_no=object.getString("out_trade_no");
		String pay_account=object.getString("pay_account");

		String kvs[] = new String[]{user_id, store_id, out_trade_no, pay_way, pay_account,total_fee, consume_id};
		String param = ProfitAccount.packagingParam(this, kvs);

		new NetConnection(new NetConnection.SuccessCallback() {
			@Override
			public void onSuccess(JSONObject result) {
				dialog.dismiss();
				ToastUtils.showToast(WXPayEntryActivity.this, getString(R.string.request_success), Toast.LENGTH_SHORT);
				Intent intent=new Intent(WXPayEntryActivity.this,OnlinePayActivity.class);
				intent.putExtra("total_fee",total_fee);
				startActivity(intent);
				WXPayEntryActivity.this.finish();
			}
		}, new NetConnection.FailCallback() {
			@Override
			public void onFail(JSONObject result) {
				dialog.dismiss();
				try {
					Tools.handleResult(WXPayEntryActivity.this, result.getString("status"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, param);
	}
}