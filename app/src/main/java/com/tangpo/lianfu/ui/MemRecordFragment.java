package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.llb.util.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.MemberAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.MemberManagement;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemRecordFragment extends Fragment implements View.OnClickListener {

    private PullToRefreshListView listView;
    private MemberAdapter adapter = null;
    private List<Member> list = new ArrayList<>();

    private int page = 0;
    private Gson gson = new Gson();
    private String user_id = null;
    private String store_id = null;

    private ProgressDialog dialog = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mem_record_fragment, container, false);

        Bundle bundle = getArguments();
        if(bundle != null) {
            user_id = bundle.getString("userid");
            store_id = bundle.getString("storeid");
        }
        init(view);
        return view;
    }

    private void init(View view) {
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        getMemberList();
        listView = (PullToRefreshListView)view.findViewById(R.id.list);

        adapter = new MemberAdapter(list, getActivity());
        listView.setAdapter(adapter);

        dialog.dismiss();

        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                list.clear();
                getMemberList();
            }

            @Override
            public void onLoadMore() {
                page ++;
                getMemberList();
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    private void getMemberList(){
        String kvs[] = new String[]{user_id, store_id, "", "", "", page + "", "10"};
        String param = MemberManagement.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        Member member = gson.fromJson(object.toString(), Member.class);
                        list.add(member);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                Tools.showToast(getString(R.string.server_exception));
            }
        }, param);
    }
}
