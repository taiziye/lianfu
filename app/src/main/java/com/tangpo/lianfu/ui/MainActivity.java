package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.Login;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements View.OnClickListener {

    private EditText user_name;
    private EditText user_pass;
    private Button login;

    private TextView forget;
    private TextView register;

    private ImageView login_as;

    private ProgressDialog pd = null;

    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        String token = Configs.getCatchedToken(this);
        //判断用户是否登录，如果已登录，则跳过该页面
        if (token != null) {  //如果已登录
            //根据登录身份跳转到相应的界面
            Tools.gotoActivity(MainActivity.this, HomePageActivity.class);
            this.finish();
        }
        init();
    }

    private void init() {
        user_name = (EditText) findViewById(R.id.user_name);
        user_pass = (EditText) findViewById(R.id.user_pass);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);

        forget = (TextView) findViewById(R.id.forget);
        forget.setOnClickListener(this);
        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        login_as = (ImageView) findViewById(R.id.login_as);
        login_as.setOnClickListener(this);
    }

    private void login() {
        String name = user_name.getText().toString();
        if (name.equals("")) {
            pd.dismiss();
            ToastUtils.showToast(this, getString(R.string.username_cannot_be_null), Toast.LENGTH_SHORT);
            return;
        }
        String pass = user_pass.getText().toString();
        if (pass.equals("")) {
            pd.dismiss();
            ToastUtils.showToast(this, getString(R.string.password_cannot_be_null), Toast.LENGTH_SHORT);
            return;
        }
        //String openId="";
        String kvs[] = new String[]{name, pass};

        String params = Login.packagingParam(kvs);

        System.out.println(Escape.unescape(params));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                try {
                    Log.e("tag", "tag " + result.toString());
                    JSONObject jsonObject = result.getJSONObject("param");
                    String sessid = jsonObject.getString("session_id");
                    Configs.cacheToken(getApplicationContext(), sessid);
                    Configs.cacheUser(getApplicationContext(), jsonObject.toString());
                    System.out.println(Escape.unescape(result.toString()));
                    intent = new Intent(MainActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                System.out.println(Escape.unescape(result.toString()));
                try {
                    if("2".equals(result.getString("status"))) {
                        Tools.showToast(MainActivity.this, "用户名或密码错误");
                    }else{
                        Tools.showToast(MainActivity.this, getString(R.string.fail_to_login));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    public void register() {
        intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void forgetPassword() {
//        intent=new Intent(MainActivity.this,ForgetPasswordActivity.class);
//        startActivity(intent);
//        finish();
    }

    public void loginAs() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                pd = ProgressDialog.show(MainActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
                login();
                break;
            case R.id.register:
                register();
                break;
            case R.id.forget:
                forgetPassword();
                break;
            case R.id.login_as:
                loginAs();
                break;
        }
    }
}
