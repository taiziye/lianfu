package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.NewDiscount;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

public class AddDiscountActivity extends Activity implements View.OnClickListener {
    private EditText type;
    private EditText discount;

    private Button commit;
    private Button cancel;
    private ProgressDialog dialog;

    private String userid;
    private String storeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_discount_activity);

        userid = getIntent().getExtras().getString("userid");
        storeid = getIntent().getExtras().getString("storeid");

        type = (EditText) findViewById(R.id.discount_type);
        discount = (EditText) findViewById(R.id.discount);

        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                addDiscount();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }

    private void addDiscount() {
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String []{userid, storeid, type.getText().toString(), discount.getText().toString()};

        String param = NewDiscount.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Intent intent = new Intent();
                Discount dis = new Discount(type.getText().toString(), discount.getText().toString(), "", "", "", "");
                intent.putExtra("discount", dis);
                setResult(RESULT_OK, intent);
                Tools.showToast(AddDiscountActivity.this, getString(R.string.add_success));
                AddDiscountActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    if("1".equals(result.getString("status"))) {
                        Tools.showToast(AddDiscountActivity.this, getString(R.string.add_failed));
                    } else if("2".equals(result.getString("status"))) {
                        Tools.showToast(AddDiscountActivity.this, getString(R.string.format_error));
                    } else if("9".equals(result.getString("status"))) {
                        Tools.showToast(AddDiscountActivity.this, getString(R.string.login_timeout));
                        SharedPreferences preferences = AddDiscountActivity.this.getSharedPreferences(Configs.APP_ID, AddDiscountActivity.this.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(Configs.KEY_TOKEN);
                        editor.commit();
                        Intent intent = new Intent(AddDiscountActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if("10".equals(result.getString("status"))) {
                        Tools.showToast(AddDiscountActivity.this, getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
