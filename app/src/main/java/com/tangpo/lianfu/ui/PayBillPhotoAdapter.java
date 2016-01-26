package com.tangpo.lianfu.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.tangpo.lianfu.entity.PhotoAibum;
import com.tangpo.lianfu.entity.PhotoItem;

import java.util.ArrayList;

/**
 * Created by shengshoubo on 2016/1/25.
 */
public class PayBillPhotoAdapter extends BaseAdapter {
    private Context context;
    private PhotoAibum aibum;
    private ArrayList<PhotoItem> gl_arr;
    public PayBillPhotoAdapter(Context context, PhotoAibum aibum,ArrayList<PhotoItem> gl_arr) {
        this.context = context;
        this.aibum = aibum;
        this.gl_arr=gl_arr;
    }

    @Override
    public int getCount() {
        if (gl_arr==null) {
            return aibum.getBitList().size();
        }else{
            return gl_arr.size();
        }

    }

    @Override
    public PhotoItem getItem(int position) {
        if(gl_arr==null){
            return aibum.getBitList().get(position);
        }else{
            return gl_arr.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PayBillPhotoGridItem item;
        if(convertView == null){
            item = new PayBillPhotoGridItem(context);
            item.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.MATCH_PARENT));
        }else{
            item = (PayBillPhotoGridItem)convertView;
        }
        // 通过ID 加载缩略图
        if (gl_arr==null) {
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), aibum.getBitList().get(position).getPhotoID(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
            item.SetBitmap(bitmap);
            boolean flag = aibum.getBitList().get(position).isSelect();
//            item.setChecked(flag);
        }else{
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), gl_arr.get(position).getPhotoID(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
            item.SetBitmap(bitmap);
        }
        return item;
    }
}
