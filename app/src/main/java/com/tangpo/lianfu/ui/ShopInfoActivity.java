package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Store;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class ShopInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button edit;

    private EditText shop_name;
    private EditText shop_host;
    private EditText contact_name;
    private EditText contact_tel;
    private EditText const_tel;
    private EditText contact_intel;
    private EditText shop_employee;
    private EditText contact_email;
    private EditText occupation;
    private EditText address;
    private EditText detail_address;
    private EditText commodity;
    private EditText map_locate;

    private UserEntity user = null;

    private ProgressDialog dialog = null;
    private Gson gson = null;

    private Store store = null;
    private String userid = "";
    private String storeid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shop_info_activity);
        user = (UserEntity) getIntent().getExtras().getSerializable("user");
        getStoreInfo();
        init();
    }

    private void init() {
        gson = new Gson();
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        edit = (Button)findViewById(R.id.edit);
        edit.setOnClickListener(this);

        shop_name = (EditText)findViewById(R.id.shop_name);
        shop_host = (EditText)findViewById(R.id.shop_host);
        contact_name = (EditText)findViewById(R.id.contact_name);
        contact_tel = (EditText)findViewById(R.id.contact_tel);
        const_tel = (EditText)findViewById(R.id.const_tel);
        contact_intel = (EditText)findViewById(R.id.contact_intel);
        shop_employee = (EditText)findViewById(R.id.shop_employee);
        contact_email = (EditText)findViewById(R.id.contact_email);
        occupation = (EditText)findViewById(R.id.occupation);
        address = (EditText)findViewById(R.id.address);
        detail_address = (EditText)findViewById(R.id.detail_address);
        commodity = (EditText)findViewById(R.id.commodity);
        map_locate = (EditText)findViewById(R.id.map_locate);
        getStoreInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                break;
        }
    }

    private void getStoreInfo(){
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{user.getStore_id(), user.getUser_id()};
        String param = StoreDetail.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    store = gson.fromJson(result.getString("param"), Store.class);
                    shop_name.setText(store.getStore());
                    shop_host.setText(store.getContact());
                    contact_name.setText(store.getContact());
                    const_tel.setText(store.getTel());
                    //occupation.setText(store.getBusiness());
                    //address.setText();
                    detail_address.setText(store.getAddress());
                    commodity.setText(store.getBusiness());
                    /**
                     * 需要修改的：地图定位，加载图片
                     */
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    if(result.getString("status").equals("9")){
                        Tools.showToast(getString(R.string.login_timeout));
                    } else {
                        Tools.showToast(getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
