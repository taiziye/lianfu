package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.AddMember;
import com.tangpo.lianfu.utils.MD5Tool;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class AddMemberActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button commit;

    private CheckBox admit;

    private Spinner sex;

    private TextView update_type;
    private TextView bankTextView;
    private TextView select_bank;

    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText id_card;
    private EditText bank_card;
    private EditText bank_nameTextView;

    private String sexStr = "";
    private ProgressDialog dialog = null;

    private String userid=null;
    private SharedPreferences preferences=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_member_activity);
        init();
    }

    private void init() {
        preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
        try {
            JSONObject jsonObject=new JSONObject(preferences.getString(Configs.KEY_USER,""));
            userid=jsonObject.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button)findViewById(R.id.commit);
        commit.setOnClickListener(this);

        admit = (CheckBox)findViewById(R.id.admit);
        sex = (Spinner) findViewById(R.id.sex);
        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] sexes = getResources().getStringArray(R.array.spinner);
                sexStr = sexes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        update_type = (TextView)findViewById(R.id.update_type);
        bankTextView = (TextView)findViewById(R.id.bank);
        select_bank = (TextView)findViewById(R.id.select_bank);
        select_bank.setOnClickListener(this);
        user_name = (EditText)findViewById(R.id.user_name);
        contact_tel = (EditText)findViewById(R.id.contact_tel);
        rel_name = (EditText)findViewById(R.id.rel_name);
        id_card = (EditText)findViewById(R.id.id_card);
        bank_card = (EditText)findViewById(R.id.bank_card);
        bank_nameTextView = (EditText)findViewById(R.id.bank_name);

        commit.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.commit:
                dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
                addMember();
                break;
            case R.id.select_bank:
                break;
            case R.id.admit:
                commit.setClickable(true);
                break;
        }
    }

    private void addMember(){
        String user_id = user_name.getText().toString();
        String username = rel_name.getText().toString();
        String phone = contact_tel.getText().toString();
        String pw = MD5Tool.md5(phone.substring(phone.length() - 6));
        String service_center = "";
        String service_address = "";
        String referrer = "";
        String sex = sexStr;
        String birth = "";
        String qq = "";
        String email = "";
        String address = "";
        String bank_account = bank_card.getText().toString();
        String bank_name = bank_nameTextView.getText().toString();
        String bank = bankTextView.getText().toString();
        String bank_address = "";
        String kvs[] = new String []{user_id, username, pw, phone, service_center, service_address,
        referrer, sex, birth, qq, email, address, bank_account, bank_name, bank, bank_address};
        String param = AddMember.packagingParam(this, kvs);

        final Member member = new Member(bank, bank_account, bank_name, userid, phone, "", user_id, username, sex);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Tools.showToast(getString(R.string.add_success));
                Intent intent = new Intent();
                intent.putExtra("member", member);
                AddMemberActivity.this.setResult(MemManageFragment.REQUEST_CODE, intent);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    if(result.getString("status").equals("1")){
                        Tools.showToast(getString(R.string.format_error));
                    }else if(result.getString("status").equals("10")){
                        Tools.showToast(getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
