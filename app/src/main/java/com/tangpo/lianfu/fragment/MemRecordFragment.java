package com.tangpo.lianfu.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.MemRecourdAdapter;
import com.tangpo.lianfu.entity.MemRecord;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CheckConsumeRecord;
import com.tangpo.lianfu.ui.HomePageActivity;
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
public class MemRecordFragment extends Fragment implements View.OnClickListener {

    private PullToRefreshListView listView;
    private MemRecourdAdapter adapter = null;
    private List<MemRecord> list = new ArrayList<>();

    private LinearLayout time;
    private boolean f1 = false;
    private LinearLayout money;
    private boolean f2 = false;

    private int page = 1;
    private int paramcentcount;

    private Gson gson = new Gson();
    public static String user_id = null;

    private ProgressDialog dialog = null;

    private Button search;
    private Button add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (view == null) {
            view = inflater.inflate(R.layout.mem_record_fragment, container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        /*Bundle bundle = getArguments();
        if (bundle != null) {
            user_id = bundle.getString("userid");
        }*/
        user_id = ((HomePageActivity)getActivity()).getUserid();
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        getConsumeRecord("");
    }

    private void init(View view) {

        listView = (PullToRefreshListView) view.findViewById(R.id.list);
        time = (LinearLayout) view.findViewById(R.id.time);
        time.setOnClickListener(this);
        money = (LinearLayout) view.findViewById(R.id.money);
        money.setOnClickListener(this);

        search= (Button) view.findViewById(R.id.search);
        search.setOnClickListener(this);

        add= (Button) view.findViewById(R.id.add);
        add.setOnClickListener(this);

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
                        getActivity(),
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                getConsumeRecord("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if(page<=paramcentcount){
                    getConsumeRecord("");
                }else{
                    Tools.showToast(getActivity(),getString(R.string.alread_last_page));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time:
                if(list.size() > 0) {
                    if(f1) {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<MemRecord>() {
                            @Override
                            public int compare(MemRecord lhs, MemRecord rhs) {
                                return Tools.CompareDate(lhs.getDatetime(), rhs.getDatetime());
                            }
                        });
                    } else {
                        f1 = !f1;
                        Collections.sort(list, new Comparator<MemRecord>() {
                            @Override
                            public int compare(MemRecord lhs, MemRecord rhs) {
                                return Tools.CompareDate(rhs.getDatetime(), lhs.getDatetime());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.money:
                if(list.size() > 0) {
                    if(f2) {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<MemRecord>() {
                            @Override
                            public int compare(MemRecord lhs, MemRecord rhs) {
                                float f1 = Float.parseFloat(lhs.getFee());
                                float f2 = Float.parseFloat(rhs.getFee());
                                if(f1 > f2) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        });
                    } else {
                        f2 = !f2;
                        Collections.sort(list, new Comparator<MemRecord>() {
                            @Override
                            public int compare(MemRecord lhs, MemRecord rhs) {
                                float f1 = Float.parseFloat(lhs.getFee());
                                float f2 = Float.parseFloat(rhs.getFee());
                                if(f1 > f2) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;

            case R.id.search:
                final EditText editText=new EditText(getActivity());
                editText.setHint(getString(R.string.please_input_storename));
                new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(R.string.search_consume_record))
                        .setView(editText).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString().trim();
                        list.clear();
                        getConsumeRecord(name);
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;

            case R.id.add:
                Tools.showToast(getActivity(), getActivity().getString(R.string.ordinary_member_has_not_permission));
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                list = (List<MemRecord>) msg.obj;
                adapter = new MemRecourdAdapter(getActivity(), list);
                listView.setAdapter(adapter);
                listView.getRefreshableView().setSelection((page - 1) * 10 + 1);
            }
        }
    };

    private void getConsumeRecord(String name) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{user_id, name,page + "","10"};
        String param = CheckConsumeRecord.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                listView.onRefreshComplete();
                dialog.dismiss();
                try {
                    paramcentcount=Integer.valueOf(result.getString("paramcentcount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        MemRecord member = gson.fromJson(object.toString(), MemRecord.class);
                        list.add(member);
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
                    if("404".equals(result.getString("status"))){
                        Tools.showToast(getActivity(), "没有数据");
                    } else {
                        Tools.handleResult(getActivity(), result.getString("status"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
