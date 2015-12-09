package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.PositionAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.entity.Store;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class MemberHomeFragment extends Fragment implements View.OnClickListener {

    private Button double_code;
    private ImageView locate;
    private Button map;

    private EditText search;

    private PullToRefreshListView listView;

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private PositionAdapter adapter = null;

    private ArrayList<FindStore> storeList = new ArrayList<>();
    private ArrayList<String> v = new ArrayList<>();

    private Gson gson = null;

    private String userid = null;
    private String lng = "0.000000";
    private String lat = "0.000000";

    private SharedPreferences preferences=null;

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Tools.closeActivity();
        getActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_home_fragment, container, false);
        bundle = getArguments();

        init(view);

        if (bundle != null) {
            userid = bundle.getString("userid");
            getCollectedStore();
            getStores();
        }

        return view;
    }

    private void init(View view) {
        double_code = (Button) view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        locate = (ImageView) view.findViewById(R.id.locate);
        locate.setOnClickListener(this);
        map = (Button) view.findViewById(R.id.map);
        map.setOnClickListener(this);

        double_code.setOnClickListener(this);
        locate.setOnClickListener(this);
        map.setOnClickListener(this);

        search = (EditText) view.findViewById(R.id.search);

        listView = (PullToRefreshListView) view.findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),ShopActivity.class);
                //intent.putExtra("store_id",storeList.get(position-1).getId());
                intent.putExtra("store",storeList.get(position-1));
                intent.putExtra("userid",userid);
                startActivity(intent);
            }
        });

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
                /*Intent intent = new Intent(getActivity(), MapActivity.class);
                Log.e("tag", storeList.size() + " " + storeList.getClass());
                Bundle bundle=new Bundle();
                bundle.putParcelableArrayList("list",storeList);
                intent.putExtras(bundle);
                startActivity(intent);*/
                Fragment fragment = new MapActivity();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("list",storeList);
                bundle.putString("userid", userid);
                fragment.setArguments(bundle);
                transaction.replace(R.id.frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
                    adapter = new PositionAdapter(getActivity(), list, v);
                    listView.setAdapter(adapter);
                    break;
                case 2:
                    v = (ArrayList<String>) msg.obj;
                    break;
            }
        }
    };

    private void getStores() {
        if(!Tools.checkLAN()) {
            Log.e("tag", "check");
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }
        preferences=getActivity().getSharedPreferences(Configs.APP_ID, Context.MODE_PRIVATE);
        lat=preferences.getFloat(Configs.KEY_LATITUDE,0.000000f)+"";
        lng=preferences.getFloat(Configs.KEY_LONGITUDE,0.000000f)+"";
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));

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
                    Tools.handleResult(getActivity(), result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    private void getCollectedStore(){
        if(!Tools.checkLAN()) {
            Log.e("tag", "check");
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }

        String kvs[] = new String[]{userid};
        String params = com.tangpo.lianfu.parms.CheckCollectedStore.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Store store = gson.fromJson(object.toString(), Store.class);
                        v.add(store.getId());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 2;
                msg.obj = v;
                mHandler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                try {
                    Tools.handleResult(getActivity(), result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }
}
