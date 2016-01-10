package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ChatActivity extends Activity {
    public static ChatActivity activityInstance;
    private String toChatUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);

        toChatUsername = getIntent().getStringExtra("userid");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
