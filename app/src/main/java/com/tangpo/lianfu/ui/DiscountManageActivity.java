package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.DiscountManageAdapter;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
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

    private Button back;
    private Button edit;
    private PullToRefreshListView listView;

    private UserEntity user = null;
    private DiscountManageAdapter adapter = null;
    private List<Discount> list = null;
    private int page = 0;
    private Gson gson = null;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.discount_manage_activity);
        user = (UserEntity) getIntent().getExtras().getSerializable("user");

        init();
    }

    private void init() {
        list = new ArrayList<>();
        gson = new Gson();
        back = (Button) findViewById(R.id.back);
        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(this);

        listView = (PullToRefreshListView) findViewById(R.id.list);

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        getDiscount();
        adapter = new DiscountManageAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 0;
                list.clear();
                getDiscount();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getDiscount();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                break;
        }
    }

    private void getDiscount() {
        String kvs[] = new String[]{user.getUser_id(), user.getStore_id(), page + "", "10"};
        String param = ManageDiscount.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
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
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    if (result.getString("status").equals("9")) {
                        Tools.showToast(getString(R.string.login_timeout));
                    } else {
                        Tools.showToast(getString(R.string.server_exception));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
