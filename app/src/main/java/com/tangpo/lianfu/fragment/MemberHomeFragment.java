package com.tangpo.lianfu.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.PositionAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.ui.MapActivity;
import com.tangpo.lianfu.ui.MipcaActivityCapture;
import com.tangpo.lianfu.ui.ShopActivity;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class MemberHomeFragment extends Fragment implements View.OnClickListener {

    private final static int SCANNIN_STORE_INFO = 4;
    private final static int GET_STORE_INFO = 5;
    private Button double_code;
    private TextView locate;
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
    private String centcount;
    private int page = 1;
    private int paramcentcount;
    private boolean flag = false;  //判断是刷新还是加载数据 false为刷新  true为加载

    private Intent intent=null;
    private FindStore store=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_home_fragment, container, false);
        bundle = getArguments();

        init(view);
        hide();
        if (bundle != null) {
            userid = bundle.getString("userid");
            //getCollectedStore();
            SharedPreferences preferences=getActivity().getSharedPreferences(Configs.APP_ID, Context.MODE_PRIVATE);
            String logintype=preferences.getString(Configs.KEY_LOGINTYPE, null);
            if (logintype!=null){
                findStore("");
            }else{
                getStores();
            }
        }

        return view;
    }

    private void hide() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void init(View view) {
        double_code = (Button) view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        locate = (TextView) view.findViewById(R.id.locate);
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

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null) {
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //
                storeList.clear();
                page = 1;
                flag=false;
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
                page = page + 1;
                if(page<=paramcentcount){
                    getStores();
                }else{
                    Tools.showToast(getActivity(), getString(R.string.alread_last_page));
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                        }
                    }, 500);
                }
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
                intent=new Intent();
                intent.setClass(getActivity(),MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,SCANNIN_STORE_INFO);
                break;
            case R.id.locate:
                if("0".equals(hereabout)) {
                    hereabout=1 + "";
                } else {
                    hereabout=0 + "";
                }
                storeList.clear();
                getStores();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SCANNIN_STORE_INFO:
                if(resultCode==getActivity().RESULT_OK){
                    Bundle bundle=data.getExtras();
                    String result=bundle.getString("result");
                    //在这里处理返回来的store_id、service_center、referrer
                    String store_id= Uri.parse(result).getQueryParameter("store_id");
                    String service_center=Uri.parse(result).getQueryParameter("service_center");
                    String referrer=Uri.parse(result).getQueryParameter("referrer");

                    if(store_id!=null&&service_center!=null&&referrer!=null){
                        getStoreDetail(store_id,userid);
                    }
                }
        }
    }

    private void getStoreDetail(String store_id,String userid){
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{store_id, userid};
        String param = StoreDetail.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    store = gson.fromJson(result.getJSONObject("param").toString(),FindStore.class);
                    Message msg=new Message();
                    msg.what=GET_STORE_INFO;
                    msg.obj=store;
                    mHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        },param);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    storeList = (ArrayList<FindStore>) msg.obj;
                    adapter = new PositionAdapter(getActivity(), storeList);
                    /*if (!flag) listView.setAdapter(adapter);
                    else adapter.notifyDataSetInvalidated();*/
                    listView.setAdapter(adapter);
                    listView.getRefreshableView().setSelection((page - 1) * 10 + 1);
                    break;
                case 2:
                    break;
                case 3:
                    storeList = (ArrayList<FindStore>) msg.obj;
                    adapter = new PositionAdapter(getActivity(), storeList);
                    listView.setAdapter(adapter);
                    search.getText().clear();
                    if (centcount != null && Integer.parseInt(centcount) >= page) {
                        //
                        Tools.showToast(getActivity(), "已全部加载完成");
                    }
                    break;
                case GET_STORE_INFO:
                    FindStore store= (FindStore) msg.obj;
                    String favoriate="0";

                    Intent intent=new Intent(getActivity(),ShopActivity.class);
                    intent.putExtra("store",store);
                    intent.putExtra("userid",userid);
                    intent.putExtra("favorite",favoriate);
                    startActivity(intent);
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

        String kvs[] = new String[]{"", "", "", "1", "10", hereabout, str, "", ""};
        String params = com.tangpo.lianfu.parms.FindStore.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                listView.onRefreshComplete();
                try {
                    paramcentcount=Integer.valueOf(result.getString("paramcentcount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    centcount = result.getString("paramcentcount");
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
                listView.onRefreshComplete();
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
                    paramcentcount=Integer.valueOf(result.getString("paramcentcount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}
