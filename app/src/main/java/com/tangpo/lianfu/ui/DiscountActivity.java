package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.llb.util.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.DiscountAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Discount;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.ManageDiscount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private PullToRefreshListView listView;

    private DiscountAdapter adapter = null;

    private List<Discount> list = null;

    private int index = 0;

    private SharedPreferences preferences;

    private String userid = null;
    private String store_id = null;

    private int page = 0;

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
        confirm = (Button)findViewById(R.id.confirm);
        delete = (Button)findViewById(R.id.delete);
        add = (Button)findViewById(R.id.add);

        sum = (TextView)findViewById(R.id.sum);

        listView = (PullToRefreshListView)findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                index = position;
                adapter.setChecked(position);
                listView.setAdapter(adapter);
            }
        });

        listView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                list.clear();
                getDiscount();
            }

            @Override
            public void onLoadMore() {
                page ++;
                getDiscount();
            }
        });

        getDiscount();

        adapter = new DiscountAdapter(this, list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.confirm:
                Intent intent = new Intent();
                intent.putExtra("type", list.get(index).getType());
                intent.putExtra("discount", list.get(index).getDicount());
                this.setResult(ConsumeRecordActivity.REQUEST_CODE, intent);
                break;
            case R.id.delete:
                break;
            case R.id.add:
                break;
        }
    }

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
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {

            }
        }, param);
    }
}
