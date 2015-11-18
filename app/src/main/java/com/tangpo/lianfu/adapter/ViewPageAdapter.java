package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangpo.lianfu.entity.MemberCollect;
import com.tangpo.lianfu.utils.ViewPagerCompat;

import java.util.List;

/**
 * Created by 果冻 on 2015/11/15.
 */
public class ViewPageAdapter extends PagerAdapter {

    private Context context;
    private List<View> listView;
    private LayoutInflater inflater;
    private ViewHolder holder = null;

    public ViewPageAdapter(Context context, List<View> list) {
        this.context = context;
        this.listView = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public float getPageWidth(int position) {
        if((position == 0) || (position == (listView.size() - 1))) {
            return 0.9f;
        } else
            return 0.8f;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(listView.get(position));
        return listView.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
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
