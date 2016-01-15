package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.DiscountAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Dis;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetAlternativeDiscount;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class DiscountActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button confirm;
    private Button search;

    private PullToRefreshListView listView;

    private DiscountAdapter adapter = null;

    private List<Dis> list = new ArrayList<>();

    private int index = -1;

    private SharedPreferences preferences;

    private ProgressDialog dialog = null;

    private String userid = null;
    private String store_id = null;
    private int page = 1;
    private Gson gson = null;

    private int paramcentcount;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.deleteActivity(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dicount_activity);

        Tools.gatherActivity(this);

        preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
        String user = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(user);
            userid = jsonObject.getString("user_id");
            store_id = jsonObject.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        init();
    }

    private void init() {
        gson = new Gson();

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);

        search= (Button) findViewById(R.id.search);
        search.setOnClickListener(this);
        /*delete = (LinearLayout) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        add = (LinearLayout) findViewById(R.id.add);
        add.setOnClickListener(this);*/

       // sum = (TextView) findViewById(R.id.sum);
        getDiscount("");

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
                        DiscountActivity.this,
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                getDiscount("");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if (page <= paramcentcount) {
                    getDiscount("");
                } else {
                    Tools.showToast(DiscountActivity.this, getString(R.string.alread_last_page));
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
                index = position - 1;
                adapter.setSelected(position-1);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.confirm:
                if(index==-1){
                    ToastUtils.showToast(this,getString(R.string.please_choose_discount), Toast.LENGTH_SHORT);
                }else{

                        intent = new Intent();
                        intent.putExtra("discount", list.get(index));
                        setResult(RESULT_OK, intent);
                        finish();
                }
                break;
            case R.id.delete:
                //deleteDiscount();
                break;
            case R.id.add:
                intent = new Intent(this, AddDiscountActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("storeid", store_id);
                startActivityForResult(intent, 1);
                break;
            case R.id.search:

                final EditText editText=new EditText(DiscountActivity.this);
                editText.setHint(getString(R.string.please_input_discount_name));
                new AlertDialog.Builder(DiscountActivity.this).setTitle(DiscountActivity.this.getString(R.string.search_discount))
                        .setView(editText).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString().trim();
                        list.clear();
                        getDiscount(name);
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    list = (ArrayList<Dis>) msg.obj;
                    adapter = new DiscountAdapter(DiscountActivity.this, list);
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Dis dis = (Dis) data.getSerializableExtra("discount");
            list.add(dis);
            adapter = new DiscountAdapter(DiscountActivity.this, list);
            listView.setAdapter(adapter);
        }
    }

    /*private void deleteDiscount() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        if(index == -1) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_choose_discount_to_delete));
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{userid, list.get(index).getId()};

        String param = DeleteDiscount.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                list.remove(list.get(index));
                adapter = new DiscountAdapter(DiscountActivity.this, list);
                listView.setAdapter(adapter);
                Tools.showToast(DiscountActivity.this, getString(R.string.delete_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(DiscountActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }*/

    private void getDiscount(String name) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{userid, store_id,page+"","10",name};
        String param = GetAlternativeDiscount.packagingParam(this, kvs);

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
                        Dis discount = gson.fromJson(object.toString(), Dis.class);
                        list.add(discount);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                mHandler.sendMessage(msg);

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                listView.onRefreshComplete();
                dialog.dismiss();
            }
        }, param);
    }
}
