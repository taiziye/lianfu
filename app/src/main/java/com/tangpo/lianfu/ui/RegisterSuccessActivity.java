package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class RegisterSuccessActivity extends Activity implements OnClickListener {

    private Button back_home;
    private Button perfect_info;

    private ImageView logo;

    private UserEntity user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_success);

        init();
    }

    private void init() {
        back_home = (Button) findViewById(R.id.back_home);
        back_home.setOnClickListener(this);
        perfect_info = (Button) findViewById(R.id.perfect_info);
        perfect_info.setOnClickListener(this);

        logo = (ImageView) findViewById(R.id.logo);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_home:
                SharedPreferences preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(Configs.KEY_TOKEN);
                editor.commit();
                Tools.gotoActivity(RegisterSuccessActivity.this, MainActivity.class);
                finish();
                break;
            case R.id.perfect_info:
                /*Intent intent = new Intent(RegisterSuccessActivity.this, PersonalInfoActivity.class);
                startActivity(intent);*/
                break;
        }
    }

    private void cacheUser() {
        /*String json = JsonConvert.SerializeObject(user);
        Configs.cacheUser(getApplicationContext(), jsonObject.toString());*/
    }
}
