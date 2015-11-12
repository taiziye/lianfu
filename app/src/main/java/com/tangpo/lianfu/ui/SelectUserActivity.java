package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.llb.util.PullToRefreshListView;
import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class SelectUserActivity extends Activity implements View.OnClickListener {

    private Button cancel;

    private EditText search_text;

    private PullToRefreshListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_user_activity);

        init();
        //如果search_text不为空则改变cancel为搜索
        if(search_text.getText().toString().length() != 0) {
            cancel.setText("搜索");
        }else {
            cancel.setText(getResources().getString(R.string.search));
        }
    }

    private void init() {
        cancel = (Button)findViewById(R.id.cancel);

        search_text = (EditText)findViewById(R.id.search_text);

        list = (PullToRefreshListView)findViewById(R.id.list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if (search_text.getText().toString().length() != 0) {
                    //搜索
                }else{
                    finish();
                }
                break;
        }
    }
}
