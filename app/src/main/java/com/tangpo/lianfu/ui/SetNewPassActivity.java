package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.SetNewPass;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class SetNewPassActivity extends Activity implements View.OnClickListener {
    private Button back;
    private Button confirm;
    private EditText code;
    private EditText pass;
    private EditText newpass;

    private String username;
    private ProgressDialog dialog = null;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.confirm:
                update();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setnew_pass);
        username = getIntent().getStringExtra("username");
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);

        code = (EditText) findViewById(R.id.code);
        pass = (EditText) findViewById(R.id.pass);
        newpass = (EditText) findViewById(R.id.newpass);
    }

    private void update(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        String codeStr = code.getText().toString().trim();
        if (codeStr.length() == 0) {
            Tools.showToast(getApplicationContext(), "请输入验证码");
            return;
        }
        String pwd = pass.getText().toString().trim();
        if (pwd.length()<6) {
            Tools.showToast(getApplicationContext(), "输入的密码不能少于6位");
            return;
        }
        if (!pwd.equals(newpass.getText().toString().trim())) {
            Tools.showToast(getApplicationContext(), "两次输入的密码不一致，请重新输入");
            newpass.selectAll();
            return;
        }
        String[] kvs = new String[]{username, codeStr, pwd};
        String param = SetNewPass.packagingParam(getApplicationContext(), kvs);
        dialog = ProgressDialog.show(SetNewPassActivity.this, getString(R.string.please_wait), getString(R.string.connecting));

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                dialog.dismiss();
                Tools.showToast(getApplicationContext(), "修改成功");
                Intent intent = new Intent(SetNewPassActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                dialog.dismiss();
                try {
                    if ("1".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "验证码错误");
                    } else if ("2".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "验证码已失效");
                    } else if ("10".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else {
                        Tools.showToast(getApplicationContext(), result.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
