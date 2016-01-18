package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ViewPageAdapter;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.fragment.MemberHomeFragment;
import com.tangpo.lianfu.utils.GetBitmap;
import com.tangpo.lianfu.utils.Tools;
import com.tangpo.lianfu.utils.ViewPagerCompat;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends Fragment implements ViewPager.OnPageChangeListener {

    private static final int SCANNIN_STORE_INFO = 1;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private ArrayList<LatLng> list = new ArrayList<LatLng>();

    private ViewPagerCompat vp;

    private List<View> listViews = new ArrayList<>();

    private ArrayList<FindStore> storeList = null;

    private ViewPageAdapter adapter = null;

    private Overlay myOverlay=null;

    private Button btn_back;
    private Button back;

    private String userid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * 在应用程序创建时初始化SDK引用的Context全局变量，注意这里需要获取整个应用的Context，即ApplicationContext
         * 并且注意要在setContentView方法之前实现
         *注意：在SDK各功能组件使用之前都需要调用
         SDKInitializer.initialize(getApplicationContext());，因此我们建议该方法放在Application的初始化方法中
         */
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.activity_test_baidu_map, container, false);

        //获取地图控件引用
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        //获取地图
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        storeList = getArguments().getParcelableArrayList("list");
        userid = getArguments().getString("userid");
        vp = (ViewPagerCompat) view.findViewById(R.id.vp);

        //标注覆盖物，定义Maker坐标点
        float lng = (float)Integer.valueOf(storeList.get(0).getLng()) / 1000000;
        float lat = (float)Integer.valueOf(storeList.get(0).getLat()) / 1000000;
        LatLng cenpt = new LatLng(lat, lng);

        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(13).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //显示当前设备位置
        BitmapDescriptor mCurrentMaker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions overlayOptions = new MarkerOptions().position(cenpt).icon(mCurrentMaker).zIndex(13);
        myOverlay=mBaiduMap.addOverlay(overlayOptions);
        //卫星地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        //实时交通图
        //mBaiduMap.setTrafficEnabled(true);
        //百度城市热力图
        // mBaiduMap.setBaiduHeatMapEnabled(true);

        for (int i = 0; i < storeList.size(); i++) {
            //标注覆盖物，定义Maker坐标点
            float Lng = (float)Integer.valueOf(storeList.get(i).getLng()) / 1000000;
            float Lat = (float)Integer.valueOf(storeList.get(i).getLat()) / 1000000;
            LatLng pt = new LatLng(Lat, Lng);
            list.add(pt);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(GetBitmap.getBitmap(i));
            //构建MarkerOption，用于在地图上添加Marker  alpha(float)设置透明度
            OverlayOptions options = new MarkerOptions().position(pt).icon(bitmap).zIndex(13).animateType(MarkerOptions.MarkerAnimateType.grow);
            mBaiduMap.addOverlay(options);

            //适配ViewPager
            View viewp = LayoutInflater.from(getActivity()).inflate(R.layout.viewpage_list, null);
            ImageView img = (ImageView) viewp.findViewById(R.id.img);
            TextView shop_name = (TextView) viewp.findViewById(R.id.shop_name);
            TextView commodity = (TextView) viewp.findViewById(R.id.commodity);
            TextView address = (TextView) viewp.findViewById(R.id.address);
            //初始化
            //img.setImageURI(null);
            Tools.setPhoto(getActivity(), storeList.get(i).getPhoto(), img);
            shop_name.setText(storeList.get(i).getStore());
            commodity.setText(storeList.get(i).getBusiness());
            address.setText(storeList.get(i).getAddress());

            listViews.add(viewp);
        }

        adapter = new ViewPageAdapter(getActivity(), listViews, userid, storeList);

        vp.setAdapter(adapter);
        vp.setCurrentItem(0);
        vp.setOffscreenPageLimit(5);
        vp.setPageMargin(dip2px(getActivity(), 50));
        vp.setOnPageChangeListener(this);

        btn_back= (Button) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MemberHomeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("userid", userid);
                fragment.setArguments(bundle);
                transaction.replace(R.id.frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        back = (Button) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                Fragment fragment = new MemberHomeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putString("userid", userid);
                fragment.setArguments(bundle);
                transaction.replace(R.id.frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

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
        for (int i = 0; i < list.size(); i++) {
            if (position == i) {
                locate(list.get(i));
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void locate(LatLng cenpt) {
        //Toast.makeText(this, "latitude" + curLatitude + "," + "longitude" + curLongitude, Toast.LENGTH_SHORT).show();
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(13).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        //显示当前设备位置
        BitmapDescriptor mCurrentMaker = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions overlayOptions = new MarkerOptions().position(cenpt).icon(mCurrentMaker).zIndex(11);
        myOverlay.remove();
        myOverlay=mBaiduMap.addOverlay(overlayOptions);
    }

    // dip转换成px（像素）
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
