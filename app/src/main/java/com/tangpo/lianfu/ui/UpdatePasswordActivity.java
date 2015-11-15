package com.tangpo.lianfu.ui;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.utils.MD5Tool;
import com.tangpo.lianfu.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdatePasswordActivity extends ActionBarActivity {

    private EditText etOld;
    private EditText etNew;
    private EditText etNewCheck;
    private Button confirm;
    private Button cancel;
    private UserEntity user=null;
    private ProgressDialog dialog=null;

    private String newPassword=null;
    private String oldPassword=null;
    private String newPasswordCheck=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_password);
        init();
    }

    private void init(){
        etOld= (EditText) findViewById(R.id.etOld);
        etNew= (EditText) findViewById(R.id.etNew);
        etNewCheck= (EditText) findViewById(R.id.etNewCheck);
        oldPassword=etOld.getText().toString();
        newPassword=etOld.getText().toString();
        newPasswordCheck=etNewCheck.getText().toString();
        confirm= (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldPassword.equals("")) {
                    ToastUtils.showToast(UpdatePasswordActivity.this, getString(R.string.old_password_cannot_be_null), Toast.LENGTH_SHORT);
                    etNew.setText("");
                    etNewCheck.setText("");
                    return;
                }
                if (newPassword.equals("")) {
                    ToastUtils.showToast(UpdatePasswordActivity.this, getString(R.string.new_password_cannot_be_null), Toast.LENGTH_SHORT);
                    etOld.setText("");
                    etNewCheck.setText("");
                    return;
                }
                if (!newPassword.equals(etNewCheck)) {
                    ToastUtils.showToast(UpdatePasswordActivity.this, getString(R.string.new_password_do_not_match), Toast.LENGTH_SHORT);
                    etNew.setText("");
                    etNewCheck.setText("");
                    return;
                }
                dialog = ProgressDialog.show(UpdatePasswordActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
                updatePassword();
            }
        });

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updatePassword(){
        user= (UserEntity) getIntent().getSerializableExtra("user");

        String userid=user.getUser_id();
        String old_pw= MD5Tool.md5(oldPassword);
        String new_pw=MD5Tool.md5(newPassword);
        String kvs[]=new String[]{userid,old_pw,new_pw};
        String params= com.tangpo.lianfu.parms.UpdatePassword.packagingParam(kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                ToastUtils.showToast(UpdatePasswordActivity.this,getString(R.string.update_password_success),Toast.LENGTH_SHORT);
                finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if(status.equals("1")){
                        ToastUtils.showToast(UpdatePasswordActivity.this,getString(R.string.old_password_error),Toast.LENGTH_SHORT);
                        etOld.setText("");
                        etNew.setText("");
                        etNewCheck.setText("");
                    }else if(status.equals("10")){
                        ToastUtils.showToast(UpdatePasswordActivity.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
