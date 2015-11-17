package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemberInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button edit;
    private Button send;

    private EditText user_name;
    private EditText contact_tel;
    private EditText rel_name;
    private EditText member_level;
    private EditText id_card;
    private EditText bank;
    private EditText bank_card;
    private EditText bank_name;

    private Member member = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
        Tools.deleteActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.member_info_activity);
        Tools.gatherActivity(this);
        member = (Member) getIntent().getExtras().getSerializable("member");

        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(this);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);

        user_name = (EditText) findViewById(R.id.user_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        rel_name = (EditText) findViewById(R.id.rel_name);
        member_level = (EditText) findViewById(R.id.member_level);
        id_card = (EditText) findViewById(R.id.id_card);
        bank = (EditText) findViewById(R.id.bank);
        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_name = (EditText) findViewById(R.id.bank_name);

        user_name.setText(member.getUser_id());
        contact_tel.setText(member.getPhone());
        rel_name.setText(member.getName());
        member_level.setText("");
        id_card.setText(member.getId_number());
        bank.setText(member.getBank());
        bank_card.setText(member.getBank_account());
        bank_name.setText(member.getBank_name());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                break;
            case R.id.send:
                break;
        }
    }

    private void editMember() {
        /*String kvs[] = new String []{member.getUser_id(), user_name.getText().toString(),
        member.};*/
    }
}
