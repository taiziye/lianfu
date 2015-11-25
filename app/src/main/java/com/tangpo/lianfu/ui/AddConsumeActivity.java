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
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.entity.Manager;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.entity.Store;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CommitConsumeRecord;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

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
    private TextView contact_tel;
    private EditText consume_money;
    private TextView user_name;
    private TextView discount;
    private TextView select_user;
    private TextView select_discount;
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
        contact_tel = (TextView) findViewById(R.id.contact_tel);
        consume_money = (EditText) findViewById(R.id.consum_money);

        user_name = (TextView) findViewById(R.id.user_name);
        discount = (TextView) findViewById(R.id.discount);
        select_user = (TextView) findViewById(R.id.select_user);
        select_user.setOnClickListener(this);
        select_discount = (TextView) findViewById(R.id.select_discount);
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
            case R.id.select_user:
                intent = new Intent(AddConsumeActivity.this, SelectUserActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, SELECT_MEM);
                break;
            case R.id.select_discount:
                intent = new Intent(AddConsumeActivity.this, DiscountActivity.class);
                startActivityForResult(intent, SELECT_DIS);
                break;
        }
    }

    private Member mem = null;
    private Discount dis = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == SELECT_MEM) {
                mem = (Member) data.getExtras().getSerializable("user");
                user_name.setText(mem.getUser_id());
                name.setText(mem.getName());
                contact_tel.setText(mem.getPhone());
            } else if (requestCode == SELECT_DIS) {
                dis = (Discount) data.getExtras().getSerializable("discount");
                discount.setText(dis.getDiscount());
            }
        }
    }

    private void commitConsume() {
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{user.getUser_id(), user.getStore_id(), dis.getDiscount(),
                consume_money.getText().toString(), mem.getUser_id()};

        Log.e("tag",user.getStore_id());
        String param = CommitConsumeRecord.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Tools.showToast(AddConsumeActivity.this, getString(R.string.add_success));
                AddConsumeActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(AddConsumeActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
