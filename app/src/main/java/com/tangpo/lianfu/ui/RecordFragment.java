package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.tangpo.lianfu.adapter.ConsumRecordAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.ProfitManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private Button search;
    private Button edit;
    private Button add;

    private PullToRefreshListView list;

    private ConsumRecordAdapter adapter = null;

    private List<EmployeeConsumeRecord> recordList = null;

    private String userid = "";

    private int page = 1;

    private Gson gson = null;

    private int index = 0;

    private SharedPreferences preferences = null;

    private String store_id = null;

    private String employeename = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment, container, false);

        Bundle bundle = getArguments();
        if(bundle != null) {
            userid = bundle.getString("userid");
            employeename = bundle.getString("employeename");
        }
        init(view);

        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                recordList.clear();
                page = 1;
                getConsumeRecord();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getConsumeRecord();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                index = position;
            }
        });
        return view;
    }

    private void init(View view) {
        search = (Button)view.findViewById(R.id.search);
        edit = (Button)view.findViewById(R.id.edit);
        add = (Button)view.findViewById(R.id.add);

        list = (PullToRefreshListView)view.findViewById(R.id.list);

        recordList = new ArrayList<>();
        getConsumeRecord();
        adapter = new ConsumRecordAdapter(recordList, getActivity(), store_id, employeename);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
                break;
            case R.id.edit:
                Intent intent = new Intent(getActivity(), ConsumeRecordActivity.class);
                intent.putExtra("record", recordList.get(index));
                getActivity().startActivity(intent);
                break;
            case R.id.add:
                break;
        }
    }

    private void getConsumeRecord(){
        preferences=getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String user=preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject=new JSONObject(user);
            store_id = jsonObject.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String kvs[] = new String []{userid, store_id, "", "", "", page + "", "10"};
        String param = ProfitManagement.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        EmployeeConsumeRecord record = gson.fromJson(object.toString(), EmployeeConsumeRecord.class);
                        recordList.add(record);
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
