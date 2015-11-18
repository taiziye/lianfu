package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.MemberAdapter;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CheckConsumeRecord;
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

    private int page = 1;
    private Gson gson = new Gson();
    private String user_id = null;

    private ProgressDialog dialog = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.closeActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mem_record_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            user_id = bundle.getString("userid");
            Log.e("tag", "userid=" + user_id);
        }
        init(view);
        return view;
    }

    private void init(View view) {
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        getConsumeRecord();
        listView = (PullToRefreshListView) view.findViewById(R.id.list);

        adapter = new MemberAdapter(list, getActivity());
        listView.setAdapter(adapter);

        dialog.dismiss();

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                list.clear();
                getConsumeRecord();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getConsumeRecord();
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    private void getConsumeRecord() {
        String kvs[] = new String[]{user_id, "10", page + ""};
        String param = CheckConsumeRecord.packagingParam(getActivity(), kvs);

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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                Tools.showToast(getActivity(), getString(R.string.server_exception));
            }
        }, param);
    }
}
