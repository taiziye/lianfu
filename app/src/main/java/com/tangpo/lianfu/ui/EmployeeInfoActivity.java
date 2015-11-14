package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class EmployeeInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button edit;

    private EditText manage_level;
    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText sex;
    private EditText id_card;
    private EditText bank;
    private EditText bank_card;
    private EditText bank_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.employee_info_activity);

        init();
    }

    private void init() {
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        edit = (Button)findViewById(R.id.edit);
        edit.setOnClickListener(this);

        manage_level = (EditText)findViewById(R.id.manage_level);
        user_name = (EditText)findViewById(R.id.user_name);
        contact_tel = (EditText)findViewById(R.id.contact_tel);
        rel_name = (EditText)findViewById(R.id.rel_name);
        sex = (EditText)findViewById(R.id.sex);
        id_card = (EditText)findViewById(R.id.id_card);
        bank = (EditText)findViewById(R.id.bank);
        bank_card = (EditText)findViewById(R.id.bank_card);
        bank_name = (EditText)findViewById(R.id.bank_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                manage_level.setVisibility(View.VISIBLE);
                user_name.setVisibility(View.VISIBLE);
                contact_tel.setVisibility(View.VISIBLE);
                rel_name.setVisibility(View.VISIBLE);
                id_card.setVisibility(View.VISIBLE);
                bank.setVisibility(View.VISIBLE);
                bank_card.setVisibility(View.VISIBLE);
                bank_name.setVisibility(View.VISIBLE);
                break;
        }
    }
}
