package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.DiscountAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Discount;
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
 * Created by 果冻 on 2015/11/8.
 */
public class DiscountActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button confirm;
    private LinearLayout delete;
    private LinearLayout add;

    private TextView sum;

    private ListView listView;

    private DiscountAdapter adapter = null;

    private List<Discount> list = new ArrayList<>();

    private int index = -1;

    private SharedPreferences preferences;

    private ProgressDialog dialog = null;

    private String userid = null;
    private String store_id = null;

    private int page = 1;

    private Gson gson = null;

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
        delete = (LinearLayout) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        add = (LinearLayout) findViewById(R.id.add);
        add.setOnClickListener(this);

        sum = (TextView) findViewById(R.id.sum);

        listView = (ListView) findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                adapter.setSelected(position);
                adapter.notifyDataSetChanged();
            }
        });
        getDiscount();

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.confirm:
                intent = new Intent();
                intent.putExtra("discount", list.get(index));
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.delete:
                deleteDiscount();
                break;
            case R.id.add:
                intent = new Intent(this, AddDiscountActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("storeid", store_id);
                startActivityForResult(intent, 1);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    list = (ArrayList<Discount>) msg.obj;
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
            Discount dis = (Discount) data.getSerializableExtra("discount");
            list.add(dis);
            adapter = new DiscountAdapter(DiscountActivity.this, list);
            listView.setAdapter(adapter);
        }
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
    }

    private void getDiscount() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), getString(R.string.network_has_not_connect));
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String kvs[] = new String[]{userid, store_id, page + "", "10"};
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

                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                mHandler.sendMessage(msg);

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
            }
        }, param);
    }
}
