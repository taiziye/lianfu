package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.google.gson.Gson;
import com.tangpo.lianfu.MyApplication;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.broadcast.NewMessageReceiver;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Chat;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.fragment.EmployeeFragment;
import com.tangpo.lianfu.fragment.EmployeeHomeFragment;
import com.tangpo.lianfu.fragment.EmployeeManageFragment;
import com.tangpo.lianfu.fragment.ManageHomeFragment;
import com.tangpo.lianfu.fragment.ManagerFragment;
import com.tangpo.lianfu.fragment.MemCollectFragment;
import com.tangpo.lianfu.fragment.MemFragment;
import com.tangpo.lianfu.fragment.MemManageFragment;
import com.tangpo.lianfu.fragment.MemRecordFragment;
import com.tangpo.lianfu.fragment.MemberHomeFragment;
import com.tangpo.lianfu.fragment.RecordFragment;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetChatAccount;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class HomePageActivity extends FragmentActivity implements View.OnClickListener, EMEventListener {
    public static File cache;
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

    private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction transaction;
    private Fragment[] fragment;
    private int index = 0;
    private int currentIndex = 0;
    private SharedPreferences preferences;
    private String userType;
    private String userid = null;
    private String employeename = null;
    private String store_id = null;
    private String store_name = "";
    private Gson gson=null;
    private UserEntity userEntity;

    public String getUserid() {
        return userid;
    }

    public String getStore_id() {
        return store_id;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public String getEmployeename() {
        return employeename;
    }

    public String getStore_name() {
        return store_name;
    }

    public String getUserName() {
        return userEntity.getName();
    }

    @Override
    public void onClick(View v) {
        transaction = fragmentManager.beginTransaction();
        //transaction.remove(fragment);
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
//                    Bundle bundle = new Bundle();
//                    bundle.putString("userid", userid);
//                    bundle.putString("storeid", store_id);
//                    bundle.putSerializable("user", userEntity);
//                    fragment = new ManageHomeFragment();
//                    fragment.setArguments(bundle);
                    index = 0;
                } else if (userType.equals("2")) {  //员工
//                    Bundle bundle = new Bundle();
//                    bundle.putString("userid", userid);
//                    bundle.putString("storeid", store_id);
//                    bundle.putSerializable("user", userEntity);
//                    fragment = new EmployeeHomeFragment();
//                    fragment.setArguments(bundle);
                    index = 5;
                } else {  //会员
                    one_i.setImageResource(R.drawable.map_locate_r);
                    two_i.setImageResource(R.drawable.s_collect);
                    three_i.setImageResource(R.drawable.record);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("userid", userid);
//                    fragment = new MemberHomeFragment();
//                    fragment.setArguments(bundle);
                    index = 7;
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
//                    Bundle bundle = new Bundle();
//                    bundle.putString("userid", userid);
//                    bundle.putString("employeename", employeename);
//                    bundle.putString("username", userEntity.getName());
//                    bundle.putString("storename", store_name);
//                    fragment = new RecordFragment();
//                    fragment.setArguments(bundle);
                    index = 1;
                } else {  //会员
                    SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
                    String logintype=preferences.getString(Configs.KEY_LOGINTYPE,"");
                    if(logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
                        Intent intent=new Intent(HomePageActivity.this, BoundOrRegister.class);
                        startActivity(intent);
                    }else{
                        one_i.setImageResource(R.drawable.map_locate);
                        two_i.setImageResource(R.drawable.s_collect_r);
                        three_i.setImageResource(R.drawable.record);

//                        Bundle bundle = new Bundle();
//                        bundle.putString("userid", userid);
//                        fragment = new MemCollectFragment();
//                        fragment.setArguments(bundle);
                        index = 8;
                    }
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
//                    Bundle bundle = new Bundle();
//                    bundle.putString("userid", userid);
//                    bundle.putString("storeid", store_id);
//                    fragment = new MemManageFragment();
//                    fragment.setArguments(bundle);
                    index = 2;
                } else {  //会员
                    SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
                    String logintype=preferences.getString(Configs.KEY_LOGINTYPE,"");
                    if(logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
                        Intent intent=new Intent(HomePageActivity.this,BoundOrRegister.class);
                        startActivity(intent);
                    }else {
                        one_i.setImageResource(R.drawable.map_locate);
                        two_i.setImageResource(R.drawable.s_collect);
                        three_i.setImageResource(R.drawable.record_r);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("userid", userid);
//                        bundle.putString("storeid", store_id);
//                        fragment = new MemRecordFragment();
//                        fragment.setArguments(bundle);
                        index = 9;
                    }
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
                //fragment = new EmployeeManageFragment();
                index = 3;
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
                    //fragment = new ManagerFragment();
                    index = 4;
                } else if (userType.equals("2")) {  //员工
                    //fragment = new EmployeeFragment();
                    index = 6;
                } else {  //会员
                    one_i.setImageResource(R.drawable.map_locate);
                    two_i.setImageResource(R.drawable.s_collect);
                    three_i.setImageResource(R.drawable.record);

//                    Bundle bundle=new Bundle();
//                    bundle.putSerializable("user", userEntity);
//                    fragment = new MemFragment();
//                    fragment.setArguments(bundle);
                    index = 10;
                }
                break;
        }
        //transaction.replace(R.id.frame, fragment);
        //transaction.add(R.id.frame, fragment).show(fragment);
        //transaction.addToBackStack(null);
        //transaction.commit();
        /*if (currentIndex != index) {
            transaction.hide(fragment[currentIndex]);
            if (!fragment[index].isAdded()) {
                transaction.add(R.id.frame, fragment[index]);
            }
            transaction.show(fragment[index]).commit();
        }*/
        transaction.replace(R.id.frame, fragment[index]).commit();
        currentIndex = index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_page_activity);
        Tools.gatherActivity(this);
    }


    //这里需要统一修改
    private void init() {
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
        if (userType.equals("1") || userType.equals("2")||userType.equals("0")) {  //如果是非管理员登录，则隐藏改按钮
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
    public void onBackPressed() {
        if (currentIndex == 0 || currentIndex == 5 || currentIndex == 7) {
            //Tools.showToast(HomePageActivity.this, "再按一次返回键退出程序");
            exitBy2Click();
        } else {
            transaction = fragmentManager.beginTransaction();
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

            if ("3".equals(userType) || "4".equals(userType)) {
                index = 0;
                //transaction.replace(R.id.frame, fragment[0]).commit();
                //transaction.hide(fragment[currentIndex]).show(fragment[0]).commit();
            } else if ("2".equals(userType)) {
                index = 5;
                //transaction.replace(R.id.frame, fragment[5]).commit();
                //transaction.hide(fragment[currentIndex]).show(fragment[5]).commit();
            } else {
                index = 7;
                one_i.setImageResource(R.drawable.map_locate_r);
                two_i.setImageResource(R.drawable.s_collect);
                three_i.setImageResource(R.drawable.record);

                //transaction.replace(R.id.frame, fragment[7]).commit();
                //transaction.hide(fragment[currentIndex]).show(fragment[7]).commit();
            }
            transaction.replace(R.id.frame, fragment[index]).commit();
            currentIndex = index;
        }
//        Tools.closeActivity();
    }

    private static boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            Tools.showToast(HomePageActivity.this, "再按一次返回键退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            Tools.closeActivity();
            System.exit(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        transaction = fragmentManager.beginTransaction();
        if (data != null) {
            switch (requestCode) {
                case MemManageFragment.REQUEST_CODE:
                    fragment[currentIndex].onActivityResult(requestCode, resultCode, data);
                    break;
                case MemManageFragment.REQUEST_EDIT:
                    //transaction = fragmentManager.beginTransaction();
                    fragment[currentIndex].onActivityResult(requestCode, resultCode, data);
                    break;
                case EmployeeManageFragment.ADD_REQUEST_CODE:
                case EmployeeManageFragment.EDIT_REQUEST_CODE:
                    //transaction = fragmentManager.beginTransaction();
                    fragment[currentIndex].onActivityResult(requestCode, resultCode, data);
                    break;
                case RecordFragment.REQUEST_CODE:
                case RecordFragment.REQUEST_EDIT:
                    //fragmentManager = getFragmentManager();
                    //transaction = fragmentManager.beginTransaction();
                    fragment[currentIndex].onActivityResult(requestCode, resultCode, data);
                    break;
//                case ManagerFragment.REQUEST_CODE:
                default:
                    //fragmentManager=getFragmentManager();
                    //transaction=fragmentManager.beginTransaction();
//                    fragment.onActivityResult(requestCode,resultCode,data);
//                    break;
                    fragment[currentIndex].onActivityResult(requestCode, resultCode, data);
                    break;
            }
            transaction.commit();
        }
    }

    public void change(int n) {
        switch (n) {
            case 1:
                one_i.setImageResource(R.drawable.home_page);
                one_t.setTextColor(Color.BLACK);
                two_i.setImageResource(R.drawable.record_r);
                two_t.setTextColor(Color.RED);
                break;
            case 2:
                one_i.setImageResource(R.drawable.home_page);
                one_t.setTextColor(Color.BLACK);
                three_i.setImageResource(R.drawable.member_manage_r);
                three_t.setTextColor(Color.RED);
                break;
            case 3:
                one_i.setImageResource(R.drawable.home_page);
                one_t.setTextColor(Color.BLACK);
                four_i.setImageResource(R.drawable.employee_manage_r);
                four_t.setTextColor(Color.RED);
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ChatAccount ac = (ChatAccount) msg.obj;
                    ChatAccount.getInstance().copy(ac);
                    /*if (!EMChat.getInstance().isLoggedIn()) {
                        //未登录
                        login(ac);
                    }*/
                    login(ac);
                    break;
            }
        }
    };

    /**
     * 登陆
     */
    private void login(ChatAccount account) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(HomePageActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
        final long start = System.currentTimeMillis();
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(account.getEasemod_id(), account.getPwd(), new EMCallBack() {
            @Override
            public void onSuccess() {
                //
                //EMGroupManager.getInstance().loadAllGroups();
                dialog.dismiss();
            }

            @Override
            public void onError(int i, String s) {
                //
                dialog.dismiss();
            }

            @Override
            public void onProgress(int i, String s) {
                //
            }
        });
    }

    /**
     * 获取环信账号
     * @param id
     */
    private void getAccounts(String id) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs = new String[]{id};
        String param = GetChatAccount.packagingParam(getApplicationContext(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                ChatAccount account = null;
                try {
                    JSONArray array = result.getJSONArray("param");
                    for (int i = 0; i<array.length(); i++) {
                        account = gson.fromJson(array.getJSONObject(i).toString(), ChatAccount.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = account;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                try {
                    if ("3".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "用户不存在");
                    } else if ("10".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else {
                        Tools.showToast(getApplicationContext(), result.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //NewMessageReceiver.unregister(HomePageActivity.this);
        if (cache.exists()) {
            File[] files = cache.listFiles();
            for (File file : files) {
                file.delete();
            }
            cache.delete();
        }
        EMChatManager.getInstance().logout();
        Tools.closeActivity();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventNewMessage});
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //注销监听
                EMChatManager.getInstance().unregisterEventListener(HomePageActivity.this);
            }
        }, 5000);*/
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        if(!cache.exists()){
            cache.mkdirs();
        }

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
        //注册广播
        //NewMessageReceiver.register(HomePageActivity.this);
        fragment = new Fragment[]{new ManageHomeFragment(), new RecordFragment(), new MemManageFragment(), new EmployeeManageFragment(),
                new ManagerFragment(), new EmployeeHomeFragment(), new EmployeeFragment(), new MemberHomeFragment(), new MemCollectFragment(),
                new MemRecordFragment(), new MemFragment()};

        transaction = fragmentManager.beginTransaction();
        if(!userid.equals("游客")) getAccounts(userid);

        if (userType.equals("3") || userType.equals("4")) { //管理员
            //Bundle bundle = new Bundle();
            //bundle.putString("userid", userid);
            //bundle.putString("storeid", store_id);
            //bundle.putSerializable("user", userEntity);
            //fragment = new ManageHomeFragment();
            //fragment.setArguments(bundle);
            index = 0;
        } else if (userType.equals("2")) {  //员工
//            Bundle bundle = new Bundle();
//            bundle.putString("userid", userid);
//            bundle.putString("storeid", store_id);
//            bundle.putSerializable("user", userEntity);
            //fragment = new EmployeeHomeFragment();
            //fragment.setArguments(bundle);
            index = 5;
        } else {  //会员
//            Bundle bundle = new Bundle();
//            bundle.putString("userid", userid);
            //fragment = new MemberHomeFragment();
            //fragment.setArguments(bundle);
            index = 7;
        }

        //transaction.add(R.id.frame, fragment);
        //transaction.addToBackStack(null);
        //transaction.commit();
        /*if (currentIndex != index) {
            transaction.hide(fragment[currentIndex]);
            if (!fragment[index].isAdded()) {
                transaction.add(R.id.frame, fragment[index]);
            }
            transaction.show(fragment[index]).commit();
        }*/
        //transaction.add(R.id.frame, fragment[index]).show(fragment[index]).commit();
        transaction.replace(R.id.frame, fragment[index]).commit();
        currentIndex = index;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EMChatManager.getInstance().unregisterEventListener(HomePageActivity.this);
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        EMMessage message = null;
        EMConversation conversation = null;
        Chat chat;
        switch (event.getEvent()) {
            case EventOfflineMessage:
            case EventNewMessage:
                //message = (EMMessage) event.getData();
                message = (EMMessage) event.getData();
                String username = message.getFrom();
                conversation = EMChatManager.getInstance().getConversation(username);
                conversation.addMessage(message);
                String user = message.getUserName();
                String latestmsg = ((TextMessageBody)message.getBody()).getMessage();
                String time = Tools.long2DateString(message.getMsgTime());
                ChatAccount ac = new ChatAccount("", user, message.getUserName(), "", message.getFrom().toLowerCase(), "", "", ChatAccount.getInstance().getPhoto(), latestmsg, time);
                //Tools.saveAccount(ac);
                NewMessageReceiver.notifier(HomePageActivity.this, message, ac);
                break;
            //case EventNewMessage:
            //message = (EMMessage) event.getData();
            //conversation.addMessage(message);
            //break;
        }
    }
}
