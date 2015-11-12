package com.tangpo.lianfu.ui;

import android.app.Activity;
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
public class AddConsumeActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button commit;

    private EditText shop_name;
    private EditText name;
    private EditText contact_tel;
    private EditText consume_money;

    private TextView user_name;
    private TextView discount;
    private TextView select_user;
    private TextView select_discount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_consume_activity);

        init();
    }

    private void init() {
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button)findViewById(R.id.commit);
        commit.setOnClickListener(this);

        shop_name = (EditText)findViewById(R.id.shop_name);
        name = (EditText)findViewById(R.id.name);
        contact_tel = (EditText)findViewById(R.id.contact_tel);
        consume_money = (EditText)findViewById(R.id.consum_money);

        user_name = (TextView)findViewById(R.id.user_name);
        discount = (TextView)findViewById(R.id.discount);
        select_user = (TextView)findViewById(R.id.select_user);
        select_user.setOnClickListener(this);
        select_discount = (TextView)findViewById(R.id.select_discount);
        select_discount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.commit:
                break;
            case R.id.select_user:
                break;
            case R.id.select_discount:
                break;
        }
    }
}
