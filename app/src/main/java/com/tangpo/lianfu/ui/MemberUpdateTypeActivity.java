package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemberUpdateTypeActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button edit;

    private PullToRefreshListView list;

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
        setContentView(R.layout.member_update_type_activity);

        Tools.gatherActivity(this);

        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        edit = (Button) findViewById(R.id.edit);

        back.setOnClickListener(this);
        edit.setOnClickListener(this);
        list = (PullToRefreshListView) findViewById(R.id.list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                ToastUtils.showToast(this,getString(R.string.new_function_has_not_online), Toast.LENGTH_SHORT);
                break;
        }
    }
}
