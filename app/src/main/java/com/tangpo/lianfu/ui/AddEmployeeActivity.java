package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.AddEmployee;
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

    private String userid = null;

    private ProgressDialog dialog = null;

    String rank = null;
    String username = null;
    String phone = null;
    String pw = null;
    String name = null;
    String id_num = null;
    String bankStr = null;
    String bank_account = null;
    String bank_nameStr = null;
    String sex = null;

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

        manage_level = (Spinner) findViewById(R.id.manage_level);
        list = new ArrayList<>();
        list.add("管理员");
        list.add("员工");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        manage_level.setAdapter(adapter);
        manage_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).equals("管理员")){
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
        /*select_level = (TextView)findViewById(R.id.select_level);
        select_level.setOnClickListener(this);*/
        //select_type = (TextView)findViewById(R.id.select_type);
        //select_type.setOnClickListener(this);
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
        list.add("男");
        list.add("女");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).equals("男")) {
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
            /*case R.id.select_type:
                break;*/
            case R.id.select_bank:
                /**
                 * 未实现，没有接口
                 */
                break;
        }
    }

    private void addEmployee() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

//        rank = manage_level.getText().toString();
        rank = "0";
        username = user_name.getText().toString();
        phone = contact_tel.getText().toString();
        pw = MD5Tool.md5(phone.substring(phone.length() - 6));
        name = rel_name.getText().toString();
        id_num = id_card.getText().toString();
        bankStr = bank.getText().toString();
        bank_account = bank_card.getText().toString();
        bank_nameStr = bank_name.getText().toString();

//        final Employee employee=new Employee()
        /**
         * 需要修改   2015-11-14 shengshoubo已修改
         */
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
                        Tools.showToast(AddEmployeeActivity.this, "请输入正确的手机号");
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
