package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.gson.Gson;
import com.llb.util.PullToRefreshListView;
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
import java.util.List;

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
    private List<Employee> list = null;

    private SharedPreferences preferences = null;
    private String userid = null;
    private String store_id = null;
    private Gson gson = null;

    private int page = 0;

    @Nullable
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
        return view;
    }

    private void init(View view){
        gson = new Gson();
        list = new ArrayList<>();

        search = (Button)view.findViewById(R.id.search);
        search.setOnClickListener(this);
        add = (Button)view.findViewById(R.id.add);
        add.setOnClickListener(this);
        listView = (PullToRefreshListView) view.findViewById(R.id.list);

        getEmployeeList();

        adapter = new EmployeeAdapter(getActivity(), list);

        listView.setAdapter(adapter);

        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                page = 0;
                getEmployeeList();
            }

            @Override
            public void onLoadMore() {
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

    private void getEmployeeList(){
        String kvs[] = new String[]{userid, store_id, page + "", "10"};
        String param = StaffManagement.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        Employee employee = gson.fromJson(object.toString(), Employee.class);
                        list.add(employee);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {

            }
        }, param);
    }
}
