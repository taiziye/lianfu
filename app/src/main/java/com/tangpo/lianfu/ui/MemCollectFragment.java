package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.MemberCollectAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.MemberCollect;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class MemCollectFragment extends Fragment implements View.OnClickListener {

    private Button locate;
    private Button map;

    private EditText search;

    private PullToRefreshListView listView;
    private MemberCollectAdapter adapter = null;
    private List<MemberCollect> list = new ArrayList<>();

    private ProgressDialog dialog = null;
    private SharedPreferences preferences = null;

    private String userid;

    private Gson gson = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mem_collect_fragment, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userid = bundle.getString("userid");
        }
        init(view);
        return view;
    }

    private void init(View view) {
        gson = new Gson();
        getCollectStore();

        locate = (Button) view.findViewById(R.id.locate);
        locate.setOnClickListener(this);
        map = (Button) view.findViewById(R.id.map);
        map.setOnClickListener(this);

        search = (EditText) view.findViewById(R.id.search);
        search.setOnClickListener(this);

        listView = (PullToRefreshListView) view.findViewById(R.id.list);
        adapter = new MemberCollectAdapter(getActivity(), list);
        listView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.locate:
                break;
            case R.id.map:
                break;
            case R.id.search:
                break;
        }
    }

    private void getCollectStore() {
        //获取收藏店铺列表
        preferences = getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);
        Set<String> storeSet = preferences.getStringSet(Configs.KEY_STORE, null);

        if (storeSet != null) {
            Iterator<String> it = storeSet.iterator();
            while (it.hasNext()) {
                try {
                    JSONObject object = new JSONObject(it.next());
                    MemberCollect store = gson.fromJson(object.toString(), MemberCollect.class);
                    list.add(store);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
