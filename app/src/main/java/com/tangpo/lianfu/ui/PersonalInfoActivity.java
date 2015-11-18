package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.EditMaterial;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class PersonalInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button edit;

    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText update_type;
    private EditText id_card;
    private EditText bank;
    private EditText bank_card;
    private EditText bank_name;

    private UserEntity user = null;

    private ProgressDialog dialog = null;

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
        setContentView(R.layout.personal_info_activity);

        Tools.gatherActivity(this);

        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        edit = (Button) findViewById(R.id.confirm);
        edit.setOnClickListener(this);

        user_name = (EditText) findViewById(R.id.user_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        rel_name = (EditText) findViewById(R.id.rel_name);
        update_type = (EditText) findViewById(R.id.update_type);
        id_card = (EditText) findViewById(R.id.id_card);
        bank = (EditText) findViewById(R.id.bank);
        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_name = (EditText) findViewById(R.id.bank_name);

        user = (UserEntity) getIntent().getExtras().getSerializable("user");

        user_name.setText(user.getUser_id());
        contact_tel.setText(user.getPhone());
        rel_name.setText(user.getName());
        //update_type.setText();
        id_card.setText(user.getId_number());
        bank.setText(user.getBank());
        bank_card.setText(user.getBank_account());
        bank_name.setText(user.getBank_name());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.confirm:
                dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
                updatePersonalInfo();
                break;
        }
    }

    private void updatePersonalInfo() {
        String user_id = user_name.getText().toString();
        String phone = contact_tel.getText().toString();
        String name = rel_name.getText().toString();
        String id_number = id_card.getText().toString();
        String bankStr = bank.getText().toString();
        String bank_account = bank_card.getText().toString();
        String bank_nameStr = bank_name.getText().toString();

        String kvs[] = new String[]{user_id, name, id_number, phone, "", "", "", user.getSex(),
                user.getBirth(), user.getQq(), user.getEmail(), user.getAddress(), bank_account,
                bank_nameStr, bankStr, user.getBank_address()};

        String param = EditMaterial.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Log.e("tag", result.toString());
                //Tools.showToast(getString(R.string.update_success));
                ToastUtils.showToast(PersonalInfoActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT);
                PersonalInfoActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                Log.e("tag", result.toString());
                try {
                    if (result.getString("status").equals("1")) {
                        Tools.showToast(PersonalInfoActivity.this, getString(R.string.format_error));
                    } else if (result.getString("status").equals("10")) {
                        Tools.showToast(PersonalInfoActivity.this, getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
