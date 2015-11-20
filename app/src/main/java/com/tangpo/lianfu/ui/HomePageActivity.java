package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.MemberManagement;
import com.tangpo.lianfu.utils.ImageBt;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class HomePageActivity extends Activity implements View.OnClickListener {

    private LinearLayout frame;
    private ImageBt one;
    private ImageBt two;
    private ImageBt three;
    private ImageBt four;
    private ImageBt five;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private Fragment fragment;

    private SharedPreferences preferences;
    private String userType;
    private String userid = null;
    private String employeename = null;
    private String store_id = null;
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
        userEntity=gson.fromJson(user,UserEntity.class);
        userType=userEntity.getUser_type();
        userid=userEntity.getUser_id();
        employeename=userEntity.getName();
        store_id=userEntity.getStore_id();
        init();

        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        /*if (userType.equals("2")) { //管理员
            Bundle bundle = new Bundle();
            bundle.putString("userid", userid);
            bundle.putString("storeid", store_id);
            fragment = new ManageHomeFragment();
            fragment.setArguments(bundle);
            getMember();
        } else if (userType.equals("1")) {  //员工
            Bundle bundle = new Bundle();
            bundle.putString("userid", userid);
            bundle.putString("storeid", store_id);
            fragment = new EmployeeHomeFragment();
            fragment.setArguments(bundle);
            getMember();
        } else {  //会员
            fragment = new MemberHomeFragment();
        }*/
        Log.e("tag", "userid =" + userid + "  storeid =" + store_id);

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
        /*Bundle bundle = new Bundle();
        bundle.putString("userid", userid);
        fragment.setArguments(bundle);*/

        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //这里需要统一修改
    private void init() {
        //根据不同的需要显示不同的Fragment
        frame = (LinearLayout) findViewById(R.id.frame);

        //根据不同的身份信息需要隐藏一个button，且注意更换button的text
        one = (ImageBt) findViewById(R.id.one);
        one.setOnClickListener(this);
        one.setText("地面店铺");
        two = (ImageBt) findViewById(R.id.two);
        two.setOnClickListener(this);
        two.setText("消费记录");
        three = (ImageBt) findViewById(R.id.three);
        three.setOnClickListener(this);
        three.setText("会员管理");

        four = (ImageBt) findViewById(R.id.four);
        four.setOnClickListener(this);
        if (userType.equals("1") || userType.equals("2")) {  //如果是非管理员登录，则隐藏改按钮
            four.setVisibility(View.GONE);
        } else {
            four.setText(getResources().getString(R.string.employee_management));
        }
        five = (ImageBt) findViewById(R.id.five);
        five.setOnClickListener(this);
        five.setText(getResources().getString(R.string.personal));

        if (userType.equals("2") || userType.equals("3") || userType.equals("4")) {
            one.setText(getResources().getString(R.string.home_page));
            two.setText(getResources().getString(R.string.record));
            three.setText(getResources().getString(R.string.member));
        } else {
            one.setText(getResources().getString(R.string.shop));
            two.setText(getResources().getString(R.string.collect));
            three.setText(getResources().getString(R.string.record));
        }

        one.setImage(R.drawable.home_page_r);
        two.setImage(R.drawable.record);
        three.setImage(R.drawable.member_manage);
        four.setImage(R.drawable.employee_manage);
        five.setImage(R.drawable.personal);
    }

    @Override
    public void onClick(View v) {
        transaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.one:
                one.setImage(R.drawable.home_page_r);
                two.setImage(R.drawable.record);
                three.setImage(R.drawable.member_manage);
                four.setImage(R.drawable.employee_manage);
                five.setImage(R.drawable.personal);

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
                break;
            case R.id.two:
                one.setImage(R.drawable.home_page);
                two.setImage(R.drawable.record_r);
                three.setImage(R.drawable.member_manage);
                four.setImage(R.drawable.employee_manage);
                five.setImage(R.drawable.personal);

                if (userType.equals("2") || userType.equals("3") || userType.equals("4")) { //管理员
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("employeename", employeename);
                    fragment = new RecordFragment();
                    fragment.setArguments(bundle);
                } else {  //会员
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    fragment = new MemCollectFragment();
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.three:
                one.setImage(R.drawable.home_page);
                two.setImage(R.drawable.record);
                three.setImage(R.drawable.member_manage_r);
                four.setImage(R.drawable.employee_manage);
                five.setImage(R.drawable.personal);

                if (userType.equals("2") || userType.equals("3") || userType.equals("4")) { //管理员
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("storeid", store_id);
                    fragment = new MemManageFragment();
                    fragment.setArguments(bundle);
                } else {  //会员
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("storeid", store_id);
                    fragment = new MemRecordFragment();
                    fragment.setArguments(bundle);
                }
                break;
            case R.id.four:
                one.setImage(R.drawable.home_page);
                two.setImage(R.drawable.record);
                three.setImage(R.drawable.member_manage);
                four.setImage(R.drawable.employee_manage_r);
                five.setImage(R.drawable.personal);

                fragment = new EmployeeManageFragment();
                break;
            case R.id.five:
                one.setImage(R.drawable.home_page);
                two.setImage(R.drawable.record);
                three.setImage(R.drawable.member_manage);
                four.setImage(R.drawable.employee_manage);
                five.setImage(R.drawable.personal_r);

                if (userType.equals("3") || userType.equals("4")) { //管理员
                    fragment = new ManagerFragment();
                } else if (userType.equals("2")) {  //员工
                    Log.e("tag", "tag = EmployeeFragment");
                    fragment = new EmployeeFragment();
                } else {  //会员
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

    private void getMember() {
        String kvs[] = new String[]{userid, store_id, "", "", "", "1", "10"};
        String param = MemberManagement.packagingParam(this, kvs);
        final Set<String> set = new HashSet<>();

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("tag", result.toString());
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        set.add(object.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Configs.cacheMember(HomePageActivity.this, set);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                try {
                    Tools.handleResult(HomePageActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("tag", result.toString());
            }
        }, param);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case MemManageFragment.REQUEST_CODE:
                case MemManageFragment.REQUEST_EDIT:
                    fragmentManager = getFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                case EmployeeManageFragment.ADD_REQUEST_CODE:
                case EmployeeManageFragment.EDIT_REQUEST_CODE:
                    fragmentManager = getFragmentManager();
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
