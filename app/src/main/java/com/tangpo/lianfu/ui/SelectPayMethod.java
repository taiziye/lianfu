package com.tangpo.lianfu.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

public class SelectPayMethod extends FragmentActivity implements View.OnClickListener {

    private LinearLayout pay_by_alipay;
    private LinearLayout pay_by_wechat;
    private LinearLayout pay_by_bankcard;
    private LinearLayout dialogLayout;

    private ImageView img1;
    private ImageView img2;
    private ImageView img3;

    private Bundle bundle;

    private Intent intent=null;

    private IWXAPI api;

    private ProgressDialog dia=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_pay_method);
        Tools.gatherActivity(SelectPayMethod.this);
        init();
    }

    private void init(){
        dialogLayout= (LinearLayout) findViewById(R.id.dialog_layout);
        dialogLayout.setOnClickListener(this);

        pay_by_alipay= (LinearLayout) findViewById(R.id.pay_by_alipay);
        pay_by_alipay.setOnClickListener(this);

        pay_by_wechat= (LinearLayout) findViewById(R.id.pay_by_wechat);
        pay_by_wechat.setOnClickListener(this);

        pay_by_bankcard= (LinearLayout) findViewById(R.id.pay_by_bankcard);
        pay_by_bankcard.setOnClickListener(this);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);

        img1.setImageResource(R.drawable.ali_pay);
        img2.setImageResource(R.drawable.weixin_pay);
        img3.setImageResource(R.drawable.card_pay);

        bundle=getIntent().getExtras();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pay_by_alipay:
                intent=new Intent(SelectPayMethod.this,PayByAliPay.class);
                bundle.putString("pay_way","2");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.pay_by_wechat:
                //此功能即将开发
                // 通过WXAPIFactory工厂，获取IWXAPI的实例
                //api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
                ToastUtils.showToast(SelectPayMethod.this,getString(R.string.wechat_pay_has_not_online), Toast.LENGTH_SHORT);
                //finish();
                //getIndent();
                break;
            case R.id.pay_by_bankcard:
                ToastUtils.showToast(SelectPayMethod.this,getString(R.string.bankcard_pay_has_not_online),Toast.LENGTH_SHORT);
                //finish();
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    private void  getIndent(){
        //这里是服务器提供的订单的信息
//        String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
//        Button payBtn = (Button) findViewById(R.id.appay_btn);
//        payBtn.setEnabled(false);
//        Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
//        try{
//            byte[] buf = Util.httpGet(url);
//            if (buf != null && buf.length > 0) {
//                String content = new String(buf);
//                Log.e("get server pay params:", content);
//                JSONObject json = new JSONObject(content);
//                if(null != json && !json.has("retcode") ){
//                    PayReq req = new PayReq();
//                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
//                    req.appId			= json.getString("appid");
//                    req.partnerId		= json.getString("partnerid");
//                    req.prepayId		= json.getString("prepayid");
//                    req.nonceStr		= json.getString("noncestr");
//                    req.timeStamp		= json.getString("timestamp");
//                    req.packageValue	= json.getString("package");
//                    req.sign			= json.getString("sign");
//                    req.extData			= "app data"; // optional
//                    Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
//                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//                    api.sendReq(req);
//                }else{
//                    Log.d("PAY_GET", "返回错误"+json.getString("retmsg"));
//                    Toast.makeText(PayActivity.this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
//                }
//            }else{
//                Log.d("PAY_GET", "服务器请求错误");
//                Toast.makeText(PayActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
//            }
//        }catch(Exception e){
//            Log.e("PAY_GET", "异常："+e.getMessage());
//            Toast.makeText(PayActivity.this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
    }
}
