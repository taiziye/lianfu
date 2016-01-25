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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Employee;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.AddEmployee;
import com.tangpo.lianfu.parms.GetTypeList;
import com.tangpo.lianfu.utils.MD5Tool;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class AddEmployeeActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button commit;

    private TextView manage_level;
    private ImageView level;

    private TextView bank;
    private ImageView select_bank;

    private TextView upgrade;
    private ImageView select_type;

    private TextView gender;
    private ImageView sex;

    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText id_card;
    private EditText bank_card;
    private EditText bank_name;
    private ToggleButton setuse;

    private List<String> list = null;
    private ArrayAdapter<String> adapter = null;

    private String userid = null;
    private ProgressDialog dialog = null;

    private String rank = null;
    private String username = null;
    private String phone = null;
    private String pw = null;
    private String name = null;
    private String sexstr = null;
    private String id_num = null;
    private String upgrades=null;
    private String bankStr = null;
    private String bank_account = null;
    private String bank_nameStr = null;
    private String is_server = null;

    private String[] typelist = null;
    private String[] banklist = null;
    private String[] entries = null;
    private boolean[] state = null;
    private ListView lv = null;

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
        setContentView(R.layout.add_employee_activity);
        userid = getIntent().getExtras().getString("userid");
        Tools.gatherActivity(this);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);

        manage_level = (TextView) findViewById(R.id.manage_level);
        manage_level.setOnClickListener(this);
        level = (ImageView) findViewById(R.id.level);
        level.setOnClickListener(this);

        bank = (TextView) findViewById(R.id.bank);
        bank.setOnClickListener(this);
        select_bank= (ImageView) findViewById(R.id.select_bank);
        select_bank.setOnClickListener(this);

        upgrade = (TextView)findViewById(R.id.upgrade);
        upgrade.setOnClickListener(this);
        select_type= (ImageView) findViewById(R.id.select_type);
        select_type.setOnClickListener(this);

        gender= (TextView) findViewById(R.id.gender);
        gender.setOnClickListener(this);
        sex= (ImageView) findViewById(R.id.sex);
        sex.setOnClickListener(this);

        user_name = (EditText) findViewById(R.id.user_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        rel_name = (EditText) findViewById(R.id.rel_name);
        id_card = (EditText) findViewById(R.id.id_card);
        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_name = (EditText) findViewById(R.id.bank_name);

        setuse= (ToggleButton) findViewById(R.id.setuse);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.commit:
                addEmployee();
                break;
            case R.id.upgrade:
            case R.id.select_type:
                if (typelist == null) {
                    getUpdateType();
                } else {
                    setType();
                }
                break;
            case R.id.select_bank:
            case R.id.bank:
                if(banklist == null) {
                    getList("bank");
                } else {
                    setBank(banklist, "bank");
                }
                break;
            case R.id.manage_level:
            case R.id.level:
                if (entries == null) {
                    getList("ygtype");
                } else {
                    setLevelList();
                }
                break;
            case R.id.gender:
            case R.id.sex:
                setGender();
                break;
            case R.id.setuse:
                break;
        }
    }

    private void setLevelList() {
        new AlertDialog.Builder(this).setTitle("请选择员工管理级别").setItems(entries, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
                manage_level.setText(entries[which]);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void setGender() {
        final String genders[]=new String[]{getString(R.string.male),getString(R.string.female)};
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            JSONObject object = null;
            super.handleMessage(msg);
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
                        banklist = object.getString("listtxts").split("\\,");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setBank(banklist, "bank");
                    break;
                case 3:
                    object = (JSONObject) msg.obj;
                    try {
                        entries = object.getString("listtxts").split("\\,");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setBank(entries, "ygtype");
            }
        }
    };

    private void setType() {
        state = new boolean[typelist.length];

        AlertDialog dialog = new AlertDialog.Builder(AddEmployeeActivity.this).setTitle("请选择员工升级类型")
                .setMultiChoiceItems(typelist, state, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        String s = "";
                        for (int i = 0; i < typelist.length; i++) {
                            if (lv.getCheckedItemPositions().get(i)) {
                                s += lv.getAdapter().getItem(i) + " ";
                            }
                        }
                        upgrade.setText(s);
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        dialog.dismiss();
                    }
                }).create();
        lv = dialog.getListView();
        dialog.show();
    }

    private void setBank(String[] list, final String param) {
        new AlertDialog.Builder(AddEmployeeActivity.this).setTitle("请选开户银行").setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("bank".equals(param)) bank.setText(banklist[which]);
                else manage_level.setText(entries[which]);
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

    private void getList(final String list) {
        //
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String [] kvs = new String[]{list, ""};
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
                if ("bank".equals(list)) msg.what = 2;
                else msg.what = 3;
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

    private void addEmployee() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        rank = manage_level.getText().toString().trim();

        username = user_name.getText().toString();
        if(username.length() == 0||username==null) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_username));
            return;
        }

        phone = contact_tel.getText().toString();
        if(!Tools.isMobileNum(phone)) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_correct_phonenumber));
            return;
        }

        pw = MD5Tool.md5(phone.substring(phone.length() - 6));

        name = rel_name.getText().toString();
        if(name.length() == 0||name==null) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_name));
            return;
        }

        id_num = id_card.getText().toString();
        bankStr = bank.getText().toString();
        bank_account = bank_card.getText().toString();
        bank_nameStr = bank_name.getText().toString();

        is_server="0";
        if(setuse.isChecked()){
            is_server="1";
        }else{
            is_server="0";
        }

        if(upgrade.getText().toString().contains("BNZZ")){
            upgrades+="BNZZ";
            if(upgrade.getText().toString().contains("BN50")){
                upgrades+=",BN50";
            }
        }else{
            if(upgrade.getText().toString().contains("BN50")){
                upgrades+="BN50";
            }
        }
        if (gender.getText().toString().equals(getString(R.string.male))) {
            sexstr = "0";
        } else {
            sexstr = "1";
        }

        if(id_num.length() == 0||id_num==null) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_idnumber));
            return;
        }
//        if(bank_account.length() == 0||bank_account==null) {
//            Tools.showToast(getApplicationContext(), getString(R.string.please_input_bank_account));
//            return;
//        }
//        if(bank_nameStr.length() == 0||bank_nameStr==null) {
//            Tools.showToast(getApplicationContext(), getString(R.string.please_input_bank_name));
//            return;
//        }
        if(upgrades.length()==0||upgrades==null){
            Tools.showToast(getApplicationContext(),"请选择升级类型");
            return;
        }
        if(sexstr==null||sexstr.length()==0){
            Tools.showToast(getApplicationContext(),"请选择性别");
            return;
        }
        final Employee employee=new Employee();

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{userid, rank, username, pw, name, upgrades,
                phone, sexstr, id_num, bankStr,bank_account, bank_nameStr,is_server};
        String params = AddEmployee.packagingParam(AddEmployeeActivity.this, kvs);

        /**
         * 返回值没有employee_id
         */
        employee.setUser_id("1111");
        employee.setRank(rank);
        employee.setUsername(username);
        employee.setName(name);
        employee.setUpgrade(upgrades);
        employee.setPhone(phone);
        employee.setSex(sexstr);
        employee.setId_number(id_num);
        employee.setBank(bankStr);
        employee.setBank_account(bank_account);
        employee.setBank_name(bank_nameStr);
        employee.setIsServer(is_server);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                ToastUtils.showToast(AddEmployeeActivity.this, getString(R.string.add_success), Toast.LENGTH_SHORT);
                Intent intent = new Intent();
                intent.putExtra("employee", employee);
                setResult(RESULT_OK, intent);
                AddEmployeeActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    if("300".equals(result.getString("status"))) {
                        Tools.showToast(AddEmployeeActivity.this, getString(R.string.please_input_correct_phonenumber));
                    } else {
                        Tools.handleResult(AddEmployeeActivity.this, result.getString("status"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }
}
