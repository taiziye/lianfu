package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ComputeProfitAdapter;
import com.tangpo.lianfu.entity.Profit;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.ProfitManagement;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class OfflineProfitPayActivity extends Activity implements View.OnClickListener {

    private Button back;
//    private Button offline;
//    private Button online;
    private Button compute;

    private PullToRefreshListView listView;

    private CheckBox select_all;

    private TextView money;

    private ComputeProfitAdapter adapter = null;
    private List<Profit> list = new ArrayList<>();
    private int checkNum;

    private Gson gson = new Gson();

    private String userid = null;
    private String store_id = null;
    private int page = 1;
    private Map<Integer, String> set = new HashMap<Integer, String>();

    private double tmp = 0;

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.offline_profit_pay_activity);

        userid = getIntent().getExtras().getString("userid");
        store_id = getIntent().getExtras().getString("storeid");

        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
//        offline = (Button) findViewById(R.id.offline);
//        offline.setOnClickListener(this);
//        online = (Button) findViewById(R.id.online);
//        online.setOnClickListener(this);
        compute = (Button) findViewById(R.id.compute);
        compute.setOnClickListener(this);
        listView= (PullToRefreshListView) findViewById(R.id.list);

        getProfitPay();

        select_all = (CheckBox) findViewById(R.id.select_all);
        select_all.setOnClickListener(this);

        money = (TextView) findViewById(R.id.money);
        money.setText(0 + "");

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
                tmp=0;
                money.setText(tmp + "");
                select_all.setChecked(false);

                // 下拉的时候刷新数据
                int flags = DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL;

                String label = DateUtils.formatDateTime(
                        OfflineProfitPayActivity.this,
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                getProfitPay();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                select_all.setChecked(false);
                getProfitPay();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ComputeProfitAdapter.ViewHolder holder = (ComputeProfitAdapter.ViewHolder) view.getTag();
                //改变CheckBox的状态
                holder.check.toggle();

                //将CheckBox的选中状态记录下来
                adapter.getIsSelected().put(position - 1, holder.check.isChecked());
                //调整选定的条目
                if (holder.check.isChecked() == true) {
                    tmp += Double.parseDouble(list.get(position - 1).getProfit());
                    set.put(position - 1, list.get(position - 1).getId());
                    checkNum++;
                } else {
                    tmp -= Double.parseDouble(list.get(position - 1).getProfit());
                    set.remove(position - 1);
                    checkNum--;
                }

                if(checkNum == list.size()) {
                    select_all.setChecked(true);
                } else {
                    if(checkNum == 0) tmp = 0;
                    select_all.setChecked(false);
                }
                money.setText(tmp + "");
            }
        });
    }

    private void dataChanged(){
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            /*case R.id.offline:
                offline.setBackgroundColor(Color.WHITE);
                online.setBackgroundColor(Color.GRAY);
                break;
            case R.id.online:
                online.setBackgroundColor(Color.WHITE);
                offline.setBackgroundColor(Color.GRAY);
                break;*/
            case R.id.compute:
//                Compute();
                if(tmp==0){
                    ToastUtils.showToast(this,getString(R.string.pay_amount_cannot_be_null), Toast.LENGTH_SHORT);
                    return;
                }
                Intent intent=new Intent(OfflineProfitPayActivity.this,SelectPayMethod.class);
                Bundle bundle=new Bundle();
                bundle.putString("user_id",userid);
                bundle.putString("store_id",store_id);
                bundle.putString("total_fee",tmp+"");
                bundle.putString("consume_id",getConsumeId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.select_all:
                if (select_all.isChecked()) {
                    tmp=0;
                    set.clear();
                    for (int i = 0; i < list.size(); i++) {
                        adapter.getIsSelected().put(i, true);
                        set.put(i,list.get(i).getId());
                        tmp += Double.parseDouble(list.get(i).getProfit());
                    }
                    checkNum = list.size();
                    money.setText(tmp + "");
                    dataChanged();
                }else{
                    for(int i=0;i<list.size();i++){
                        if(adapter.getIsSelected().get(i)){
                            adapter.getIsSelected().put(i,false);
                        }
                    }
                    tmp=0;
                    set.clear();
                    checkNum=0;
                    money.setText(tmp+"");
                    dataChanged();
                }
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    list = (List<Profit>) msg.obj;
                    adapter = new ComputeProfitAdapter(OfflineProfitPayActivity.this, list);
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

    private void getProfitPay() {
        String kvs[] = new String[]{userid, store_id, "", "", "", page + "", "10"};
        String param = ProfitManagement.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                listView.onRefreshComplete();
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Profit profit = gson.fromJson(object.toString(), Profit.class);
                        list.add(profit);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(adapter != null) adapter.setIsSelected(list.size());

                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                mHandler.sendMessage(msg);

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                listView.onRefreshComplete();
                try {
                    Tools.handleResult(OfflineProfitPayActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    private String getConsumeId() {
        String str = "";
        for (int i = 0; i < set.size(); i++) {
            if (i != 0)
                str += ",";
            str += set.get(i);
        }
        return str;
    }
}
