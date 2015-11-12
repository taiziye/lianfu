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
 * Created by 果冻 on 2015/11/8.
 */
public class PayBillActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button upload;
    private Button pay_online;

    private TextView shop;
    private TextView select;

    private EditText money;
    private EditText contact_tel;
    private EditText bill_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_bill);
    }

    private void init(){
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        upload = (Button)findViewById(R.id.upload);
        upload.setOnClickListener(this);
        pay_online = (Button)findViewById(R.id.pay_online);
        pay_online.setOnClickListener(this);

        shop = (TextView)findViewById(R.id.shop);
        select = (TextView)findViewById(R.id.select);
        select.setOnClickListener(this);

        money = (EditText)findViewById(R.id.money);
        contact_tel = (EditText)findViewById(R.id.contact_tel);
        bill_num = (EditText)findViewById(R.id.bill_num);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.upload:
                break;
            case R.id.pay_online:
                break;
            case R.id.select:
                break;
        }
    }
}
