package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Employee;
import com.tangpo.lianfu.entity.UserEntity;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.EditStore;
import com.tangpo.lianfu.parms.GetTypeList;
import com.tangpo.lianfu.parms.StaffManagement;
import com.tangpo.lianfu.parms.StoreInfo;
import com.tangpo.lianfu.utils.ToastUtils;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by 果冻 on 2015/11/8.
 */
public class ShopInfoActivity extends Activity implements View.OnClickListener {

    private Button back;
    private Button edit;

    private LinearLayout vocation;
    private LinearLayout location;
//    private LinearLayout select;
    private EditText shop_name;
    private EditText shop_host;
    private EditText contact_name;
    private EditText contact_tel;
    private EditText const_tel;
    private EditText contact_intel;
//    private EditText shop_employee;
    private EditText contact_email;
    private EditText occupation;
    private EditText address;
    private EditText detail_address;
    private EditText commodity;
    private Button map_locate;
    private View view;
    private ListView listView;

    private ImageView top_ad;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;

    private ProgressDialog dialog = null;
    private Gson gson = null;

    private com.tangpo.lianfu.entity.StoreInfo store = null;
    private UserEntity user = null;

    private String[] vocationlist = null;
    private String[] provincelist = null;
    private String[] provinceid = null;
    private String[] citylist = null;
    private String[] cityid = null;
    private String[] suburblist = null;
    private String[] suburbid = null;
    private String one = "";
    private String two = "";
    private int page = 1;
    private String[] stafflist = new String [10];
    private ArrayList<Employee> employeelist = new ArrayList<Employee>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tools.deleteActivity(this);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shop_info_activity);

        Tools.gatherActivity(this);

        user = (UserEntity) getIntent().getExtras().getSerializable("user");
        init();
    }

    private void init() {
        gson = new Gson();
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);
        edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(this);
        vocation = (LinearLayout) findViewById(R.id.vocation);
        vocation.setOnClickListener(this);
        location = (LinearLayout) findViewById(R.id.location);
        location.setOnClickListener(this);
//        select = (LinearLayout) findViewById(R.id.select);
//        select.setOnClickListener(this);

        shop_name = (EditText) findViewById(R.id.shop_name);
        shop_host = (EditText) findViewById(R.id.shop_host);
        contact_name = (EditText) findViewById(R.id.contact_name);
        contact_tel = (EditText) findViewById(R.id.contact_tel);
        const_tel = (EditText) findViewById(R.id.const_tel);
        contact_intel = (EditText) findViewById(R.id.contact_intel);
//        shop_employee = (EditText) findViewById(R.id.shop_employee);
//        shop_employee.setOnClickListener(this);
        contact_email = (EditText) findViewById(R.id.contact_email);
        occupation = (EditText) findViewById(R.id.occupation);
        occupation.setOnClickListener(this);
        address = (EditText) findViewById(R.id.address);
        address.setOnClickListener(this);
        detail_address = (EditText) findViewById(R.id.detail_address);
        commodity = (EditText) findViewById(R.id.commodity);
        map_locate = (Button) findViewById(R.id.map_locate);
        map_locate.setOnClickListener(this);

        top_ad = (ImageView) findViewById(R.id.top_ad);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);

        view = View.inflate(getApplicationContext(), R.layout.dialog_layout, null);
        listView = (ListView) view.findViewById(R.id.list);
        getStoreInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                editShopInfo();
                break;
            case R.id.map_locate:
                Intent intent=new Intent(ShopInfoActivity.this,StoreLocationActivity.class);
                intent.putExtra("lng",store.getLng());
                intent.putExtra("lat",store.getLat());
                startActivity(intent);
                break;
//            case R.id.select:
//            case R.id.shop_employee:
//                //选择客服员工
//                getEmployeeList();
//                break;
            case R.id.vocation:
            case R.id.occupation:
                //选择行业
                if (vocationlist == null) {
                    getList("trade", "");
                } else {
                    //
                    setVocation();
                }
                break;
            case R.id.locate:
            case R.id.address:
                //选择地区
                if(provincelist == null || citylist == null || suburblist == null) {
                    getList("sheng", "");
                }else {
                    //
                    setProvince();
                }
                break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject object = null;
            switch ( msg.what ) {
                case 1:
                    store = (com.tangpo.lianfu.entity.StoreInfo) msg.obj;
                    shop_name.setText(store.getStore());
                    shop_host.setText(store.getContact());
                    contact_name.setText(store.getLinkman());
                    contact_tel.setText(store.getPhone());
                    const_tel.setText(store.getTel());
                    contact_intel.setText(store.getQq());
                    contact_email.setText(store.getEmail());
                    occupation.setText(store.getTrade());
                    address.setText(store.getSheng()+store.getShi()+store.getXian());
                    detail_address.setText(store.getAddress());
                    commodity.setText(store.getBusiness());
                    /**
                     * 需要修改的：地图定位，加载图片
                     */
                    Tools.setPhoto(ShopInfoActivity.this, store.getBanner(), top_ad);
                    String tmp[] = store.getPhoto().split("\\,");
                    img1.setVisibility(View.INVISIBLE);
                    img2.setVisibility(View.INVISIBLE);
                    img3.setVisibility(View.INVISIBLE);
                    if(tmp.length>0){
                        img1.setVisibility(View.VISIBLE);
                        Tools.setPhoto(ShopInfoActivity.this, tmp[0], img1);
                    }
                    if(tmp.length>1){
                        img2.setVisibility(View.VISIBLE);
                        Tools.setPhoto(ShopInfoActivity.this, tmp[1], img2);
                    }
                    if(tmp.length>2){
                        img3.setVisibility(View.VISIBLE);
                        Tools.setPhoto(ShopInfoActivity.this,tmp[2],img3);
                    }
                    break;
                case 2:
                    object = (JSONObject) msg.obj;
                    try {
                        vocationlist = object.getString("listtxts").split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setVocation();
                    break;
                case 3:
                    object = (JSONObject) msg.obj;
                    try {
                        provincelist = object.getString("listtxts").split(",");
                        provinceid = object.getString("listids").split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setProvince();
                    break;
                case 4:
                    object = (JSONObject) msg.obj;
                    try {
                        citylist = object.getString("listtxts").split(",");
                        cityid = object.getString("listids").split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setCity();
                    break;
                case 5:
                    object = (JSONObject) msg.obj;
                    try {
                        suburblist = object.getString("listtxts").split(",");
                        suburbid = object.getString("listids").split(",");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setSuburb();
                    break;
//                case 6:
//                    employeelist = (ArrayList<Employee>) msg.obj;
//                    int index = (page - 1) * 10;
//                    for(int i=0; i<10; i++) {
//                        stafflist[i] = employeelist.get(index + i).getName();
//                    }
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, stafflist);
//                    listView.setAdapter(adapter);
//                    setEmployee();
//                    break;
            }
        }
    };

    private void setVocation() {
        //
        new AlertDialog.Builder(ShopInfoActivity.this).setTitle("请选择所属行业").setItems(vocationlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                occupation.setText(vocationlist[which]);
            }
        }).show();
    }
    private void setProvince() {
        //
        new AlertDialog.Builder(ShopInfoActivity.this).setTitle("请选择店铺所在的省份").setItems(provincelist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                one = provincelist[which];
                getList("shi", provinceid[which]);
            }
        }).show();
    }

    private void setCity() {
        //
        new AlertDialog.Builder(ShopInfoActivity.this).setTitle("请选择店铺所在的城市").setItems(citylist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                two = citylist[which];
                getList("xian", cityid[which]);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //
                one = "";
                setProvince();
            }
        }).show();
    }

    private void setSuburb() {
        //
        new AlertDialog.Builder(ShopInfoActivity.this).setTitle("请选择店铺所在的区域").setItems(suburblist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                address.setText(one + two + suburblist[which]);
                one = two = "";
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //
                two = "";
                setCity();
            }
        }).show();
    }

//    private void setEmployee() {
//        //
//        Log.e("tag", stafflist.length + "");
//        AlertDialog dialog = new AlertDialog.Builder(ShopInfoActivity.this).setTitle("请选择客服员工")
//                .setItems(stafflist, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //
//                        shop_employee.setText(stafflist[(page - 1) * 10 + which]);
//                    }
//                }).setPositiveButton("下一页", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //
//                        page++;
//                        if (employeelist.size() < page * 10) {
//                            getEmployeeList();
//                        } else {
//                            Message msg = new Message();
//                            msg.what = 6;
//                            handler.sendMessage(msg);
//                        }
//                        // 条件成立能关闭 AlertDialog 窗口
//                        try
//                        {
//                            Field field = dialog.getClass().getSuperclass().getDeclaredField( "mShowing" );
//                            field.setAccessible( true );
//                            field.set( dialog,
//                                    true ); // true - 使之可以关闭(此为机关所在，其它语句相同)
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                }).setNegativeButton("上一页", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //
//                        if (page > 1) {
//                            page--;
//                            Message msg = new Message();
//                            msg.what = 6;
//                            handler.sendMessage(msg);
//                        } else {
//                            //
//                            Tools.showToast(getApplicationContext(), "已是第一页");
//                            // 条件不成立不能关闭 AlertDialog 窗口
//                            try {
//                                Field field = dialog.getClass().getSuperclass().getDeclaredField( "mShowing" );
//                                field.setAccessible( true );
//                                field.set( dialog,false ); // false - 使之不能关闭(此为机关所在，其它语句相同)
//                            }
//                            catch ( Exception e )
//                            {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).create();
//        dialog.show();
//    }

    private void getStoreInfo() {
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }

        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[] = new String[]{user.getStore_id(), user.getUser_id()};
        String param = StoreInfo.packagingParam(this, kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                try {
                    store = gson.fromJson(result.getJSONObject("param").toString(), com.tangpo.lianfu.entity.StoreInfo.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = store;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    Tools.handleResult(ShopInfoActivity.this, result.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    private void editShopInfo(){
        if(!Tools.checkLAN()) {
            Tools.showToast(getApplicationContext(), "网络未连接，请联网后重试");
            return;
        }
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String kvs[]=new String[]{store.getStore_id(),shop_name.getText().toString(),shop_host.getText().toString(),
                contact_name.getText().toString(),contact_tel.getText().toString(),const_tel.getText().toString(),
                store.getLng(),store.getLat(),contact_intel.getText().toString(),contact_email.getText().toString(),detail_address.getText().toString(),
                store.getSinguser(),occupation.getText().toString(), store.getSheng(),store.getShi(), store.getXian(),commodity.getText().toString()};
        String params= EditStore.packagingParam(this,kvs);
        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                ToastUtils.showToast(ShopInfoActivity.this, getString(R.string.edit_success), Toast.LENGTH_SHORT);
                finish();
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if(status.equals("1")){
                        ToastUtils.showToast(ShopInfoActivity.this,getString(R.string.format_error),Toast.LENGTH_SHORT);
                    }else if(status.equals("10")){
                        ToastUtils.showToast(ShopInfoActivity.this,getString(R.string.server_exception),Toast.LENGTH_SHORT);
                    }else{
                        ToastUtils.showToast(ShopInfoActivity.this,getString(R.string.input_error),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },params);
    }

    private void getList(final String one, String two){
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));
        String[] kvs = new String[]{one, two};
        String param = GetTypeList.packagingParam(getApplicationContext(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                JSONObject object = null;
                try {
                    object = result.getJSONObject("param");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                if ("trade".equals(one)) {
                    msg.what = 2;
                } else if ("sheng".equals(one)) {
                    msg.what = 3;
                } else if ("shi".equals(one)){
                    msg.what = 4;
                } else if ("xian".equals(one)) {
                    msg.what = 5;
                }
                msg.obj = object;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                dialog.dismiss();
                try {
                    if ("10".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), getString(R.string.server_exception));
                    } else if("3".equals(result.getString("status"))) {
                        Tools.showToast(getApplicationContext(), "列表不存在");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, param);
    }

    private void getEmployeeList() {
        dialog = ProgressDialog.show(this, getString(R.string.connecting), getString(R.string.please_wait));

        String[] kvs = new String []{user.getUser_id(), user.getStore_id(), "", "", "", page + "", "10"};
        String param = StaffManagement.packagingParam(getApplicationContext(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //
                dialog.dismiss();
                JSONObject object = null;
                try {
                    JSONArray objects = result.getJSONArray("param");
                    for (int i = 0; i<objects.length(); i++) {
                        object = objects.getJSONObject(i);
                        Employee employee = gson.fromJson(object.toString(), Employee.class);
                        employeelist.add(employee);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 6;
                msg.obj = employeelist;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //
                dialog.dismiss();
            }
        }, param);
    }
}
