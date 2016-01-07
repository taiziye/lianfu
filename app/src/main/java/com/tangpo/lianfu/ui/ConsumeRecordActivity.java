package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Dis;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.EditConsumeRecord;
import com.tangpo.lianfu.utils.ToastUtils;
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
    private EmployeeConsumeRecord record = null;
    private SharedPreferences preferences;
    private Set<String> members = null;
    private Intent intent = null;
    private String user_id= "";
    private String consume_id= "";
    private ProgressDialog dialog=null;

    private String mfee;
    private String mdiscount;

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

        intent = getIntent();
        if (intent != null) {
            record = (EmployeeConsumeRecord) intent.getSerializableExtra("record");
            user_id=intent.getStringExtra("user_id");
            consume_id=record.getId();
            user_name.setText(record.getUsername());
            name.setText(record.getName());
//            username=record.getUsername();
//            consume_id=intent.getStringExtra("consume_id");
//            username = intent.getStringExtra("username");
//            user_name.setText(username);
//            name.setText(intent.getStringExtra("name"));
            if (members != null) {
                Iterator<String> it = members.iterator();
                while (it.hasNext()) {
                    try {
                        JSONObject object = new JSONObject(it.next());
                        if (object.getString("user_id").equals(record.getId())) {
                            contact_tel.setText(object.getString("phone"));
                            update_type.setText(object.getString("update_type"));
                            id_card.setText(object.getString("id_card"));
                            bank.setText(object.getString("bank"));
                            bank_card.setText(object.getString("bank_card"));
                            bank_name.setText(object.getString("bank_name"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            consume_money.setText("￥"+Float.valueOf(record.getFee()));
            mfee=record.getFee();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                editConsumeRecord();
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
            Dis dis = (Dis) data.getExtras().getSerializable("discount");
            discount_type.setText(dis.getTypename());
            discount_text.setText(Float.valueOf(dis.getAgio())/10+"折");
            mdiscount=dis.getAgio();
        }
    }

    private void editConsumeRecord(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        if (mdiscount == null || mdiscount.length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_choose_discount));
            return;
        }
        if(mfee==null || mfee.length() == 0 || Float.valueOf(mfee) <= 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_correct_amount));
            return;
        }
        String kvs[]=new String[]{user_id,consume_id,mfee,mdiscount};
        String params= EditConsumeRecord.packagingParam(ConsumeRecordActivity.this,kvs);
        record.setFee(mfee);
        record.setDiscount(mdiscount);
        record.setDesc(discount_type.getText().toString());

        dialog=ProgressDialog.show(ConsumeRecordActivity.this,getString(R.string.connecting),getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                ToastUtils.showToast(ConsumeRecordActivity.this, getString(R.string.edit_success), Toast.LENGTH_SHORT);
                Intent intent = new Intent();
                intent.putExtra("record", record);
                setResult(RESULT_OK, intent);
                ConsumeRecordActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if(status.equals("9")){
                        ToastUtils.showToast(ConsumeRecordActivity.this,getString(R.string.login_timeout),Toast.LENGTH_SHORT);
                    }else if(status.equals("10")){
                        ToastUtils.showToast(ConsumeRecordActivity.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    } else {
                        ToastUtils.showToast(ConsumeRecordActivity.this,result.getString("info"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }
}
