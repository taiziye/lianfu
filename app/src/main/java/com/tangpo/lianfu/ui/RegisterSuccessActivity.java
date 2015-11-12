package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class RegisterSuccessActivity extends Activity implements OnClickListener {

    private Button back_home;
    private Button perfect_info;

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_success);

        init();
    }

    private void init() {
        back_home = (Button)findViewById(R.id.back_home);
        back_home.setOnClickListener(this);
        perfect_info = (Button)findViewById(R.id.perfect_info);
        perfect_info.setOnClickListener(this);

        logo = (ImageView)findViewById(R.id.logo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_home:
                Tools.gotoActivity(RegisterSuccessActivity.this, HomePageActivity.class);
                finish();
                break;
            case R.id.perfect_info:
                break;
        }
    }
}
