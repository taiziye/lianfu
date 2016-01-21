package com.tangpo.lianfu.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.broadcast.NewMessageReceiver;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.entity.Manager;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.HomePage;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.ui.AddConsumeActivity;
import com.tangpo.lianfu.ui.AddMemberActivity;
import com.tangpo.lianfu.ui.ConversationActivity;
import com.tangpo.lianfu.ui.HomePageActivity;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.ui.MipcaActivityCapture;
import com.tangpo.lianfu.ui.OfflineProfitPayActivity;
import com.tangpo.lianfu.ui.ShopActivity;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class EmployeeHomeFragment extends Fragment implements View.OnClickListener {


    private final static int SCANNIN_STORE_INFO = 1;
    private final static int GET_STORE_INFO = 2;
    private Button scan;
    private Button chat;

    private TextView shop_name;
    private TextView record;
    private TextView add_record;
    private TextView profit;
    private TextView profit_can;
    private TextView profit_compute;
    private TextView mem;
    private TextView add_mem;
    private LinearLayout recordpage;
    private LinearLayout memberpage;

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private Manager manager = null;
    private Gson mGson = null;

    private String storeid = null;
    private Intent intent;
    private String userid=null;
    private String store_id = null;
    private SharedPreferences preferences=null;
    private UserEntity user;

    private FindStore store=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (view == null) {
            view = inflater.inflate(R.layout.employee_home_fragment, container, false);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        bundle = getArguments();

        init(view);
        return view;
    }

    private void init(View view) {
        preferences = getActivity().getSharedPreferences(Configs.APP_ID, Context.MODE_PRIVATE);
        try {
            JSONObject jsonObject = new JSONObject(preferences.getString(Configs.KEY_USER, ""));
            userid = jsonObject.getString("user_id");
            storeid = jsonObject.getString("store_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        mGson = new Gson();

        scan = (Button) view.findViewById(R.id.scan);
        scan.setOnClickListener(this);
        chat = (Button) view.findViewById(R.id.chat);
        chat.setOnClickListener(this);
        recordpage = (LinearLayout) view.findViewById(R.id.recordpage);
        recordpage.setOnClickListener(this);
        memberpage = (LinearLayout) view.findViewById(R.id.memberpage);
        memberpage.setOnClickListener(this);

        shop_name = (TextView) view.findViewById(R.id.shop_name);
        record = (TextView) view.findViewById(R.id.record);
        add_record = (TextView) view.findViewById(R.id.add_record);
        add_record.setOnClickListener(this);
        profit = (TextView) view.findViewById(R.id.profit);
        profit_can = (TextView) view.findViewById(R.id.profit_can);
        profit_compute = (TextView) view.findViewById(R.id.profit_compute);
        profit_compute.setOnClickListener(this);
        mem = (TextView) view.findViewById(R.id.mem);
        add_mem = (TextView) view.findViewById(R.id.add_mem);
        add_mem.setOnClickListener(this);

        //初始化控件，填充数据
        if (bundle != null) {
            userid = bundle.getString("userid");
            store_id = bundle.getString("storeid");
            user = (UserEntity) bundle.getSerializable("user");
            String[] kvs = new String[]{userid};
            String params = HomePage.packagingParam(getActivity(), kvs);

            if(!Tools.checkLAN()) {
                Tools.showToast(getActivity(), "网络未连接，请联网后重试");
                return;
            }

            new NetConnection(new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    dialog.dismiss();

                    try {
                        JSONObject object = result.getJSONObject("param");
                        manager = mGson.fromJson(object.toString(), Manager.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(manager.getStore_name() == null || manager.getStore_name().length() == 0) {
                        shop_name.setText("");
                    } else {
                        shop_name.setText(manager.getStore_name());
                    }
                    if (manager.getIncome() == null || manager.getIncome().length() == 0)
                        record.setText("0人");
                    else
                        record.setText("" + manager.getIncome() + "人");

                    if (manager.getMem_num() == null || manager.getMem_num().length() == 0)
                        mem.setText("0人");
                    else
                        mem.setText("" + manager.getMem_num() + "人");

                    if (manager.getProfit() == null || manager.getIncome().length() == 0)
                        profit.setText("0元");
                    else {
                        String tmp = manager.getIncome();
                        int l = tmp.length();
                        if(l>2) profit.setText(tmp.substring(0, l-2) + "元");
                        else profit.setText(tmp + "元");
                    }

                    if (manager.getPayback() == null || manager.getNeed_pay().length() == 0)
                        profit_can.setText("0元");
                    else {
                        String tmp = manager.getNeed_pay();
                        int l = tmp.length();
                        if(l>2) profit.setText(tmp.substring(0, l-2) + "元");
                        else profit.setText(tmp + "元");
                    }

                    Configs.cacheManager(getActivity(), result.toString());
                }
            }, new NetConnection.FailCallback() {
                @Override
                public void onFail(JSONObject result) {
                    dialog.dismiss();
                    try {
                        if (result.getString("status").equals("9")) {
                            ToastUtils.showToast(getActivity(), getString(R.string.login_timeout), Toast.LENGTH_SHORT);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            getActivity().startActivity(intent);
                        } else if (result.getString("status").equals("10")) {
                            ToastUtils.showToast(getActivity(), getString(R.string.server_exception), Toast.LENGTH_SHORT);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    shop_name.setText("");
                    record.setText("0元");
                    mem.setText("0人");
                    profit_can.setText("0");
                    profit.setText("0元");
                }
            }, params);
        } else {
            shop_name.setText("");
            record.setText("0元");
            mem.setText("0人");
            profit_can.setText("0");
            profit.setText("0元");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NewMessageReceiver.getUnread() > 0) {
            chat.setBackgroundResource(R.drawable.msgs);
        } else {
            //NewMessageReceiver.setUnread(0);
            chat.setBackgroundResource(R.drawable.chat);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment;
        Bundle bundle;
        switch (v.getId()) {
            case R.id.scan:
                intent=new Intent();
                intent.setClass(getActivity(),MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,SCANNIN_STORE_INFO);
                break;
            case R.id.chat:
                intent = new Intent(getActivity(), ConversationActivity.class);
                startActivity(intent);
                break;
            case R.id.add_record:
                intent = new Intent(getActivity(), AddConsumeActivity.class);
                intent.putExtra("userid", userid);
                getActivity().startActivity(intent);
                break;
            case R.id.profit_compute:
                intent = new Intent(getActivity(), OfflineProfitPayActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("storeid", storeid);
                getActivity().startActivity(intent);
                break;
            case R.id.add_mem:
                intent = new Intent(getActivity(), AddMemberActivity.class);
                intent.putExtra("userid", userid);
                getActivity().startActivity(intent);
                break;
            case R.id.recordpage:
                fragment = new RecordFragment();
                bundle = new Bundle();
                bundle.putString("userid", userid);
                bundle.putString("employeename", user.getName());
                bundle.putString("username", user.getName());
                bundle.putString("storename", user.getStorename());
                fragment.setArguments(bundle);
                transaction.replace(R.id.frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                ((HomePageActivity)getActivity()).change(1);
                break;
            case R.id.memberpage:
                fragment = new MemManageFragment();
                bundle = new Bundle();
                bundle.putString("userid", userid);
                bundle.putString("storeid", store_id);
                fragment.setArguments(bundle);
                transaction.replace(R.id.frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                ((HomePageActivity)getActivity()).change(2);
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
                    store = mGson.fromJson(result.getJSONObject("param").toString(),FindStore.class);
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
