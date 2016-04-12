package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.RequestAdapter;
import com.tangpo.lianfu.entity.HXUser;
import com.tangpo.lianfu.entity.InvitedMessage;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetSpecifyHX;
import com.tangpo.lianfu.utils.InviteMessageDao;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 果冻 on 2016/4/10.
 */
public class AddFriendActivity extends Activity {
    private Button back;
    private ListView list;
    private RequestAdapter adapter;

    private InviteMessageDao messageDao;
    private List<InvitedMessage> msgs;

    private ArrayList<HXUser> users;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addfriend);
        init();
    }

    private void init() {
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list = (ListView) findViewById(R.id.list);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(AddFriendActivity.this).setTitle("删除提示框").setMessage("确认删除该数据？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                messageDao.deleteMessage(users.get(position).getMessage().getFrom());
                                users.remove(users.get(position));
                                adapter.notifyDataSetChanged();
                            }})
                        .setNegativeButton("取消",null)
                        .show();

                return false;
            }
        });

        messageDao = new InviteMessageDao(this);
        msgs = messageDao.getMessageList();
        String ids = "";
        for(int i=0; i<msgs.size(); i++) {
            if(i!=0) ids += ",";
            ids += msgs.get(i).getFrom();
        }
        gson = new Gson();
        users = new ArrayList<HXUser>();
        if (ids.length() > 1)
            getAccounts(ids);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    users = (ArrayList<HXUser>) msg.obj;
                    for (int i=0; i<msgs.size(); i++) {
                        for (int j=0; j<users.size(); j++) {
                            if(msgs.get(i).getFrom().equalsIgnoreCase(users.get(j).getEasemod_id())) {
                                users.get(j).setMessage(msgs.get(i));
                            }
                        }
                    }
                    adapter = new RequestAdapter(AddFriendActivity.this, users);
                    list.setAdapter(adapter);
                    break;
            }
        }
    };

    /**
     * 根据环信账号获取账户信息
     * @param id
     */
    private void getAccounts(final String id) {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        String[] kvs = new String[]{id};
        String param = GetSpecifyHX.packagingParam(AddFriendActivity.this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray array = result.getJSONArray("param");
                    for (int i = 0; i<array.length(); i++) {
                        HXUser user = gson.fromJson(array.getJSONObject(i).toString(), HXUser.class);
                        users.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = users;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                try {
                    if ("3".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "用户不存在");
                    } else if ("10".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else {
                        Tools.showToast(getApplicationContext(), result.getString("info"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }
}
