package com.tangpo.lianfu.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ViewPageAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.utils.GetBitmap;
import com.tangpo.lianfu.utils.ViewPagerCompat;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    private MapView mMapView=null;
    private BaiduMap mBaiduMap=null;
    private ArrayList<LatLng> list=new ArrayList<LatLng>();

    private ViewPagerCompat vp;

    private List<View> listViews = new ArrayList<>();

    private ArrayList<FindStore> storeList = null;

    private ViewPageAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        //设定初始地图中心点坐标
        SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
        float curLatitude=preferences.getFloat(Configs.KEY_LATITUDE, (float) 30.283178);
        float curLongitude=preferences.getFloat(Configs.KEY_LONGITUDE,(float)120.132947);
        //Toast.makeText(this, "latitude" + curLatitude + "," + "longitude" + curLongitude, Toast.LENGTH_SHORT).show();
        LatLng cenpt = new LatLng(curLatitude,curLongitude);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(13).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //显示当前设备位置
        BitmapDescriptor mCurrentMaker= BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions overlayOptions=new MarkerOptions().position(cenpt).icon(mCurrentMaker).zIndex(11).animateType(MarkerOptions.MarkerAnimateType.drop);
        mBaiduMap.addOverlay(overlayOptions);

        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //卫星地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        //实时交通图
        //mBaiduMap.setTrafficEnabled(true);
        //百度城市热力图
        // mBaiduMap.setBaiduHeatMapEnabled(true);

        //标注覆盖物
        //定义Maker坐标点
        LatLng pt1=new LatLng(30.283178,120.132947);
        LatLng pt2=new LatLng(30.284924,120.09644);
        LatLng pt3=new LatLng(30.241503,120.13266);
        LatLng pt4=new LatLng(30.316105,120.092703);
        LatLng pt5=new LatLng(30.276192,120.166005);
        LatLng pt6=new LatLng(30.311117,120.147895);
        LatLng pt7=new LatLng(30.325084,120.129498);
        LatLng pt8=new LatLng(30.312364,120.198488);
        LatLng pt9=new LatLng(30.21679,120.234132);
        LatLng pt10=new LatLng(30.191321,120.200787);

        list.add(pt1);
        list.add(pt2);
        list.add(pt3);
        list.add(pt4);
        list.add(pt5);
        list.add(pt6);
        list.add(pt7);
        list.add(pt8);
        list.add(pt9);
        list.add(pt10);
        for(int i=0;i<list.size();i++){
            //构建Marker图标
            BitmapDescriptor bitmap= BitmapDescriptorFactory.fromResource(GetBitmap.getBitmap(i));
            //构建MarkerOption，用于在地图上添加Marker  alpha(float)设置透明度
            OverlayOptions options=new MarkerOptions().position(list.get(i)).icon(bitmap).zIndex(0).animateType(MarkerOptions.MarkerAnimateType.grow);
            Marker mMakerD=(Marker)(mBaiduMap.addOverlay(options));
            //在地图上添加Marker，并显示
        }
        OverlayOptions textOption=new TextOptions().bgColor(0xAAFFFF00).fontSize(24).fontColor(0xFFFF00FF).text("联富地面店").rotate(-30).position(list.get(0));
        mBaiduMap.addOverlay(textOption);

        vp = (ViewPagerCompat) findViewById(R.id.vp);

        storeList = getIntent().getExtras().getParcelableArrayList("list");

        for(int i=0; i<10; i++){
            View view = LayoutInflater.from(this).inflate(R.layout.viewpage_list, null);
            ImageView img = (ImageView) view.findViewById(R.id.img);
            TextView shop_name = (TextView) view.findViewById(R.id.shop_name);
            TextView commodity = (TextView) view.findViewById(R.id.shop_name);
            TextView address = (TextView)view.findViewById(R.id.address);

            //初始化
            //img.setImageURI(null);
            shop_name.setText(storeList.get(i).getStore());
            //commodity.setText(storeList.get(i).get);
            address.setText(storeList.get(i).getAddress());

            listViews.add(view);
        }

        adapter = new ViewPageAdapter(this, listViews);
        vp.setAdapter(adapter);
        vp.setOnPageChangeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i=0;i<10;i++){
            if(position==i){
                float curLng=(float)(Integer.valueOf(storeList.get(i).getLng())/(10^6));
                float curLat=(float)(Integer.valueOf(storeList.get(i).getLat())/(10^6));

            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
