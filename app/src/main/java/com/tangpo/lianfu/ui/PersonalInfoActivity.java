package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.EditMaterial;
import com.tangpo.lianfu.parms.GetTypeList;
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

    private LinearLayout type;
    private LinearLayout select;
    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText update_type;
    private EditText id_card;
    private EditText bank;
    private EditText bank_card;
    private EditText bank_name;

    private UserEntity user = null;
    private String[] idlist = null;
    private String[] banklist = null;

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
        type = (LinearLayout) findViewById(R.id.type);
        select = (LinearLayout) findViewById(R.id.select);
        select.setOnClickListener(this);

        user_name = (EditText) findViewById(R.id.user_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        rel_name = (EditText) findViewById(R.id.rel_name);
        update_type = (EditText) findViewById(R.id.update_type);
        id_card = (EditText) findViewById(R.id.id_card);
        bank = (EditText) findViewById(R.id.bank);
        bank.setOnClickListener(this);
        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_name = (EditText) findViewById(R.id.bank_name);

        if(getIntent().getExtras() != null) {
            user = (UserEntity) getIntent().getExtras().getSerializable("user");

            if("0".equals(user.getUser_type()) || "1".equals(user.getUser_type())) {
                type.setVisibility(View.VISIBLE);
            } else {
                type.setVisibility(View.GONE);
            }
            user_name.setText(user.getName());
            contact_tel.setText(user.getPhone());
            rel_name.setText(user.getBank_name());
            update_type.setText("BNZZ");
            id_card.setText(user.getId_number());
            bank.setText(user.getBank());
            bank_card.setText(user.getBank_account());
            bank_name.setText(user.getBank_name());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if(user != null)
                    finish();
                else {
                    SharedPreferences preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(Configs.KEY_TOKEN);
                    editor.commit();
                    Tools.gotoActivity(PersonalInfoActivity.this, MainActivity.class);
                    finish();
                }
                break;
            case R.id.confirm:
                SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
                String logintype=preferences.getString(Configs.KEY_LOGINTYPE,null);
                if(logintype!=null){
                    Intent intent = new Intent(PersonalInfoActivity.this,BoundOrRegister.class);
                    startActivity(intent);
                    return;
                }
                updatePersonalInfo();
                break;
            case R.id.select:
            case R.id.bank:
                if(banklist == null){
                    getBankList();
                } else {
                    new AlertDialog.Builder(PersonalInfoActivity.this).setTitle("请选择开户银行").setItems(banklist, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bank.setText(banklist[which]);
                        }
                    }).show();
                }
                break;
        }
    }

    private void updatePersonalInfo() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String user_id = user_name.getText().toString().length() == 0 ? user.getName() : user_name.getText().toString();
        String phone = contact_tel.getText().toString().length() == 0 ? user.getPhone() : contact_tel.getText().toString();
        String name = rel_name.getText().toString().length() == 0 ? user.getBank_name() : rel_name.getText().toString();
        String id_number = id_card.getText().toString().length() == 0 ? user.getId_number() : id_card.getText().toString();
        String bankStr = bank.getText().toString().length() == 0 ? user.getBank() : bank.getText().toString();
        String bank_account = bank_card.getText().toString().length() == 0 ? user.getBank_account() : bank_card.getText().toString();
        String bank_nameStr = bank_name.getText().toString().length() == 0 ? user.getBank_name() : bank_name.getText().toString();

        if (!Tools.isMobileNum(phone)) {
            Tools.showToast(getApplicationContext(), "请填写正确的电话号码");
            return;
        }
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{user_id, name, id_number, phone, "", "", "", user.getSex(),
                user.getBirth(), user.getQq(), user.getEmail(), user.getAddress(), bank_account,
                bank_nameStr, bankStr, user.getBank_address()};
        String param = EditMaterial.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                //Tools.showToast(getString(R.string.update_success));
                ToastUtils.showToast(PersonalInfoActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT);
                PersonalInfoActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(PersonalInfoActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    private void getBankList() {
        //
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String [] kvs = new String[]{"bank", ""};
        String param = GetTypeList.packagingParam(getApplicationContext(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                dialog.dismiss();
                JSONObject object = null;
                try {
                    object = result.getJSONObject("param");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = object;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                dialog.dismiss();
                try {
                    if ("10".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else if("3".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "列表不存在");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject object = (JSONObject) msg.obj;
                    try {
                        idlist = object.getString("listids").split(",");
                        banklist = object.getString("listtxts").split(",");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new AlertDialog.Builder(PersonalInfoActivity.this).setTitle("请选择开户银行").setItems(banklist, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bank.setText(banklist[which]);
                        }
                    }).show();
                    break;
            }
        }
    };
}
