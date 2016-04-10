package com.tangpo.lianfu.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetUnionPayOrder;
import com.tangpo.lianfu.parms.PayBill;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shengshoubo on 2016/4/9.
 */
public class PayByBankCard extends FragmentActivity implements View.OnClickListener {

    private Button back;
    private WebView webView;

    private ProgressDialog dialog=null;
    private Bundle bundle=null;
    private String user_id=null;
    private String store_id=null;
    private String idlist=null;
    private String paymode=null;
    private String fee=null;

    private String orderid=null;
    private String amount=null;
    private String payurl=null;

    //这里使用的是匿名内部类
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    idlist= (String) msg.obj;
                    getIndent();
                    break;
                case 2:
                    JSONObject jsonObject= (JSONObject) msg.obj;
                    try {
                        orderid=jsonObject.getString("orderid");
                        amount=jsonObject.getString("amount");
                        payurl=jsonObject.getString("payurl");
                        webView.loadUrl(payurl);
                        //Log.e("tag","payurl:"+payurl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Tools.deleteActivity(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_pay_by_bank_card);
        Tools.gatherActivity(this);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);

        webView= (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.requestFocus();
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        bundle=getIntent().getExtras();
        store_id=bundle.getString("store_id");
        user_id=bundle.getString("user_id");
        paymode=bundle.getString("paymode");
        fee=bundle.getString("fee");
        payBill();


    }

    private void payBill(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(),getString(R.string.network_has_not_connect));
            return;
        }
        String user_id=bundle.getString("user_id");
        String store_id=bundle.getString("store_id");
        String pay_way=bundle.getString("pay_way");
        String fee=bundle.getString("fee");
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
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=result.getJSONObject("param");
                    idlist=jsonObject.getString("cost_id");
                    Message msg=new Message();
                    msg.obj=idlist;
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
                    Tools.handleResult(PayByBankCard.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    private void getIndent(){
        if(!Tools.checkLAN()){
            ToastUtils.showToast(this,getString(R.string.network_has_not_connect),Toast.LENGTH_SHORT);
            return;
        }
        String[] kvs=new String[]{store_id,user_id,idlist,paymode,fee};
        String params= GetUnionPayOrder.packagingParam(this,kvs);
        dialog=ProgressDialog.show(this,getString(R.string.connecting),getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=result.getJSONObject("param");
                    Message msg=new Message();
                    msg.what=2;
                    msg.obj=jsonObject;
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
                    if("0".equals(status)){
                        ToastUtils.showToast(PayByBankCard.this,getString(R.string.order_success),Toast.LENGTH_SHORT);
                    }else if("1".equals(status)){
                        ToastUtils.showToast(PayByBankCard.this,getString(R.string.format_error),Toast.LENGTH_SHORT);
                    }else if("10".equals(status)){
                        ToastUtils.showToast(PayByBankCard.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }else if("300".equals(status)){
                        ToastUtils.showToast(PayByBankCard.this,getString(R.string.input_error),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
