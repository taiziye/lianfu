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
import com.tangpo.lianfu.adapter.EmployeeAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Employee;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.StaffManagement;

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
    public static final int ADD_REQUEST_CODE = 2;
    public static final int EDIT_REQUEST_CODE = 3;

    private Button search;
    private Button add;

    private PullToRefreshListView listView = null;
    private EmployeeAdapter adapter = null;
    private ArrayList<Employee> list = new ArrayList<>();

    private SharedPreferences preferences = null;
    private String userid = null;
    private String store_id = null;
    private Gson gson = null;

    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_manage_fragment, container, false);

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String user=preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject=new JSONObject(user);
            userid = jsonObject.getString("user_id");
            store_id = jsonObject.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        init(view);

        getEmployeeList();
        return view;
    }

    private void init(View view){
        gson = new Gson();

        search = (Button)view.findViewById(R.id.search);
        search.setOnClickListener(this);
        add = (Button)view.findViewById(R.id.add);
        add.setOnClickListener(this);

        listView = (PullToRefreshListView) view.findViewById(R.id.emlist);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                list.clear();
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
                intent.putExtra("employee", list.get(position));
                intent.putExtra("userid", userid);
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                    list = (ArrayList<Employee>) msg.obj;
                    Log.e("tag", "====++++++++====" + list.get(0).getRank());

                    adapter = new EmployeeAdapter(getActivity(), list);
                    listView.setAdapter(adapter);

                    /*adapter = new EmployeeAdapter(getActivity(), list);
                    listView.setAdapter(adapter);*/
                    break;
            }
        }
    };

    private void getEmployeeList(){
        String kvs[] = new String[]{userid, store_id, page + "", "10"};
        String param = StaffManagement.packagingParam(getActivity(), kvs);

        final Set<String> set = new HashSet<>();
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        System.out.println(object.toString());
                        Employee employee = gson.fromJson(object.toString(), Employee.class);
                        list.add(employee);
                        set.add(object.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list", list);
                msg.obj = list;
                msg.setData(bundle);
                mHandler.sendMessage(msg);

                Configs.cacheEmployee(getActivity(), set);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {

            }
        }, param);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        list.clear();
        getEmployeeList();
    }
}
