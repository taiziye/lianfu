package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.llb.util.PullToRefreshListView;
import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemCollectFragment extends Fragment implements View.OnClickListener {

    private Button locate;
    private Button map;

    private EditText search;

    private PullToRefreshListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mem_collect_fragment, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        locate = (Button)view.findViewById(R.id.locate);
        locate.setOnClickListener(this);
        map = (Button)view.findViewById(R.id.map);
        map.setOnClickListener(this);

        search = (EditText)view.findViewById(R.id.search);
        search.setOnClickListener(this);

        list = (PullToRefreshListView)view.findViewById(R.id.list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.locate:
                break;
            case R.id.map:
                break;
            case R.id.search:
                break;
        }
    }
}
