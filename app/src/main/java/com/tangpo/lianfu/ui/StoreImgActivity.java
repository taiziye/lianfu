package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ShopImgAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.entity.Store;
import com.tangpo.lianfu.entity.StoreInfo;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.DeleteStorePicture;
import com.tangpo.lianfu.parms.UploadStorePicture;
import com.tangpo.lianfu.utils.Tools;
import com.tangpo.lianfu.utils.UploadImage;
import com.tangpo.lianfu.wxapi.WXPayEntryActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StoreImgActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button add;

    private ListView listView=null;
    private ShopImgAdapter adapter=null;

    private List<String> serverImgPath=null;
    private List<String> localImgPath=null;
    private String user_id=null;
    private String store_id=null;

    private ProgressDialog dialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_ad);

        Intent intent=getIntent();
        serverImgPath= intent.getStringArrayListExtra("imgPath");
        user_id=intent.getStringExtra("user_id");
        store_id=intent.getStringExtra("store_id");

        localImgPath=intent.getStringArrayListExtra("paths");
        for(int i=0;i<serverImgPath.size();i++){
            Log.e("tag",serverImgPath.get(i));
        }
        init();
    }

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!intent.getAction().equals(PhotoActivity.ACTION)){
                return;
            }
            localImgPath=intent.getStringArrayListExtra("paths");
            Message msg=new Message();
            msg.what=1;
            handler.sendMessage(msg);
            if(broadcastReceiver!=null){
                unregisterReceiver(broadcastReceiver);
                //broadcastReceiver=null;
            }
        }
    };

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                if(localImgPath!=null&&localImgPath.size()!=0){
                for(int i=0;i<localImgPath.size();i++){
                        uploadStorePicture(localImgPath.get(i));
                    }
                }
            }
        }
    };

    private void init(){
        back= (Button) findViewById(R.id.back);
        add= (Button) findViewById(R.id.add);
        back.setOnClickListener(this);
        add.setOnClickListener(this);

        listView= (ListView) findViewById(R.id.list);
        adapter=new ShopImgAdapter(serverImgPath,this,store_id,user_id);
        listView.setAdapter(adapter);
    }

    private void uploadStorePicture(final String imgPath) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        String photo= UploadImage.imgToBase64(imgPath);
        String[] kvs=new String[]{store_id,user_id,"1",photo};
        String param= UploadStorePicture.packagingParam(StoreImgActivity.this,kvs);

        dialog=ProgressDialog.show(StoreImgActivity.this,getString(R.string.connecting),getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                Tools.showToast(StoreImgActivity.this, getString(R.string.upload_success));
                //serverImgPath.add(imgPath);
                //adapter.notifyDataSetChanged();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if("1".equals(status)){
                        Tools.showToast(StoreImgActivity.this,getString(R.string.upload_fail));
                    }else{
                        Tools.showToast(StoreImgActivity.this,getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },param);
    }


    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.add:
                if(serverImgPath.size()==8){
                    Tools.showToast(this,getString(R.string.only_can_add_8_pictures_at_most));
                    return;
                }
                registerReceiver(broadcastReceiver,new IntentFilter(PhotoActivity.ACTION));
//                intent=new Intent(StoreImgActivity.this, ChooseShopImg.class);
                intent=new Intent(StoreImgActivity.this,SelectPicActivity.class);
                intent.putExtra("name","StoreImgActivity");
//                startActivityForResult(intent,REQUEST_CODE);
                startActivityForResult(intent,1);
                //finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uri.parse(data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH)));
        if(data!=null){
            //serverImgPath.add(data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH));
            uploadStorePicture(data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH));
            //adapter.notifyDataSetChanged();
        }
        //String path=data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
        //receipt_photo=UploadImage.imgToBase64(data.getStringExtra(SelectPicActivity.SMALL_KEY_PHOTO_PATH));
        //uploadStorePicture(path);
    }
}
