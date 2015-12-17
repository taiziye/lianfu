package com.tangpo.lianfu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.CircularImage;

/**
 * Created by 果冻 on 2015/12/15.
 */
public class ChatAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;

    public ChatAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //根据数据设置holder
        if (true) {  //根据情形是否需要显示
            holder.he.setVisibility(View.GONE);
        } else if (true) {
            holder.me.setVisibility(View.GONE);
        }
        return convertView;
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
    }
}
