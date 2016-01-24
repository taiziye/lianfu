package com.tangpo.lianfu.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.EmployeeAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Employee;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.StaffManagement;
import com.tangpo.lianfu.ui.AddEmployeeActivity;
import com.tangpo.lianfu.ui.EmploeeInfoActivity;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class EmployeeManageFragment extends Fragment implements View.OnClickListener {
    public static final int ADD_REQUEST_CODE = 3;
    public static final int EDIT_REQUEST_CODE = 4;

    private Button search;
    private Button add;
    private PullToRefreshListView listView = null;
    private EmployeeAdapter adapter = null;
    private ArrayList<Employee> memList = new ArrayList<>();
    private LinearLayout status;
    private boolean f1 = false;
    private LinearLayout manager;
    private boolean f2 = false;
    private LinearLayout name;
    private boolean f3 = false;
    private LinearLayout service;
    private boolean f4 = false;
    private SharedPreferences preferences = null;
    private String userid = null;
    private String store_id = null;
    private Gson gson = null;
    private int page = 1;
    private int paramcentcount;
    private int index = 0;
    private ProgressDialog dialog = null;
    private UserEntity userEntity=null;
    private View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.employee_manage_fragment, container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String user = preferences.getString(Configs.KEY_USER, "0");

        init(view);
        UserEntity userEntity=gson.fromJson(user,UserEntity.class);
        userid=userEntity.getUser_id();
        store_id=userEntity.getStore_id();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        memList.clear();
        getEmployeeList("");
    }

    private void init(View view) {
        gson = new Gson();

        search = (Button) view.findViewById(R.id.search);
        search.setOnClickListener(this);
        add = (Button) view.findViewById(R.id.add);
        add.setOnClickListener(this);

        listView = (PullToRefreshListView) view.findViewById(R.id.emlist);
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

        name = (LinearLayout) view.findViewById(R.id.name);
        name.setOnClickListener(this);

        manager = (LinearLayout) view.findViewById(R.id.manager);
        manager.setOnClickListener(this);

        service = (LinearLayout) view.findViewById(R.id.service);
        service.setOnClickListener(this);

        status = (LinearLayout) view.findViewById(R.id.status);
        status.setOnClickListener(this);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                memList.clear();
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
                getEmployeeList("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if(page<=paramcentcount){
                    getEmployeeList("");
                }else {
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EmploeeInfoActivity.class);
                index = position-1;
                intent.putExtra("employee", memList.get(position - 1));
                intent.putExtra("userid", userid);
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                final EditText editText=new EditText(getActivity());
                editText.setHint(getString(R.string.please_input_username_or_tel));
                new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(R.string.search_employee))
                        .setView(editText).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString().trim();
                        memList.clear();
                        getEmployeeList(name);
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
                Intent intent = new Intent(getActivity(), AddEmployeeActivity.class);
                intent.putExtra("userid", userid);
                startActivityForResult(intent, ADD_REQUEST_CODE);
                break;
            case R.id.name:
                if(memList.size() > 0) {
                    if(f1) {
                        f1 = !f1;
                        Collections.sort(memList, new Comparator<Employee>() {
                            @Override
                            public int compare(Employee lhs, Employee rhs) {
                                return lhs.getName().compareTo(rhs.getName());
                            }
                        });
                    } else {
                        f1 = !f1;
                        Collections.sort(memList, new Comparator<Employee>() {
                            @Override
                            public int compare(Employee lhs, Employee rhs) {
                                return rhs.getName().compareTo(lhs.getName());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.manager:
                if(memList.size() > 0) {
                    if(f2) {
                        f2 = !f2;
                        Collections.sort(memList, new Comparator<Employee>() {
                            @Override
                            public int compare(Employee lhs, Employee rhs) {
                                return lhs.getRank().compareTo(rhs.getRank());
                            }
                        });
                    } else {
                        f2 = !f2;
                        Collections.sort(memList, new Comparator<Employee>() {
                            @Override
                            public int compare(Employee lhs, Employee rhs) {
                                return rhs.getRank().compareTo(lhs.getRank());
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.service:
                if(memList.size() > 0) {
                    if(f3) {
                        f3 = !f3;
                        Collections.sort(memList, new Comparator<Employee>() {
                            @Override
                            public int compare(Employee lhs, Employee rhs) {
                                return lhs.getIsServer().compareTo(rhs.getIsServer());
                            }
                        });
                    } else {
                        f3 = !f3;
                        Collections.sort(memList, new Comparator<Employee>() {
                            @Override
                            public int compare(Employee lhs, Employee rhs) {
                                return rhs.getIsServer().compareTo(lhs.getIsServer());
                            }
                        });

                    }
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.status:
                if(memList.size() > 0) {
                    if(f4) {
                        f4 = !f4;
                        Collections.sort(memList, new Comparator<Employee>() {
                            @Override
                            public int compare(Employee lhs, Employee rhs) {
                                return lhs.getIsstop().compareTo(rhs.getIsstop());
                            }
                        });
                    } else {
                        f4 = !f4;
                        Collections.sort(memList, new Comparator<Employee>() {
                            @Override
                            public int compare(Employee lhs, Employee rhs) {
                                return rhs.getIsstop().compareTo(lhs.getIsstop());
                            }
                        });

                    }
                    adapter.notifyDataSetChanged();
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
                    memList.addAll((ArrayList<Employee>) msg.obj);
                    adapter = new EmployeeAdapter(getActivity(), memList);
                    listView.setAdapter(adapter);
                    listView.getRefreshableView().setSelection((page - 1) * 10 + 1);
                    break;
            }
        }
    };

    private void getEmployeeList(String name) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[]=new String[]{userid, store_id,"",name,"", page + "", "10"};
        String param = StaffManagement.packagingParam(getActivity(), kvs);

        final Set<String> set = new HashSet<>();
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                listView.onRefreshComplete();
                try {
                    paramcentcount= Integer.valueOf(result.getString("paramcentcount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<Employee> tmp = new ArrayList<>();
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Employee employee = gson.fromJson(object.toString(), Employee.class);

                        tmp.add(employee);
                        set.add(object.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = tmp;
                mHandler.sendMessage(msg);

                Configs.cacheEmployee(getActivity(), set);
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
        }, param);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ADD_REQUEST_CODE) {
                Employee employee = data.getExtras().getParcelable("employee");
                memList.add(0, employee);
                adapter.notifyDataSetChanged();
                //getEmployeeList();
            } else {
                //编辑
                Employee employee = data.getExtras().getParcelable("employee");
                memList.remove(index);
                memList.add(index, employee);
                adapter.notifyDataSetInvalidated();
            }
        }
    }
}
