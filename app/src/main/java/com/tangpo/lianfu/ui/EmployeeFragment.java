package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.utils.CircularImage;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class EmployeeFragment extends Fragment implements OnClickListener {

    private final static int SCANNIN_STORE_INFO = 1;
    private final static int GET_STORE_INFO = 2;
    private Button double_code;
    private Button chat;
    private Button login_out;

    private CircularImage img;
    private ImageView next;

    private TextView power;
    private TextView name;
    private TextView user_name;
    private LinearLayout personal_info;
    private LinearLayout modify_pass;
    private TextView remainder;

    private SharedPreferences preferences = null;
    private Gson gson = null;
    private UserEntity user = null;
    private String userid=null;

    private Intent intent = null;

    private ProgressDialog dialog=null;

    private FindStore store=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_fragment, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        gson = new Gson();
        double_code = (Button) view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        chat = (Button) view.findViewById(R.id.chat);
        chat.setOnClickListener(this);
        login_out = (Button) view.findViewById(R.id.login_out);
        login_out.setOnClickListener(this);
        img = (CircularImage) view.findViewById(R.id.img);
        power = (TextView) view.findViewById(R.id.power);
        name = (TextView) view.findViewById(R.id.name);
        remainder = (TextView) view.findViewById(R.id.remainder);
        user_name = (TextView) view.findViewById(R.id.user_name);
        personal_info = (LinearLayout) view.findViewById(R.id.personal_info);
        personal_info.setOnClickListener(this);
        modify_pass = (LinearLayout) view.findViewById(R.id.modify_pass);
        modify_pass.setOnClickListener(this);
        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String str = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(str);
            user = gson.fromJson(jsonObject.toString(), UserEntity.class);
            userid=user.getUser_id();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Tools.setPhoto(getActivity(), user.getPhoto(), img);
        user_name.setText("");
        power.setText("员工");
        name.setText(user.getName());
        remainder.setText(user.getMoney());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.double_code:
                intent=new Intent();
                intent.setClass(getActivity(),MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,SCANNIN_STORE_INFO);
                break;
            case R.id.chat:
                break;
            case R.id.personal_info:
                intent = new Intent(getActivity(), PersonalInfoActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("flag", "1");
                startActivity(intent);
                //Tools.showToast(getActivity(), "请期待下一个版本");
                break;
            case R.id.modify_pass:
                intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.login_out:
                Configs.cleanData(getActivity());
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SCANNIN_STORE_INFO:
                if(resultCode==getActivity().RESULT_OK){
                    Bundle bundle=data.getExtras();
                    String result=bundle.getString("result");
                    //在这里处理返回来的store_id、service_center、referrer
                    String store_id= Uri.parse(result).getQueryParameter("store_id");
                    String service_center=Uri.parse(result).getQueryParameter("service_center");
                    String referrer=Uri.parse(result).getQueryParameter("referrer");

                    if(store_id!=null&&service_center!=null&&referrer!=null){
                        getStoreDetail(store_id,userid);
                    }
                }
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_STORE_INFO:
                    FindStore store= (FindStore) msg.obj;
                    String favoriate="0";

                    Intent intent=new Intent(getActivity(),ShopActivity.class);
                    intent.putExtra("store",store);
                    intent.putExtra("userid",userid);
                    intent.putExtra("favorite",favoriate);
                    startActivity(intent);
                    break;
            }
        }
    };

    private void getStoreDetail(String store_id,String userid){
        if(!Tools.checkLAN()) {
            Tools.showToast(getActivity(), "网络未连接，请联网后重试");
            return;
        }
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{store_id, userid};
        String param = StoreDetail.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    store = gson.fromJson(result.getJSONObject("param").toString(),FindStore.class);
                    Message msg=new Message();
                    msg.what=GET_STORE_INFO;
                    msg.obj=store;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(getActivity(), result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },param);
    }

}
