package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class AddEmployeeActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button commit;

    private TextView manage_level;
    private TextView bank;
    private TextView select_level;
    //private TextView select_type;
    private TextView select_bank;

    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText id_card;
    private EditText bank_card;
    private EditText bank_name;

    private String userid = "";

    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_employee_activity);

        userid = getIntent().getExtras().getString("userid");

        init();
    }

    private void init() {
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button)findViewById(R.id.commit);
        commit.setOnClickListener(this);

        manage_level = (TextView)findViewById(R.id.manage_level);
        bank = (TextView)findViewById(R.id.bank);
        select_level = (TextView)findViewById(R.id.select_level);
        select_level.setOnClickListener(this);
        //select_type = (TextView)findViewById(R.id.select_type);
        //select_type.setOnClickListener(this);
        select_bank = (TextView)findViewById(R.id.select_bank);
        select_bank.setOnClickListener(this);

        user_name = (EditText)findViewById(R.id.user_name);
        contact_tel = (EditText)findViewById(R.id.contact_tel);
        rel_name = (EditText)findViewById(R.id.rel_name);
        id_card = (EditText)findViewById(R.id.id_card);
        bank_card = (EditText)findViewById(R.id.bank_card);
        bank_name = (EditText)findViewById(R.id.bank_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.commit:
                dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
                break;
            case R.id.select_level:
                break;
            /*case R.id.select_type:
                break;*/
            case R.id.select_bank:
                break;
        }
    }

    private void addEmployee(){
        String rank = manage_level.getText().toString();
        String username = user_name.getText().toString();
        String phone = contact_tel.getText().toString();
        String name = rel_name.getText().toString();
        String id_num = id_card.getText().toString();
        String bankStr = bank.getText().toString();
        String bank_account = bank_card.getText().toString();
        String bank_nameStr = bank_name.getText().toString();

        /**
         * 需要修改
         */
        String kvs[] = new String[]{};
    }
}
