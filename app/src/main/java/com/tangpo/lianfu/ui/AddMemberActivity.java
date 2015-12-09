package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.AddMember;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.MD5Tool;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class AddMemberActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button commit;
    private CheckBox admit;
    private Spinner sex;
    private Spinner uplevel;
    private TextView bankTextView;
    private TextView select_bank;
    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText id_card;
    private EditText bank_card;
    private EditText bank_nameTextView;
    private String sexStr = "0";
    private String uplevelStr = "";
    private ProgressDialog dialog = null;
    private String userid = null;

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
        setContentView(R.layout.add_member_activity);

        Tools.gatherActivity(this);

        userid = getIntent().getExtras().getString("userid");
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button) findViewById(R.id.commit);
        commit.setBackgroundColor(Color.GRAY);
        commit.setClickable(false);
        commit.setOnClickListener(this);

        admit = (CheckBox) findViewById(R.id.admit);
        admit.setOnClickListener(this);
        sex = (Spinner) findViewById(R.id.sex);
        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] sexes = getResources().getStringArray(R.array.sex);
                sexStr = position + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uplevel = (Spinner) findViewById(R.id.up_level);
        uplevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] uplevels = getResources().getStringArray(R.array.uplevel);
                uplevelStr = uplevels[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bankTextView = (TextView) findViewById(R.id.bank);
        select_bank = (TextView) findViewById(R.id.select_bank);
        select_bank.setOnClickListener(this);
        user_name = (EditText) findViewById(R.id.user_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        rel_name = (EditText) findViewById(R.id.rel_name);
        id_card = (EditText) findViewById(R.id.id_card);
        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_nameTextView = (EditText) findViewById(R.id.bank_name);
        admit = (CheckBox) findViewById(R.id.admit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.commit:
//                if (!admit.isChecked()){
//                    ToastUtils.showToast(this,"");
//                }
                addMember();
                break;
            case R.id.select_bank:
                break;
            case R.id.admit:
                Log.e("tag", admit.isChecked() + "");
                if (admit.isChecked()) {
                    admit.setChecked(true);
                    commit.setClickable(true);
                    commit.setBackgroundResource(R.drawable.add_mem);
                } else {
                    admit.setChecked(false);
                    commit.setBackgroundColor(Color.GRAY);
                    commit.setClickable(false);
                }
                break;
        }
    }
    private void addMember() {
        if(!Tools.checkLAN()) {
            Log.e("tag", "check");
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String user_id = user_name.getText().toString();
        String username = rel_name.getText().toString();
        String phone = contact_tel.getText().toString();
        if (user_id.equals("")){
            ToastUtils.showToast(this,getString(R.string.username_cannot_be_null),Toast.LENGTH_SHORT);
            return;
        }
        if(username.equals("")){
            ToastUtils.showToast(this,getString(R.string.realname_cannot_be_null),Toast.LENGTH_SHORT);
            return;
        }
        if(!Tools.isMobileNum(phone)){
            ToastUtils.showToast(this,getString(R.string.phone_format_error),Toast.LENGTH_SHORT);
            return;
        }
        String pw= phone.substring(phone.length() - 6);

        String service_center = "";
        String service_address = "";
        String referrer = "";
        String birth = "";
        String qq = "";
        String email = "";
        String address = "";
        String register_time = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date());
        String bank_account = bank_card.getText().toString();
        String bank_name = bank_nameTextView.getText().toString();
        String bank = bankTextView.getText().toString();
        String bank_address = "";
        String kvs[] = new String[]{username, user_id, pw, phone, service_center, service_address,
                referrer, sexStr, birth, qq, email, address, bank_account, bank_name, bank, bank_address, uplevelStr};
        String param = AddMember.packagingParam(this, kvs);
        final Member member = new Member(bank, bank_account, bank_name, user_id, phone, register_time,userid, username,   sexStr);
        Log.e("tag", member.toString());

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("tag", result.toString());
                dialog.dismiss();
                Tools.showToast(AddMemberActivity.this, getString(R.string.success));
                Intent intent = new Intent();
                intent.putExtra("member", member);
                AddMemberActivity.this.setResult(MemManageFragment.REQUEST_CODE, intent);
                AddMemberActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                Log.e("tag", result.toString());
                try {
                    if("300".equals(result.getString("status"))){
                        ToastUtils.showToast(AddMemberActivity.this,Escape.unescape(result.toString()), Toast.LENGTH_SHORT);
                    }
                    else {
                        Tools.handleResult(AddMemberActivity.this, result.getString("status"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
