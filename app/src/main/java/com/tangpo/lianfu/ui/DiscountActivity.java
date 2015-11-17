package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.DiscountAdapter;
import com.tangpo.lianfu.adapter.EmployeeAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.entity.Employee;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.ManageDiscount;

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
    private Button delete;
    private Button add;

    private TextView sum;

    private ListView listView;

    private DiscountAdapter adapter = null;

    private List<Discount> list = new ArrayList<>();

    private int index = 0;

    private SharedPreferences preferences;

    private String userid = null;
    private String store_id = null;

    private int page = 1;

    private Gson gson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dicount_activity);

        preferences=getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
        String user=preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject=new JSONObject(user);
            userid = jsonObject.getString("user_id");
            store_id = jsonObject.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        init();
    }

    private void init(){
        gson = new Gson();

        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(this);
        confirm = (Button)findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        delete = (Button)findViewById(R.id.delete);
        delete.setOnClickListener(this);
        add = (Button)findViewById(R.id.add);
        add.setOnClickListener(this);

        sum = (TextView)findViewById(R.id.sum);

        listView = (ListView)findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                adapter.setSelected(position);
                adapter.notifyDataSetChanged();
            }
        });

        /*listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                list.clear();
                getDiscount();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getDiscount();
            }
        });*/

        getDiscount();

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("discount", list.get(position));
                setResult(RESULT_OK, intent);
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.confirm:
                Intent intent = new Intent();
                intent.putExtra("discount", list.get(index));
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.delete:
                break;
            case R.id.add:
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
                    Log.e("tag", "list" + list.size());
                    adapter = new DiscountAdapter(DiscountActivity.this, list);
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

    private void getDiscount() {
        String kvs [] = new String []{userid, store_id, page + "", "10"};
        String param = ManageDiscount.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jsonArray = result.getJSONArray("param");
                    for(int i=0; i<jsonArray.length(); i++){
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

            }
        }, param);
    }
}
