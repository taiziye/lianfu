package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/12/9.
 */
public class RelateActivity extends Activity{
    private Button back;
    private Button submit;
    private EditText username;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relate);

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelateActivity.this.finish();
            }
        });
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relateAccount();
            }
        });
        username = (EditText) findViewById(R.id.user_name);
        pass = (EditText) findViewById(R.id.pass);
    }

    //关联账户
    private void relateAccount() {
        String user = username.getText().toString().trim();
        String pwd = pass.getText().toString().trim();

        if(user.length() == 0){
            Tools.showToast(getApplicationContext(), getString(R.string.account));
            return;
        }
        if (pwd.length() == 0) {
            Tools.showToast(getApplicationContext(), getString(R.string.pwd));
            return;
        }
    }
}
