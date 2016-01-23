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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.RepayAdapter;
import com.tangpo.lianfu.entity.Repay;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.PlatformRebateRecord;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/25.
 */
public class RepayActivity extends Activity implements View.OnClickListener {

    private RepayAdapter adapter;
    private Button back;
    private PullToRefreshListView listView;
    private List<Repay> list = new ArrayList<>();
    private String userid = "";

    private LinearLayout id;
    private boolean f1 = false;
    private LinearLayout fee;
    private boolean f2 = false;
    private LinearLayout repay;
    private boolean f3 = false;
    //private LinearLayout time;
    private boolean f4 = false;
    private Gson gson = new Gson();
    private int page = 1;
    private int paramcentcount;

    private Button search;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_repay);

        //从上一个页面获取userID
        userid = getIntent().getExtras().getString("userid");

        listView = (PullToRefreshListView) findViewById(R.id.list);

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepayActivity.this.finish();
            }
        });

        id = (LinearLayout) findViewById(R.id.id);
        id.setOnClickListener(this);
        fee = (LinearLayout) findViewById(R.id.fee);
        fee.setOnClickListener(this);
        repay = (LinearLayout) findViewById(R.id.repay);
        repay.setOnClickListener(this);

        search= (Button) findViewById(R.id.search);
        search.setOnClickListener(this);
        //time = (LinearLayout) findViewById(R.id.time);
        //time.setOnClickListener(this);

        getRepayList();

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
                        RepayActivity.this,
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                getRepayList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if(page<=paramcentcount){
                    getRepayList();
                }else{
                    Tools.showToast(RepayActivity.this,getString(R.string.alread_last_page));
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    list = (List<Repay>) msg.obj;
                    adapter = new RepayAdapter(RepayActivity.this, list);
                    listView.setAdapter(adapter);
                    listView.getRefreshableView().setSelection((page - 1) * 10 + 1);
            }
        }
    };

    private void getRepayList(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String kvs[] = new String[]{userid, "", page + "", "10"};
        String param = PlatformRebateRecord.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                listView.onRefreshComplete();

                try {
                    paramcentcount=Integer.valueOf(result.getString("paramcentcount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Repay repay = gson.fromJson(object.toString(), Repay.class);
                        list.add(repay);
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
                try {
                    Tools.handleResult(RepayActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id:
                if(list.size() > 0) {
                    if(f1) {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<Repay>() {
                            @Override
                            public int compare(Repay lhs, Repay rhs) {
                                float f1 = Float.parseFloat(lhs.getFee());
                                float f2 = Float.parseFloat(rhs.getFee());
                                if (f1 > f2) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        });
                    } else {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<Repay>() {
                            @Override
                            public int compare(Repay lhs, Repay rhs) {
                                float f1 = Float.parseFloat(lhs.getFee());
                                float f2 = Float.parseFloat(rhs.getFee());
                                if (f1 > f2) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.fee:
                if(list.size() > 0) {
                    if(f2) {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<Repay>() {
                            @Override
                            public int compare(Repay lhs, Repay rhs) {
                                return lhs.getPay_status().compareTo(rhs.getPay_status());
                            }
                        });
                    } else {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<Repay>() {
                            @Override
                            public int compare(Repay lhs, Repay rhs) {
                                return rhs.getPay_status().compareTo(lhs.getPay_status());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.repay:
                if(list.size() > 0) {
                    if(f3) {
                        f3 = !f3;
                        Collections.sort(list, new Comparator<Repay>() {
                            @Override
                            public int compare(Repay lhs, Repay rhs) {
                                return Tools.CompareDate(lhs.getPay_date(), rhs.getPay_date());
                            }
                        });
                    } else {
                        f3 = !f3;
                        Collections.sort(list, new Comparator<Repay>() {
                            @Override
                            public int compare(Repay lhs, Repay rhs) {
                                return Tools.CompareDate(rhs.getPay_date(), lhs.getPay_date());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.time:
                if(list.size() > 0) {
                    if(f4) {
                        f4 = !f4;
                        Collections.sort(list, new Comparator<Repay>() {
                            @Override
                            public int compare(Repay lhs, Repay rhs) {
                                return Tools.CompareDate(lhs.getConsume_date(), rhs.getConsume_date());
                            }
                        });
                    } else {
                        f4 = !f4;
                        Collections.sort(list, new Comparator<Repay>() {
                            @Override
                            public int compare(Repay lhs, Repay rhs) {
                                return Tools.CompareDate(rhs.getConsume_date(), lhs.getConsume_date());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;

            case R.id.search:
                final EditText editText=new EditText(RepayActivity.this);
                editText.setHint(getString(R.string.please_input_username_or_tel));
                new AlertDialog.Builder(RepayActivity.this).setTitle(RepayActivity.this.getString(R.string.search_repay_record))
                        .setView(editText).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString().trim();
                        list.clear();
                        getRepayList();
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
}
