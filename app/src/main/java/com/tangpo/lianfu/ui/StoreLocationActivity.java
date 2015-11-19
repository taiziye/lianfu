package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

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
import com.tangpo.lianfu.utils.Tools;

public class StoreLocationActivity extends Activity {

    private Button back;

    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

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
        SDKInitializer.initialize(getApplication());
        setContentView(R.layout.activity_store_location);

        Tools.gatherActivity(this);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //float longitude = Float.valueOf(intent.getStringExtra("lng")) / (10 ^ 6);
        //float latitude = Float.valueOf(intent.getStringExtra("lat")) / (10 ^ 6);
        double Lng=(double)(Integer.valueOf(getIntent().getStringExtra("lng")))/1000000;
        double Lat=(double)(Integer.valueOf(getIntent().getStringExtra("lat")))/1000000;
        LatLng cenpt = new LatLng(Lat, Lng);
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(13).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        BitmapDescriptor mCurrentMaker = BitmapDescriptorFactory.fromResource(R.drawable.shop_gound_r);
        OverlayOptions overlayOptions = new MarkerOptions().position(cenpt).icon(mCurrentMaker).zIndex(11).animateType(MarkerOptions.MarkerAnimateType.drop);
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
