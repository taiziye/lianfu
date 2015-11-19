package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.entity.UserConsumRecord;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class ConsumeRecordActivity extends Activity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1;

    private Button back;
    private Button edit;
    private Button discount;

    private EditText user_name;
    private EditText name;
    private EditText contact_tel;
    private EditText update_type;
    private EditText id_card;
    private EditText bank;
    private EditText bank_card;
    private EditText bank_name;
    private EditText consume_money;
    private EditText discount_type;
    private EditText discount_text;
    private EditText son_money;

    private EmployeeConsumeRecord record = null;

    private SharedPreferences preferences;

    private Set<String> members = null;

    private Intent intent = null;

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
        setContentView(R.layout.consum_record_activity);

        Tools.gatherActivity(this);

        preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
        members = preferences.getStringSet(Configs.KEY_MEMBERS, null);

        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(this);
        discount = (Button) findViewById(R.id.discount);
        discount.setOnClickListener(this);

        user_name = (EditText) findViewById(R.id.user_name);
        name = (EditText) findViewById(R.id.name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        update_type = (EditText) findViewById(R.id.update_type);
        id_card = (EditText) findViewById(R.id.id_card);
        bank = (EditText) findViewById(R.id.bank);
        bank_card = (EditText) findViewById(R.id.bank_card);
        bank_name = (EditText) findViewById(R.id.bank_name);
        consume_money = (EditText) findViewById(R.id.consume_money);
        discount_type = (EditText) findViewById(R.id.discount_type);
        discount_text = (EditText) findViewById(R.id.discount_text);
        son_money = (EditText) findViewById(R.id.son_money);

        intent = getIntent();
        if (intent != null) {
            record = (EmployeeConsumeRecord) intent.getSerializableExtra("record");
            user_name.setText(record.getId());
            name.setText(record.getUsername());
            if (members != null) {
                Iterator<String> it = members.iterator();
                while (it.hasNext()) {
                    try {
                        JSONObject object = new JSONObject(it.next());
                        //collectedStore.add(object.getString("id"));
                        if (object.getString("user_id").equals(record.getId())) {
                            contact_tel.setText(object.getString("phone"));
                            //update_type.setText("1");
                            //id_card.setText("11111");
                            bank.setText(object.getString("bank"));
                            bank_card.setText(object.getString("bank_card"));
                            bank_name.setText(object.getString("bank_name"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            consume_money.setText(record.getFee());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                break;
            case R.id.discount:
                intent = new Intent(this, DiscountActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Discount dis = (Discount) data.getExtras().getSerializable("discount");
            discount_type.setText(dis.getDesc());
            discount_text.setText(dis.getDiscount());
        }
    }
}
