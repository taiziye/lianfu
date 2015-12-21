package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;
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
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }
        if(type.getText().toString().length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_input_correct_discount_type));
            return;
        }
        if (discount.getText().toString().length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_choose_correct_discount));
            return;
        }
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String []{userid, storeid, type.getText().toString(), discount.getText().toString()};
        String param = NewDiscount.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Intent intent = new Intent();
                Discount dis = new Discount(type.getText().toString(), discount.getText().toString(), "", "", "", "0");
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
                    Tools.handleResult(AddDiscountActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
