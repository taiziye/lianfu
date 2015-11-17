package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.RegisterMember;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.MD5Tool;
import com.tangpo.lianfu.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class PersonalMsgActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button next;

    private TextView text;

    private EditText user_name;
    private EditText pass;
    private EditText check_pass;
    private EditText referee;
    private EditText service_address;
    private EditText service;

    private CheckBox check;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.personal_msg_activity);

        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setEnabled(false);
        next.setOnClickListener(this);

        text = (TextView) findViewById(R.id.text);
        text.setText(getResources().getString(R.string.personal_info));

        user_name = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.pass);
        check_pass = (EditText) findViewById(R.id.check_pass);
        referee = (EditText) findViewById(R.id.referee);
        service_address = (EditText) findViewById(R.id.service_address);
        service = (EditText) findViewById(R.id.service);

        check = (CheckBox) findViewById(R.id.check);
        check.setOnClickListener(this);
    }

    private void postPersonalInfo() {
        String username = user_name.getText().toString();
        if (!TextUtils.equals(pass.getText().toString(), check_pass.getText().toString())) {
            ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.password_not_matched), Toast.LENGTH_SHORT);
            return;
        }
        String password = MD5Tool.md5(pass.getText().toString());
        String phone = Configs.getCatchedPhoneNum(PersonalMsgActivity.this);
        String service_center = service.getText().toString();
        String service_add = service_address.getText().toString();
        String referrer = referee.getText().toString();
        String sex = "";
        String birth = "";
        String qq = "";
        String email = "";
        String address = "";
        String bank_account = "";
        String bank_name = "";
        String bank = "中国银行";
        String bank_address = "";
        String kvs[] = new String[]{username, password, phone, service_center, service_add, referrer
                , sex, birth, qq, email, address, bank_account, bank_name, bank, bank_address};
        String params = RegisterMember.packagingParam(this, kvs);

        System.out.println(Escape.unescape(params));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();

                System.out.println(result.toString());
                ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT);
                Intent intent = new Intent(PersonalMsgActivity.this, RegisterSuccessActivity.class);
                finish();
                startActivity(intent);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status = result.getString("status");
                    if (status.equals("1")) {
                        ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.username_already_exist), Toast.LENGTH_SHORT);
                    } else if (status.equals("2")) {
                        ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.format_error), Toast.LENGTH_SHORT);
                    } else {
                        ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.server_exception), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.next:
                dialog = ProgressDialog.show(PersonalMsgActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
                postPersonalInfo();
                break;
            case R.id.check:
                if (check.isChecked()) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
                break;
        }
    }
}
