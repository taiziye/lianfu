package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.FindPass;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class ForgetPasswordActivity extends Activity implements OnClickListener {
    private Button back;
    private EditText phone;
    private Button next;

    private ProgressDialog dialog = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.next:
                getCode();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_find_pass);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

        phone = (EditText) findViewById(R.id.phone_num);
    }

    private void getCode() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        final String username = phone.getText().toString().trim();
        if (username.length() == 0) {
            Tools.showToast(getApplicationContext(), "请输入用户名");
            return;
        }
        String kvs[] = new String[]{username};
        String param = FindPass.packagingParam(getApplicationContext(), kvs);
        dialog = ProgressDialog.show(ForgetPasswordActivity.this, getString(R.string.please_wait), getString(R.string.connecting));

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                dialog.dismiss();
                Tools.showToast(getApplicationContext(), "验证码发送成功");

                Intent intent = new Intent(ForgetPasswordActivity.this, SetNewPassActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                dialog.dismiss();
                try {
                    if ("1".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "格式错误");
                    } else if ("3".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "账号不存在");
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
