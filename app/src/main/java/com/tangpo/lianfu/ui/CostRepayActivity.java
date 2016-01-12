package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.CostRepayAdapter;
import com.tangpo.lianfu.entity.Cost;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CostBack;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by 果冻 on 2016/1/5.
 */
public class CostRepayActivity extends Activity implements View.OnClickListener {
    private Button back;
    private LinearLayout money;
    private boolean f1 = false;
    private LinearLayout state;
    private boolean f2 = false;
    private LinearLayout time;
    private boolean f3 = false;

    private PullToRefreshListView listView;
    private CostRepayAdapter adapter;
    private ArrayList<Cost> list = new ArrayList<>();
    private Gson gson = new Gson();

    private String userid = null;
    private String storeid = null;
    private int page = 1;
    private int paramcentcount;

    private String backstate = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.money:
                if (list.size() > 0) {
                    if (f1) {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<Cost>() {
                            @Override
                            public int compare(Cost lhs, Cost rhs) {
                                float f1 = Float.parseFloat(lhs.getCost());
                                float f2 = Float.parseFloat(rhs.getCost());
                                if (f1 > f2) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        });
                    } else {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<Cost>() {
                            @Override
                            public int compare(Cost lhs, Cost rhs) {
                                float f1 = Float.parseFloat(lhs.getCost());
                                float f2 = Float.parseFloat(rhs.getCost());
                                if (f1 > f2) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        });
                    }
                    adapter.notifyDataSetInvalidated();
                }
                break;
            case R.id.state:
                if (list.size() > 0) {
                    if (f2) {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<Cost>() {
                            @Override
                            public int compare(Cost lhs, Cost rhs) {
                                return lhs.getBackstate().compareTo(rhs.getBackstate());
                            }
                        });
                    } else {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<Cost>() {
                            @Override
                            public int compare(Cost lhs, Cost rhs) {
                                return rhs.getBackstate().compareTo(lhs.getBackstate());
                            }
                        });
                    }
                    adapter.notifyDataSetInvalidated();
                }
                break;
            case R.id.time:
                if (list.size() > 0) {
                    if (f3) {
                        f3 = !f3;
                        Collections.sort(list, new Comparator<Cost>() {
                            @Override
                            public int compare(Cost lhs, Cost rhs) {
                                return Tools.CompareDate(lhs.getBackdate(), rhs.getBackdate());
                            }
                        });
                    } else {
                        f3 = !f3;
                        Collections.sort(list, new Comparator<Cost>() {
                            @Override
                            public int compare(Cost lhs, Cost rhs) {
                                return Tools.CompareDate(rhs.getBackdate(), lhs.getBackdate());
                            }
                        });
                    }
                    adapter.notifyDataSetInvalidated();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cost_repay);
        userid = getIntent().getStringExtra("userid");
        storeid = getIntent().getStringExtra("storeid");
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        money = (LinearLayout) findViewById(R.id.money);
        money.setOnClickListener(this);
        state = (LinearLayout) findViewById(R.id.state);
        state.setOnClickListener(this);
        time = (LinearLayout) findViewById(R.id.time);
        time.setOnClickListener(this);

        getCostList(backstate);

        listView = (PullToRefreshListView) findViewById(R.id.list);
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
                page = 1;
                list.clear();
                // 下拉的时候刷新数据
                int flags = DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL;

                String label = DateUtils.formatDateTime(
                        CostRepayActivity.this,
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                getCostList(backstate);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if(page<=paramcentcount){
                    getCostList(backstate);
                }else{
                    Tools.showToast(CostRepayActivity.this,getString(R.string.alread_last_page));
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           listView.onRefreshComplete();
                        }
                    },500);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CostRepayActivity.this, CostRepayDetailActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("storeid", storeid);
                intent.putExtra("costid", list.get(position-1).getCost_id());
                startActivity(intent);
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    list = (ArrayList<Cost>) msg.obj;
                    adapter = new CostRepayAdapter(CostRepayActivity.this, list);
                    listView.setAdapter(adapter);
                    listView.getRefreshableView().setSelection((page - 1) * 10 + 1);
                    break;
            }
        }
    };

    private void getCostList(String backstate) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs = new String[]{userid, storeid, page + "", "10", backstate};
        String param = CostBack.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                listView.onRefreshComplete();
                try {
                    paramcentcount=Integer.valueOf(result.getString("paramcentcount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray array = result.getJSONArray("param");
                    JSONObject object = null;
                    for (int i = 0; i<array.length(); i++) {
                        object = array.getJSONObject(i);
                        Cost cost = gson.fromJson(object.toString(), Cost.class);
                        list.add(cost);
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
                //
                listView.onRefreshComplete();
                try {
                    if ("404".equals(result.getString("status"))){
                        Tools.showToast(getApplicationContext(), "无数据");
                    } else if ("10".equals(result.getString("status"))){
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else {
                        Tools.showToast(getApplicationContext(), result.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

}
