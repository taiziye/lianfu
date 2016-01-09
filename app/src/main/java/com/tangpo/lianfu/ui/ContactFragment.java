package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ContactFragment extends Fragment {
    private ListView listView = null;
    private EditText query = null;
    private Button clear = null;
    private InputMethodManager inputMethodManager = null;

    @Nullable
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
    }
}
