package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.MemberManagement;

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
    private Button one;
    private Button two;
    private Button three;
    private Button four;
    private Button five;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private Fragment fragment;

    private SharedPreferences preferences;
    private String userType;
    private String userid = null;
    private String employeename = null;
    private String store_id = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_page_activity);

        preferences=getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
        String user=preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject=new JSONObject(user);
            userType=jsonObject.getString("user_type");
            userid = jsonObject.getString("user_id");
            employeename = jsonObject.getString("name");
            store_id = jsonObject.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        init();

        fragmentManager =getFragmentManager();
        transaction =fragmentManager.beginTransaction();
        if (userType.equals("2")) { //管理员
            fragment = new ManageHomeFragment();
            getMember();
        }else if (userType.equals("1")){  //员工
            fragment = new EmployeeHomeFragment();
            getMember();
        }else {  //会员
            fragment = new MemberHomeFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("userid", userid);
        fragment.setArguments(bundle);

        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //这里需要统一修改
    private void init() {
        //根据不同的需要显示不同的Fragment
        frame = (LinearLayout)findViewById(R.id.frame);

        //根据不同的身份信息需要隐藏一个button，且注意更换button的text
        one = (Button)findViewById(R.id.one);
        one.setOnClickListener(this);
        two = (Button)findViewById(R.id.two);
        two.setOnClickListener(this);
        three = (Button)findViewById(R.id.three);
        three.setOnClickListener(this);

        four = (Button)findViewById(R.id.four);
        four.setOnClickListener(this);
        if(userType.equals("0")||userType.equals("1")){  //如果是非管理员登录，则隐藏改按钮
            four.setVisibility(View.GONE);
        }else{
            four.setText(getResources().getString(R.string.employee_management));
        }
        five = (Button)findViewById(R.id.five);
        five.setOnClickListener(this);
        five.setText(getResources().getString(R.string.personal));

        if(userType.equals("2")||userType.equals("1")) {
            one.setText(getResources().getString(R.string.home_page));
            two.setText(getResources().getString(R.string.record));
            three.setText(getResources().getString(R.string.employee));
        } else {
            one.setText(getResources().getString(R.string.shop));
            two.setText(getResources().getString(R.string.collect));
            three.setText(getResources().getString(R.string.record));
        }
    }

    @Override
    public void onClick(View v) {
        transaction =fragmentManager.beginTransaction();
        switch (v.getId()){
            case R.id.one:
                if(userType.equals("2")){ //管理员
                    fragment = new ManageHomeFragment();
                }else if (userType.equals("1")){  //员工
                    fragment = new EmployeeHomeFragment();
                }else {  //会员
                    fragment = new MemberHomeFragment();
                }
                break;
            case R.id.two:
                if(userType.equals("2") || userType.equals("1")){ //管理员
                    Bundle bundle = new Bundle();
                    bundle.putString("userid", userid);
                    bundle.putString("employeename", employeename);
                    fragment.setArguments(bundle);
                    fragment = new RecordFragment();
                } else {  //会员
                    fragment = new MemCollectFragment();
                }
                break;
            case R.id.three:
                if(userType.equals("2") || userType.equals("1")){ //管理员
                    fragment = new MemManageFragment();
                } else {  //会员
                    fragment = new MemRecordFragment();
                }
                break;
            case R.id.four:
                fragment = new EmployeeManageFragment();
                break;
            case R.id.five:
                if(userType.equals("2")){ //管理员
                    fragment = new ManagerFragment();
                }else if (userType.equals("1")){  //员工
                    fragment = new EmployeeFragment();
                }else {  //会员
                    fragment = new MemFragment();
                }
                break;
        }
        transaction.replace(R.id.frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getMember(){
        String kvs[] = new String[]{userid, store_id, "", "", "", "0", "10"};
        String param = MemberManagement.packagingParam(this, kvs);
        final Set<String> set = new HashSet<>();

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for(int i=0; i<jsonArray.length(); i++){
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
            }
        }, param);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            switch (requestCode){
                case MemManageFragment.REQUEST_CODE:
                    fragmentManager =getFragmentManager();
                    transaction =fragmentManager.beginTransaction();
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                case EmployeeManageFragment.ADD_REQUEST_CODE:
                    break;
            }
        }
    }
}
