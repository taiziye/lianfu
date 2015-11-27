package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.FindStore;
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
    private ImageView collect;
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
    private FindStore findStore=null;
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
        findStore=getIntent().getParcelableExtra("store");
        store_id=findStore.getId();
        user_id=getIntent().getExtras().getString("userid");
        init();
    }

    private void init() {
        gson=new Gson();
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        collect = (ImageView) findViewById(R.id.collect);
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
                Intent payIntent=new Intent(ShopActivity.this,PayBillActivity.class);
                payIntent.putExtra("userid",user_id);
                payIntent.putExtra("store_id",store_id);
                payIntent.putExtra("storename",store.getStore());
                startActivity(payIntent);
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
                Log.e("tag", "store " + result.toString());
                try {
                    store = gson.fromJson(result.getJSONObject("param").toString(), Store.class);
                    detail_address.setText(store.getAddress());
                    tel.setText(store.getTel());
                    qq.setText("");
                    email.setText("");
                    commodity.setText(store.getBusiness());
                    /**
                     * 需要修改的：地图定位，加载图片
                     */
                    Log.e("tag", "photo = " + store.getPhoto());
                    String tmp[] = store.getPhoto().split("\\,");
                    Tools.setPhoto(ShopActivity.this, store.getBanner(), img_shop);

                    if(tmp.length <1){
                        Tools.setPhoto(ShopActivity.this, "", img1);
                        Tools.setPhoto(ShopActivity.this, "", img2);
                        Tools.setPhoto(ShopActivity.this, "", img3);
                        Tools.setPhoto(ShopActivity.this, "", img4);
                        Tools.setPhoto(ShopActivity.this, "", img5);
                        Tools.setPhoto(ShopActivity.this, "", img6);
                        Tools.setPhoto(ShopActivity.this, "", img7);
                        Tools.setPhoto(ShopActivity.this, "", img8);
                    }else if(tmp.length <2){
                        Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                        Tools.setPhoto(ShopActivity.this, "", img2);
                        Tools.setPhoto(ShopActivity.this, "", img3);
                        Tools.setPhoto(ShopActivity.this, "", img4);
                        Tools.setPhoto(ShopActivity.this, "", img5);
                        Tools.setPhoto(ShopActivity.this, "", img6);
                        Tools.setPhoto(ShopActivity.this, "", img7);
                        Tools.setPhoto(ShopActivity.this, "", img8);
                    }else if(tmp.length <3){
                        Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                        Tools.setPhoto(ShopActivity.this, tmp[1], img2);
                        Tools.setPhoto(ShopActivity.this, "", img3);
                        Tools.setPhoto(ShopActivity.this, "", img4);
                        Tools.setPhoto(ShopActivity.this, "", img5);
                        Tools.setPhoto(ShopActivity.this, "", img6);
                        Tools.setPhoto(ShopActivity.this, "", img7);
                        Tools.setPhoto(ShopActivity.this, "", img8);
                    }else if(tmp.length <4){
                        Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                        Tools.setPhoto(ShopActivity.this, tmp[1], img2);
                        Tools.setPhoto(ShopActivity.this, tmp[2], img3);
                        Tools.setPhoto(ShopActivity.this, "", img4);
                        Tools.setPhoto(ShopActivity.this, "", img5);
                        Tools.setPhoto(ShopActivity.this, "", img6);
                        Tools.setPhoto(ShopActivity.this, "", img7);
                        Tools.setPhoto(ShopActivity.this, "", img8);
                    }else if(tmp.length <5){
                        Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                        Tools.setPhoto(ShopActivity.this, tmp[1], img2);
                        Tools.setPhoto(ShopActivity.this, tmp[2], img3);
                        Tools.setPhoto(ShopActivity.this, tmp[3], img4);
                        Tools.setPhoto(ShopActivity.this, "", img5);
                        Tools.setPhoto(ShopActivity.this, "", img6);
                        Tools.setPhoto(ShopActivity.this, "", img7);
                        Tools.setPhoto(ShopActivity.this, "", img8);
                    }else if(tmp.length <6){
                        Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                        Tools.setPhoto(ShopActivity.this, tmp[1], img2);
                        Tools.setPhoto(ShopActivity.this, tmp[2], img3);
                        Tools.setPhoto(ShopActivity.this, tmp[3], img4);
                        Tools.setPhoto(ShopActivity.this, tmp[4], img5);
                        Tools.setPhoto(ShopActivity.this, "", img6);
                        Tools.setPhoto(ShopActivity.this, "", img7);
                        Tools.setPhoto(ShopActivity.this, "", img8);
                    }else if(tmp.length <7){
                        Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                        Tools.setPhoto(ShopActivity.this, tmp[1], img2);
                        Tools.setPhoto(ShopActivity.this, tmp[2], img3);
                        Tools.setPhoto(ShopActivity.this, tmp[3], img4);
                        Tools.setPhoto(ShopActivity.this, tmp[4], img5);
                        Tools.setPhoto(ShopActivity.this, tmp[5], img6);
                        Tools.setPhoto(ShopActivity.this, "", img7);
                        Tools.setPhoto(ShopActivity.this, "", img8);
                    }else if(tmp.length <8){
                        Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                        Tools.setPhoto(ShopActivity.this, tmp[1], img2);
                        Tools.setPhoto(ShopActivity.this, tmp[2], img3);
                        Tools.setPhoto(ShopActivity.this, tmp[3], img4);
                        Tools.setPhoto(ShopActivity.this, tmp[4], img5);
                        Tools.setPhoto(ShopActivity.this, tmp[5], img6);
                        Tools.setPhoto(ShopActivity.this, tmp[6], img7);
                        Tools.setPhoto(ShopActivity.this, "", img8);
                    }else if(tmp.length <9){
                        Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                        Tools.setPhoto(ShopActivity.this, tmp[1], img2);
                        Tools.setPhoto(ShopActivity.this, tmp[2], img3);
                        Tools.setPhoto(ShopActivity.this, tmp[3], img4);
                        Tools.setPhoto(ShopActivity.this, tmp[4], img5);
                        Tools.setPhoto(ShopActivity.this, tmp[5], img6);
                        Tools.setPhoto(ShopActivity.this, tmp[6], img7);
                        Tools.setPhoto(ShopActivity.this, tmp[7], img8);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(store == null) {
                    Tools.showToast(getApplicationContext(), "该店铺不存在");
                    ShopActivity.this.finish();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                Log.e("tag", "store_fail " + result.toString());
                try {
                    Tools.handleResult(ShopActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(store == null) {
                    Tools.showToast(getApplicationContext(), "该店铺不存在");
                    ShopActivity.this.finish();
                }
            }
        }, param);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                collect.setImageResource(R.drawable.s_collect_r);
            }
        }
    };

    private void collectStore(){
        dialog=ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{store_id, user_id};
        String params= CollectStore.packagingParam(this,kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Message msg = new Message();
                msg.what = 1;
                handler.handleMessage(msg);
                ToastUtils.showToast(ShopActivity.this,getString(R.string.collect_success), Toast.LENGTH_SHORT);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(ShopActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }
}
