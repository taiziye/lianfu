package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.Manager;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.HomePage;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class ManageHomeFragment extends Fragment implements View.OnClickListener {

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

    private Intent intent;

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private Manager man = null;
    private Gson mGson = null;

    private String store_id = null;
    private SharedPreferences preferences = null;
    private String userid = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.closeActivity();
    }

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

        //初始化控件，填充数据
        if (bundle != null) {
            if(!Tools.checkLAN()) {
                Log.e("tag", "check");
                Tools.showToast(getActivity(), "网络未连接，请联网后重试");
                return;
            }

            dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
            userid = bundle.getString("userid");
            store_id = bundle.getString("storeid");
            String[] kvs = new String[]{userid};
            String params = HomePage.packagingParam(getActivity(), kvs);

            Log.e("tag", "userid " + userid + "storeid " + store_id);

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
                    Log.e("tag", man.toString());

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
                        String tmp = man.getIncome();
                        int l = tmp.length();
                        if(l>2) pay.setText("" + tmp.substring(0, l-2) + "元");
                        else pay.setText("" + tmp + "元");
                        Log.e("tag", tmp.substring(0, l-2));
                    }

                    if (man.getPayback() == null || man.getPayback().length() == 0)
                        pay_can.setText("0元");
                    else {
                        String tmp = man.getNeed_pay();
                        int l = tmp.length();
                        if (l>2) pay_can.setText("" + tmp.substring(0, l-2) + "元");
                        else pay_can.setText("" + tmp + "元");
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
                        else rebate.setText(tmp + "元");
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
                    record.setText("0");
                    mem.setText("0人");
                    pay.setText("0元");
                    pay_can.setText("0元");
                    employee.setText("0人");
                    manager.setText("0人");
                    rebate.setText("0");
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
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan:
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
        }
    }
}
