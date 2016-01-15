package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.entity.Store;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CancelCollectedStore;
import com.tangpo.lianfu.parms.CollectStore;
import com.tangpo.lianfu.parms.GetSpecifyServer;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

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
    private TextView shop_name;

    private String store_id=null;
    private String user_id=null;
    private String favorite ="";
    private Store store=null;
    private FindStore findStore=null;
    private ProgressDialog dialog=null;
    private Gson gson=null;
    private String[] tmp = null;
//    private ArrayList<StoreServer> servers = new ArrayList<>();

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
        if(getIntent() != null) {
            findStore=getIntent().getParcelableExtra("store");
            store_id=findStore.getId();
            favorite=findStore.getFavorite();
            if(favorite==null){
                favorite=getIntent().getStringExtra("favorite");
            }
            user_id=getIntent().getExtras().getString("userid");
        }
        init();
    }

    private void init() {
        gson=new Gson();
        shop_name = (TextView) findViewById(R.id.shop_name);
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
        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
        img7.setOnClickListener(this);
        img8.setOnClickListener(this);

        img1.setVisibility(View.INVISIBLE);
        img2.setVisibility(View.INVISIBLE);
        img3.setVisibility(View.INVISIBLE);
        img4.setVisibility(View.INVISIBLE);
        img5.setVisibility(View.INVISIBLE);
        img6.setVisibility(View.INVISIBLE);
        img7.setVisibility(View.INVISIBLE);
        img8.setVisibility(View.INVISIBLE);

        detail_address = (TextView) findViewById(R.id.detail_address);
        tel = (TextView) findViewById(R.id.tel);
        qq = (TextView) findViewById(R.id.qq);
        email = (TextView) findViewById(R.id.email);
        commodity = (TextView) findViewById(R.id.commodity);

        if(favorite==null){
            favorite="1";
        }
        if(favorite.equals("1")) {
            collect.setBackgroundResource(R.drawable.s_collect_r);
        } else {
            collect.setBackgroundResource(R.drawable.s_collect);
        }
        getStoreInfo();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.collect:
                if(favorite.equals("1")) {
                    cancelCollect();
                } else {
                    collectStore();
                }

                break;
            case R.id.locate:
                intent=new Intent(ShopActivity.this,StoreLocationActivity.class);
                intent.putExtra("lng",store.getLng());
                intent.putExtra("lat",store.getLat());
                startActivity(intent);
                break;
            case R.id.contact:
                //ToastUtils.showToast(ShopActivity.this,getString(R.string.new_function_has_not_online),Toast.LENGTH_SHORT);
                getServer();
                break;
            case R.id.pay:
                SharedPreferences preferences=getSharedPreferences(Configs.APP_ID, Context.MODE_PRIVATE);
                String logintype=preferences.getString(Configs.KEY_LOGINTYPE, "");
                if(logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
                    intent=new Intent(this, BoundOrRegister.class);
                    startActivity(intent);
                    return;
                }
                Intent payIntent=new Intent(ShopActivity.this,PayBillActivity.class);
                payIntent.putExtra("userid",user_id);
                payIntent.putExtra("store_id",store_id);
                payIntent.putExtra("storename",store.getStore());
                startActivity(payIntent);
                break;
            case R.id.img1:
                setBigPhoto(0);
                break;
            case R.id.img2:
                setBigPhoto(1);
                break;
            case R.id.img3:
                setBigPhoto(2);
                break;
            case R.id.img4:
                setBigPhoto(3);
                break;
            case R.id.img5:
                setBigPhoto(4);
                break;
            case R.id.img6:
                setBigPhoto(5);
                break;
            case R.id.img7:
                setBigPhoto(6);
                break;
            case R.id.img8:
                setBigPhoto(7);
                break;
        }
    }

    private void setBigPhoto(int n) {
        Intent intent = new Intent(ShopActivity.this, PictureActivity.class);
        intent.putExtra("flag", "url");
        intent.putExtra("url", tmp[n]);
        startActivity(intent);
    }

    private void getStoreInfo() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{store_id, user_id};
        String param = StoreDetail.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    store = gson.fromJson(result.getJSONObject("param").toString(), Store.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 3;
                msg.obj = store;
                handler.sendMessage(msg);
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

                if(store == null) {
                    //Tools.showToast(getApplicationContext(), "该店铺不存在");
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
                favorite = "1";
                collect.setBackgroundResource(R.drawable.s_collect_r);
            }else if (msg.what == 2) {
                favorite = "0";
                collect.setBackgroundResource(R.drawable.s_collect);
            } else if (msg.what == 3 && store != null) {
                store = (Store) msg.obj;

                detail_address.setText(store.getAddress());
                shop_name.setText(store.getStore());
                tel.setText(store.getTel());
                qq.setText(store.getQq());
                email.setText(store.getEmail());
                commodity.setText(store.getBusiness());
                tmp = store.getPhoto().split("\\,");
                Tools.setPhoto(ShopActivity.this, store.getBanner(), img_shop);

                if("0".equals(store.getLat()) || "0".equals(store.getLng())) {
                    locate.setVisibility(View.GONE);
                }

                if (tmp.length>0){
                    img1.setVisibility(View.VISIBLE);
                    Tools.setPhoto(ShopActivity.this, tmp[0], img1);
                }
                if(tmp.length>1){
                    img2.setVisibility(View.VISIBLE);
                    Tools.setPhoto(ShopActivity.this, tmp[1], img2);
                }
                if(tmp.length>2){
                    img3.setVisibility(View.VISIBLE);
                    Tools.setPhoto(ShopActivity.this, tmp[2], img3);
                }
                if(tmp.length>3){
                    img4.setVisibility(View.VISIBLE);
                    Tools.setPhoto(ShopActivity.this, tmp[3], img4);
                }
                if(tmp.length>4){
                    img5.setVisibility(View.VISIBLE);
                    Tools.setPhoto(ShopActivity.this, tmp[4], img5);
                }
                if(tmp.length>5){
                    img6.setVisibility(View.VISIBLE);
                    Tools.setPhoto(ShopActivity.this, tmp[5], img6);
                }
                if(tmp.length>6){
                    img7.setVisibility(View.VISIBLE);
                    Tools.setPhoto(ShopActivity.this, tmp[6], img7);
                }
                if(tmp.length>7){
                    img8.setVisibility(View.VISIBLE);
                    Tools.setPhoto(ShopActivity.this, tmp[7], img8);
                }
            } else if (msg.what == 4) {
                JSONArray array = (JSONArray) msg.obj;
                Intent intent = new Intent(ShopActivity.this, ConversationActivity.class);
                intent.putExtra("servers", array.toString());
                intent.putExtra("userid", user_id);
                startActivity(intent);
            }
        }
    };

    private void getServer() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        String[] kvs = new String[]{store_id};
        String param = GetSpecifyServer.packagingParam(getApplicationContext(), kvs);
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                dialog.dismiss();
                JSONArray array = null;
                try {
                    array = result.getJSONArray("param");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 4;
                msg.obj = array;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                dialog.dismiss();
                try {
                    if ("3".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "店铺不存在客服");
                    } else if ("10".equals(result.getString("status"))) {
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

    private void cancelCollect() {
        String kvs[]=new String[]{store_id,user_id};
        String params= CancelCollectedStore.packagingParam(getApplicationContext(), kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                ToastUtils.showToast(getApplicationContext(),getString(R.string.request_success),Toast.LENGTH_SHORT);
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                try {
                    String status=result.getString("status");
                    if(status.equals("1")){
                        ToastUtils.showToast(getApplicationContext(),getString(R.string.operate_fail),Toast.LENGTH_SHORT);
                    }else if(status.equals("9")){
                        ToastUtils.showToast(getApplicationContext(),getString(R.string.login_timeout),Toast.LENGTH_SHORT);
                    }else{
                        ToastUtils.showToast(getApplicationContext(),getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    private void collectStore(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

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
                    if(result.getString("status").equals("300")) {
                        Tools.showToast(getApplicationContext(), "已收藏过该店铺");
                    } else {
                        Tools.handleResult(ShopActivity.this, result.getString("status"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }
}
