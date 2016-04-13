package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.HXUserAdapter;
import com.tangpo.lianfu.entity.HXUser;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetHXUser;
import com.tangpo.lianfu.parms.GetUserList;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2016/3/30.
 */
public class AddFriendsActivity extends Activity implements View.OnClickListener {
    private Button back;
    private ImageView search;
    private EditText query;
    private Button clear;
    private ImageView img;
    private TextView id;
    private TextView name;

    private PullToRefreshListView list;

    private Gson gson;
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<HXUser> users = new ArrayList<>();

    private HXUserAdapter adapter;

    private String userid = "";
    private String flag = "";
    private String userName = "";
    private String queryStr;
    private int paramcentcount = 0;
    private int page = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.search:
                queryStr = query.getText().toString();
                getUserInfo(queryStr);
                break;
            case R.id.clear:
                query.setText("");
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.addfriends_activity);
        userid = getIntent().getStringExtra("userid");
        flag = getIntent().getStringExtra("flag");
        userName = getIntent().getStringExtra("name");
        init();
    }

    private void init(){
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        search = (ImageView) findViewById(R.id.search);
        search.setOnClickListener(this);
        query = (EditText) findViewById(R.id.query);
        clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(this);
        img = (ImageView) findViewById(R.id.img);
        id = (TextView) findViewById(R.id.id);
        name = (TextView) findViewById(R.id.name);
        list = (PullToRefreshListView) findViewById(R.id.list);
        list.setVisibility(View.GONE);

        list.setMode(PullToRefreshBase.Mode.BOTH);
        list.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
        list.getLoadingLayoutProxy(true, false).setPullLabel("");
        list.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
        list.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
        // 上拉加载更多时的提示文本设置
        list.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
        list.getLoadingLayoutProxy(false, true).setPullLabel("");
        list.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        list.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");

        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                users.clear();
                // 下拉的时候刷新数据
                int flags = DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL;

                String label = DateUtils.formatDateTime(
                        AddFriendsActivity.this,
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getUserInfo(queryStr);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if(page<=paramcentcount){
                    getUserInfo(queryStr);
                }else{
                    Tools.showToast(AddFriendsActivity.this,getString(R.string.alread_last_page));
                    list.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            list.onRefreshComplete();
                        }
                    },500);
                }
            }
        });

        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        gson = new Gson();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    users.clear();
                    //members.addAll((ArrayList<Member>) msg.obj);
                    //Log.e("tag", "size " + members.size());
                    StringBuilder id = new StringBuilder();
                    for (int i=0; i<members.size(); i++){
                        /*if(i!=0) id.append(",");
                        id.append(members.get(i).getUsername());*/
                        //Log.e("tag", "id " + members.get(i).getUsername());
                        getHXuser(members.get(i).getUsername());
                    }
                    //getHXuser(id.toString());
                    break;
                case 2:
                    //users.addAll((ArrayList<HXUser>) msg.obj);
                    adapter = new HXUserAdapter(AddFriendsActivity.this, users, userName);
                    list.setVisibility(View.VISIBLE);
                    list.setAdapter(adapter);
                    list.getRefreshableView().setSelection((page - 1) * 10 + 1);
                    members.clear();
                    break;
            }
        }
    };

    private void getUserInfo(String query){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs;
        if(Tools.isMobileNum(query)) kvs = new String[]{"","","",query,page + "","10"};
        else kvs = new String[]{"",query,"","","1","10"};
        String param = GetUserList.packagingParam(getApplicationContext(), kvs);


        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //Log.e("tag", "res " + result.toString());
                list.onRefreshComplete();
                members.clear();
                try {
                    paramcentcount=Integer.valueOf(result.getString("paramcentcount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray array = result.getJSONArray("param");
                    for(int i=0; i<array.length(); i++){
                        Member member = gson.fromJson(array.getJSONObject(i).toString(), Member.class);
                        members.add(member);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what=1;
                //msg.obj=members;
                handler.handleMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                list.onRefreshComplete();
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

    private void getHXuser(String username){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs=new String[]{username};
        String param = GetHXUser.packagingParam(getApplicationContext(), kvs);
        Log.e("tag", "param " + param);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray array = result.getJSONArray("param");
                    for (int i=0; i<array.length(); i++){
                        HXUser user = gson.fromJson(array.getJSONObject(i).toString(), HXUser.class);
                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 2;
                //msg.obj = users;
                handler.handleMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
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

}
