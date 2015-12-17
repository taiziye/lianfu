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
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.PositionAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.http.NetConnection;
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
    private ImageView start;
    private Button map;
    private EditText search;
    private PullToRefreshListView listView;
    private Bundle bundle = null;
    private ProgressDialog dialog = null;
    private PositionAdapter adapter = null;
    private ArrayList<FindStore> storeList = new ArrayList<>();
    private Gson gson = null;
    private String userid = null;
    private String lng = "0.000000";
    private String lat = "0.000000";
    private SharedPreferences preferences=null;
    private String hereabout = "0";
    private int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_home_fragment, container, false);
        bundle = getArguments();

        init(view);

        if (bundle != null) {
            userid = bundle.getString("userid");
            //getCollectedStore();
            getStores();
        }

        return view;
    }

    private void init(View view) {
        double_code = (Button) view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        locate = (ImageView) view.findViewById(R.id.locate);
        locate.setOnClickListener(this);
        start = (ImageView) view.findViewById(R.id.start);
        start.setOnClickListener(this);
        map = (Button) view.findViewById(R.id.map);
        map.setOnClickListener(this);

        /*double_code.setOnClickListener(this);
        locate.setOnClickListener(this);
        map.setOnClickListener(this);*/
        search = (EditText) view.findViewById(R.id.search);

        listView = (PullToRefreshListView) view.findViewById(R.id.list);

        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
        listView.getLoadingLayoutProxy(true, false).setPullLabel("");
        listView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
        listView.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
        // 上拉加载更多时的提示文本设置
        listView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
        listView.getLoadingLayoutProxy(false, true).setPullLabel("");
        listView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        listView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //
                storeList.clear();
                page = 1;
                // 下拉的时候刷新数据
                int flags = DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL;

                String label = DateUtils.formatDateTime(
                        getActivity(),
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getStores();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //
                page = page + 1;

                // 下拉的时候刷新数据
                int flags = DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL;
                String label = DateUtils.formatDateTime(
                        getActivity(),
                        System.currentTimeMillis(), flags);
                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getStores();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),ShopActivity.class);
                intent.putExtra("store",storeList.get(position-1));
                intent.putExtra("userid",userid);
                intent.putExtra("favorite", storeList.get(position - 1).getFavorite());
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
                if("0".equals(hereabout)) {
                    hereabout=1 + "";
                } else {
                    hereabout=0 + "";
                }
                break;
            case R.id.map:
                /*Intent intent = new Intent(getActivity(), MapActivity.class);
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
            case R.id.start:
                String str = search.getText().toString().trim();
                if (str.length() == 0) {
                    //
                    storeList.clear();
                    getStores();
                } else {
                    storeList.clear();
                    findStore(str);
                }
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    storeList = (ArrayList<FindStore>) msg.obj;
                    adapter = new PositionAdapter(getActivity(), storeList);
                    listView.setAdapter(adapter);
                    break;
                case 2:
                    break;
                case 3:
                    storeList = (ArrayList<FindStore>) msg.obj;
                    adapter = new PositionAdapter(getActivity(), storeList);
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

    private void findStore(String str) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{"", "", "", "", "10", "", str, "", ""};
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
                msg.what = 3;
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

    private void getStores() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }
        preferences=getActivity().getSharedPreferences(Configs.APP_ID, Context.MODE_PRIVATE);
        lat=preferences.getFloat(Configs.KEY_LATITUDE,0.000000f)+"";
        lng=preferences.getFloat(Configs.KEY_LONGITUDE,0.000000f)+"";

        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));


        String kvs[] = new String[]{lng, lat, userid, page + "", "10", hereabout, "", "", ""};
        String params = com.tangpo.lianfu.parms.FindStore.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                listView.onRefreshComplete();
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
                listView.onRefreshComplete();
                try {
                    Tools.handleResult(getActivity(), result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

//    private void getCollectedStore(){
//        if(!Tools.checkLAN()) {
//            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
//            return;
//        }
//
//        String kvs[] = new String[]{userid};
//        String params = com.tangpo.lianfu.parms.CheckCollectedStore.packagingParam(getActivity(), kvs);
//
//        new NetConnection(new NetConnection.SuccessCallback() {
//            @Override
//            public void onSuccess(JSONObject result) {
//                //
//                try {
//                    JSONArray jsonArray = result.getJSONArray("param");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject object = jsonArray.getJSONObject(i);
//                        Store store = gson.fromJson(object.toString(), Store.class);
//                        v.add(store.getId());
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                Message msg = new Message();
//                msg.what = 2;
//                msg.obj = v;
//                mHandler.sendMessage(msg);
//            }
//        }, new NetConnection.FailCallback() {
//            @Override
//            public void onFail(JSONObject result) {
//                //
//                try {
//                    Tools.handleResult(getActivity(), result.getString("status"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, params);
//    }
}
