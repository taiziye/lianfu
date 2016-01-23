package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.EditMaterial;
import com.tangpo.lianfu.parms.GetTypeList;
import com.tangpo.lianfu.parms.Login;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class PersonalInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button edit;

    private LinearLayout type;
    private LinearLayout select;
    private LinearLayout userlayout;
    private LinearLayout frame;
    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText update_type;
    private EditText id_card;
    private EditText bank;
    private EditText bank_card;
    private EditText bank_name;
    private EditText sex;

    private EditText birth;
    private ImageView ivbirth;

    private EditText qq;
    private EditText email;
    private EditText address;
    private EditText user_level;
    private ImageView sexi;

    private UserEntity user = null;
    private String[] idlist = null;
    private String[] banklist = null;
    private String flag = "";
    private ProgressDialog dialog = null;
    private ProgressDialog pd = null;
    private Gson gson = new Gson();
    private SharedPreferences preferences = null;

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
        userlayout = (LinearLayout) findViewById(R.id.user);
        user_level = (EditText) findViewById(R.id.level);
        frame = (LinearLayout) findViewById(R.id.frame);

        user_name = (EditText) findViewById(R.id.user_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        rel_name = (EditText) findViewById(R.id.rel_name);
        update_type = (EditText) findViewById(R.id.update_type);
        id_card = (EditText) findViewById(R.id.id_card);

        bank = (EditText) findViewById(R.id.bank);
        bank.setOnClickListener(this);

        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_name = (EditText) findViewById(R.id.bank_name);

        sex = (EditText) findViewById(R.id.sex);
        sex.setOnClickListener(this);

        birth = (EditText) findViewById(R.id.birth);
        birth.setOnClickListener(this);
        ivbirth= (ImageView) findViewById(R.id.ivbirth);
        ivbirth.setOnClickListener(this);

        qq = (EditText) findViewById(R.id.qq);
        email = (EditText) findViewById(R.id.email);
        address = (EditText) findViewById(R.id.address);
        sexi = (ImageView) findViewById(R.id.sexi);
        sexi.setOnClickListener(this);

        preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
        String str = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(str);
            user = gson.fromJson(jsonObject.toString(), UserEntity.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(getIntent().getExtras() != null) {
            //user = (UserEntity) getIntent().getExtras().getSerializable("user");
            flag = getIntent().getStringExtra("flag");

            if("0".equals(user.getUser_type()) || "1".equals(user.getUser_type())) {
                type.setVisibility(View.GONE);
                userlayout.setVisibility(View.GONE);
            } else {
                type.setVisibility(View.GONE);
                userlayout.setVisibility(View.GONE);
            }
            user_name.setText(user.getUsername());
            contact_tel.setText(user.getPhone());
            rel_name.setText(user.getName());
            update_type.setText("BNZZ");
            id_card.setText(user.getId_number());
            bank.setText(user.getBank());
            bank_card.setText(user.getBank_account());
            bank_name.setText(user.getBank_name());
            user_level.setText(user.getUlevel());
            if("0".equals(user.getSex())) {
                sex.setText("男");
            }else {
                sex.setText("女");
            }
            birth.setText(user.getBirth());
            qq.setText(user.getQq());
            email.setText(user.getEmail());
            address.setText(user.getAddress());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if("1".equals(flag)) {
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
                } else if ("2".equals(flag)) {
                    String name = getIntent().getStringExtra("name");
                    String pass = getIntent().getStringExtra("pass");
                    login(name, pass);
                }
                break;
            case R.id.confirm:
                SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
                String logintype=preferences.getString(Configs.KEY_LOGINTYPE,null);
                if(logintype!=null){
                    Intent intent = new Intent(PersonalInfoActivity.this, BoundOrRegister.class);
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
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                break;
            case R.id.sex:
            case R.id.sexi:
                new AlertDialog.Builder(PersonalInfoActivity.this).setTitle("请选择性别").setItems(new String[]{"男", "女"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0) {
                                    sex.setText("男");
                                } else {
                                    sex.setText("女");
                                }
                            }
                        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
            case R.id.birth:
            case R.id.ivbirth:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        birth.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
//                        String birthString=String.format("%d/%0d/%0d",year,monthOfYear+1,dayOfMonth);
//                        birth.setText(birthString);
                        Date date=new Date(year-1900,monthOfYear,dayOfMonth);
                        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
                        birth.setText(format.format(date));
                    }
                },1989,11,20).show();
        }
    }

    private void login(String name, String pass) {
        if(!Tools.checkLAN()) {
            Tools.showToast(PersonalInfoActivity.this, "网络未连接，请联网后重试");
            return;
        }
        pd = ProgressDialog.show(PersonalInfoActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{name, pass};
        String params = Login.packagingParam(kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                try {
                    JSONObject jsonObject = result.getJSONObject("param");
                    String sessid = jsonObject.getString("session_id");
                    Configs.cacheToken(getApplicationContext(), sessid);
                    Configs.cacheUser(getApplicationContext(), jsonObject.toString());
                    System.out.println(Escape.unescape(result.toString()));
                    user = gson.fromJson(jsonObject.toString(), UserEntity.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
               msg.what = 2;
                msg.obj = user;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                try {
                    if("2".equals(result.getString("status"))) {
                        Tools.showToast(PersonalInfoActivity.this, "用户名或密码错误");
                    }else{
                        Tools.showToast(PersonalInfoActivity.this, getString(R.string.fail_to_login));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    private void updatePersonalInfo() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        final String phone = contact_tel.getText().toString().length() == 0 ? user.getPhone() : contact_tel.getText().toString();
        final String name = rel_name.getText().toString().length() == 0 ? user.getBank_name() : rel_name.getText().toString();
        final String id_number = id_card.getText().toString().length() == 0 ? user.getId_number() : id_card.getText().toString();
        final String bankStr = bank.getText().toString().length() == 0 ? user.getBank() : bank.getText().toString();
        final String bank_account = bank_card.getText().toString().length() == 0 ? user.getBank_account() : bank_card.getText().toString();
        final String bank_nameStr = bank_name.getText().toString().length() == 0 ? user.getBank_name() : bank_name.getText().toString();
        final String sexStr = "男".equals(sex.getText().toString().trim()) ? "0":"1";
        final String birthStr = birth.getText().toString().trim();
        final String qqStr = qq.getText().toString().trim();
        final String emailStr = email.getText().toString().trim();
        final String addressStr = address.getText().toString().trim();

        if (!Tools.isMobileNum(phone)) {
            Tools.showToast(getApplicationContext(), "请填写正确的电话号码");
            return;
        }
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{user.getUser_id(), name, id_number, phone, "", "", "", sexStr,
                birthStr, qqStr, emailStr, addressStr, bank_account,
                bank_nameStr, bankStr, user.getBank_address()};
        String param = EditMaterial.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                //Tools.showToast(getString(R.string.update_success));
                ToastUtils.showToast(PersonalInfoActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT);
                //更新本地数据
                user.setName(name);
                user.setId_number(id_number);
                user.setPhone(phone);
                user.setSex(sexStr);
                user.setBirth(birthStr);
                user.setQq(qqStr);
                user.setEmail(emailStr);
                user.setAddress(addressStr);
                user.setBank_account(bank_account);
                user.setBank_name(bank_nameStr);
                user.setBank(bankStr);

                Configs.cacheUser(getApplicationContext(), user.toJSONString());
                PersonalInfoActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    //Tools.handleResult(PersonalInfoActivity.this, result.getString("status"));
                    Tools.showToast(getApplicationContext(), result.getString("info"));
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
                case 2:
                    Intent intent = new Intent(PersonalInfoActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    Tools.closeActivity();
                    finish();
                    break;
            }
        }
    };
}
