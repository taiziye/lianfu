package com.tangpo.lianfu.ui;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ContactAdapter;
import com.tangpo.lianfu.entity.ChatAccount;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ContactFragment extends Fragment {
    private ListView listView = null;
    private EditText query = null;
    private Button clear = null;
    private InputMethodManager inputMethodManager = null;
    private ArrayList<ChatAccount> accounts = new ArrayList<>();

    private ContactAdapter adapter = null;
    private String hx_id = "";
    private String photo = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        init(view);
        return view;
    }

    private void init(View view){
        listView = (ListView) view.findViewById(R.id.list);
        query = (EditText) view.findViewById(R.id.query);
        clear = (Button) view.findViewById(R.id.clear);

        accounts.addAll((ArrayList<ChatAccount>) getArguments().getSerializable("acstr"));
        hx_id = getArguments().getString("hxid");
        photo = getArguments().getString("photo");
        adapter = new ContactAdapter(getActivity(), accounts);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                String username = accounts.get(position).getName();
                //String userid = accounts.get(position).getUser_id();
                String hxid = accounts.get(position).getEasemod_id();
                intent.putExtra("account", accounts.get(position));
                //intent.putExtra("userid", userid);
                intent.putExtra("username", username);
                intent.putExtra("hxid", hxid);
                intent.putExtra("myid", hx_id);
                intent.putExtra("photo", photo);
                startActivity(intent);
            }
        });

        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
                if (s.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyBoard();
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard();
                return false;
            }
        });
    }

    private void hideSoftKeyBoard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
