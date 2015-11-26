package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.RegisterMember;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.MD5Tool;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class PersonalMsgActivity extends Activity implements View.OnClickListener {

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private Button double_code;
    private Button next;

    private TextView text;

    private EditText user_name;
    private EditText pass;
    private EditText check_pass;
    private EditText referee;
    private EditText storeid;
    private EditText service;
    private TextView link;

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
        /*back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);*/

        double_code= (Button) findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setEnabled(false);
        next.setOnClickListener(this);

        text = (TextView) findViewById(R.id.text);
        text.setText(getResources().getString(R.string.personal_info));

        user_name = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.pass);
        check_pass = (EditText) findViewById(R.id.check_pass);
        referee = (EditText) findViewById(R.id.referee);
        storeid = (EditText) findViewById(R.id.store_id);
        service = (EditText) findViewById(R.id.service);

        link = (TextView) findViewById(R.id.link);
        link.setOnClickListener(this);

        check = (CheckBox) findViewById(R.id.check);
        check.setOnClickListener(this);
    }

    private void postPersonalInfo() {
        String username = user_name.getText().toString();
        if (!TextUtils.equals(pass.getText().toString(), check_pass.getText().toString())) {
            ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.password_not_matched), Toast.LENGTH_SHORT);
            return;
        }
        dialog = ProgressDialog.show(PersonalMsgActivity.this, getString(R.string.connecting), getString(R.string.please_wait));

        String password = pass.getText().toString();
        String phone = Configs.getCatchedPhoneNum(PersonalMsgActivity.this);
        String service_center = service.getText().toString();
        String store_id = storeid.getText().toString();
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
        String kvs[] = new String[]{username, password, phone, service_center, store_id, referrer
                , sex, birth, qq, email, address, bank_account, bank_name, bank, bank_address};
        String params = RegisterMember.packagingParam(this, kvs);

        System.out.println(Escape.unescape(params));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Log.e("tag", result.toString());
                System.out.println(result.toString());
                ToastUtils.showToast(PersonalMsgActivity.this, getString(R.string.register_success), Toast.LENGTH_SHORT);
                Intent intent = new Intent(PersonalMsgActivity.this, RegisterSuccessActivity.class);
                startActivity(intent);
                finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(PersonalMsgActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.double_code:
                //扫描二维码
                intent=new Intent();
                intent.setClass(PersonalMsgActivity.this,MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,SCANNIN_GREQUEST_CODE);
                break;
            case R.id.next:
                postPersonalInfo();
                break;
            case R.id.check:
                if (check.isChecked()) {
                    next.setEnabled(true);
                } else {
                    next.setEnabled(false);
                }
                break;
            case R.id.link:
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri url = Uri.parse("http://www.51xfzf.com/lfindex/helpinfo.aspx?id=5");
                intent.setData(url);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SCANNIN_GREQUEST_CODE:
                if(resultCode==RESULT_OK){
                    Bundle bundle=data.getExtras();
                    String result=bundle.getString("result");
                    //在这里处理返回来的store_id、service_center、referrer
                    String id=Uri.parse(result).getQueryParameter("store_id");
                    String center=Uri.parse(result).getQueryParameter("service_center");
                    String refer=Uri.parse(result).getQueryParameter("referrer");
                    if(id!=null){
                        storeid.setText(id);
                    }
                    if(center!=null){
                        service.setText(center);
                    }
                    if(refer!=null){
                        referee.setText(refer);
                    }
                }
        }
    }
}
