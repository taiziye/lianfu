package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class ShopActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button collect;
    private Button locate;
    private Button contact;
    private Button pay;

    private ImageView img_shop;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    private ImageView img6;
    private ImageView img7;
    private ImageView img8;

    private TextView detail_address;
    private TextView tel;
    private TextView qq;
    private TextView email;
    private TextView commodity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shop_activity);

        init();
    }

    private void init() {
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        collect = (Button)findViewById(R.id.collect);
        collect.setOnClickListener(this);
        locate = (Button)findViewById(R.id.locate);
        locate.setOnClickListener(this);
        contact = (Button)findViewById(R.id.contact);
        contact.setOnClickListener(this);
        pay = (Button)findViewById(R.id.pay);
        pay.setOnClickListener(this);

        img_shop = (ImageView)findViewById(R.id.img_shop);
        img1 = (ImageView)findViewById(R.id.img1);
        img2 = (ImageView)findViewById(R.id.img2);
        img3 = (ImageView)findViewById(R.id.img3);
        img4 = (ImageView)findViewById(R.id.img4);
        img5 = (ImageView)findViewById(R.id.img5);
        img6 = (ImageView)findViewById(R.id.img6);
        img7 = (ImageView)findViewById(R.id.img7);
        img8 = (ImageView)findViewById(R.id.img8);

        detail_address = (TextView)findViewById(R.id.detail_address);
        tel = (TextView)findViewById(R.id.tel);
        qq = (TextView)findViewById(R.id.qq);
        email = (TextView)findViewById(R.id.email);
        commodity = (TextView)findViewById(R.id.commodity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                break;
            case R.id.collect:
                break;
            case R.id.locate:
                break;
            case R.id.contact:
                break;
            case R.id.pay:
                break;
        }
    }
}
