package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ConsumRecordAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.EmployeeConsumeRecord;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.ConsumeRecord;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_CODE = 5;
    public static final int REQUEST_EDIT = 6;

    private Button search;
    private Button edit;
    private Button add;

    private LinearLayout time;
    private boolean f1 = false;
    private LinearLayout money;
    private boolean f2 = false;
    private LinearLayout store;
    private boolean f3 = false;
    private LinearLayout profit;
    private boolean f4 = false;

    private PullToRefreshListView list;

    private ConsumRecordAdapter adapter = null;

    private List<EmployeeConsumeRecord> recordList = null;

    private String userid = "";
    private String username = "";

    private int page = 1;

    private Gson gson = new Gson();

    private int index = 0;

    private SharedPreferences preferences = null;

    private String store_id = null;
    private String store_name = "";

    private String employeename = null;

    private ProgressDialog dialog = null;

    private Intent intent = null;

    private boolean isEdit = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userid = bundle.getString("userid");
            employeename = bundle.getString("employeename");
            username = bundle.getString("username");
            store_name = bundle.getString("storename");
        }
        init(view);

        return view;
    }

    private void init(View view) {
        search = (Button) view.findViewById(R.id.search);
        search.setOnClickListener(this);
        edit = (Button) view.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        add = (Button) view.findViewById(R.id.add);
        add.setOnClickListener(this);
        time = (LinearLayout) view.findViewById(R.id.time);
        time.setOnClickListener(this);
        money = (LinearLayout) view.findViewById(R.id.money);
        money.setOnClickListener(this);
        store = (LinearLayout) view.findViewById(R.id.store);
        store.setOnClickListener(this);
        profit = (LinearLayout) view.findViewById(R.id.profit);
        profit.setOnClickListener(this);

        list = (PullToRefreshListView) view.findViewById(R.id.list);

        recordList = new ArrayList<>();
        getConsumeRecord();

        list.setMode(PullToRefreshBase.Mode.BOTH);
        list.getLoadingLayoutProxy(true, false).setLastUpdatedLabel("下拉刷新");
        list.getLoadingLayoutProxy(true, false).setPullLabel("");
        list.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新");
        list.getLoadingLayoutProxy(true, false).setReleaseLabel("放开以刷新");
        // 上拉加载更多时的提示文本设置
        list.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("上拉加载");
        list.getLoadingLayoutProxy(false, true).setPullLabel("");
        list.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        list.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");

        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                recordList.clear();
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
                getConsumeRecord();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                getConsumeRecord();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                index = position - 1;
                intent = new Intent(getActivity(), ConsumeRecordActivity.class);
                intent.putExtra("record", recordList.get(index));
                intent.putExtra("username", recordList.get(index).getUsername());
                intent.putExtra("user_id",userid);
                intent.putExtra("consume_id",recordList.get(index).getId());
                getActivity().startActivityForResult(intent, REQUEST_EDIT);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                break;
            case R.id.edit:
                if(!isEdit){
                    edit.setText(getString(R.string.cancel));
                    adapter.setEdit(true);
                    adapter.notifyDataSetChanged();
                    isEdit = true;
                } else {
                    edit.setText(getString(R.string.edit));
                    adapter.setEdit(false);
                    adapter.notifyDataSetChanged();
                    isEdit = false;
                }
                break;
            case R.id.add:
                intent = new Intent(getActivity(), AddConsumeActivity.class);
                getActivity().startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.time:
                if(recordList.size() > 0) {
                    if(f1) {
                        f1 = !f1;
                        Collections.sort(recordList, new Comparator<EmployeeConsumeRecord>() {
                            @Override
                            public int compare(EmployeeConsumeRecord lhs, EmployeeConsumeRecord rhs) {
                                return Tools.Compare(lhs.getConsume_date(), rhs.getConsume_date());
                            }
                        });
                    } else {
                        f1 = !f1;
                        Collections.sort(recordList, new Comparator<EmployeeConsumeRecord>() {
                            @Override
                            public int compare(EmployeeConsumeRecord lhs, EmployeeConsumeRecord rhs) {
                                return Tools.Compare(rhs.getConsume_date(), lhs.getConsume_date());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.money:
                if(recordList.size() > 0) {
                    if(f2) {
                        f2 = !f2;
                        Collections.sort(recordList, new Comparator<EmployeeConsumeRecord>() {
                            @Override
                            public int compare(EmployeeConsumeRecord lhs, EmployeeConsumeRecord rhs) {
                                float f1 = Float.parseFloat(lhs.getFee());
                                float f2 = Float.parseFloat(rhs.getFee());
                                if(f1 > f2)
                                    return 1;
                                else
                                    return -1;
                            }
                        });
                    } else {
                        f2 = !f2;
                        Collections.sort(recordList, new Comparator<EmployeeConsumeRecord>() {
                            @Override
                            public int compare(EmployeeConsumeRecord lhs, EmployeeConsumeRecord rhs) {
                                float f1 = Float.parseFloat(lhs.getFee());
                                float f2 = Float.parseFloat(rhs.getFee());
                                if(f1 > f2)
                                    return -1;
                                else
                                    return 1;
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.store:
                break;
            case R.id.profit:
                if(recordList.size() > 0) {
                    if(f4) {
                        f4 = !f4;
                        Collections.sort(recordList, new Comparator<EmployeeConsumeRecord>() {
                            @Override
                            public int compare(EmployeeConsumeRecord lhs, EmployeeConsumeRecord rhs) {
                                float f1 = Float.parseFloat(lhs.getDiscount());
                                float f2 = Float.parseFloat(rhs.getDiscount());
                                if(f1 > f2)
                                    return 1;
                                else
                                    return -1;
                            }
                        });
                    } else {
                        f4 = !f4;
                        Collections.sort(recordList, new Comparator<EmployeeConsumeRecord>() {
                            @Override
                            public int compare(EmployeeConsumeRecord lhs, EmployeeConsumeRecord rhs) {
                                float f1 = Float.parseFloat(lhs.getDiscount());
                                float f2 = Float.parseFloat(rhs.getDiscount());
                                if(f1 > f2)
                                    return -1;
                                else
                                    return 1;
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == REQUEST_CODE) {
                //新增
                EmployeeConsumeRecord record = (EmployeeConsumeRecord) data.getExtras().getSerializable("record");
                recordList.add(0, record);
                if(adapter != null) {
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("tag", "null");
                }
            } else {
                //编辑
                //getConsumeRecord();
                EmployeeConsumeRecord record = (EmployeeConsumeRecord) data.getExtras().getSerializable("record");
                recordList.remove(index);
                recordList.add(index, record);
                adapter.notifyDataSetInvalidated();
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    recordList = (List<EmployeeConsumeRecord>) msg.obj;
                    adapter = new ConsumRecordAdapter(recordList, getActivity(), store_id, employeename, userid, store_name);
                    list.setAdapter(adapter);
                    break;
            }
        }
    };

    private void getConsumeRecord() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String user = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(user);
            store_id = jsonObject.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String kvs[] = new String[]{store_id, "", "", "0", page+"","10"};
        String param = ConsumeRecord.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                list.onRefreshComplete();
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        EmployeeConsumeRecord record = gson.fromJson(object.toString(), EmployeeConsumeRecord.class);
                        recordList.add(record);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = recordList;
                mHandler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                list.onRefreshComplete();
                dialog.dismiss();
                try {
                    Tools.handleResult(getActivity(), result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
