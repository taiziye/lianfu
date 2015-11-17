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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.PositionAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class MemberHomeFragment extends Fragment implements View.OnClickListener {

    private Button double_code;
    private Button locate;
    private Button map;

    private EditText search;

    private PullToRefreshListView listView;

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private SharedPreferences preferences;

    private PositionAdapter adapter = null;

    private ArrayList<FindStore> storeList = new ArrayList<>();

    private Gson gson = null;

    private String userid = null;
    private String lng = "0.000000";
    private String lat = "0.000000";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_home_fragment, container, false);
        bundle = getArguments();

        init(view);

        if (bundle != null) {
            userid = bundle.getString("userid");
            getStores();

            /*String tmp[] = new String []{userid};
            String tmpParams = CheckCollectedStore.packagingParam(getActivity(), tmp);

            new NetConnection(new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Set<String> store = new HashSet<String>();
                    JSONObject stores = null;
                    try {
                        stores = result.getJSONObject("param");
                        System.out.println(result.toString());
                        store.add(stores.toString());
                        Configs.cacheCollectedStore(getActivity(), store);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new NetConnection.FailCallback() {
                @Override
                public void onFail(JSONObject result) {
                    //
                }
            }, tmpParams);*/
        }

        return view;
    }

    private void init(View view) {
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        double_code = (Button) view.findViewById(R.id.double_code);
        locate = (Button) view.findViewById(R.id.locate);
        map = (Button) view.findViewById(R.id.map);

        search = (EditText) view.findViewById(R.id.search);

        listView = (PullToRefreshListView) view.findViewById(R.id.list);

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);

        gson = new Gson();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.double_code:
                break;
            case R.id.locate:
                break;
            case R.id.map:
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putParcelableArrayListExtra("list", storeList);
                startActivity(intent);
                break;
            case R.id.search:
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ArrayList<FindStore> list = (ArrayList<FindStore>) msg.obj;

                    Log.e("tag", "tag = " + list.get(0).getAddress());
                    Log.e("tag", storeList.size() + "size");
                    adapter = new PositionAdapter(getActivity(), list);
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

    private void getStores() {
        String kvs[] = new String[]{lng, lat, userid};

        String params = com.tangpo.lianfu.parms.FindStore.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();

                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        FindStore store = gson.fromJson(object.toString(), FindStore.class);
                        storeList.add(store);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = storeList;
                mHandler.sendMessage(msg);

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    if (result.getString("status").equals("9")) {
                        ToastUtils.showToast(getActivity(), getString(R.string.login_timeout), Toast.LENGTH_SHORT);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                    } else if (result.getString("status").equals("10")) {
                        ToastUtils.showToast(getActivity(), getString(R.string.server_exception), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }
}
