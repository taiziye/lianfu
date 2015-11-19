package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.utils.CircularImage;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class ManagerFragment extends Fragment implements OnClickListener {

    private Button double_code;
    private Button chat;
    private Button login_out;

    private CircularImage img;
    private ImageView next;

    private TextView power;
    private TextView name;
    private TextView shop_info;
    private TextView personal_info;
    private TextView discount_manage;
    private TextView update_type;
    private TextView modify_pass;

    private SharedPreferences preferences = null;
    private Gson gson = null;
    private UserEntity user = null;

    private Intent intent = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Tools.closeActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_fragment, container, false);

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
        next = (ImageView) view.findViewById(R.id.next);
        next.setOnClickListener(this);

        power = (TextView) view.findViewById(R.id.power);
        name = (TextView) view.findViewById(R.id.name);
        shop_info = (TextView) view.findViewById(R.id.shop_info);
        shop_info.setOnClickListener(this);
        personal_info = (TextView) view.findViewById(R.id.personal_info);
        personal_info.setOnClickListener(this);
        discount_manage = (TextView) view.findViewById(R.id.discount_manage);
        discount_manage.setOnClickListener(this);
        update_type = (TextView) view.findViewById(R.id.update_type);
        update_type.setOnClickListener(this);
        modify_pass = (TextView) view.findViewById(R.id.modify_pass);
        modify_pass.setOnClickListener(this);

        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String str = preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject = new JSONObject(str);
            user = gson.fromJson(jsonObject.toString(), UserEntity.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * 获取头像
         */
        img.setImageURI(null);

        power.setText("管");
        name.setText(user.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.double_code:
                break;
            case R.id.chat:
                break;
            case R.id.shop_info:
                intent = new Intent(getActivity(), ShopInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.personal_info:
                intent = new Intent(getActivity(), PersonalInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.discount_manage:
                intent = new Intent(getActivity(), DiscountManageActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.update_type:
                break;
            case R.id.modify_pass:
                intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.login_out:
                SharedPreferences preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(Configs.KEY_TOKEN);
                editor.commit();
                intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}
