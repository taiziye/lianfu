package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.CircularImage;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_fragment, container);

        init(view);

        return view;
    }

    private void init(View view) {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.double_code:
                break;
            case R.id.chat:
                break;
            case R.id.personal_info:
                break;
            case R.id.modify_pass:
                break;
        }
    }
}
