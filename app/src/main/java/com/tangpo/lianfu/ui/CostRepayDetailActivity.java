package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.CostRepayDetailAdapter;
import com.tangpo.lianfu.entity.CostRepayDetail;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CostBackDetail;
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
public class CostRepayDetailActivity extends Activity implements OnClickListener {
    private Button back;
    private LinearLayout name;
    private boolean f1 = false;
    private LinearLayout money;
    private boolean f2 = false;
    private LinearLayout backmoney;
    private boolean f3 = false;
    private LinearLayout time;
    private boolean f4 = false;
    private PullToRefreshListView listView;
    private ArrayList<CostRepayDetail> list = new ArrayList<>();
    private CostRepayDetailAdapter adapter = null;
    private Gson gson = new Gson();

    private String userid = null;
    private String storeid = null;
    private String costid = null;
    private int page = 1;
    private int paramcentcount;

    private String nameStr = "";

    private Button search;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.name:
                if (list.size()>0) {
                    if (f1) {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<CostRepayDetail>() {
                            @Override
                            public int compare(CostRepayDetail lhs, CostRepayDetail rhs) {
                                return lhs.getName().compareTo(rhs.getName());
                            }
                        });
                    } else {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<CostRepayDetail>() {
                            @Override
                            public int compare(CostRepayDetail lhs, CostRepayDetail rhs) {
                                return rhs.getName().compareTo(lhs.getName());
                            }
                        });
                    }
                    adapter.notifyDataSetInvalidated();
                }
                break;
            case R.id.money:
                if (list.size()>0) {
                    if (f2) {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<CostRepayDetail>() {
                            @Override
                            public int compare(CostRepayDetail lhs, CostRepayDetail rhs) {
                                float f1 = Float.parseFloat(lhs.getCost());
                                float f2 = Float.parseFloat(rhs.getCost());
                                if (f1>f2){
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        });
                    } else {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<CostRepayDetail>() {
                            @Override
                            public int compare(CostRepayDetail lhs, CostRepayDetail rhs) {
                                float f1 = Float.parseFloat(lhs.getCost());
                                float f2 = Float.parseFloat(rhs.getCost());
                                if (f1>f2){
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
            case R.id.backmoney:
                if (list.size()>0) {
                    if (f3) {
                        f3 = !f3;
                        Collections.sort(list, new Comparator<CostRepayDetail>() {
                            @Override
                            public int compare(CostRepayDetail lhs, CostRepayDetail rhs) {
                                float f1 = Float.parseFloat(lhs.getBackcost());
                                float f2 = Float.parseFloat(rhs.getBackcost());
                                if (f1>f2){
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        });
                    } else {
                        f3 = !f3;
                        Collections.sort(list, new Comparator<CostRepayDetail>() {
                            @Override
                            public int compare(CostRepayDetail lhs, CostRepayDetail rhs) {
                                float f1 = Float.parseFloat(lhs.getBackcost());
                                float f2 = Float.parseFloat(rhs.getBackcost());
                                if (f1>f2){
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
            case R.id.time:
                if (list.size()>0) {
                    if (f4) {
                        f4 = !f4;
                        Collections.sort(list, new Comparator<CostRepayDetail>() {
                            @Override
                            public int compare(CostRepayDetail lhs, CostRepayDetail rhs) {
                                return Tools.CompareDate(lhs.getConsume_date(), rhs.getConsume_date());
                            }
                        });
                    } else {
                        f4 = !f4;
                        Collections.sort(list, new Comparator<CostRepayDetail>() {
                            @Override
                            public int compare(CostRepayDetail lhs, CostRepayDetail rhs) {
                                return Tools.CompareDate(rhs.getConsume_date(), lhs.getConsume_date());
                            }
                        });
                    }
                    adapter.notifyDataSetInvalidated();
                }
                break;
            case R.id.search:
                final EditText editText=new EditText(CostRepayDetailActivity.this);
                editText.setHint(getString(R.string.please_input_username_or_tel));
                new AlertDialog.Builder(CostRepayDetailActivity.this).setTitle(CostRepayDetailActivity.this.getString(R.string.search_cost_repay_record))
                        .setView(editText).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString().trim();
                        list.clear();
                        getCostDetailList(name);
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cost_repay_detail);
        userid = getIntent().getStringExtra("userid");
        storeid = getIntent().getStringExtra("storeid");
        costid = getIntent().getStringExtra("costid");
        init();
    }

    private void init(){
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        name = (LinearLayout) findViewById(R.id.name);
        name.setOnClickListener(this);
        money = (LinearLayout) findViewById(R.id.money);
        money.setOnClickListener(this);
        backmoney = (LinearLayout) findViewById(R.id.backmoney);
        backmoney.setOnClickListener(this);
        time = (LinearLayout) findViewById(R.id.time);
        time.setOnClickListener(this);

        search= (Button) findViewById(R.id.search);
        search.setOnClickListener(this);

        getCostDetailList(nameStr);

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
                        CostRepayDetailActivity.this,
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                getCostDetailList(nameStr);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if(page<=paramcentcount){
                    getCostDetailList(nameStr);
                }else{
                    Tools.showToast(CostRepayDetailActivity.this,getString(R.string.alread_last_page));
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                        }
                    },500);
                }
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    list = (ArrayList<CostRepayDetail>) msg.obj;
                    adapter = new CostRepayDetailAdapter(getApplicationContext(), list);
                    listView.setAdapter(adapter);
                    listView.getRefreshableView().setSelection((page - 1) * 10 + 1);
                    if (list.size() == 0) {
                        Tools.showToast(getApplicationContext(), "无数据");
                    }
                    break;
            }
        }
    };

    private void getCostDetailList(String name) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs = new String[]{userid, storeid,costid, page + "", "10", name};
        String param = CostBackDetail.packagingParam(getApplicationContext(), kvs);

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
                    for (int i=0; i<array.length(); i++) {
                        object = array.getJSONObject(i);
                        CostRepayDetail cost = gson.fromJson(object.toString(), CostRepayDetail.class);
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
                    if ("404".equals(result.getString("status"))) {
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
