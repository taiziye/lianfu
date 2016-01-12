package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.DiscountManageAdapter;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.DeleteDiscount;
import com.tangpo.lianfu.parms.ManageDiscount;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class DiscountManageActivity extends Activity implements View.OnClickListener {

    private static final int EDIT_COUDE = 2;
    private Button back;
    private Button edit;
    private PullToRefreshListView listView;

    private UserEntity user = null;
    private DiscountManageAdapter adapter = null;
    private List<Discount> list = null;
    private int page = 1;
    private int paramcentcount;

    private Gson gson = null;
    private ProgressDialog dialog = null;
    private int index = 0;
    private LinearLayout add;
    private LinearLayout fragment;

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
        setContentView(R.layout.discount_manage_activity);
        user = (UserEntity) getIntent().getExtras().getSerializable("user");

        Tools.gatherActivity(this);

        init();
    }

    private void init() {
        list = new ArrayList<>();
        gson = new Gson();
        back = (Button) findViewById(R.id.back);
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(this);
        add = (LinearLayout) findViewById(R.id.add);
        add.setOnClickListener(this);
        fragment = (LinearLayout) findViewById(R.id.fragment);

        listView = (PullToRefreshListView) findViewById(R.id.list);

        getDiscount();

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
                        DiscountManageActivity.this,
                        System.currentTimeMillis(), flags);

                // 更新最后刷新时间
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                getDiscount();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if(page<=paramcentcount){
                    getDiscount();
                }else{
                    Tools.showToast(DiscountManageActivity.this,getString(R.string.alread_last_page));
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
                if(!isEdit) {
                    Intent intent = new Intent(getApplicationContext(), DiscountEditActivity.class);
                    index = position-1;
                    intent.putExtra("userid", user.getUser_id());
                    intent.putExtra("storeid", user.getStore_id());
                    intent.putExtra("discount", list.get(position - 1));
                    startActivityForResult(intent, EDIT_COUDE);
                } else {
                    index = position-1;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null && requestCode == EDIT_COUDE) {
            Discount discount = (Discount) data.getSerializableExtra("discount");
            list.remove(index);
            list.add(index, discount);
            adapter.notifyDataSetChanged();
        } else  if(data != null) {
            Discount dis = (Discount) data.getSerializableExtra("discount");
            list.add(dis);
            adapter = new DiscountManageAdapter(getApplicationContext(), list, user.getUser_id());
            listView.setAdapter(adapter);
        }
    }

    private boolean isEdit = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                /**
                 * 不知道是如何编辑的
                 */
                if(!isEdit){
                    adapter.setEdit(true);
                    adapter.notifyDataSetChanged();
                    isEdit = true;
                } else {
                    adapter.setEdit(false);
                    adapter.notifyDataSetChanged();
                    isEdit = false;
                }
                break;
            case R.id.delete:
                deleteDiscount();
                break;
            case R.id.add:
                Intent intent = new Intent(this, AddDiscountActivity.class);
                intent.putExtra("userid", user.getUser_id());
                intent.putExtra("storeid", user.getStore_id());
                startActivityForResult(intent, 1);
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    list = (List<Discount>) msg.obj;
                    adapter = new DiscountManageAdapter(DiscountManageActivity.this, list, user.getUser_id());
                    listView.setAdapter(adapter);
                    listView.getRefreshableView().setSelection((page - 1) * 10 + 1);
                    break;
            }
        }
    };

    private void getDiscount() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{user.getUser_id(), user.getStore_id(), page + "", "10"};
        String param = ManageDiscount.packagingParam(this, kvs);

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
                        Discount discount = gson.fromJson(object.toString(), Discount.class);
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
                dialog.dismiss();
                listView.onRefreshComplete();
                try {
                    Tools.handleResult(DiscountManageActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    private void deleteDiscount() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        if(index == -1) {
            Tools.showToast(getApplicationContext(), getString(R.string.please_choose_discount_to_delete));
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{user.getUser_id(), list.get(index).getId()};

        String param = DeleteDiscount.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                list.remove(list.get(index));
                adapter = new DiscountManageAdapter(DiscountManageActivity.this, list, user.getUser_id());
                listView.setAdapter(adapter);
                Tools.showToast(DiscountManageActivity.this, getString(R.string.delete_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(DiscountManageActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
