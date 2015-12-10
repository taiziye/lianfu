package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.OAuthBind;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/12/9.
 */
public class Boundlianfu extends Activity{
    private Button back;
    private Button submit;
    private EditText username;
    private EditText pass;
    private ProgressDialog pd=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relate);

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boundlianfu.this.finish();
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
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        SharedPreferences preferences=getSharedPreferences(Configs.APP_ID,MODE_PRIVATE);
        String openid=preferences.getString(Configs.KEY_OPENID, "");
        String logintype=preferences.getString(Configs.KEY_LOGINTYPE,"");
        String kvs[]=new String[]{user,pwd,openid,logintype};
        String params= OAuthBind.packagingParam(this,kvs);
        pd=ProgressDialog.show(this,getString(R.string.connecting),getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                try {
                    Configs.cleanData(Boundlianfu.this);
                    JSONObject jsonObject = result.getJSONObject("param");
                    String sessid = jsonObject.getString("session_id");
                    Configs.cacheToken(getApplicationContext(), sessid);
                    Configs.cacheUser(getApplicationContext(), jsonObject.toString());
                    //System.out.println(Escape.unescape(result.toString()));
                    Intent intent = new Intent(Boundlianfu.this, HomePageActivity.class);
                    startActivity(intent);
                    Boundlianfu.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                try {
                    String status=result.getString("status");
                    if(status.equals("2")){
                        ToastUtils.showToast(Boundlianfu.this,getString(R.string.username_or_pwd_error), Toast.LENGTH_SHORT);
                    }else if(status.equals("3")){
                        ToastUtils.showToast(Boundlianfu.this,getString(R.string.account_not_exist),Toast.LENGTH_SHORT);
                    }else {
                        ToastUtils.showToast(Boundlianfu.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }
}
