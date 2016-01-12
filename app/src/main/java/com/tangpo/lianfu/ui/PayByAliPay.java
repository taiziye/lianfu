package com.tangpo.lianfu.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetAlipayOrder;
import com.tangpo.lianfu.parms.PayBill;
import com.tangpo.lianfu.utils.Key;
import com.tangpo.lianfu.utils.PayResult;
import com.tangpo.lianfu.utils.SignUtils;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class PayByAliPay extends FragmentActivity {

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private static final int COST_ID = 3;

    private static final int ORDER_INFO = 4;

    //商户的PID
    public  String partner = null;
    // 商户收款账号
    public  String seller_id = null;
    //回调地址
    public  String notify_url=null;
    //商品标题
    private String subject=null;
    //商品详情
    private String body=null;
    // 支付宝公钥
    public  String RSA_PUBLIC_KEY =null;
    // 商户私钥，pkcs8格式
    public  String RSA_PRIVATE_KEY =null;
    //订单号
    public  String out_trade_no=null;
    //交易金额
    private String total_fee=null;

    private TextView tvSubject;
    private TextView tvBody;
    private TextView tvPrice;

    private Bundle bundle=null;
    private String consume_id=null;
    private String cost_id=null;

    private ProgressDialog dialog=null;

    private boolean isExistAccount=false;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastUtils.showToast(PayByAliPay.this, getString(R.string.pay_success), Toast.LENGTH_SHORT);
                        Intent intent=new Intent(PayByAliPay.this,OnlinePayActivity.class);
                        intent.putExtra("total_fee",total_fee);
                        startActivity(intent);
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtils.showToast(PayByAliPay.this, getString(R.string.pay_result_is_confirming), Toast.LENGTH_SHORT);
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastUtils.showToast(PayByAliPay.this, getString(R.string.pay_fail), Toast.LENGTH_SHORT);
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    if((boolean)msg.obj==false){
                        ToastUtils.showToast(PayByAliPay.this, getString(R.string.please_check_and_login_your_alipay_account), Toast.LENGTH_SHORT);
                    }
                    isExistAccount= (boolean) msg.obj;
                    if(isExistAccount==true){
                        pay();
                    }
                    break;
                }
                case COST_ID: {
                    if(msg.obj!=null){
                        cost_id= (String) msg.obj;
                        String store_id=bundle.getString("store_id");
                        String user_id=bundle.getString("user_id");
                        String idlist=cost_id;
                        String paymode=bundle.getString("paymode");
                        String fee=bundle.getString("fee");
                        getIndent(store_id,user_id,idlist,paymode,fee);
                    }
                    break;
                }
                case ORDER_INFO:{
                    if(msg.obj!=null){
                        JSONObject object= (JSONObject) msg.obj;
                        try {
                            partner=object.getString("partner");
                            seller_id=object.getString("seller_id");
                            notify_url=object.getString("notify_url");
                            subject=object.getString("subject");
                            body=object.getString("body");
                            RSA_PUBLIC_KEY=object.getString("RSA_PUBLIC_KEY");
                            RSA_PRIVATE_KEY=object.getString("RSA_PRIVATE_KEY");
                            out_trade_no=object.getString("out_trade_no");
                            total_fee=object.getString("total_fee");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_by_ali_pay);

        tvSubject= (TextView) findViewById(R.id.product_subject);
        tvBody= (TextView) findViewById(R.id.product_body);
        tvPrice= (TextView) findViewById(R.id.product_price);


        bundle=getIntent().getExtras();
        String store_id=bundle.getString("store_id");
        String user_id=bundle.getString("user_id");
        String paymode=bundle.getString("paymode");
        String fee=bundle.getString("fee");
        Log.e("tag",fee);
        String idlist=null;
        consume_id=bundle.getString("consume_id");
        if(consume_id==null||consume_id.length()==0){
            payBill();
        }else{
            idlist=consume_id;
            getIndent(store_id,user_id,idlist,paymode,fee);
        }
        /**
         * 这里测试的时候先注释这一行，转一分钱到平台支付宝账号
         */
        total_fee=fee;
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    private void pay() {
        if(isExistAccount==false)return;
        if (TextUtils.isEmpty(partner) || TextUtils.isEmpty(RSA_PRIVATE_KEY)
                || TextUtils.isEmpty(seller_id)) {
            new AlertDialog.Builder(this)
                    .setTitle("警告")
                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    finish();
                                }
                            }).show();
            return;
        }
        // 订单
        String orderInfo = getOrderInfo(subject, body, total_fee);

        // 对订单做RSA 签名
        String sign = sign(orderInfo);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayByAliPay.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     *
     */
    public void check(View view) {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(PayByAliPay.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    public String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + partner + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + seller_id + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notify_url
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    public String sign(String content) {
        return SignUtils.sign(content, Key.RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
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
        String phone=bundle.getString("phone")+"";
        String receipt_no=bundle.getString("receipt_no")+"";
        String receipt_photo=bundle.getString("receipt_photo")+"";
        String online=bundle.getString("online");

        String kvs[]=new String[]{user_id,store_id,fee,phone,receipt_no,receipt_photo,online,pay_way};
        if (fee.equals("")){
            ToastUtils.showToast(this,getString(R.string.fee_can_not_be_null),Toast.LENGTH_SHORT);
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
                    cost_id=jsonObject.getString("cost_id");
                    Message msg=new Message();
                    msg.what=COST_ID;
                    msg.obj=cost_id;
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
                    Tools.handleResult(PayByAliPay.this, result.getString("status"));
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
        String params= GetAlipayOrder.packagingParam(this, kvs);
        dialog=ProgressDialog.show(this,getString(R.string.connecting),getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    JSONObject jsonObject=result.getJSONObject("param");
                    tvSubject.setText(jsonObject.getString("subject"));
                    tvBody.setText(jsonObject.getString("body"));
                    tvPrice.setText(jsonObject.getString("total_fee"));
                    Message msg=new Message();
                    msg.what=ORDER_INFO;
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
                    if(status.equals("1")){
                        ToastUtils.showToast(PayByAliPay.this,getString(R.string.format_error),Toast.LENGTH_SHORT);
                    }else if(status.equals("10")){
                        ToastUtils.showToast(PayByAliPay.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }else if(status.equals("300")){
                        ToastUtils.showToast(PayByAliPay.this,getString(R.string.input_error),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }
}
