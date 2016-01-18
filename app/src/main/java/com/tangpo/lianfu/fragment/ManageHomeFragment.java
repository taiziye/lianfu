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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.entity.Manager;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.HomePage;
import com.tangpo.lianfu.parms.StoreDetail;
import com.tangpo.lianfu.ui.AddConsumeActivity;
import com.tangpo.lianfu.ui.AddEmployeeActivity;
import com.tangpo.lianfu.ui.AddMemberActivity;
import com.tangpo.lianfu.ui.CostRepayActivity;
import com.tangpo.lianfu.ui.HomePageActivity;
import com.tangpo.lianfu.ui.MainActivity;
import com.tangpo.lianfu.ui.MipcaActivityCapture;
import com.tangpo.lianfu.ui.OfflineProfitPayActivity;
import com.tangpo.lianfu.ui.RepayActivity;
import com.tangpo.lianfu.ui.ShopActivity;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class ManageHomeFragment extends Fragment implements View.OnClickListener {

    private final static int SCANNIN_STORE_INFO = 3;
    private final static int GET_STORE_INFO = 4;
    private Button scan;
    private Button chat;

    private TextView shop_name;
    private TextView record;
    private TextView add_record;
    private TextView pay;
    private TextView pay_can;
    private TextView profit_pay;
    private TextView mem;
    private TextView add_mem;
    private TextView manager;
    private TextView employee;
    private TextView add_employee;
    private TextView rebate;
    private TextView rebate_pay;
    private TextView cost;
    private TextView cost_pay;
    private LinearLayout recordpage;
    private LinearLayout memberpage;
    private LinearLayout employeepage;
    private Intent intent;

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private Manager man = null;
    private Gson mGson = null;

    private String store_id = null;
    private SharedPreferences preferences = null;
    private String userid = null;
    private UserEntity userEntity;

    private FindStore store=null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_home_fragment, container, false);

        bundle = getArguments();

        init(view);
        return view;
    }

    private void init(View view) {

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, Context.MODE_PRIVATE);
        try {
            JSONObject jsonObject = new JSONObject(preferences.getString(Configs.KEY_USER, ""));
            userid = jsonObject.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mGson = new Gson();

        scan = (Button) view.findViewById(R.id.scan);
        scan.setOnClickListener(this);
        chat = (Button) view.findViewById(R.id.chat);
        chat.setOnClickListener(this);
        recordpage = (LinearLayout) view.findViewById(R.id.recordpage);
        recordpage.setOnClickListener(this);
        memberpage = (LinearLayout) view.findViewById(R.id.memberpage);
        memberpage.setOnClickListener(this);
        employeepage = (LinearLayout) view.findViewById(R.id.employeepage);
        employeepage.setOnClickListener(this);

        shop_name = (TextView) view.findViewById(R.id.shop_name);
        record = (TextView) view.findViewById(R.id.record);
        add_record = (TextView) view.findViewById(R.id.add_record);
        add_record.setOnClickListener(this);
        pay = (TextView) view.findViewById(R.id.pay);
        profit_pay = (TextView) view.findViewById(R.id.profit_pay);
        profit_pay.setOnClickListener(this);
        pay_can = (TextView) view.findViewById(R.id.pay_can);
        mem = (TextView) view.findViewById(R.id.mem);
        add_mem = (TextView) view.findViewById(R.id.add_mem);
        add_mem.setOnClickListener(this);
        manager = (TextView) view.findViewById(R.id.manager);
        employee = (TextView) view.findViewById(R.id.employee);
        add_employee = (TextView) view.findViewById(R.id.add_employee);
        add_employee.setOnClickListener(this);
        rebate = (TextView) view.findViewById(R.id.rebate);
        rebate_pay = (TextView) view.findViewById(R.id.rebate_pay);
        rebate_pay.setOnClickListener(this);
        cost = (TextView) view.findViewById(R.id.cost);
        cost_pay = (TextView) view.findViewById(R.id.cost_pay);
        cost_pay.setOnClickListener(this);

        //初始化控件，填充数据
        if (bundle != null) {
            if(!Tools.checkLAN()) {
                Tools.showToast(getActivity(), "网络未连接，请联网后重试");
                return;
            }

            dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
            userid = bundle.getString("userid");
            store_id = bundle.getString("storeid");
            userEntity = (UserEntity) bundle.getSerializable("user");
            String[] kvs = new String[]{userid};
            String params = HomePage.packagingParam(getActivity(), kvs);

            new NetConnection(new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    dialog.dismiss();
                    try {
                        JSONObject object = result.getJSONObject("param");
                        man = mGson.fromJson(object.toString(), Manager.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = man;
                    handler.sendMessage(msg);
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

                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }, params);
        } else {
            shop_name.setText("");
            record.setText("0");
            mem.setText("0人");
            pay.setText("0元");
            pay_can.setText("0元");
            employee.setText("0人");
            manager.setText("0人");
            rebate.setText("0");
            cost.setText("0元");
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    man = (Manager) msg.obj;
                    if(man.getStore_name() == null || man.getStore_name().length() == 0) {
                        shop_name.setText("");
                    } else {
                        shop_name.setText(man.getStore_name());
                    }

                    if (man.getIncome() == null || man.getIncome().length() == 0)
                        record.setText("0");
                    else
                        record.setText("" + man.getIncome() + "");

                    if (man.getMem_num() == null || man.getMem_num().length() == 0)
                        mem.setText("0人");
                    else
                        mem.setText("" + man.getMem_num() + "人");

                    if (man.getProfit() == null || man.getProfit().length() == 0)
                        pay.setText("0元");
                    else{
                        String tmp = man.getProfit();
                        int l = tmp.length();
                        if(l>2) pay.setText("" + tmp.substring(0, l-2) + "元");
                        else pay.setText("" + 0 + "元");
                    }

                    if (man.getPayback() == null || man.getPayback().length() == 0)
                        pay_can.setText("0元");
                    else {
                        String tmp = man.getNeed_pay();
                        int l = tmp.length();
                        if (l>2) pay_can.setText("" + tmp.substring(0, l-2) + "元");
                        else pay_can.setText("" + 0 + "元");
                    }

                    if (man.getAdmin_num() == null || man.getAdmin_num().length() == 0)
                        manager.setText("0人");
                    else
                        manager.setText("" + man.getAdmin_num() + "人");

                    if (man.getStaff_num() == null || man.getStaff_num().length() == 0)
                        employee.setText("0人");
                    else
                        employee.setText("" + man.getStaff_num() + "人");

                    if(man.getPayback() == null || man.getPayback().length() == 0) {
                        rebate.setText("0.00元");
                    } else {
                        String tmp = man.getPayback();
                        int l = tmp.length();
                        if (l>2) rebate.setText(tmp.substring(0, l - 2) + "元");
                        else rebate.setText(0 + "元");
                    }

                    if (man.getCostback() == null || man.getCostback().length() == 0) {
                        cost.setText("0.00元");
                    } else {
                        String tmp = man.getCostback();
                        int l = tmp.length();
                        if (l>2) cost.setText(tmp.substring(0, l-2) + "元");
                        else cost.setText(0 + "元");
                    }
                    break;
                case 2:
                    shop_name.setText("");
                    record.setText("0");
                    mem.setText("0人");
                    pay.setText("0元");
                    pay_can.setText("0元");
                    employee.setText("0人");
                    manager.setText("0人");
                    rebate.setText("0");
                    break;

                case GET_STORE_INFO:
                    FindStore store= (FindStore) msg.obj;
                    String favoriate="0";

                    Intent intent=new Intent(getActivity(),ShopActivity.class);
                    intent.putExtra("store", store);
                    intent.putExtra("userid", userid);
                    intent.putExtra("favorite", favoriate);
                    startActivity(intent);
                    break;
            }
        }
    };

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
                break;
            case R.id.add_record:
                intent = new Intent(getActivity(), AddConsumeActivity.class);
                intent.putExtra("userid", userid);
                getActivity().startActivity(intent);
                break;
            case R.id.profit_pay:
                intent = new Intent(getActivity(), OfflineProfitPayActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("storeid", store_id);
                getActivity().startActivity(intent);
                break;
            case R.id.add_mem:
                intent = new Intent(getActivity(), AddMemberActivity.class);
                intent.putExtra("userid", userid);
                getActivity().startActivity(intent);
                break;
            case R.id.add_employee:
                intent = new Intent(getActivity(), AddEmployeeActivity.class);
                intent.putExtra("userid", userid);
                getActivity().startActivity(intent);
                break;
            case R.id.rebate_pay:
                intent = new Intent(getActivity(), RepayActivity.class);
                intent.putExtra("userid", userid);
                getActivity().startActivity(intent);
                break;
            case R.id.cost_pay:
                intent = new Intent(getActivity(), CostRepayActivity.class);
                intent.putExtra("userid", userid);
                intent.putExtra("storeid", userEntity.getStore_id());
                getActivity().startActivity(intent);
                break;
            case R.id.recordpage:
                fragment = new RecordFragment();
                bundle = new Bundle();
                bundle.putString("userid", userid);
                bundle.putString("employeename", userEntity.getName());
                bundle.putString("username", userEntity.getName());
                bundle.putString("storename", userEntity.getStorename());
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
            case R.id.employeepage:
                fragment = new EmployeeManageFragment();
                transaction.replace(R.id.frame, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                //activity.change(3);
                ((HomePageActivity)getActivity()).change(3);
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