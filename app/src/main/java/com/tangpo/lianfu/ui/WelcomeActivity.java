package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class WelcomeActivity extends Activity implements LocationListener{
    private static final String TAG = "WelcomeActivity";
    private boolean isStartGuide;
    private LocationManager locationManager=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        initAppConfig();
        /*ActivityManager.getInstance().addActivity(WelcomeActivity.this);
        Tools.changeSystemBar(this);

        if (User.getInstance(WelcomeActivity.this).checkLogin()) {
            GetAccount.getData(WelcomeActivity.this,
                    User.getInstance(WelcomeActivity.this).getTmId());
        }

        isStartGuide = AppInfo.getInstance(WelcomeActivity.this).isStartGuide();
        */
        // 在欢迎界面停留一秒
        new Handler().postDelayed(new Runnable() {
            public void run() {
				/*
				 * if (isStartGuide) {
				 * Tools.gotoActivity(WelcomeActivity.this,GuideActivity.class);
				 * } else {
				 * Tools.gotoActivity(WelcomeActivity.this,MainActivity.class);
				 * }
				 */
                Tools.gotoActivity(WelcomeActivity.this, MainActivity.class);
                WelcomeActivity.this.finish();
            }
        }, 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    /**
     * 异步启动服务进行数据库及其他资源的初始化
     */
    private void initAppConfig() {
        //获取到当前手机的位置信息并缓存到本地的SharedPreference
        Log.e(TAG, "启动服务进行数据库及其他资源的初始化！");
        locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo.isAvailable()){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000,10,this);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,10000,10,this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Configs.cacheCurLocation(this, location.getLatitude(), location.getLongitude());
        //SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
