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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class ConsumeRecordActivity extends Activity implements View.OnClickListener {
    public static final int REQUEST_CODE = 1;

    private Button back;
    private Button edit;
    private ImageView discount;
    private EditText user_name;
    private EditText name;
    private EditText contact_tel;
    private EditText update_type;
    private EditText id_card;
    private EditText bank;
    private EditText bank_card;
    private EditText bank_name;
    private EditText consume_money;
//    private EditText profit;
    private EditText discount_type;
    private TextView discount_text;
    private EmployeeConsumeRecord record = null;
    private SharedPreferences preferences;
    private Set<String> members = null;
    private Intent intent = null;
    private String user_id= "";
    private String consume_id= "";
    private ProgressDialog dialog=null;

    private LinearLayout frame1;
    private LinearLayout frame2;
    private LinearLayout frame3;
    private TextView ticket;
    private ImageView ticketpic;

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

        discount = (ImageView) findViewById(R.id.discount);
        discount.setOnClickListener(this);
        discount_text = (TextView) findViewById(R.id.discount_text);
        discount_text.setOnClickListener(this);

//        profit = (EditText) findViewById(R.id.profit);
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

        frame1 = (LinearLayout) findViewById(R.id.frame1);
        frame1.setVisibility(View.GONE);
        frame2 = (LinearLayout) findViewById(R.id.frame2);
        frame2.setVisibility(View.GONE);
        frame3 = (LinearLayout) findViewById(R.id.frame3);
        ticket = (TextView) findViewById(R.id.ticket);
        ticketpic = (ImageView) findViewById(R.id.ticketpic);

        intent = getIntent();
        if (intent != null) {
            record = (EmployeeConsumeRecord) intent.getSerializableExtra("record");
            user_id=intent.getStringExtra("user_id");
            consume_id=record.getId();
            user_name.setText(record.getUsername());
            name.setText(record.getName());
            contact_tel.setText(record.getPhone());
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
            DecimalFormat formatter=new DecimalFormat("##0.00");
            consume_money.setText(formatter.format(Float.valueOf(record.getFee())));
            mfee=record.getFee();

            frame1.setVisibility(View.VISIBLE);
            ticket.setText(record.getTicket());
            frame2.setVisibility(View.VISIBLE);
            Tools.setPhoto(this, record.getTicketpic(), ticketpic);

            ticketpic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(ConsumeRecordActivity.this, PictureActivity.class);
                    intent.putExtra("flag","url");
                    intent.putExtra("url",record.getTicketpic());
                    startActivity(intent);
                }
            });
//            if (record.getTicket().length() != 0) {
//                frame1.setVisibility(View.VISIBLE);
//                ticket.setText(record.getTicket());
//            }
//            if (record.getTicketpic().length() != 0) {
//                frame2.setVisibility(View.VISIBLE);
//                Tools.setPhoto(this, record.getTicketpic(), ticketpic);
//            }

            if ("2".equals(record.getIsPass())) {
                setUnable();
            }
        }
    }

    private void setUnable() {
        contact_tel.setEnabled(false);
        consume_money.setEnabled(false);
        discount_text.setClickable(false);
        discount.setClickable(false);
        frame3.setVisibility(View.GONE);
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
            case R.id.discount_text:
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

        if("2".equals(record.getIsPass())){
            Tools.showToast(ConsumeRecordActivity.this,getString(R.string.can_not_commit_the_comsume_record_has_been_audit));
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
        mfee=consume_money.getText().toString();
        String kvs[]=new String[]{user_id,consume_id,mfee,mdiscount};
        String params= EditConsumeRecord.packagingParam(ConsumeRecordActivity.this,kvs);
        record.setFee(consume_money.getText().toString());
        record.setDiscount(mdiscount);
        record.setDesc(discount_type.getText().toString());
        DecimalFormat formatter=new DecimalFormat("##0.00");
        record.setGains(formatter.format((1-Float.valueOf(mdiscount)/100)*Float.valueOf(consume_money.getText().toString())));

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
