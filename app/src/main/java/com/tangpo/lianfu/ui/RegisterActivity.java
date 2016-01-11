package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CheckCode;
import com.tangpo.lianfu.parms.GetCode;
import com.tangpo.lianfu.utils.Escape;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class RegisterActivity extends Activity implements OnClickListener {

    private Button back;
    private Button next;

    private EditText nation;
    private EditText phone_Num;
    private EditText code;

    private Button get_code;
    private ProgressDialog pd = null;

    private Timer timer=null;
    private TimerTask task=null;
    private int i=60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);

        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
        finish();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        nation = (EditText) findViewById(R.id.nation);
        phone_Num = (EditText) findViewById(R.id.phone_num);
        code = (EditText) findViewById(R.id.code);
        get_code = (Button) findViewById(R.id.get_code);
        get_code.setOnClickListener(this);
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if((int)msg.obj==0){
                stopTime();
                get_code.setText(getString(R.string.reget_check_code));
                get_code.setClickable(true);
                get_code.setBackgroundColor(Color.parseColor("#FF6A6A"));
                i=60;
            }else{
                get_code.setText(msg.obj + getString(R.string.second));
                startTime();
            }
        }
    };

    public void startTime(){
        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                i--;
                Message msg=mHandler.obtainMessage();
                msg.obj=i;
                mHandler.sendMessage(msg);
            }
        };
        timer.schedule(task, 1000);
    }

    public void stopTime(){
        timer.cancel();
    }

    private void getCode() {
        String phone = phone_Num.getText().toString();
        if (phone.equals("")) {
            ToastUtils.showToast(RegisterActivity.this, getString(R.string.phone_num_cannot_be_null), Toast.LENGTH_LONG);
            return;
        } else if(!Tools.isMobileNum(phone)) {
            ToastUtils.showToast(RegisterActivity.this, "电话号码不存在", Toast.LENGTH_LONG);
            return;
        }
        String kvs[] = new String[]{phone};
        String params = GetCode.packagingParam(kvs);

        pd = ProgressDialog.show(RegisterActivity.this, getString(R.string.send_message), getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                ToastUtils.showToast(RegisterActivity.this, getString(R.string.message_send_success), Toast.LENGTH_LONG);
                startTime();
                get_code.setClickable(false);
                get_code.setBackgroundColor(Color.GRAY);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                try {
                    String status = result.getString("status");
                    if (status.equals("1")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.format_error), Toast.LENGTH_LONG);
                    } else if (status.equals("10")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.server_exception), Toast.LENGTH_LONG);
                    } else {
                        ToastUtils.showToast(RegisterActivity.this, result.getString("info"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    private void checkCode() {
        final String phone = phone_Num.getText().toString();
        if (phone.equals("")) {
            //pd.dismiss();
            ToastUtils.showToast(RegisterActivity.this, getString(R.string.phone_num_cannot_be_null), Toast.LENGTH_LONG);
            return;
        }
        String check_code = code.getText().toString();
        if (check_code.equals("")) {
            //pd.dismiss();
            ToastUtils.showToast(RegisterActivity.this, getString(R.string.check_code_cannot_be_null), Toast.LENGTH_LONG);
            return;
        }
        String kvs[] = new String[]{phone, check_code};
        String params = CheckCode.packagingParam(kvs);

        System.out.println(Escape.unescape(params));

        pd = ProgressDialog.show(RegisterActivity.this, getString(R.string.checking_code), getString(R.string.please_wait));
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                pd.dismiss();
                System.out.println(result.toString());
                Configs.cachePhoneNum(RegisterActivity.this, phone);
                Intent intent = new Intent(RegisterActivity.this, PersonalMsgActivity.class);
                intent.putExtra("tel", phone);
                Tools.gatherActivity(RegisterActivity.this);
                startActivity(intent);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                pd.dismiss();
                try {
                    String status = result.getString("status");
                    if (status.equals("1")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.code_error), Toast.LENGTH_LONG);
                    } else if (status.equals("2")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.code_invalid), Toast.LENGTH_LONG);
                    } else if (status.equals("10")) {
                        ToastUtils.showToast(RegisterActivity.this, getString(R.string.server_exception), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SharedPreferences preferences = getSharedPreferences(Configs.APP_ID, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(Configs.KEY_TOKEN);
                editor.commit();
               Tools.gotoActivity(RegisterActivity.this, MainActivity.class);
//                RegisterActivity.this.finish();
                break;
            case R.id.next:
                if(!Tools.checkLAN()) {
                    Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
                    return;
                }
                checkCode();
                break;
            case R.id.get_code:
                if(!Tools.checkLAN()) {
                    Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
                    return;
                }
                getCode();
                break;
        }
    }
}
