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
import android.widget.EditText;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.MemberCollectAdapter;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.entity.MemRecord;
import com.tangpo.lianfu.entity.MemberCollect;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CheckCollectedStore;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemCollectFragment extends Fragment implements View.OnClickListener {

    private Button locate;
    private Button map;

    private EditText search;

    private PullToRefreshListView listView;
    private MemberCollectAdapter adapter = null;
    private List<FindStore> list = new ArrayList<>();

    private ProgressDialog dialog = null;
    private SharedPreferences preferences = null;

    private String userid;

    private Gson gson = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.closeActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mem_collect_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userid = bundle.getString("userid");
        }
        init(view);
        return view;
    }

    private void init(View view) {
        gson = new Gson();
        getCollectStore();

        locate = (Button) view.findViewById(R.id.locate);
        locate.setOnClickListener(this);
        map = (Button) view.findViewById(R.id.map);
        map.setOnClickListener(this);

        search = (EditText) view.findViewById(R.id.search);
        search.setOnClickListener(this);

        listView = (PullToRefreshListView) view.findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("tag", "index " + position);
                Intent intent=new Intent(getActivity(),ShopActivity.class);
                intent.putExtra("store",list.get(position-1));
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locate:
                break;
            case R.id.map:
                break;
            case R.id.search:
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                list = (List<FindStore>) msg.obj;
                Log.e("tag", "size " + list.size());
                adapter = new MemberCollectAdapter(getActivity(), list, userid);
                listView.setAdapter(adapter);
            }
        }
    };

    private void getCollectStore() {
        if(!Tools.checkLAN()) {
            Log.e("tag", "check");
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String []{userid};
        String parm = CheckCollectedStore.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("tag", "collect " + result.toString());
                listView.onRefreshComplete();
                dialog.dismiss();
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        FindStore store = gson.fromJson(object.toString(), FindStore.class);
                        Log.e("tag", "collect " + store.toString());
                        list.add(store);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                listView.onRefreshComplete();
                dialog.dismiss();
                try {
                    Tools.handleResult(getActivity(), result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, parm);
    }
}
