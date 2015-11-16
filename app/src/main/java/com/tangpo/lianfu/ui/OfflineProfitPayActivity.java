package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class OfflineProfitPayActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button offline;
    private Button online;
    private Button compute;

    private PullToRefreshListView list;

    private CheckBox select_all;

    private TextView money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.offline_profit_pay_activity);

        init();
    }

    private void init() {
        back = (Button)findViewById(R.id.back);
        offline = (Button)findViewById(R.id.offline);
        online = (Button)findViewById(R.id.online);
        compute = (Button)findViewById(R.id.compute);

        list = (PullToRefreshListView)findViewById(R.id.list);

        select_all = (CheckBox)findViewById(R.id.select_all);

        money = (TextView)findViewById(R.id.money);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.offline:
                break;
            case R.id.online:
                break;
            case R.id.compute:
                break;
            case R.id.select_all:
                break;
        }
    }
}
