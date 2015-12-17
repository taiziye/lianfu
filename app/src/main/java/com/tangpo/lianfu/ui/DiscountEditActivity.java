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
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/12/16.
 */
public class DiscountEditActivity extends Activity implements View.OnClickListener {
    private Button back;
    private Button commit;
    private EditText name;
    private EditText discount;

    private ProgressDialog dialog;

    private String user_id;
    private String store_id;
    private Discount dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_editdiscount);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
        discount = (EditText) findViewById(R.id.discount);

        if(getIntent() != null) {
            user_id = getIntent().getStringExtra("userid");
            store_id = getIntent().getStringExtra("storeid");
            dis = (Discount) getIntent().getSerializableExtra("discount");

            name.setText(dis.getDesc());
            discount.setText(dis.getDiscount());
        } else {
            name.setText("");
            discount.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.commit:
                String nameStr = name.getText().toString().trim();
                String discountStr = discount.getText().toString().trim();
                updateDiscount(nameStr, discountStr);
                break;
        }
    }

    private void updateDiscount(final String name, final String discount) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{user_id, store_id, dis.getId(), name, discount};
        String params = com.tangpo.lianfu.parms.EditDiscount.packagingParam(getApplicationContext(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                Tools.showToast(getApplicationContext(), "修改成功");
                Intent intent = new Intent();
                dis.setDesc(name);
                dis.setDiscount(discount);
                intent.putExtra("discount", dis);
                setResult(RESULT_OK, intent);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                try {
                    if ("1".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "修改失败");
                    } else {
                        Tools.handleResult(getApplicationContext(), result.getString("status"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }
}
