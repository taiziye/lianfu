package com.tangpo.lianfu.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tangpo.lianfu.R;

/**
 * Created by shengshoubo on 2016/1/25.
 */
public class PayBillPhotoGridItem extends RelativeLayout {
    private Context mContext;
    private ImageView mImageView;

    public PayBillPhotoGridItem(Context context) {
        this(context, null, 0);
    }

    public PayBillPhotoGridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayBillPhotoGridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.paybill_photoalbum_gridview_item, this);
        mImageView = (ImageView)findViewById(R.id.photo_img_view);
    }

    public void setImgResID(int id){
        if(mImageView != null){
            mImageView.setBackgroundResource(id);
        }
    }

    public void SetBitmap(Bitmap bit){
        if(mImageView != null){
            mImageView.setImageBitmap(bit);
        }
    }
}
