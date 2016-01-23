package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ComputeProfitAdapter;
import com.tangpo.lianfu.entity.ProfitPay;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.ProfitPayRecord;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private LinearLayout name;
    private boolean f1 = false;
    private LinearLayout time;
    private boolean f2 = false;
    private LinearLayout repay;
    private boolean f3 = false;
    private LinearLayout status;
    private boolean f4 = false;
    private String flag = "";

    private RelativeLayout frame2;
    private ImageView search;


    private CheckBox select_all;
    private TextView money;
    private ComputeProfitAdapter adapter = null;
    private List<ProfitPay> list = new ArrayList<>();
    private int checkNum;
    private Gson gson = new Gson();

    private String userid = null;
    private String store_id = null;
    private int page = 1;
    private Map<Integer, String> set = new HashMap<Integer, String>();
    private ProgressDialog dialog = null;
    private double tmp = 0;

    private int paramcentcount;

    public static HashMap<Integer,Boolean> checkedItems=new HashMap<>();

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
        compute = (Button) findViewById(R.id.compute);
        compute.setOnClickListener(this);
        listView= (PullToRefreshListView) findViewById(R.id.list);


        search = (ImageView) findViewById(R.id.search);
        search.setOnClickListener(this);
        frame2 = (RelativeLayout) findViewById(R.id.frame1);

        getProfitPay(flag, "");

        select_all = (CheckBox) findViewById(R.id.select_all);
        select_all.setOnClickListener(this);
        name = (LinearLayout) findViewById(R.id.name);
        name.setOnClickListener(this);
        time = (LinearLayout) findViewById(R.id.time);
        time.setOnClickListener(this);
        repay = (LinearLayout) findViewById(R.id.repay);
        repay.setOnClickListener(this);
        status = (LinearLayout) findViewById(R.id.status);
        status.setOnClickListener(this);

        money = (TextView) findViewById(R.id.money);
        money.setText("0.00");

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
//                tmp=0.00f;
//                money.setText("0.00");
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

                getProfitPay( flag, "" );
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
//                tmp=0.00f;
//                money.setText("0.00");
                select_all.setChecked(false);
                if(page<=paramcentcount){
                    getProfitPay(flag, "");
                    //listView.onRefreshComplete();
                }else{
                    //listView.onRefreshComplete();
                    Tools.showToast(OfflineProfitPayActivity.this,getString(R.string.alread_last_page));
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listView.onRefreshComplete();
                        }
                    },500);
                }
                adapter.notifyDataSetChanged();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ComputeProfitAdapter.ViewHolder holder = (ComputeProfitAdapter.ViewHolder) view.getTag();
                //改变CheckBox的状态
                if("0".equals(list.get(position-1).getPay_status())){
                    holder.check.toggle();
                    //将CheckBox的选中状态记录下来
                    adapter.getIsSelected().put(position - 1, holder.check.isChecked());
                    checkedItems.put(position-1,holder.check.isChecked());
                    //调整选定的条目
                    if (holder.check.isChecked() == true) {
                        if(list.get(position - 1).getProfit() != null) tmp += Double.parseDouble(list.get(position - 1).getProfit());
                        set.put(position - 1, list.get(position - 1).getId());
                        checkNum++;
                    } else {
                        if(list.get(position - 1).getProfit() != null) tmp -= Double.parseDouble(list.get(position - 1).getProfit());
                        set.remove(position - 1);
                        checkNum--;
                    }

                    if(checkNum == list.size()) {
                        select_all.setChecked(true);
                    } else {
                        if(checkNum == 0) tmp = 0.00f;
                        select_all.setChecked(false);
                    }
                    money.setText(String.format("%.2f", tmp));
                }else{
                    Tools.showToast(OfflineProfitPayActivity.this,getString(R.string.please_choose_unpayed_order));
                }
            }
        });
    }

    private void dataChanged(){
        if(adapter==null){
            ToastUtils.showToast(this,getString(R.string.no_account_profit),Toast.LENGTH_SHORT);
            return;
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.compute:
//                Compute();
//                if(tmp==0){
//                    ToastUtils.showToast(this,getString(R.string.pay_amount_cannot_be_null), Toast.LENGTH_SHORT);
//                    return;
//                }
                if(tmp==0){
                    Tools.showToast(OfflineProfitPayActivity.this,getString(R.string.no_account_profit));
                    break;
                }
                Intent intent=new Intent(OfflineProfitPayActivity.this,SelectPayMethod.class);
                Bundle bundle=new Bundle();
                bundle.putString("user_id",userid);
                bundle.putString("store_id",store_id);
                bundle.putString("fee",money.getText().toString());
                bundle.putString("consume_id",getConsumeId());
                bundle.putString("paymode","1");
                bundle.putString("online","true");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.select_all:
                if (select_all.isChecked()) {
                    tmp=0.00f;
                    set.clear();
                    for (int i = 0; i < list.size(); i++) {
                        if("0".equals(list.get(i).getPay_status())){
                            adapter.getIsSelected().put(i, true);
                            checkedItems.put(i,true);
                            set.put(i, list.get(i).getId());
                            if(list.get(i).getProfit() != null)
                                tmp += Double.parseDouble(list.get(i).getProfit());
                        }
                    }
                    checkNum = list.size();
                    money.setText(String.format("%.2f", tmp));
                    dataChanged();
                }else{
                    for(int i=0;i<list.size();i++){
                        if(adapter.getIsSelected().get(i)){
                            adapter.getIsSelected().put(i,false);
                            checkedItems.put(i,false);
                        }
                    }
                    tmp=0.00f;
                    set.clear();
                    checkNum=0;
                    money.setText(String.format("%.2f", tmp));
                    dataChanged();
                }
                break;
            case R.id.name:
                if(list.size() > 0) {
                    if(f1) {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<ProfitPay>() {
                            @Override
                            public int compare(ProfitPay lhs, ProfitPay rhs) {
                                return lhs.getId().compareTo(rhs.getId());
                            }
                        });
                    } else {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<ProfitPay>() {
                            @Override
                            public int compare(ProfitPay lhs, ProfitPay rhs) {
                                return rhs.getId().compareTo(lhs.getId());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.time:
                if(list.size() > 0) {
                    if(f2) {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<ProfitPay>() {
                            @Override
                            public int compare(ProfitPay lhs, ProfitPay rhs) {
                                float f1 = Float.parseFloat(lhs.getFee());
                                float f2 = Float.parseFloat(rhs.getFee());
                                if (f1 > f2) {
                                    return 1;
                                } else
                                return -1;
                            }
                        });
                    } else {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<ProfitPay>() {
                            @Override
                            public int compare(ProfitPay lhs, ProfitPay rhs) {
                                float f1 = Float.parseFloat(lhs.getFee());
                                float f2 = Float.parseFloat(rhs.getFee());
                                if (f1 > f2) {
                                    return -1;
                                } else
                                    return 1;
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
                        Collections.sort(list, new Comparator<ProfitPay>() {
                            @Override
                            public int compare(ProfitPay lhs, ProfitPay rhs) {
                                float f1 = Float.parseFloat(lhs.getProfit());
                                float f2 = Float.parseFloat(rhs.getProfit());
                                if (f1 > f2) {
                                    return 1;
                                } else
                                    return -1;
                            }
                        });
                    } else {
                        f3 = !f3;
                        Collections.sort(list, new Comparator<ProfitPay>() {
                            @Override
                            public int compare(ProfitPay lhs, ProfitPay rhs) {
                                float f1 = Float.parseFloat(lhs.getProfit());
                                float f2 = Float.parseFloat(rhs.getProfit());
                                if (f1 > f2) {
                                    return -1;
                                } else
                                    return 1;
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.status:
                list.clear();
                setList();
                set.clear();
                select_all.setChecked(false);
                money.setText("0.00");
                break;
            case R.id.search:
                final EditText editText=new EditText(OfflineProfitPayActivity.this);
                editText.setHint(getString(R.string.please_input_username_or_tel));
                new AlertDialog.Builder(OfflineProfitPayActivity.this).setTitle(OfflineProfitPayActivity.this.getString(R.string.search_consume_record))
                        .setView(editText).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString().trim();
                        list.clear();
                        getProfitPay(flag,name);
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

    private void setList() {
        new AlertDialog.Builder(this).setItems(new String[]{"全部", "未支付", "已支付"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
                switch (which) {
                    case 0:
                        flag = "";
                        list.clear();
                        page=1;
                        break;
                    case 1:
                        flag = "0";
                        list.clear();
                        page=1;
                        break;
                    case 2:
                        flag = "1";
                        list.clear();
                        page=1;
                        break;
                }
                getProfitPay(flag, "");
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    list = (List<ProfitPay>) msg.obj;
                    adapter = new ComputeProfitAdapter(OfflineProfitPayActivity.this, list);
                    listView.setAdapter(adapter);
                    listView.getRefreshableView().setSelection((page - 1) * 10 + 1);
                    break;
            }
        }
    };

    private void getProfitPay(String status, String name) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String kvs[] = new String[]{userid, store_id, " ", status, name,"",page + "", "10"};
        String param = ProfitPayRecord.packagingParam(this, kvs);
        dialog = ProgressDialog.show(OfflineProfitPayActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
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
                        ProfitPay profitPay = gson.fromJson(object.toString(), ProfitPay.class);
                        list.add(profitPay);
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
                dialog.dismiss();
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
//        for (int i = 0; i < set.size(); i++) {
//            if (i != 0)
//                str += ",";
//            str += set.get(i);
//        }
        int first=1;
        for(Map.Entry<Integer,String> entry:set.entrySet()){
            if(first==1)first=0;
            else str+=",";
            str+=entry.getValue();
        }
        //Log.e("tag",str);
        return str;
    }
}
