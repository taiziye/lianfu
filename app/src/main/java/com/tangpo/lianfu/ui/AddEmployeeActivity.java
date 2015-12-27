package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
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
    private Spinner manage_level;
    private TextView bank;
    private TextView select_level;
    private TextView select_bank;
    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText id_card;
    private EditText bank_card;
    private EditText bank_name;
    private Spinner spinner = null;
    private List<String> list = null;
    private ArrayAdapter<String> adapter = null;
    private LinearLayout select;

    private String userid = null;
    private ProgressDialog dialog = null;
    private String rank = null;
    private String username = null;
    private String phone = null;
    private String pw = null;
    private String name = null;
    private String id_num = null;
    private String bankStr = null;
    private String bank_account = null;
    private String bank_nameStr = null;
    private String sex = null;
    private TextView select_type = null;
    private String[] typelist = null;
    private String[] banklist = null;
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
        select = (LinearLayout) findViewById(R.id.select);
        select.setOnClickListener(this);

        manage_level = (Spinner) findViewById(R.id.manage_level);
        list = new ArrayList<>();
        list.add(getString(R.string.manager));
        list.add(getString(R.string.employee));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        manage_level.setAdapter(adapter);
        manage_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).equals(getString(R.string.manager))){
                    rank="1";
                }else{
                    rank="0";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bank = (TextView) findViewById(R.id.bank);
        bank.setOnClickListener(this);
        select_level = (TextView)findViewById(R.id.update_type);
        select_level.setOnClickListener(this);
        select_type = (TextView)findViewById(R.id.select_type);
        select_type.setOnClickListener(this);
        select_bank = (TextView) findViewById(R.id.select_bank);
        select_bank.setOnClickListener(this);

        user_name = (EditText) findViewById(R.id.user_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        rel_name = (EditText) findViewById(R.id.rel_name);
        id_card = (EditText) findViewById(R.id.id_card);
        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_name = (EditText) findViewById(R.id.bank_name);

        spinner = (Spinner) findViewById(R.id.spinner);
        list = new ArrayList<>();
        list.add(getString(R.string.male));
        list.add(getString(R.string.female));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).equals(getString(R.string.male))) {
                    sex = "0";
                } else {
                    sex = "1";
                }
                view.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setVisibility(View.VISIBLE);
            }
        });

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
            /*case R.id.select_level:
                break;*/
            case R.id.select:
            case R.id.update_type:
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
                    getBankList();
                } else {
                    setBank();
                }
                break;
        }
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
                        select_level.setText(s);
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
    private void setBank() {
        new AlertDialog.Builder(AddEmployeeActivity.this).setTitle("请选择员工升级类型").setItems(banklist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bank.setText(banklist[which]);
            }
        }).show();
    }

    private void getUpdateType() {
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String[] kvs = new String[]{"ygtype", ""};
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

    private void addEmployee() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        //rank = manage_level.getText().toString();
        rank = "0";
        username = user_name.getText().toString();
        phone = contact_tel.getText().toString();
        pw = MD5Tool.md5(phone.substring(phone.length() - 6));
        name = rel_name.getText().toString();
        id_num = id_card.getText().toString();
        bankStr = bank.getText().toString();
        bank_account = bank_card.getText().toString();
        bank_nameStr = bank_name.getText().toString();

        if(username.length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_username));
            return;
        }
        if(!Tools.isMobileNum(phone)) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_correct_phonenumber));
            return;
        }
        if(name.length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_name));
            return;
        }
        if(id_num.length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_idnumber));
            return;
        }
        if(bank_account.length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_bank_account));
            return;
        }
        if(bank_nameStr.length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_bank_name));
            return;
        }

//        final Employee employee=new Employee()
        /**
         * 需要修改   2015-11-14 shengshoubo已修改
         */
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{userid, rank, username, pw, name, "BNZZ", phone, sex, id_num, bank_account, bank_nameStr};
        String params = AddEmployee.packagingParam(AddEmployeeActivity.this, kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                ToastUtils.showToast(AddEmployeeActivity.this, getString(R.string.add_success), Toast.LENGTH_SHORT);
                AddEmployeeActivity.this.setResult(EmployeeManageFragment.ADD_REQUEST_CODE);
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
