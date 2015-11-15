package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.tangpo.lianfu.parms.UpdatePassword;
import com.tangpo.lianfu.utils.CircularImage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 果冻 on 2015/11/3.
 */
public class EmployeeFragment extends Fragment implements OnClickListener {

    private Button double_code;
    private Button chat;

    private CircularImage img;
    private ImageView next;

    private TextView power;
    private TextView name;
    private TextView personal_info;
    private TextView modify_pass;

    private SharedPreferences preferences = null;
    private Gson gson = null;
    private UserEntity user = null;

    private Intent intent = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_fragment, container);

        init(view);

        return view;
    }

    private void init(View view) {
        gson = new Gson();

        double_code = (Button)view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        chat = (Button)view.findViewById(R.id.chat);
        chat.setOnClickListener(this);

        img = (CircularImage)view.findViewById(R.id.img);
        next = (ImageView)view.findViewById(R.id.next);

        power = (TextView)view.findViewById(R.id.power);
        name = (TextView)view.findViewById(R.id.name);
        personal_info = (TextView)view.findViewById(R.id.personal_info);
        personal_info.setOnClickListener(this);
        modify_pass = (TextView)view.findViewById(R.id.modify_pass);
        modify_pass.setOnClickListener(this);

        preferences=getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        String str=preferences.getString(Configs.KEY_USER, "0");
        try {
            JSONObject jsonObject=new JSONObject(str);
            user = gson.fromJson(jsonObject.toString(), UserEntity.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * 获取头像
         */
        img.setImageURI(null);

        power.setText("");
        name.setText(user.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.double_code:
                break;
            case R.id.chat:
                break;
            case R.id.personal_info:
                intent = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.modify_pass:
                intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
        }
    }
}
