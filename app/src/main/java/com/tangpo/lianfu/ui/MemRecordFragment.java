package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.llb.util.PullToRefreshListView;
import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemRecordFragment extends Fragment implements View.OnClickListener {

    private PullToRefreshListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mem_record_fragment, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        list = (PullToRefreshListView)view.findViewById(R.id.list);
    }

    @Override
    public void onClick(View v) {
    }
}
