package com.tangpo.lianfu.ui;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetWeichatOrder;
import com.tangpo.lianfu.parms.PayBill;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class PayByWechat extends FragmentActivity {

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
        tvPrice.setText(price+"元");
    }

    public void check(View v){
        payBill();
    }

    private void payBill(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        String user_id=bundle.getString("user_id");
        String store_id=bundle.getString("store_id");
        String pay_way=bundle.getString("pay_way");
        String fee=bundle.getString("fee");
        String phone=bundle.getString("phone");
        String receipt_no=bundle.getString("receipt_no");
        String receipt_photo=bundle.getString("receipt_photo");
        String online="true";

        String kvs[]=new String[]{user_id,store_id,fee,phone,receipt_no,receipt_photo,online,pay_way};
        if (fee.equals("")){
            ToastUtils.showToast(this, getString(R.string.fee_can_not_be_null), Toast.LENGTH_SHORT);
            return;
        }
        //dialog= ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String params= PayBill.packagingParam(this, kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //dialog.dismiss();
                try {
                    JSONObject jsonObject=result.getJSONObject("param");
                    cost_id=jsonObject.getString("cost_id");
                    Log.e("tag",cost_id);
                    getIndent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // ToastUtils.showToast(SelectPayMethod.this,getString(R.string.request_success),Toast.LENGTH_SHORT);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //dialog.dismiss();
                try {
                    Tools.handleResult(PayByWechat.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    private void getIndent(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        String store_id=bundle.getString("store_id");
        String user_id=bundle.getString("user_id");
        String idlist=cost_id;
        String paymode="0";
        String fee=bundle.getString("fee");
        String[] kvs=new String[]{store_id,user_id,idlist,paymode,fee};
        String params= GetWeichatOrder.packagingParam(this, kvs);
        dialog=ProgressDialog.show(this,getString(R.string.connecting),getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=result.getJSONObject("param");
                    Log.e("tag", jsonObject.toString());
                    PayReq req=new PayReq();
                    req.appId=jsonObject.getString("appid");
                    req.partnerId=jsonObject.getString("partnerid");
                    req.prepayId=jsonObject.getString("prepayid");
                    req.packageValue=jsonObject.getString("package");
                    req.nonceStr=jsonObject.getString("noncestr");
                    req.timeStamp=jsonObject.getString("timestamp");
                    req.sign=jsonObject.getString("sign");
                    api.sendReq(req);
                    //finish();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay_by_wechat, menu);
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
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
