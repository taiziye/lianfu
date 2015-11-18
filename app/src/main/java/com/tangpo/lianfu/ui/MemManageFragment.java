package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.MemberAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.MemberManagement;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemManageFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_EDIT = 2;

    private Button search;
    private Button add;

    private PullToRefreshListView listView;

    private SharedPreferences preferences = null;
//    private Set<String> members = null;
    private MemberAdapter adapter = null;
    private List<Member> list = new ArrayList<>();
    private Gson gson = null;

    private String userid = null;
    private String store_id = null;

    private int page = 1;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.closeActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mem_manage_fragment, container, false);

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String user = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(user);
            userid = jsonObject.getString("user_id");
            store_id = jsonObject.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        init(view);
        return view;
    }

    private void init(View view) {
        gson = new Gson();
        search = (Button) view.findViewById(R.id.search);
        search.setOnClickListener(this);
        add = (Button) view.findViewById(R.id.add);
        add.setOnClickListener(this);

        /*Iterator<String> it = members.iterator();
        while (it.hasNext()) {
            Member member = gson.fromJson(it.next().toString(), Member.class);
            list.add(member);
        }*/

        getMembers();

        //adapter = new MemberAdapter(list, getActivity());

        listView = (PullToRefreshListView) view.findViewById(R.id.list);

//        listView.setAdapter(adapter);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                list.clear();
                getMembers();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getMembers();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("tag", "OnItemClickListener" + list.get(position - 1).toString());
                Intent intent = new Intent(getActivity(), MemberInfoActivity.class);
                intent.putExtra("member", list.get(position - 1));
                intent.putExtra("userid",userid);
                startActivityForResult(intent, REQUEST_EDIT);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                break;
            case R.id.add:
                Intent intent = new Intent(getActivity(), AddMemberActivity.class);
                intent.putExtra("userid", userid);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    list = (List<Member>) msg.obj;
                    adapter = new MemberAdapter(list, getActivity());
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

    private void getMembers() {
        String kvs[] = new String[]{userid, store_id, "", "", "", page + "", "10"};
        String param = MemberManagement.packagingParam(getActivity(), kvs);
        final Set<String> set = new HashSet<>();

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("tag", result.toString());
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Member member = gson.fromJson(object.toString(), Member.class);
                        list.add(member);
                        set.add(object.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                mHandler.sendMessage(msg);

                Configs.cacheMember(getActivity(), set);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
            }
        }, param);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQUEST_CODE) {
                Member member = (Member) data.getExtras().getSerializable("member");
                list.add(member);
                adapter.notifyDataSetChanged();
                Set<String> set = new HashSet<>();
                set.add(gson.toJson(member));
                Configs.cacheMember(getActivity(), set);
            } else {
                //编辑
            }
        }
    }
}
