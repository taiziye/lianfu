package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/12/9.
 */
public class BoundOrRegister extends Activity implements View.OnClickListener {
    private TextView relate;
    private TextView registe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bound_or_register);

        relate = (TextView) findViewById(R.id.relate);
        relate.setOnClickListener(this);
        registe = (TextView) findViewById(R.id.registe);
        registe.setOnClickListener(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
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
        Intent intent = null;
        switch (v.getId()) {
            case R.id.relate:
                intent = new Intent(BoundOrRegister.this, Boundlianfu.class);
                startActivity(intent);
                finish();
                break;
            case R.id.registe:
                intent = new Intent(BoundOrRegister.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
