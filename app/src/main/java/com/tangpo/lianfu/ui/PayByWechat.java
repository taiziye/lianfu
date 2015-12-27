package com.tangpo.lianfu.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetWeichatOrder;
import com.tangpo.lianfu.parms.PayBill;
import com.tangpo.lianfu.parms.ProfitAccount;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;
import com.tangpo.lianfu.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class PayByWechat extends FragmentActivity {

    private static final int COST_ID = 1;
    private static final int INDENT = 2;
    private String subject;
    private String body;
    private String price;

    private TextView tvSubject;
    private TextView tvBody;
    private TextView tvPrice;

    private Bundle bundle=null;

    private String cost_id=null;
    private ProgressDialog dialog=null;
    private static IWXAPI api=null;
    private String out_trade_no=null;
    private String pay_account=null;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case COST_ID:
                    cost_id= (String) msg.obj;
                    break;
                case INDENT:
                    if(msg.obj!=null){
                        JSONObject object= (JSONObject) msg.obj;
                        try {
                            out_trade_no=object.getString("out_trade_no");
                            pay_account=object.getString("pay_account");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!intent.getAction().equals(WXPayEntryActivity.ACTION)){
                return;
            }
            int errorCode=intent.getIntExtra("errCode", 1);
            if(errorCode== BaseResp.ErrCode.ERR_OK){
                ToastUtils.showToast(PayByWechat.this, getString(R.string.pay_success), Toast.LENGTH_SHORT);
                if (bundle.getString("paymode").equals("1")){
                    ProfitAccount();
                }
                Intent intent1=new Intent(PayByWechat.this,OnlinePayActivity.class);
                intent1.putExtra("total_fee",bundle.getString("fee"));
                startActivity(intent1);
                finish();
            }else if(errorCode== BaseResp.ErrCode.ERR_AUTH_DENIED){
                ToastUtils.showToast(PayByWechat.this,getString(R.string.auth_denied),Toast.LENGTH_SHORT);
            }else if(errorCode== BaseResp.ErrCode.ERR_USER_CANCEL){
                ToastUtils.showToast(PayByWechat.this,getString(R.string.user_cacel),Toast.LENGTH_SHORT);
            }else if(errorCode== BaseResp.ErrCode.ERR_SENT_FAILED){
                ToastUtils.showToast(PayByWechat.this,getString(R.string.sent_failed),Toast.LENGTH_SHORT);
            }else if(errorCode== BaseResp.ErrCode.ERR_COMM){
                ToastUtils.showToast(PayByWechat.this,getString(R.string.comm_err),Toast.LENGTH_SHORT);
            }else{
                ToastUtils.showToast(PayByWechat.this,getString(R.string.unsupport),Toast.LENGTH_SHORT);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_by_wechat);

        api= WXAPIFactory.createWXAPI(PayByWechat.this,Constants.APP_ID);
        api.registerApp(Constants.APP_ID);

        tvSubject= (TextView) findViewById(R.id.product_subject);
        tvBody= (TextView) findViewById(R.id.product_body);
        tvPrice= (TextView) findViewById(R.id.product_price);

        bundle=getIntent().getExtras();
        subject=getString(R.string.pay_profit);
        body=getString(R.string.store_consume_profit);
        /**
         * 这里测试的时候先注释这一行，转一分钱到平台支付宝账号
         */
//        price=bundle.getString("total_fee");
        //price=bundle.getString("0.01");
        price="0.01";
        tvSubject.setText(subject);
        tvBody.setText(body);
        tvPrice.setText(price + "元");
        payBill();
    }

    public void check(View v){
        String store_id=bundle.getString("store_id");
        String user_id=bundle.getString("user_id");
        String idlist=cost_id;
        String paymode=bundle.getString("paymode");
//        String fee=bundle.getString("fee");
        String fee="0.01";
        getIndent(store_id,user_id,idlist,paymode,fee);
        registerReceiver(mBroadcastReceiver,new IntentFilter(WXPayEntryActivity.ACTION));
    }

    private void payBill(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        String user_id=bundle.getString("user_id");
        String store_id=bundle.getString("store_id");
        String pay_way=bundle.getString("pay_way");
//        String fee=bundle.getString("fee");
        String fee="0.01";
        String phone=bundle.getString("phone")+"";
        String receipt_no=bundle.getString("receipt_no")+"";
        String receipt_photo=bundle.getString("receipt_photo")+"";
        String online=bundle.getString("online");

        String kvs[]=new String[]{user_id,store_id,fee,phone,receipt_no,receipt_photo,online,pay_way};
        if (fee.equals("")){
            ToastUtils.showToast(this, getString(R.string.fee_can_not_be_null), Toast.LENGTH_SHORT);
            return;
        }
        dialog= ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String params= PayBill.packagingParam(this, kvs);
        Log.e("tag",params);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=result.getJSONObject("param");
                    cost_id=jsonObject.getString("cost_id");
                    Message msg=new Message();
                    msg.obj=cost_id;
                    msg.what=1;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(PayByWechat.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    private void getIndent(String store_id,String user_id,String idlist,String paymode,String fee){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        String[] kvs=new String[]{store_id,user_id,idlist,paymode,fee};
        String params = GetWeichatOrder.packagingParam(this, kvs);
        Log.e("tag",params);
        dialog=ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=result.getJSONObject("param");
                    PayReq req=new PayReq();
                    req.appId=jsonObject.getString("appid");
                    req.partnerId=jsonObject.getString("partnerid");
                    req.prepayId=jsonObject.getString("prepayid");
                    req.packageValue=jsonObject.getString("package");
                    req.nonceStr=jsonObject.getString("noncestr");
                    req.timeStamp=jsonObject.getString("timestamp");
                    req.sign=jsonObject.getString("sign");
                    api.sendReq(req);
//                    Log.e("tag", jsonObject.toString());
                    Message msg=new Message();
                    msg.obj=jsonObject;
                    msg.what=INDENT;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if(status.equals("1")){
                        ToastUtils.showToast(PayByWechat.this,getString(R.string.format_error),Toast.LENGTH_SHORT);
                    }else if(status.equals("10")){
                        ToastUtils.showToast(PayByWechat.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }else if(status.equals("300")){
                        ToastUtils.showToast(PayByWechat.this,getString(R.string.input_error),Toast.LENGTH_SHORT);
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
        unregisterReceiver(mBroadcastReceiver);
        finish();
    }

    private void ProfitAccount() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        dialog=ProgressDialog.show(PayByWechat.this,getString(R.string.connecting),getString(R.string.please_wait));
        String user_id=bundle.getString("user_id");
        String store_id=bundle.getString("store_id");
        String pay_way=bundle.getString("pay_way");
        final String total_fee=bundle.getString("fee");
        String consume_id=bundle.getString("consume_id");
        if(out_trade_no==null){
            ToastUtils.showToast(this,"请服务器返回交易订单号！",Toast.LENGTH_SHORT);
            return;
        }
        if(pay_account==null){
            ToastUtils.showToast(this,"请服务器返回支付账户！",Toast.LENGTH_SHORT);
            return;
        }
        String kvs[] = new String[]{user_id, store_id, out_trade_no, pay_way, pay_account,total_fee, consume_id};
        String param = ProfitAccount.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                ToastUtils.showToast(PayByWechat.this, getString(R.string.request_success), Toast.LENGTH_SHORT);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(PayByWechat.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
