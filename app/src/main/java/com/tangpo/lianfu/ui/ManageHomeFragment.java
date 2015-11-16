package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
    private TextView pay_profit;
    private TextView mem;
    private TextView add_mem;
    private TextView employee;
    private TextView add_employee;

    private Intent intent;

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private Manager manager = null;
    private Gson mGson = null;

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
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        mGson = new Gson();

        scan = (Button)view.findViewById(R.id.scan);
        scan.setOnClickListener(this);
        chat = (Button)view.findViewById(R.id.chat);
        chat.setOnClickListener(this);

        shop_name = (TextView)view.findViewById(R.id.shop_name);
        record = (TextView)view.findViewById(R.id.record);
        add_record = (TextView)view.findViewById(R.id.add_record);
        add_record.setOnClickListener(this);
        pay = (TextView)view.findViewById(R.id.pay);
        pay_profit = (TextView)view.findViewById(R.id.profit_pay);
        pay_profit.setOnClickListener(this);
        mem = (TextView)view.findViewById(R.id.mem);
        add_mem = (TextView)view.findViewById(R.id.add_mem);
        add_mem.setOnClickListener(this);
        employee = (TextView)view.findViewById(R.id.employee);
        add_employee = (TextView)view.findViewById(R.id.add_employee);
        add_employee.setOnClickListener(this);

        //初始化控件，填充数据
        if(bundle != null){
            String userid = bundle.getString("userid");
            String [] kvs = new String[]{userid};
            String params = HomePage.packagingParam(getActivity(), kvs);

            new NetConnection(new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    dialog.dismiss();

                    manager = mGson.fromJson(result.toString(), Manager.class);

                    shop_name.setText(manager.getStore_name());
                    record.setText("会员消费记录共计" + manager.getIncome() + "元");
                    mem.setText("会员人数总计" + manager.getMem_num() + "人");
                    pay.setText("消费利润共计" + manager.getProfit() + "元，可支付共计" + manager.getPayback() + "元");
                    employee.setText("管理员人数总计" + manager.getAdmin_num() + "人，员工总计" + manager.getStaff_num() + "人");

                    Configs.cacheManager(getActivity(), result.toString());
                }
            }, new NetConnection.FailCallback() {
                @Override
                public void onFail(JSONObject result) {
                    dialog.dismiss();
                    try {
                        if(result.getString("status").equals("9")){
                            ToastUtils.showToast(getActivity(), getString(R.string.login_timeout), Toast.LENGTH_SHORT);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            getActivity().startActivity(intent);
                        } else if(result.getString("status").equals("10")){
                            ToastUtils.showToast(getActivity(), getString(R.string.server_exception), Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    shop_name.setText("");
                    record.setText("会员消费记录共计 元");
                    mem.setText("会员人数总计 人");
                    pay.setText("消费利润共计 元，可支付共计 元");
                    employee.setText("管理员人数总计 人，员工总计 人");
                }
            }, params);
        } else {
            shop_name.setText("");
            record.setText("会员消费记录共计 元");
            mem.setText("会员人数总计 人");
            pay.setText("消费利润共计 元，可支付共计 元");
            employee.setText("管理员人数总计 人，员工总计 人");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scan:
                break;
            case R.id.chat:
                break;
            case R.id.add_record:
                intent = new Intent(getActivity(), AddConsumeActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.profit_pay:
                intent = new Intent(getActivity(), OfflineProfitPayActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.add_mem:
                intent = new Intent(getActivity(), AddMemberActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.add_employee:
                intent = new Intent(getActivity(), AddEmployeeActivity.class);
                getActivity().startActivity(intent);
                break;
        }
    }
}
