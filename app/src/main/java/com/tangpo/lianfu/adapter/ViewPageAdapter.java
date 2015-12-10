package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.ui.ShopActivity;
import com.tangpo.lianfu.utils.ViewPagerCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/15.
 */
public class ViewPageAdapter extends PagerAdapter {

    private Context context;
    private List<View> listView;
    private LayoutInflater inflater;
    private ViewHolder holder = null;
    private String userid;
    private ArrayList<FindStore> storeList = null;

    public ViewPageAdapter(Context context, List<View> list, String userid, ArrayList<FindStore> storeList) {
        this.context = context;
        this.listView = list;
        this.userid = userid;
        this.storeList = storeList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        container.addView(listView.get(position));
        View v = listView.get(position);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopActivity.class);
                //intent.putExtra("store_id",storeList.get(position-1).getId());
                intent.putExtra("store", storeList.get(position));
                intent.putExtra("userid", userid);
                context.startActivity(intent);
            }
        });
        return listView.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, final int position, Object object) {
        View currentView = listView.get(position);
        ((ViewPagerCompat) container).removeView(currentView);
    }

    @Override
    public int getCount() {
        return listView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    private class ViewHolder {
        public ImageView img;
        public TextView shop_name;
        public TextView commodity;
        public TextView address;
    }
}
