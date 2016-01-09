package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Member;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.EditMember;
import com.tangpo.lianfu.parms.GetTypeList;
import com.tangpo.lianfu.utils.MD5Tool;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemberInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button send;

    private TextView user_name;
    private TextView contact_tel;
    private TextView rel_name;
//    private EditText member_level;
//    private EditText id_card;
//
//    private TextView bank;
//    private ImageView select_bank;
//
//    private EditText bank_card;
//    private EditText bank_name;
//
    private Member member = null;
//
//    private ProgressDialog dialog=null;

//    private String userid=null;
//    private String password=null;
//
//    private String[] banklist=null;

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
        setContentView(R.layout.member_info_activity);
        Tools.gatherActivity(this);
        member = (Member) getIntent().getExtras().getSerializable("member");
//        userid=getIntent().getExtras().getString("userid");
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
//        edit = (Button) findViewById(R.id.edit);
//        edit.setOnClickListener(this);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);

        user_name = (TextView) findViewById(R.id.user_name);
        contact_tel = (TextView) findViewById(R.id.contact_tel);
        rel_name = (TextView) findViewById(R.id.rel_name);
        //member_level = (EditText) findViewById(R.id.member_level);
//        id_card = (EditText) findViewById(R.id.id_card);
//
//        bank = (TextView) findViewById(R.id.bank);
//        bank.setOnClickListener(this);
//        select_bank= (ImageView) findViewById(R.id.select_bank);
//        select_bank.setOnClickListener(this);
//
//        bank_card = (EditText) findViewById(R.id.bank_card);
//        bank_name = (EditText) findViewById(R.id.bank_name);

        user_name.setText(member.getUsername());
        contact_tel.setText(member.getPhone());
        rel_name.setText(member.getName());
        //member_level.setText("");
//        id_card.setText(member.getId_number());
//        bank.setText(member.getBank());
//        bank_card.setText(member.getBank_account());
//        bank_name.setText(member.getBank_name());
//
//        password= MD5Tool.md5(member.getPhone().substring(member.getPhone().length()-6));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
//            case R.id.edit:
//                editMember();
//                break;
            case R.id.send:
                break;
//            case R.id.bank:
//            case R.id.select_bank:
//                if(banklist==null||banklist.length==0){
//                    getBankList();
//                }else{
//                    setBank(banklist);
//                }
//                break;
        }
    }

//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            JSONObject object = null;
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    object = (JSONObject) msg.obj;
//                    try {
//                        banklist = object.getString("listtxts").split("\\,");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    setBank(banklist);
//                    break;
//            }
//        }
//    };
//    private void setBank(final String[] list) {
//        new AlertDialog.Builder(MemberInfoActivity.this).setTitle("请选开户银行").setItems(list, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                 bank.setText(list[which]);
//                dialog.dismiss();
//            }
//        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).show();
//    }
//    private void getBankList() {
//        dialog = ProgressDialog.show(MemberInfoActivity.this, getString(R.string.connecting), getString(R.string.please_wait));
//        String [] kvs = new String[]{"bank", ""};
//        String param = GetTypeList.packagingParam(getApplicationContext(), kvs);
//
//        new NetConnection(new NetConnection.SuccessCallback() {
//            @Override
//            public void onSuccess(JSONObject result) {
//                //
//                dialog.dismiss();
//                JSONObject object = null;
//                try {
//                    object = result.getJSONObject("param");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                Message msg = new Message();
//                msg.what = 1;
//                msg.obj = object;
//                handler.sendMessage(msg);
//            }
//        }, new NetConnection.FailCallback() {
//            @Override
//            public void onFail(JSONObject result) {
//                //
//                dialog.dismiss();
//                try {
//                    if ("10".equals(result.getString("status"))) {
//                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
//                    } else if("3".equals(result.getString("status"))) {
//                        Tools.showToast(getApplicationContext(), "列表不存在");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, param);
//    }

//    private void editMember(){
//        if(!Tools.checkLAN()) {
//            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
//            return;
//        }
//        if (user_name.getText().toString().length() == 0) {
//            Tools.showToast(getApplicationContext(), "请填写用户名");
//            return;
//        }
//
//        dialog=ProgressDialog.show(MemberInfoActivity.this,getString(R.string.connecting),getString(R.string.please_wait));
//        String kvs[] = new String []{userid,member.getUser_id(), user_name.getText().toString(),
//        password,rel_name.getText().toString(),contact_tel.getText().toString(),id_card.getText().toString(),
//        member.getSex(),"","","","",bank_card.getText().toString(),bank_name.getText().toString(),
//                bank.getText().toString(),""};
//        String params= EditMember.packagingParam(MemberInfoActivity.this,kvs);
//        new NetConnection(new NetConnection.SuccessCallback() {
//            @Override
//            public void onSuccess(JSONObject result) {
//                dialog.dismiss();
//                ToastUtils.showToast(MemberInfoActivity.this,getString(R.string.edit_success), Toast.LENGTH_SHORT);
//                MemberInfoActivity.this.finish();
//            }
//        }, new NetConnection.FailCallback() {
//            @Override
//            public void onFail(JSONObject result) {
//                dialog.dismiss();
//                try {
//                    Log.e("tag",result.toString());
//                    Tools.handleResult(MemberInfoActivity.this, result.getString("status"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        },params);
//
//    }
}
