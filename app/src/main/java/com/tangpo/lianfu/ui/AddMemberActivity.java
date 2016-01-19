package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.fragment.MemManageFragment;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.AddMember;
import com.tangpo.lianfu.parms.GetTypeList;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class AddMemberActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button commit;
    private CheckBox admit;
//    private Spinner sex;
    private TextView gender;
    private ImageView sex;

    private TextView up_level;
    private ImageView up;

    private TextView bank;
    private ImageView select_bank;

    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText id_card;
    private EditText bank_card;
    private EditText bank_nameTextView;
    private String sexStr = "";
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
        commit.setOnClickListener(this);

        admit = (CheckBox) findViewById(R.id.admit);
        admit.setOnClickListener(this);

        gender= (TextView) findViewById(R.id.gender);
        gender.setOnClickListener(this);
        sex= (ImageView) findViewById(R.id.sex);
        sex.setOnClickListener(this);

        up_level = (TextView) findViewById(R.id.up_level);
        up_level.setOnClickListener(this);
        up= (ImageView) findViewById(R.id.up);
        up.setOnClickListener(this);

        bank = (TextView) findViewById(R.id.bank);
        bank.setOnClickListener(this);
        select_bank = (ImageView) findViewById(R.id.select_bank);
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
            case R.id.bank:
                if(banklist == null) {
                    getBankList();
                }else {
                    setBank();
                }
                break;
            case R.id.admit:
                if (admit.isChecked()) {
                    admit.setChecked(true);
                } else {
                    admit.setChecked(false);
                }
                break;
            case R.id.up:
            case R.id.up_level:
                if (typelist == null) {
                    getUpdateType();
                }else {
                    setType();
                }
                break;
            case R.id.gender:
            case R.id.sex:
                setGender();
        }
    }

    private void setGender() {
        final String[] genders=new String[]{getString(R.string.male),getString(R.string.female)};
        new AlertDialog.Builder(this).setTitle(getString(R.string.please_choose_gender)).setItems(genders, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender.setText(genders[which]);
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private String[] typelist = null;
    private String[] banklist = null;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject object = null;
            switch (msg.what) {
                case 1:
                    object = (JSONObject) msg.obj;
                    try {
                        typelist = object.getString("listtxts").split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setType();
                    break;
                case 2:
                    object = (JSONObject) msg.obj;
                    try {
                        banklist = object.getString("listtxts").split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setBank();
                    break;
            }
        }
    };

    private void setType() {
        new AlertDialog.Builder(AddMemberActivity.this).setTitle("请选择员工升级类型").setItems(typelist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                up_level.setText(typelist[which]);
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void setBank() {
        new AlertDialog.Builder(AddMemberActivity.this).setTitle("请选择员工升级类型").setItems(banklist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bank.setText(banklist[which]);
                dialog.dismiss();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void getUpdateType() {
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String[] kvs = new String[]{"uptype", ""};
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
                msg.what = 2;
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

    private void addMember() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        String user_id = userid;

        String username= user_name.getText().toString();
        if (username.equals("")){
            ToastUtils.showToast(this,getString(R.string.username_cannot_be_null),Toast.LENGTH_SHORT);
            return;
        }

        String phone = contact_tel.getText().toString();
        if(!Tools.isMobileNum(phone)){
            ToastUtils.showToast(this,getString(R.string.please_input_correct_phonenumber),Toast.LENGTH_SHORT);
            return;
        }

        String name=rel_name.getText().toString();
        if(name.equals("")){
            ToastUtils.showToast(this,getString(R.string.realname_cannot_be_null),Toast.LENGTH_SHORT);
            return;
        }

        if (up_level.getText().toString().trim().length() == 0) {
            ToastUtils.showToast(this,"请选择升级类型",Toast.LENGTH_SHORT);
            return;
        }

        String id_number=id_card.getText().toString();

        String pw= phone.substring(phone.length() - 6);
        String service_center = "";
        String service_address = "";
        String referrer = "";
        String sex="0";
        if(gender.getText().toString().equals(getString(R.string.male))){
            sex="0";
        }else{
            sex="1";
        }
        String birth = "";
        String qq = "";
        String email = "";
        String address = "";
        String bank_account = (bank_card.getText().length() == 0 ) ? "" : bank_card.getText().toString();
        String bank_name = (bank_nameTextView.getText().length() == 0 ) ? "" : bank_nameTextView.getText().toString();
        String tvbank = (bank.getText().length() == 0 ) ? "" : bank.getText().toString();
        String bank_address = "";
        String uplevel="1";
        if(up_level.getText().toString().equals("BNZZ")){
            uplevel="1";
        }else{
            uplevel="2";
        }
        String issms="1";
        if(admit.isChecked()){
            issms="1";
        }else{
            issms="0";
        }
        String register_time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        String kvs[] = new String[]{user_id,username,name,id_number,pw, phone, service_center, service_address,
                referrer, sex, birth, qq, email, address, bank_account, bank_name, tvbank, bank_address, uplevel,issms};
        String param = AddMember.packagingParam(this, kvs);

        final Member member = new Member(tvbank, bank_account, bank_name, name, phone, register_time,userid, username,sex);

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
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
