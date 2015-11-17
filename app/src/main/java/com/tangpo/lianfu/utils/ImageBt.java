package com.tangpo.lianfu.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/16.
 */
public class ImageBt extends LinearLayout {

    private ImageView iv;
    private TextView tv;

    public ImageBt(Context context) {
        this(context, null);
    }

    public ImageBt(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.button, this, true);
        iv = (ImageView) findViewById(R.id.img);
        tv = (TextView) findViewById(R.id.text);
    }


    /**
     * 设置图片资源
     */
    public void setImage(int resId) {
        iv.setImageResource(resId);
    }

    /**
     * 设置显示的文字
     */
    public void setText(String text) {
        tv.setText(text);
    }
}