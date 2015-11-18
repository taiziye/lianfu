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
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CheckCode;
import com.tangpo.lianfu.parms.GetCode;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class RegisterActivity extends Activity implements OnClickListener {

    private Button back;
    private Button next;

    private EditText nation;
    private EditText phone_Num;
    private EditText code;

    private Button get_code;
    private ProgressDialog pd = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.deleteActivity(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);

        Tools.gatherActivity(this);

        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);

        nation = (EditText) findViewById(R.id.nation);
        phone_Num = (EditText) findViewById(R.id.phone_num);
        code = (EditText) findViewById(R.id.code);

        get_code = (Button) findViewById(R.id.get_code);
        get_code.setOnClickListener(this);
    }

    private void getCode() {
        String phone = phone_Num.getText().toString();
        if (phone.equals("")) {
            ToastUtils.showToast(RegisterActivity.this, getString(R.string.phone_num_cannot_be_null), Toast.LENGTH_LONG);
            return;
        }
        String kvs[] = new String[]{phone};
        String params = GetCode.packagingParam(kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                ToastUtils.showToast(RegisterActivity.this, getString(R.string.message_send_success), Toast.LENGTH_LONG);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                try {
                    String status = result.getString("status");
                    if (status.equals("1")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.format_error), Toast.LENGTH_LONG);
                    } else if (status.equals("10")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.server_exception), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    private void checkCode() {
        final String phone = phone_Num.getText().toString();
        if (phone.equals("")) {
            pd.dismiss();
            ToastUtils.showToast(RegisterActivity.this, getString(R.string.phone_num_cannot_be_null), Toast.LENGTH_LONG);
            return;
        }
        String check_code = code.getText().toString();
        if (check_code.equals("")) {
            pd.dismiss();
            ToastUtils.showToast(RegisterActivity.this, getString(R.string.check_code_cannot_be_null), Toast.LENGTH_LONG);
            return;
        }
        String kvs[] = new String[]{phone, check_code};
        String params = CheckCode.packagingParam(kvs);

        System.out.println(Escape.unescape(params));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                System.out.println(result.toString());
                Configs.cachePhoneNum(RegisterActivity.this, phone);
                Intent intent = new Intent(RegisterActivity.this, PersonalMsgActivity.class);
                startActivity(intent);
                finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                try {
                    String status = result.getString("status");
                    if (status.equals("1")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.code_error), Toast.LENGTH_LONG);
                    } else if (status.equals("2")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.code_invalid), Toast.LENGTH_LONG);
                    } else if (status.equals("10")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.server_exception), Toast.LENGTH_LONG);
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
                pd = ProgressDialog.show(RegisterActivity.this, getString(R.string.checking_code), getString(R.string.please_wait));
                checkCode();
                break;
            case R.id.get_code:
                pd = ProgressDialog.show(RegisterActivity.this, getString(R.string.send_message), getString(R.string.please_wait));
                getCode();
                break;
        }
    }
}
