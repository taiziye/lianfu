package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/12/9.
 */
public class RelationActivity extends Activity implements View.OnClickListener {
    private TextView relate;
    private TextView registe;
    private TextView con;
    private String user_id = null;
    private String name=null;
    private String sex=null;
    private String address=null;
    private String photo=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relation);

        relate = (TextView) findViewById(R.id.relate);
        relate.setOnClickListener(this);
        registe = (TextView) findViewById(R.id.registe);
        registe.setOnClickListener(this);
        con = (TextView) findViewById(R.id.con);
        con.setOnClickListener(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.relate:
                intent = new Intent(RelationActivity.this, Boundlianfu.class);
                startActivity(intent);
                finish();
                break;
            case R.id.registe:
                intent = new Intent(RelationActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.con:
                createVistor();
                intent=new Intent(RelationActivity.this,HomePageActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void createVistor(){
        SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
        try {
            JSONObject jsonObject=new JSONObject(preferences.getString(Configs.KEY_THIRDUSER,""));
            String login_type=preferences.getString(Configs.KEY_LOGINTYPE,"");
            if(login_type.equals("1")){
                user_id=jsonObject.getString("id");
                name=jsonObject.getString("name");
                sex=jsonObject.getString("gender");
                if(sex.equals("m")){
                    sex="0";
                }else{
                    sex="1";
                }
                address=jsonObject.getString("location");
                photo=jsonObject.getString("profile_image_url");
                UserEntity userEntity=new UserEntity("1",user_id,"",name,"","",sex,"","","",address,"","","","","",photo);
                Configs.cacheUser(this, userEntity.toString());
            }
            if(login_type.equals("2")){
                user_id=jsonObject.getString("nickname");
                name=jsonObject.getString("nickname");
                sex=jsonObject.getString("gender");
                if(sex.equals("男")){
                    sex="0";
                }else{
                    sex="1";
                }
                address=jsonObject.getString("city");
                photo=jsonObject.getString("figureurl_qq_1");
                UserEntity userEntity=new UserEntity("1",user_id,"",name,"","",sex,"","","",address,"","","","","",photo);
                Configs.cacheUser(this, userEntity.toString());
            }
            if(login_type.equals("0")){
                user_id=jsonObject.getString("nickname");
                name=jsonObject.getString("nickname");
                sex=jsonObject.getString("sex");
                address=jsonObject.getString("city");
                photo=jsonObject.getString("headimgurl");
                UserEntity userEntity=new UserEntity("1",user_id,"",name,"","",sex,"","","",address,"","","","","",photo);
                Configs.cacheUser(this, userEntity.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
