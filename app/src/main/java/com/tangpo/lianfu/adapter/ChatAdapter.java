package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.entity.Chat;
import com.tangpo.lianfu.utils.CircularImage;
import com.tangpo.lianfu.utils.Tools;

import java.io.File;
import java.util.List;

/**
 * Created by 果冻 on 2015/12/15.
 */
public class ChatAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Chat> list;
    private String hxid;

    public ChatAdapter(Context context, List<Chat> list, String hxid) {
        this.context = context;
        this.list = list;
        this.hxid = hxid;
        this.inflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_list, parent, false);

            holder.he = (LinearLayout) convertView.findViewById(R.id.he);
            holder.me = (LinearLayout) convertView.findViewById(R.id.me);
            holder.heimg = (CircularImage) convertView.findViewById(R.id.heimg);
            holder.meimg = (CircularImage) convertView.findViewById(R.id.meimg);
            holder.he_text = (TextView) convertView.findViewById(R.id.he_text);
            holder.me_text = (TextView) convertView.findViewById(R.id.me_text);
            holder.time1 = (TextView) convertView.findViewById(R.id.time1);
            holder.time2 = (TextView) convertView.findViewById(R.id.time2);
            holder.img1 = (ImageView) convertView.findViewById(R.id.img1);
            holder.img2 = (ImageView) convertView.findViewById(R.id.img2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.me.setVisibility(View.VISIBLE);
        holder.he.setVisibility(View.VISIBLE);

        String tmp = list.get(position).getMsg();
        //根据数据设置holder
        if ( list.get(position).getHxid().toLowerCase().equals(hxid.toLowerCase()) ) {  //根据情形是否需要显示
            if (tmp.startsWith("http") || isExist(tmp)) { //图片
                holder.me_text.setVisibility(View.GONE);
                holder.img2.setVisibility(View.VISIBLE);
                Bitmap bm = BitmapFactory.decodeFile(tmp);
                holder.img2.setImageBitmap(bm);
            } else {
                holder.me_text.setVisibility(View.VISIBLE);
                holder.img2.setVisibility(View.GONE);
                holder.me_text.setText(tmp);
            }
            holder.time2.setText(list.get(position).getTime());
            Tools.setPhoto(context, list.get(position).getImg(), holder.meimg);
            holder.he.setVisibility(View.GONE);
        } else {
            if (tmp.startsWith("http") || isExist(tmp)) { //图片
                holder.he_text.setVisibility(View.GONE);
                holder.img1.setVisibility(View.VISIBLE);
                Bitmap bm = BitmapFactory.decodeFile(tmp);
                holder.img1.setImageBitmap(bm);
            } else {
                holder.he_text.setVisibility(View.VISIBLE);
                holder.img1.setVisibility(View.GONE);
                holder.he_text.setText(tmp);
            }
            holder.time1.setText(list.get(position).getTime());
            Tools.setPhoto(context, list.get(position).getImg(), holder.heimg);
            holder.me.setVisibility(View.GONE);
        }
        return convertView;
    }

    private boolean isExist(String filepath) {
        File file = new File(filepath);
        Log.e("tag", "file");
        if (!file.exists()) {
            return true;
        }
        return false;
    }

    class ViewHolder{
        LinearLayout he;
        LinearLayout me;
        CircularImage heimg;
        CircularImage meimg;
        TextView he_text;
        TextView me_text;
        TextView time1;
        TextView time2;
        ImageView img1;
        ImageView img2;
    }
}
