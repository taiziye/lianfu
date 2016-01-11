package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.Login;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class RegisterSuccessActivity extends Activity implements OnClickListener {

    private Button back_home;
    //private Button perfect_info;

    private ImageView logo;
    private UserEntity user;
    private Gson gson = new Gson();

    private ProgressDialog pd = null;

    private String name = "";
    private String pass = "";
    private String phone = "";

    private boolean flag = false;
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
        //perfect_info = (Button) findViewById(R.id.perfect_info);
        //perfect_info.setOnClickListener(this);

        logo = (ImageView) findViewById(R.id.logo);

        name = getIntent().getStringExtra("username");
        pass = getIntent().getStringExtra("passwd");
        phone = getIntent().getStringExtra("tel");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_home:
                flag = true;
                login();
                break;
//            case R.id.perfect_info:
//                flag = true;
//                login();
//                break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent;
            switch (msg.what) {
                case 1:
                    intent = new Intent(RegisterSuccessActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    Tools.closeActivity();
                    finish();
                    break;
//                case 2:
//                    intent = new Intent(RegisterSuccessActivity.this, PersonalInfoActivity.class);
//                    intent.putExtra("user", user);
//                    intent.putExtra("flag", "2");
//                    intent.putExtra("name", name);
//                    intent.putExtra("pass", pass);
//                    startActivity(intent);
//                    /*Tools.showToast(getApplicationContext(), "请期待下一版");*/
//                    break;
            }
        }
    };

    private void login() {
        if(!Tools.checkLAN()) {
            Tools.showToast(RegisterSuccessActivity.this, "网络未连接，请联网后重试");
            return;
        }
        pd = ProgressDialog.show(RegisterSuccessActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{name, pass};
        String params = Login.packagingParam(kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                try {
                    JSONObject jsonObject = result.getJSONObject("param");
                    String sessid = jsonObject.getString("session_id");
                    Configs.cacheToken(getApplicationContext(), sessid);
                    Configs.cacheUser(getApplicationContext(), jsonObject.toString());
                    //System.out.println(Escape.unescape(result.toString()));
                    user = gson.fromJson(jsonObject.toString(), UserEntity.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
//                if(flag) msg.what = 2;
//                else msg.what = 1;
                msg.what=1;
                msg.obj = user;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                try {
                    if("2".equals(result.getString("status"))) {
                        Tools.showToast(RegisterSuccessActivity.this, "用户名或密码错误");
                    }else{
                        Tools.showToast(RegisterSuccessActivity.this, getString(R.string.fail_to_login));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }
}
