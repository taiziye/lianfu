package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.MemberCollectAdapter;
import com.tangpo.lianfu.entity.FindStore;
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
    private String centcount;
    private String userid;
    private int page = 1;

    private Gson gson = null;

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
        getCollectStore("");

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
                Intent intent=new Intent(getActivity(),ShopActivity.class);
                intent.putExtra("store",list.get(position-1));
                intent.putExtra("userid", userid);
                intent.putExtra("favorite", "1");
                getActivity().startActivity(intent);
            }
        });

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
                list.clear();
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
                getCollectStore("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //
                if (centcount != null && Integer.parseInt(centcount) >= page) {
                    //
                    Tools.showToast(getActivity(), "已全部加载完成");
                } else {
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
                    getCollectStore("");
                }
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
                adapter = new MemberCollectAdapter(getActivity(), list, userid);
                listView.setAdapter(adapter);
                if (Integer.parseInt(centcount) >= page ) {
                    //
                    Tools.showToast(getActivity(), "已全部加载完成");
                }
            }
        }
    };

    private void getCollectStore(String name) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String []{userid, name, "10", page + ""};
        String parm = CheckCollectedStore.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                listView.onRefreshComplete();
                dialog.dismiss();
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        FindStore store = gson.fromJson(object.toString(), FindStore.class);
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
