package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.MemberManagement;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class SelectUserActivity extends Activity implements View.OnClickListener {

    private Button cancel;

    private EditText search_text;

    private PullToRefreshListView listView;

    private ArrayAdapter<String> adapter = null;
    private List<String> list = null;
    private List<Member> listMem = null;
    private ProgressDialog dialog = null;
    private UserEntity user = null;
    private int page = 0;
    private Gson gson = new Gson();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.deleteActivity(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_user_activity);

        Tools.gatherActivity(this);

        user = (UserEntity) getIntent().getExtras().getSerializable("user");

        init();
        //如果search_text不为空则改变cancel为搜索
        if (search_text.getText().toString().length() != 0) {
            cancel.setText("搜索");
        } else {
            cancel.setText(getResources().getString(R.string.search));
        }
    }

    private void init() {
        list = new ArrayList<>();
        listMem = new ArrayList<>();

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        cancel = (Button) findViewById(R.id.cancel);

        search_text = (EditText) findViewById(R.id.search_text);

        listView = (PullToRefreshListView) findViewById(R.id.list);

        getMemberList();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("user", listMem.get(position - 1));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 0;
                list.clear();
                getMemberList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getMemberList();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                if (search_text.getText().toString().length() != 0) {
                    //搜索
                } else {
                    finish();
                }
                break;
        }
    }

    private void getMemberList() {
        String kvs[] = new String[]{user.getUser_id(), user.getStore_id(), "", "", "", page + "", "10"};
        String param = MemberManagement.packagingParam(this, kvs);
        final Set<String> set = new HashSet<>();

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Member member = gson.fromJson(object.toString(), Member.class);
                        list.add(member.getUsername());
                        listMem.add(member);
                        set.add(object.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Configs.cacheMember(SelectUserActivity.this, set);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                dialog.dismiss();
            }
        }, param);
    }
}
