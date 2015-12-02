package com.tangpo.lianfu.ui;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tangpo.lianfu.R;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_pay_method, menu);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
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
                Intent intent=new Intent(SelectPayMethod.this,PayByAliPay.class);
                bundle.putString("pay_way","2");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.pay_by_wechat:

                break;
            case R.id.pay_by_bankcard:

                break;
            default:
                finish();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
}
