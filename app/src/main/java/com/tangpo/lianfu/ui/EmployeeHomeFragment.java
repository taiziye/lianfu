package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.User;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.HomePage;
import com.tangpo.lianfu.utils.ToastUtils;

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
    private TextView profit_compute;
    private TextView mem;
    private TextView add_mem;

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private User user = null;
    private Gson mGson = null;

    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_home_fragment, container, false);
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
        profit = (TextView)view.findViewById(R.id.profit);
        profit_compute = (TextView)view.findViewById(R.id.profit_compute);
        profit_compute.setOnClickListener(this);
        mem = (TextView)view.findViewById(R.id.mem);
        add_mem = (TextView)view.findViewById(R.id.add_mem);
        add_mem.setOnClickListener(this);

        //初始化控件，填充数据
        if(bundle != null){
            String userid = bundle.getString("userid");
            String [] kvs = new String[]{userid};
            String params = HomePage.packagingParam(getActivity(), kvs);

            new NetConnection(new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    dialog.dismiss();

                    user = mGson.fromJson(result.toString(), User.class);

                    shop_name.setText(user.getStore_name());
                    record.setText("会员消费记录共计" + user.getIncome() + "元");
                    mem.setText("会员人数总计" + user.getMem_num() + "人");
                    profit.setText("消费利润共计" + user.getProfit() + "元，可支付共计" + user.getPayback() + "元");
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
                    profit.setText("消费利润共计 元，可支付共计 元");
                }
            }, params);
        } else {
            shop_name.setText("");
            record.setText("会员消费记录共计 元");
            mem.setText("会员人数总计 人");
            profit.setText("消费利润共计 元，可支付共计 元");
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
            case R.id.profit_compute:
                intent = new Intent(getActivity(), OfflineProfitPayActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.add_mem:
                intent = new Intent(getActivity(), AddMemberActivity.class);
                getActivity().startActivity(intent);
                break;
        }
    }
}
