package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class OnlinePayActivity extends Activity implements View.OnClickListener {

    private Button back_home;

    private ImageView logo;

    private TextView money;

    private TextView profit;

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
        setContentView(R.layout.online_pay_activity);

        Tools.gatherActivity(this);

        init();
    }

    private void init() {
        back_home = (Button) findViewById(R.id.back_home);
        back_home.setOnClickListener(this);

        logo = (ImageView) findViewById(R.id.logo);
        profit= (TextView) findViewById(R.id.profit);
        money = (TextView) findViewById(R.id.money);

        String totle_fee=getIntent().getStringExtra("total_fee");
        profit.setText(totle_fee);
        money.setText(totle_fee);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_home:
                Tools.gotoActivity(OnlinePayActivity.this, HomePageActivity.class);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
