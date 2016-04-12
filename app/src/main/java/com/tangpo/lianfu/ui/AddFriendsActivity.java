package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.HXUserAdapter;
import com.tangpo.lianfu.entity.HXUser;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetHXUser;
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

    private ListView list;

    private Gson gson;
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<HXUser> users = new ArrayList<>();

    private HXUserAdapter adapter;

    private String userid = "";
    private String flag = "";
    private String userName = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.search:
                String queryStr = query.getText().toString();
                getHXuser(queryStr);
                break;
            case R.id.clear:
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
        list = (ListView) findViewById(R.id.list);
        list.setVisibility(View.GONE);

        gson = new Gson();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    members = (ArrayList<Member>) msg.obj;
                    StringBuilder id = new StringBuilder();
                    for (int i=0; i<members.size(); i++){
                        if(i!=0) id.append(",");
                        id.append(members.get(i).getUsername());
                    }
                    getHXuser(id.toString());
                    break;
                case 2:
                    users = (ArrayList<HXUser>) msg.obj;
                    adapter = new HXUserAdapter(AddFriendsActivity.this, users, userName);
                    list.setVisibility(View.VISIBLE);
                    list.setAdapter(adapter);
                    break;
            }
        }
    };

    /*private void getUserInfo(String query){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs;
        if(!Tools.isMobileNum(query)) kvs = new String[]{"","","",query,"1","10"};
        else kvs = new String[]{"","",query,"","1","10"};
        String param = GetUserList.packagingParam(getApplicationContext(), kvs);


        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
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
                msg.obj=members;
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
    }*/

    private void getHXuser(String username){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs=new String[]{username};
        String param = GetHXUser.packagingParam(getApplicationContext(), kvs);

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
                msg.obj = users;
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
