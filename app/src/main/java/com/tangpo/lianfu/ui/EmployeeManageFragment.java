package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.tangpo.lianfu.adapter.EmployeeAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Employee;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.StaffManagement;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class EmployeeManageFragment extends Fragment implements View.OnClickListener {
    public static final int ADD_REQUEST_CODE = 3;
    public static final int EDIT_REQUEST_CODE = 4;

    private Button search;
    private Button add;

    private PullToRefreshListView listView = null;
    private EmployeeAdapter adapter = null;
    private ArrayList<Employee> memList = new ArrayList<>();

    private SharedPreferences preferences = null;
    private String userid = null;
    private String store_id = null;
    private Gson gson = null;

    private int page = 1;

    private ProgressDialog dialog = null;

    private UserEntity userEntity=null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.closeActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_manage_fragment, container, false);

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String user = preferences.getString(Configs.KEY_USER, "0");

        init(view);
        UserEntity userEntity=gson.fromJson(user,UserEntity.class);
        userid=userEntity.getUser_id();
        store_id=userEntity.getStore_id();
        getEmployeeList();
        return view;
    }

    private void init(View view) {
        gson = new Gson();

        search = (Button) view.findViewById(R.id.search);
        search.setOnClickListener(this);
        add = (Button) view.findViewById(R.id.add);
        add.setOnClickListener(this);

        listView = (PullToRefreshListView) view.findViewById(R.id.emlist);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                memList.clear();
                page = 1;
                getEmployeeList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getEmployeeList();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EmploeeInfoActivity.class);
                intent.putExtra("employee", memList.get(position - 1));
                Log.e("tag", "id " + memList.get(position - 1).toString());
                intent.putExtra("userid", userid);
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                break;
            case R.id.add:
                Intent intent = new Intent(getActivity(), AddEmployeeActivity.class);
                intent.putExtra("userid", userid);
                startActivityForResult(intent, ADD_REQUEST_CODE);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    memList = (ArrayList<Employee>) msg.obj;
                    adapter = new EmployeeAdapter(getActivity(), memList);
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

    private void getEmployeeList() {
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{userid, store_id, page + "", "10"};
        String param = StaffManagement.packagingParam(getActivity(), kvs);

        Log.e("tag",param);
        final Set<String> set = new HashSet<>();
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Employee employee = gson.fromJson(object.toString(), Employee.class);

                        memList.add(employee);
                        set.add(object.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = memList;
                mHandler.sendMessage(msg);

                Configs.cacheEmployee(getActivity(), set);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
            }
        }, param);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ADD_REQUEST_CODE) {
                Employee employee = data.getExtras().getParcelable("employee");
                memList.add(employee);
                adapter.notifyDataSetChanged();
                getEmployeeList();
            } else {
                //编辑
            }
        }
    }
}
