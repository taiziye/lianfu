package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.PayBill;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;
import com.tangpo.lianfu.utils.UploadImage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class PayBillActivity extends Activity implements View.OnClickListener {

    private Button back;
//    private Button upload;
    private Button pay_online;
    private Button pay_offline;
    private ImageView imageView=null;

    private TextView shop;

    private EditText money;
    private EditText contact_tel;
    private EditText bill_num;

    private UserEntity userEntity=null;
    private SharedPreferences preferences=null;
    private Gson gson=null;
    private ProgressDialog dialog=null;

    private String imagePath=null;
    private String receipt_photo=null;

    private String user_id=null;
    private String store_id=null;

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private MemRecordFragment fragment;

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Tools.deleteActivity(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pay_bill);
        Tools.gatherActivity(this);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
//        upload = (Button) findViewById(R.id.upload);
//        upload.setOnClickListener(this);
        pay_online = (Button) findViewById(R.id.pay_online);
        pay_online.setOnClickListener(this);

        pay_offline= (Button) findViewById(R.id.pay_offline);
        pay_offline.setOnClickListener(this);

        shop = (TextView) findViewById(R.id.shop);

        money = (EditText) findViewById(R.id.money);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        bill_num = (EditText) findViewById(R.id.bill_num);

        imageView= (ImageView) findViewById(R.id.bill);
        imageView.setOnClickListener(this);

        String storename=getIntent().getStringExtra("storename");
        store_id=getIntent().getStringExtra("store_id");
        user_id=getIntent().getStringExtra("userid");
        gson=new Gson();
        preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
        String user=preferences.getString(Configs.KEY_USER,"");
        userEntity=gson.fromJson(user,UserEntity.class);

        shop.setText(storename);
        contact_tel.setText(userEntity.getPhone());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bill:
                /**
                 * 上传照片需要调用照相机
                 */
                intent=new Intent(PayBillActivity.this,SelectPicActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.pay_offline:
                payBill();
                break;
            case R.id.pay_online:
                Intent intent1=new Intent(PayBillActivity.this,SelectPayMethod.class);
                Bundle bundle=new Bundle();
                bundle.putString("store_id",store_id);
                bundle.putString("user_id",user_id);
                bundle.putString("fee",money.getText().toString());
                bundle.putString("phone",contact_tel.getText().toString());
                bundle.putString("receipt_no","");
                bundle.putString("receipt_photo","");
                bundle.putString("online","true");
                bundle.putString("paymode","0");
                if(money.getText().toString().equals("")){
                    ToastUtils.showToast(PayBillActivity.this,getString(R.string.pay_amount_cannot_be_null),Toast.LENGTH_SHORT);
                    return;
                }
                if(contact_tel.getText().toString().equals("")||contact_tel.getText().toString().length()==0){
                    ToastUtils.showToast(PayBillActivity.this,getString(R.string.phone_num_cannot_be_null),Toast.LENGTH_SHORT);
                    return;
                }
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case R.id.select:
                break;
            /*case R.id.bill:
                intent=new Intent(PayBillActivity.this,SelectPicActivity.class);
                startActivityForResult(intent, 1);
                break;*/
        }
    }

    private void payBill(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        String user_id=userEntity.getUser_id();
        String store_id=getIntent().getStringExtra("store_id");
        String fee=money.getText().toString();
        String phone=contact_tel.getText().toString();
        String receipt_no=bill_num.getText().toString();

        String online="false";
        /**
         * 这里需要修改支付方式
         */
        String pay_way="0";
        if (fee.equals("")){
            ToastUtils.showToast(this,getString(R.string.fee_can_not_be_null),Toast.LENGTH_SHORT);
            return;
        }
        if(receipt_no.equals("")){
            ToastUtils.showToast(this,getString(R.string.receipt_no_can_not_be_null),Toast.LENGTH_SHORT);
            return;
        }
        if(receipt_photo == null || receipt_photo.length() == 0) {
            Tools.showToast(getApplicationContext(), "请添加购物凭证");
            return;
        }
        if(!Tools.isMobileNum(phone)) {
            Tools.showToast(getApplicationContext(), "请填写正确的电话号码");
            return;
        }

        dialog=ProgressDialog.show(this,getString(R.string.connecting),getString(R.string.please_wait));
        String kvs[]=new String[]{user_id,store_id,fee,phone,receipt_no,receipt_photo,online,pay_way};
        String params= PayBill.packagingParam(this,kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                ToastUtils.showToast(PayBillActivity.this, "请求成功！", Toast.LENGTH_SHORT);
                PayBillActivity.this.finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(PayBillActivity.this, result.getString("status"));
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
                        Intent intent = new Intent(PayBillActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if("10".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else {
                        Tools.showToast(getApplicationContext(), "上传小票失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
//            upload.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(Uri.parse(data.getStringExtra(SelectPicActivity.SMALL_KEY_PHOTO_PATH)));
            imagePath=data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            receipt_photo=UploadImage.imgToBase64(data.getStringExtra(SelectPicActivity.SMALL_KEY_PHOTO_PATH));
        }
    }
}