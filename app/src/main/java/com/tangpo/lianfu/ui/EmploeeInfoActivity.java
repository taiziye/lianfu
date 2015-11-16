package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Employee;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.EditStaff;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class EmploeeInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button confirm;

    private EditText manage_level;
    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText sex;
    private EditText id_card;
    private EditText bank;
    private EditText bank_card;
    private EditText bank_name;

    private Employee employee = null;

    private String userid = "";

    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.employee_info_activity);

        employee = (Employee) getIntent().getExtras().getSerializable("employee");
        userid = getIntent().getExtras().getString("userid");
        init();
    }

    private void init(){
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        confirm = (Button) findViewById(R.id.edit);
        confirm.setOnClickListener(this);

        manage_level = (EditText) findViewById(R.id.manage_level);
        user_name = (EditText) findViewById(R.id.user_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        rel_name = (EditText) findViewById(R.id.rel_name);
        sex = (EditText) findViewById(R.id.sex);
        id_card = (EditText) findViewById(R.id.id_card);
        bank = (EditText) findViewById(R.id.bank);
        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_name = (EditText) findViewById(R.id.bank_name);

        manage_level.setText(employee.getRank());
        user_name.setText(employee.getUser_id());
        contact_tel.setText(employee.getPhone());
        rel_name.setText(employee.getZsname());
        /**
         * 需要修改
         */
        id_card.setText("");
        bank.setText(employee.getBank());
        bank_card.setText(employee.getBank_account());
        bank_name.setText(employee.getBank_name());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.confirm:
                dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
                updateEmployee();
                break;
        }
    }

    private void updateEmployee(){
        String employee_id = employee.getUser_id();
        String rank =  manage_level.getText().toString();
        String username = user_name.getText().toString();
        String name = rel_name.getText().toString();
        String id_number = id_card.getText().toString();
        String upgrade = "BNZZ";
        String bank_account = bank_card.getText().toString();
        String bankStr = bank.getText().toString();
        String sexStr = "";
        if (sex.getText().toString().equals("男"))
            sexStr = "0";
        else
            sexStr = "1";

        String kvs[] = new String[]{userid, employee_id, rank, username, name, id_number,
                upgrade, bank_account, bankStr, sexStr};

        String param = EditStaff.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Tools.showToast(getString(R.string.update_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                dialog.dismiss();
                try {
                    if(result.getString("status").equals("2")){
                        Tools.showToast(getString(R.string.format_error));
                    } else if(result.getString("status").equals("10")){
                        Tools.showToast(getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
