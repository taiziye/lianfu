package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Window;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class WelcomeActivity extends Activity{
    private static final String TAG = "WelcomeActivity";
    private boolean isStartGuide;
    private LocationManager locationManager = null;

    private LocationClient mLocationClient=null;

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);

        mLocationClient=new LocationClient(this);
        LocationClientOption option=new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setCoorType("bd09ll");//设置坐标类型为bd09ll
        option.setPriority(LocationClientOption.NetWorkFirst);//设置网络优先
        option.setProdName("locSDKDemo2");//设置产品线名称
        option.setScanSpan(5000);//定时定位，每隔5秒钟定位一次
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if(bdLocation==null){
                    return;
                }
                Configs.cacheCurLocation(WelcomeActivity.this, bdLocation.getLatitude(), bdLocation.getLongitude());
            }
        });

        if(mLocationClient==null){
            return;
        }
        if(mLocationClient.isStarted()) {
            mLocationClient.stop();
        }else{
            mLocationClient.start();
        }
        // 在欢迎界面停留3秒
        new Handler().postDelayed(new Runnable() {
            public void run() {

                UserEntity userEntity=new UserEntity("0","游客","","","游客","","","0","","","","","","","","游客","","0","0","0","0","0");
                String token=Configs.getCatchedToken(WelcomeActivity.this);
                if(token!=null) Tools.gotoActivity(WelcomeActivity.this, HomePageActivity.class);
                else{
                    Configs.cacheUser(WelcomeActivity.this,userEntity.toJSONString());
                    Tools.gotoActivity(WelcomeActivity.this,HomePageActivity.class);
                }
                WelcomeActivity.this.finish();
            }
        }, 3000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mLocationClient=null;
        //finish();
    }
}
