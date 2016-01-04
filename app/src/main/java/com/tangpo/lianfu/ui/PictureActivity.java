package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2016/1/1.
 */
public class PictureActivity extends Activity implements View.OnClickListener {
    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.picture);

        pic = (ImageView) findViewById(R.id.pic);
        pic.setOnClickListener(this);

        String url = getIntent().getStringExtra("url");
        Tools.setPhoto(PictureActivity.this, url, pic);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pic:
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

}
