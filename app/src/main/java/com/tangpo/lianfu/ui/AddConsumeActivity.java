package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Dis;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CommitConsumeRecord;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class AddConsumeActivity extends Activity implements View.OnClickListener {

    public static final int SELECT_MEM = 1;
    public static final int SELECT_DIS = 2;
    private Button back;
    private Button commit;
    private TextView shop_name;
    private TextView name;
    private EditText contact_tel;
    private EditText consume_money;
    private TextView user_name;
    private TextView discount;
    private ImageView select_user;
    private ImageView select_discount;
    private SharedPreferences preferences = null;
    private UserEntity user = null;
    private ProgressDialog dialog = null;
    private Gson gson = null;

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
        setContentView(R.layout.add_consume_activity);

        Tools.gatherActivity(this);

        gson = new Gson();

        preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
        String userInfo = preferences.getString(Configs.KEY_USER, "0");
        user = gson.fromJson(userInfo, UserEntity.class);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);

        shop_name = (TextView) findViewById(R.id.shop_name);
        name = (TextView) findViewById(R.id.name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        consume_money = (EditText) findViewById(R.id.consum_money);
        consume_money.setOnClickListener(this);
        consume_money.setSelectAllOnFocus(true);

        user_name = (TextView) findViewById(R.id.user_name);
        user_name.setOnClickListener(this);
        discount = (TextView) findViewById(R.id.discount);
        discount.setOnClickListener(this);
        select_user = (ImageView) findViewById(R.id.select_user);
        select_user.setOnClickListener(this);
        select_discount = (ImageView) findViewById(R.id.select_discount);
        select_discount.setOnClickListener(this);

        shop_name.setText(user.getStorename());
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.commit:
                commitConsume();
                break;
            case R.id.user_name:
            case R.id.select_user:
                intent = new Intent(AddConsumeActivity.this, SelectUserActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, SELECT_MEM);
                break;
            case R.id.discount:
            case R.id.select_discount:
                intent = new Intent(AddConsumeActivity.this, DiscountActivity.class);
                startActivityForResult(intent, SELECT_DIS);
                break;
            case R.id.consume_money:
                consume_money.setText(consume_money.getText().toString());
                consume_money.selectAll();
                break;
        }
    }

    private Member mem = null;
    private Dis dis = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == SELECT_MEM) {
                mem = (Member) data.getExtras().getSerializable("user");
                user_name.setText(mem.getUsername());
                name.setText(mem.getName());
                contact_tel.setText(mem.getPhone());
            } else if (requestCode == SELECT_DIS) {
                dis = (Dis) data.getExtras().getSerializable("discount");
                discount.setText(Float.valueOf(dis.getAgio()) / 10 + "折");
            }
        }
    }

    private void commitConsume() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        if(mem == null) {
            Tools.showToast(this, getString(R.string.please_choose_consume_user));
            return;
        }

        if(dis == null) {
            Tools.showToast(this, getString(R.string.please_choose_discount));
            return;
        }

        if(consume_money.getText().toString().length() == 0||Float.valueOf(consume_money.getText().toString())==0) {
            Tools.showToast(this, getString(R.string.please_input_correct_amount));
            return;
        }

        String kvs[] = new String[]{user.getUser_id(), user.getStore_id(), dis.getAgio(),
                consume_money.getText().toString(), mem.getUser_id()};

        final EmployeeConsumeRecord record = new EmployeeConsumeRecord();
        record.setUsername(user_name.getText().toString());
        record.setName(name.getText().toString());
        record.setDiscount(dis.getAgio());
        record.setFee(consume_money.getText().toString());
        record.setPay_status("0");
        record.setPay_date((new SimpleDateFormat("yyyy/MM/dd HH:mm")).format(new Date()));
        record.setGains("0.00");
        record.setConsume_date();

        String param = CommitConsumeRecord.packagingParam(this, kvs);

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Tools.showToast(AddConsumeActivity.this, getString(R.string.add_success));
                Intent intent = new Intent();
                intent.putExtra("record", record);
                setResult(RESULT_OK, intent);
                AddConsumeActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    if("1".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.add_failed));
                    } else if("2".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.format_error));
                    } else if("9".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.login_timeout));
                        SharedPreferences preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(Configs.KEY_TOKEN);
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else if("10".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else {
                        Tools.showToast(getApplicationContext(), result.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
