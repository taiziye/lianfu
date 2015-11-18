package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Store;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CollectStore;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class ShopActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button collect;
    private Button locate;
    private Button contact;
    private Button pay;

    private ImageView img_shop;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    private ImageView img6;
    private ImageView img7;
    private ImageView img8;

    private TextView detail_address;
    private TextView tel;
    private TextView qq;
    private TextView email;
    private TextView commodity;

    private String store_id=null;
    private String user_id=null;
    private Store store=null;
    private ProgressDialog dialog=null;
    private Gson gson=null;

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
        setContentView(R.layout.shop_activity);

        Tools.gatherActivity(this);
        store_id=getIntent().getExtras().getString("store_id");
        user_id=getIntent().getExtras().getString("userid");
        init();
    }

    private void init() {
        gson=new Gson();
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        collect = (Button) findViewById(R.id.collect);
        collect.setOnClickListener(this);
        locate = (Button) findViewById(R.id.locate);
        locate.setOnClickListener(this);
        contact = (Button) findViewById(R.id.contact);
        contact.setOnClickListener(this);
        pay = (Button) findViewById(R.id.pay);
        pay.setOnClickListener(this);

        img_shop = (ImageView) findViewById(R.id.img_shop);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        img5 = (ImageView) findViewById(R.id.img5);
        img6 = (ImageView) findViewById(R.id.img6);
        img7 = (ImageView) findViewById(R.id.img7);
        img8 = (ImageView) findViewById(R.id.img8);

        detail_address = (TextView) findViewById(R.id.detail_address);
        tel = (TextView) findViewById(R.id.tel);
        qq = (TextView) findViewById(R.id.qq);
        email = (TextView) findViewById(R.id.email);
        commodity = (TextView) findViewById(R.id.commodity);

        getStoreInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.collect:
                collectStore();
                break;
            case R.id.locate:
                Intent intent=new Intent(ShopActivity.this,StoreLocationActivity.class);
                intent.putExtra("lng",store.getLng());
                intent.putExtra("lat",store.getLat());
                startActivity(intent);
                break;
            case R.id.contact:
                break;
            case R.id.pay:
                break;
        }
    }

    private void getStoreInfo() {
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{store_id, user_id};
        String param = StoreDetail.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    Log.e("tag", "tag" + result.toString());
                    store = gson.fromJson(result.getJSONObject("param").toString(), Store.class);
                    detail_address.setText(store.getAddress());
                    tel.setText(store.getTel());
                    qq.setText("");
                    email.setText("");
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
                    if (result.getString("status").equals("9")) {
                        Tools.showToast(ShopActivity.this, getString(R.string.login_timeout));
                    } else {
                        Tools.showToast(ShopActivity.this, getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    private void collectStore(){
        dialog=ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{store_id, user_id};
        String params= CollectStore.packagingParam(this,kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                ToastUtils.showToast(ShopActivity.this,getString(R.string.collect_success), Toast.LENGTH_SHORT);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if(status.equals("1")){
                        ToastUtils.showToast(ShopActivity.this,getString(R.string.collect_failed),Toast.LENGTH_SHORT);
                    }else if(status.equals("9")){
                        ToastUtils.showToast(ShopActivity.this,getString(R.string.login_timeout),Toast.LENGTH_SHORT);
                        Intent intent=new Intent(ShopActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        ToastUtils.showToast(ShopActivity.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }
}
