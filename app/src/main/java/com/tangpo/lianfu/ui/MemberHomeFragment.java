package com.tangpo.lianfu.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.llb.util.PullToRefreshListView;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.PositionAdapter;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.FindStore;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.CheckCollectedStore;
import com.tangpo.lianfu.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 果冻 on 2015/11/7.
 */
public class MemberHomeFragment extends Fragment implements View.OnClickListener {

    private Button double_code;
    private Button locate;
    private Button map;

    private EditText search;

    private PullToRefreshListView list;

    private Bundle bundle = null;

    private ProgressDialog dialog = null;

    private SharedPreferences preferences;

    private PositionAdapter adapter = null;

    private ArrayList<FindStore> storeList = null;

    private Gson gson = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.member_home_fragment, container, false);
        bundle = getArguments();

        init(view);

        if(bundle != null) {
            int lng = (int) (preferences.getFloat(Configs.KEY_LNG, 0.0f) * (10 ^ 6));
            int lat = (int) (preferences.getFloat(Configs.KEY_LAT, 0.0f) * (10 ^ 6));
            String userid = bundle.getString("userid");
            String kvs[] = new String []{lng + "", lat + "", userid};

            String params = com.tangpo.lianfu.parms.FindStore.packagingParam(getActivity(), kvs);

            new NetConnection(new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    dialog.dismiss();
                    try {
                        JSONArray jsonArray = result.getJSONArray("param");
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            FindStore store = gson.fromJson(object.toString(), FindStore.class);
                            storeList.add(store);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter = new PositionAdapter(getActivity(), storeList);
                    list.setAdapter(adapter);
                }
            }, new NetConnection.FailCallback() {
                @Override
                public void onFail(JSONObject result) {
                    dialog.dismiss();
                    try {
                        if(result.getString("status").equals("9")){
                            ToastUtils.showToast(getActivity(), getString(R.string.login_timeout), Toast.LENGTH_SHORT);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            getActivity().startActivity(intent);
                        } else if(result.getString("status").equals("10")){
                            ToastUtils.showToast(getActivity(), getString(R.string.server_exception), Toast.LENGTH_SHORT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params);

            String tmp[] = new String []{userid};
            String tmpParams = CheckCollectedStore.packagingParam(getActivity(), tmp);

            new NetConnection(new NetConnection.SuccessCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Set<String> store = new HashSet<String>();
                    try {
                        JSONArray stores = result.getJSONArray("param");
                        JSONObject object = null;
                        for(int i=0; i<stores.length(); i++){
                            object = stores.getJSONObject(i);
                            store.add(object.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Configs.cacheCollectedStore(getActivity(), store);
                }
            }, new NetConnection.FailCallback() {
                @Override
                public void onFail(JSONObject result) {
                    //
                }
            }, tmpParams);
        }

        return view;
    }

    private void init(View view){
        dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        double_code = (Button)view.findViewById(R.id.double_code);
        locate = (Button)view.findViewById(R.id.locate);
        map = (Button)view.findViewById(R.id.map);

        search = (EditText)view.findViewById(R.id.search);

        list = (PullToRefreshListView)view.findViewById(R.id.list);

        preferences=getActivity().getSharedPreferences(Configs.APP_ID, getActivity().MODE_PRIVATE);

        storeList = new ArrayList<>();

        gson = new Gson();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.double_code:
                break;
            case R.id.locate:
                break;
            case R.id.map:
                Intent intent = new Intent(getActivity(), MapActivity.class);
                intent.putParcelableArrayListExtra("list", storeList);
                startActivity(intent);
                break;
            case R.id.search:
                break;
        }
    }
}
