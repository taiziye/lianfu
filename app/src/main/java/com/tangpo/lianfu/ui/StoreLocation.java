package com.tangpo.lianfu.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.tangpo.lianfu.R;

public class StoreLocation extends ActionBarActivity {


    private MapView mMapView=null;
    private BaiduMap mBaiduMap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_location);
        /**
         * 在应用程序创建时初始化SDK引用的Context全局变量，注意这里需要获取整个应用的Context，即ApplicationContext
         * 并且注意要在setContentView方法之前实现
         *注意：在SDK各功能组件使用之前都需要调用
         SDKInitializer.initialize(getApplicationContext());，因此我们建议该方法放在Application的初始化方法中
         */
        SDKInitializer.initialize(getApplication());
        setContentView(R.layout.activity_test_baidu_map);
        //获取地图控件引用
        mMapView= (MapView) findViewById(R.id.bmapView);
        //获取地图
        mBaiduMap=mMapView.getMap();

        Intent intent=getIntent();
        float longitude=Float.valueOf(intent.getStringExtra("lng"))/(10^6);
        float latitude=Float.valueOf(intent.getStringExtra("lat"))/(10^6);

        LatLng cenpt=new LatLng(longitude,latitude);
        //定义地图状态
        MapStatus mMapStatus=new MapStatus.Builder().target(cenpt).zoom(13).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        //显示当前商店的位置
        BitmapDescriptor mCurrentMaker= BitmapDescriptorFactory.fromResource(R.drawable.shop_gound_r);
        OverlayOptions overlayOptions=new MarkerOptions().position(cenpt).icon(mCurrentMaker).zIndex(11).animateType(MarkerOptions.MarkerAnimateType.drop);
        mBaiduMap.addOverlay(overlayOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
