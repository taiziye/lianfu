package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class HomePageActivity extends Activity implements View.OnClickListener {

    private LinearLayout frame;
    private LinearLayout one;
    private LinearLayout two;
    private LinearLayout three;
    private LinearLayout four;
    private LinearLayout five;

    private ImageView one_i;
    private ImageView two_i;
    private ImageView three_i;
    private ImageView four_i;
    private ImageView five_i;

    private TextView one_t;
    private TextView two_t;
    private TextView three_t;
    private TextView four_t;
    private TextView five_t;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private Fragment fragment;

    private SharedPreferences preferences;
    private String userType;
    private String userid = null;
    private String employeename = null;
    private String store_id = null;
    private String store_name = "";
    private Gson gson=null;
    private UserEntity userEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_page_activity);
        Tools.gatherActivity(this);

        preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
        String user = preferences.getString(Configs.KEY_USER, "0");
        gson=new Gson();
        userEntity=gson.fromJson(user, UserEntity.class);
        userType=userEntity.getUser_type();
        userid=userEntity.getUser_id();
        employeename=userEntity.getName();
        store_id=userEntity.getStore_id();
        store_name = userEntity.getStorename();
        init();

        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();

        if (userType.equals("3") || userType.equals("4")) { //管理员
            Bundle bundle = new Bundle();
            bundle.putString("userid", userid);
            bundle.putString("storeid", store_id);
            fragment = new ManageHomeFragment();
            fragment.setArguments(bundle);
        } else if (userType.equals("2")) {  //员工
            Bundle bundle = new Bundle();
            bundle.putString("userid", userid);
            bundle.putString("storeid", store_id);
            fragment = new EmployeeHomeFragment();
            fragment.setArguments(bundle);
        } else {  //会员
            Bundle bundle = new Bundle();
            bundle.putString("userid", userid);
            fragment = new MemberHomeFragment();
            fragment.setArguments(bundle);
        }

        transaction.add(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //这里需要统一修改
    private void init() {
        //根据不同的需要显示不同的Fragment
        frame = (LinearLayout) findViewById(R.id.frame);

        //根据不同的身份信息需要隐藏一个button，且注意更换button的text
        one = (LinearLayout) findViewById(R.id.one);
        one.setOnClickListener(this);
        one_i = (ImageView) findViewById(R.id.one_i);
        one_t = (TextView) findViewById(R.id.one_t);
        one_t.setText("地面店铺");
        two = (LinearLayout) findViewById(R.id.two);
        two.setOnClickListener(this);
        two_i = (ImageView) findViewById(R.id.two_i);
        two_t = (TextView) findViewById(R.id.two_t);
        two_t.setText("消费记录");
        three = (LinearLayout) findViewById(R.id.three);
        three.setOnClickListener(this);
        three_i = (ImageView) findViewById(R.id.three_i);
        three_t = (TextView) findViewById(R.id.three_t);
        three_t.setText("会员管理");

        four = (LinearLayout) findViewById(R.id.four);
        four.setOnClickListener(this);
        four_i = (ImageView) findViewById(R.id.four_i);
        four_t = (TextView) findViewById(R.id.four_t);
        if (userType.equals("1") || userType.equals("2")) {  //如果是非管理员登录，则隐藏改按钮
            four.setVisibility(View.GONE);
        } else {
            four_t.setText(getResources().getString(R.string.employee_management));
        }
        five = (LinearLayout) findViewById(R.id.five);
        five.setOnClickListener(this);
        five_i = (ImageView) findViewById(R.id.five_i);
        five_t = (TextView) findViewById(R.id.five_t);
        five_t.setText(getResources().getString(R.string.personal));

        one_t.setTextColor(Color.RED);
        one_i.setImageResource(R.drawable.home_page_r);
        two_t.setTextColor(Color.BLACK);
        three_t.setTextColor(Color.BLACK);

        four_t.setTextColor(Color.BLACK);
        four_i.setImageResource(R.drawable.employee_manage);
        five_t.setTextColor(Color.BLACK);
        five_i.setImageResource(R.drawable.personal);

        if (userType.equals("2") || userType.equals("3") || userType.equals("4")) {
            one_t.setText(getResources().getString(R.string.home_page));
            two_t.setText(getResources().getString(R.string.record));
            three_t.setText(getResources().getString(R.string.member));

            two_i.setImageResource(R.drawable.record);
            three_i.setImageResource(R.drawable.member_manage);
        } else {
            one_i.setImageResource(R.drawable.map_locate_r);
            one_t.setText(getResources().getString(R.string.shop));
            two_t.setText("收藏店铺");
            three_t.setText(getResources().getString(R.string.record));
            two_i.setImageResource(R.drawable.s_collect);
            three_i.setImageResource(R.drawable.record);
        }

    }

    @Override
    public void onClick(View v) {
        transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.one:
                one_t.setTextColor(Color.RED);
                one_i.setImageResource(R.drawable.home_page_r);
                two_t.setTextColor(Color.BLACK);
                two_i.setImageResource(R.drawable.record);
                three_t.setTextColor(Color.BLACK);
                three_i.setImageResource(R.drawable.member_manage);
                four_t.setTextColor(Color.BLACK);
                four_i.setImageResource(R.drawable.employee_manage);
                five_t.setTextColor(Color.BLACK);
                five_i.setImageResource(R.drawable.personal);

                if (userType.equals("3") || userType.equals("4")) { //管理员
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("storeid", store_id);
                    fragment = new ManageHomeFragment();
                    fragment.setArguments(bundle);
                } else if (userType.equals("2")) {  //员工
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("storeid", store_id);
                    fragment = new EmployeeHomeFragment();
                    fragment.setArguments(bundle);
                } else {  //会员
                    one_i.setImageResource(R.drawable.map_locate_r);
                    two_i.setImageResource(R.drawable.s_collect);
                    three_i.setImageResource(R.drawable.record);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    fragment = new MemberHomeFragment();
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.two:
                one_t.setTextColor(Color.BLACK);
                one_i.setImageResource(R.drawable.home_page);
                two_t.setTextColor(Color.RED);
                three_t.setTextColor(Color.BLACK);

                four_t.setTextColor(Color.BLACK);
                four_i.setImageResource(R.drawable.employee_manage);
                five_t.setTextColor(Color.BLACK);
                five_i.setImageResource(R.drawable.personal);

                if (userType.equals("2") || userType.equals("3") || userType.equals("4")) { //管理员
                    two_i.setImageResource(R.drawable.record_r);
                    three_i.setImageResource(R.drawable.member_manage);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("employeename", employeename);
                    bundle.putString("username", userEntity.getName());
                    bundle.putString("storename", store_name);
                    fragment = new RecordFragment();
                    fragment.setArguments(bundle);
                } else {  //会员
                    one_i.setImageResource(R.drawable.map_locate);
                    two_i.setImageResource(R.drawable.s_collect_r);
                    three_i.setImageResource(R.drawable.record);

                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    fragment = new MemCollectFragment();
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.three:
                one_t.setTextColor(Color.BLACK);
                one_i.setImageResource(R.drawable.home_page);
                two_t.setTextColor(Color.BLACK);

                three_t.setTextColor(Color.RED);

                four_t.setTextColor(Color.BLACK);
                four_i.setImageResource(R.drawable.employee_manage);
                five_t.setTextColor(Color.BLACK);
                five_i.setImageResource(R.drawable.personal);

                if (userType.equals("2") || userType.equals("3") || userType.equals("4")) { //管理员
                    two_i.setImageResource(R.drawable.record);
                    three_i.setImageResource(R.drawable.member_manage_r);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("storeid", store_id);
                    fragment = new MemManageFragment();
                    fragment.setArguments(bundle);
                } else {  //会员
                    one_i.setImageResource(R.drawable.map_locate);
                    two_i.setImageResource(R.drawable.s_collect);
                    three_i.setImageResource(R.drawable.record_r);
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("storeid", store_id);
                    fragment = new MemRecordFragment();
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.four:
                one_t.setTextColor(Color.BLACK);
                one_i.setImageResource(R.drawable.home_page);
                two_t.setTextColor(Color.BLACK);
                two_i.setImageResource(R.drawable.record);
                three_t.setTextColor(Color.BLACK);
                three_i.setImageResource(R.drawable.member_manage);
                four_t.setTextColor(Color.RED);
                four_i.setImageResource(R.drawable.employee_manage_r);
                five_t.setTextColor(Color.BLACK);
                five_i.setImageResource(R.drawable.personal);

                fragment = new EmployeeManageFragment();
                break;
            case R.id.five:
                one_t.setTextColor(Color.BLACK);
                one_i.setImageResource(R.drawable.home_page);
                two_t.setTextColor(Color.BLACK);
                two_i.setImageResource(R.drawable.record);
                three_t.setTextColor(Color.BLACK);
                three_i.setImageResource(R.drawable.member_manage);
                four_t.setTextColor(Color.BLACK);
                four_i.setImageResource(R.drawable.employee_manage);
                five_t.setTextColor(Color.RED);
                five_i.setImageResource(R.drawable.personal_r);

                if (userType.equals("3") || userType.equals("4")) { //管理员
                    fragment = new ManagerFragment();
                } else if (userType.equals("2")) {  //员工
                    fragment = new EmployeeFragment();
                } else {  //会员
                    one_i.setImageResource(R.drawable.map_locate);
                    two_i.setImageResource(R.drawable.s_collect);
                    three_i.setImageResource(R.drawable.record);

                    Bundle bundle=new Bundle();
                    bundle.putSerializable("user", userEntity);
                    fragment = new MemFragment();
                    fragment.setArguments(bundle);
                }
                break;
        }
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        Tools.closeActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case MemManageFragment.REQUEST_CODE:
                case MemManageFragment.REQUEST_EDIT:
                    transaction = fragmentManager.beginTransaction();
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                case EmployeeManageFragment.ADD_REQUEST_CODE:
                case EmployeeManageFragment.EDIT_REQUEST_CODE:
                    transaction = fragmentManager.beginTransaction();
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                case RecordFragment.REQUEST_CODE:
                case RecordFragment.REQUEST_EDIT:
                    fragmentManager = getFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
            }
            transaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.closeActivity();
        finish();
    }
}
