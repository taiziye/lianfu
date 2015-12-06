package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
public class EmployeeHomeFragment extends Fragment implements View.OnClickListener {

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

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private Manager manager = null;
    private Gson mGson = null;

    private String storeid = null;
    private Intent intent;
    private String userid=null;
    private SharedPreferences preferences=null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.closeActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_home_fragment, container, false);
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
            String userid = bundle.getString("userid");
            String[] kvs = new String[]{userid};
            String params = HomePage.packagingParam(getActivity(), kvs);

            if(!Tools.checkLAN()) {
                Log.e("tag", "check");
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

                    shop_name.setText(manager.getStore_name());
                    if (manager.getIncome() == null)
                        record.setText("0");
                    else
                        record.setText("" + manager.getIncome() + "");

                    if (manager.getMem_num() == null)
                        mem.setText("0人");
                    else
                        mem.setText("" + manager.getMem_num() + "人");

                    if (manager.getProfit() == null)
                        profit.setText("0");
                    else {
                        profit.setText(manager.getIncome() + "元");
                    }

                    if (manager.getPayback() == null)
                        profit_can.setText("0");
                    else {
                        profit_can.setText(manager.getNeed_pay() + "");
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
        }
    }
}
