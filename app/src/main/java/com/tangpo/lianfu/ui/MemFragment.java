package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangpo.lianfu.R;
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

    private CircularImage img;
    private ImageView next;

    private TextView power;
    private TextView name;
    private TextView personal_info;
    private TextView modify_pass;
    private TextView remainder;
    private UserEntity userEntity;

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        Tools.closeActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_fragment, container, false);

        init(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }

    private void init(View view) {

        userEntity= (UserEntity) getArguments().getSerializable("user");
        double_code = (Button) view.findViewById(R.id.double_code);
        double_code.setOnClickListener(this);
        chat = (Button) view.findViewById(R.id.chat);
        chat.setOnClickListener(this);

        img = (CircularImage) view.findViewById(R.id.img);
        next = (ImageView) view.findViewById(R.id.next);
        next.setOnClickListener(this);

        power = (TextView) view.findViewById(R.id.power);
        name = (TextView) view.findViewById(R.id.name);
        personal_info = (TextView) view.findViewById(R.id.personal_info);
        personal_info.setOnClickListener(this);
        modify_pass = (TextView) view.findViewById(R.id.modify_pass);
        modify_pass.setOnClickListener(this);
        remainder = (TextView) view.findViewById(R.id.remainder);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.double_code:
                break;
            case R.id.chat:
                break;
            case R.id.next:
                break;
            case R.id.personal_info:
                intent = new Intent(getActivity(), PersonalInfoActivity.class);
                intent.putExtra("user", userEntity);
                startActivity(intent);
                break;
            case R.id.modify_pass:
                intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                intent.putExtra("user", userEntity);
                startActivity(intent);
                break;
        }
    }
}
