package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ChatAdapter;

/**
 * Created by 果冻 on 2015/12/15.
 */
public class ChatActivity extends Activity implements View.OnClickListener {
    private Button back;
    private TextView name;
    private Button add;
    private PullToRefreshListView list;
    private ImageView expression;
    private ImageView add_img;
    private EditText chat;
    private Button send;

    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        name = (TextView) findViewById(R.id.name);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);
        list = (PullToRefreshListView) findViewById(R.id.list);
        expression = (ImageView) findViewById(R.id.expression);
        expression.setOnClickListener(this);
        add_img = (ImageView) findViewById(R.id.add_img);
        add_img.setOnClickListener(this);
        chat = (EditText) findViewById(R.id.chat);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add:
                break;
            case R.id.expression:
                break;
            case R.id.add_img:
                break;
            case R.id.send:
                break;
        }
    }
}
