package com.tangpo.lianfu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

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
                bundle.putString("pay_way","3");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.pay_by_wechat:
                //此功能即将开发
                // 通过WXAPIFactory工厂，获取IWXAPI的实例
                //api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
                //ToastUtils.showToast(SelectPayMethod.this,getString(R.string.wechat_pay_has_not_online), Toast.LENGTH_SHORT);
                intent=new Intent(SelectPayMethod.this,PayByWechat.class);
                bundle.putString("pay_way", "2");
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
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
}
