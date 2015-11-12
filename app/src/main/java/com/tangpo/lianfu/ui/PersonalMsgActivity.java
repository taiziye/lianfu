package com.tangpo.lianfu.ui;

import android.app.Activity;
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
import com.tangpo.lianfu.utils.ToastUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.personal_msg_activity);

        init();
    }

    private void init(){
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);
        next.setVisibility(View.GONE);

        text = (TextView)findViewById(R.id.text);
        text.setText(getResources().getString(R.string.personal_info));

        user_name = (EditText)findViewById(R.id.user_name);
        pass = (EditText)findViewById(R.id.pass);
        check_pass = (EditText)findViewById(R.id.check_pass);
        referee = (EditText)findViewById(R.id.referee);
        service_address = (EditText)findViewById(R.id.service_address);
        service = (EditText)findViewById(R.id.service);

        check = (CheckBox)findViewById(R.id.check);
        check.setOnClickListener(this);
    }

    private void postPersonalInfo(){
        String username=user_name.getText().toString();
        if(!TextUtils.equals(pass.getText().toString(),check_pass.getText().toString())){
            ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.password_not_matched), Toast.LENGTH_SHORT);
            return;
        }
        String password=pass.getText().toString();
        String phone= Configs.getCatchedPhoneNum(PersonalMsgActivity.this);
        String service_center=service.getText().toString();
        String service_add=service_address.getText().toString();
        String referrer=referee.getText().toString();
        String sex="";
        String birth="";
        String qq="";
        String email="";
        String address="";
        String bank_account="";
        String bank_name="";
        String bank="";
        String bank_address="";
        String kvs[]=new String[]{username,password,phone,service_center,service_add,referrer
                ,sex,birth,qq,email,address,bank_account,bank_name,bank,bank_address};
        String params= RegisterMember.packagingParam(this, kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {

            }
        },params);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.next:
                break;
            case R.id.check:
                break;
        }
    }
}
