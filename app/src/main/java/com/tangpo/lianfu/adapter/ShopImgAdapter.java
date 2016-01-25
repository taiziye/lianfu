package com.tangpo.lianfu.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.DeleteStorePicture;
import com.tangpo.lianfu.parms.StorePictureSort;
import com.tangpo.lianfu.ui.PictureActivity;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shengshoubo on 2016/1/16.
 */
public class ShopImgAdapter extends BaseAdapter {

    private Activity context;
    private List<String> list;
    private LayoutInflater container;
    private String store_id = "";
    private String user_id="";
    private ProgressDialog dialog;
    private List<Integer> order=new ArrayList<>();
    private List<String> imagePath = null;

    public ShopImgAdapter(List<String> list, Context context,
                          String store_id,String user_id) {
        this.context = (Activity)context;
        this.list = list;
        container = LayoutInflater.from(context);
        this.store_id = store_id;
        this.user_id=user_id;
        initOrder();
    }

    private void initOrder() {
        for(int i=0;i<list.size();i++){
            order.add(i);
        }
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = container.inflate(R.layout.shop_ad_list, null);
            holder = new ViewHolder();
            holder.shop_img= (ImageView) convertView.findViewById(R.id.ad);
            holder.settop= (LinearLayout) convertView.findViewById(R.id.settop);
            holder.delete= (LinearLayout) convertView.findViewById(R.id.delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (Tools.isExists(list.get(position))) {
            Tools.setPhoto(list.get(position), holder.shop_img);
        } else {
            Tools.setPhoto(context, list.get(position), holder.shop_img);
        }

        holder.shop_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PictureActivity.class);
                intent.putExtra("flag","url");
                intent.putExtra("url",list.get(position));
                context.startActivity(intent);
            }
        });

        holder.settop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int top=order.get(position);
                order.remove(position);
                order.add(0, top);
                String piclist ="";
                for(int i=0;i<order.size();i++){
                    if(i!=0)piclist+=",";
                    else piclist+=i;
                }
                sortShopImg(piclist,position);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteStoreImg(position);
            }
        });
        return convertView;
    }


    private void deleteStoreImg(final int position){
        if(!Tools.checkLAN()) {
            Tools.showToast(context, "网络未连接，请联网后重试");
            return;
        }
        dialog=ProgressDialog.show(context,context.getString(R.string.connecting),context.getString(R.string.please_wait));
        String[] kvs=new String[]{store_id,user_id,position+"","1"};
        String param= DeleteStorePicture.packagingParam(context,kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                list.remove(position);
                notifyDataSetChanged();
                Tools.showToast(context,context.getString(R.string.delete_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if("1".equals(status)){
                        Tools.showToast(context,context.getString(R.string.delete_failed));
                    }else if("10".equals(status)){
                        Tools.showToast(context,context.getString(R.string.server_exception));
                    }else {
                        Tools.showToast(context,context.getString(R.string.input_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },param);
    }

    private void sortShopImg(String piclist, final int position){
        if(!Tools.checkLAN()) {
            Tools.showToast(context, "网络未连接，请联网后重试");
            return;
        }
        dialog=ProgressDialog.show(context,context.getString(R.string.connecting),context.getString(R.string.please_wait));
        String[] kvs=new String[]{store_id,user_id,"0","1",piclist};
        String param= StorePictureSort.packagingParam(context,kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                dialog.dismiss();
                String imgPath=list.get(position);
                list.remove(position);
                list.add(0, imgPath);
                notifyDataSetChanged();
                Tools.showToast(context,context.getString(R.string.sort_success));
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                dialog.dismiss();
                try {
                    String status=result.getString("status");
                    if("1".equals(status)){
                        Tools.showToast(context,context.getString(R.string.sort_error));
                    }else if("10".equals(status)){
                        Tools.showToast(context,context.getString(R.string.server_exception));
                    }else{
                        Tools.showToast(context,context.getString(R.string.input_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },param);
    }
    private class ViewHolder {

        public ImageView shop_img;
        public LinearLayout settop;
        public LinearLayout delete;
    }
}
