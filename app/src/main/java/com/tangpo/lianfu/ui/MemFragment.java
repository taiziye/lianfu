package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.parms.UpdatePassword;
import com.tangpo.lianfu.utils.CircularImage;
import com.tangpo.lianfu.utils.Tools;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemFragment extends Fragment implements View.OnClickListener {

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
    private UserEntity userEntity;

    private SharedPreferences preferences=null;
    private String logintype=null;

//    @Override
//    public void onDestroyOptionsMenu() {
//        super.onDestroyOptionsMenu();
//        Tools.closeActivity();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_fragment, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        preferences=getActivity().getSharedPreferences(Configs.APP_ID, Context.MODE_PRIVATE);
        logintype=preferences.getString(Configs.KEY_LOGINTYPE,"");
        userEntity= (UserEntity) getArguments().getSerializable("user");

        double_code = (Button) view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        chat = (Button) view.findViewById(R.id.chat);
        chat.setOnClickListener(this);
        login_out = (Button) view.findViewById(R.id.login_out);
        login_out.setOnClickListener(this);

        img = (CircularImage) view.findViewById(R.id.img);

        power = (TextView) view.findViewById(R.id.power);
        name = (TextView) view.findViewById(R.id.name);
        user_name = (TextView) view.findViewById(R.id.user_name);
        personal_info = (LinearLayout) view.findViewById(R.id.personal_info);
        personal_info.setOnClickListener(this);
        modify_pass = (LinearLayout) view.findViewById(R.id.modify_pass);
        modify_pass.setOnClickListener(this);
        remainder = (TextView) view.findViewById(R.id.remainder);

        Tools.setPhoto(getActivity(), userEntity.getPhoto(), img);
        name.setText(userEntity.getName());
        if (logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
            power.setText("游客");
        }else {
            power.setText("会员");
        }
        user_name.setText("");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.double_code:
                break;
            case R.id.chat:
                break;
            case R.id.next:
                break;
            case R.id.personal_info:
                if(logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
                    intent=new Intent(getActivity(),BoundOrRegister.class);
                }else{
                    intent = new Intent(getActivity(), PersonalInfoActivity.class);
                    intent.putExtra("user", userEntity);
                }
                startActivity(intent);
                //Tools.showToast(getActivity(), "请期待下一个版本");
                break;
            case R.id.modify_pass:
                if(logintype.equals("0")||logintype.equals("1")||logintype.equals("2")){
                    intent=new Intent(getActivity(),BoundOrRegister.class);
                }else{
                    intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                    intent.putExtra("user", userEntity);
                }
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
}
