package com.tangpo.lianfu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tangpo.lianfu.MyApplication;
import com.tangpo.lianfu.R;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 果冻 on 2015/11/7.
 * 该类包含一些功能函数：页面跳转
 */
public class Tools {

    public static final Context context = MyApplication.getContext();

    public static final String URL = "http:\\/\\/182.92.191.236:10000\\/";

    private static List<Activity> activityList = new LinkedList<Activity>();

    /**
     * 开启activity，无参数跳转
     *
     * @param context
     * @param activity
     */
    public static void gotoActivity(Context context, Class<?> activity) {
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
        // startActivity(new
        // Intent(this,MsgListActivity.class).putExtra(Config.KEY_TOKEN,
        // token));
    }

    /**
     * 获取当前应用版本号(例：2)
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取当前应用的版本名称(例：1.2)
     *
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * * 动态设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 显示Toast
     */
    public static void showToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        View view = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.toast_layout, null);
        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        tv_message.setText(msg);
        toast.setView(view);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv_message
                .getLayoutParams();
        lp.width = width - Tools.dip2px(context, 50);
        tv_message.setLayoutParams(lp);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 检查输入的手机号码是否正确
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNum(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern
                    .compile("^(((13|15|18|14|17)[0-9]{9})|((13|15|18|14|17)[0-9]{9})(,|,)((13|15|18|14|17)[0-9]{9}))$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 加载网络图片
     *
     * @param context
     * @param path    图片存放路径
     * @param img     放置图片的控件
     */
    public static void setPhoto(Context context, String path, final ImageView img) {

        ImageLoader imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.camera)
                .showImageForEmptyUri(R.drawable.camera).showImageOnFail(R.drawable.camera)
                .resetViewBeforeLoading(false).delayBeforeLoading(1000).cacheInMemory(false).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300)).handler(new Handler()).build();

        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.displayImage(path, img, options);
    }

    /**
     * 收集Activity实例
     *
     * @param activity
     */
    public static void gatherActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void deleteActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static void closeActivity() {
        for (int i = activityList.size() - 1; i >= 0; i--) {
            activityList.get(i).finish();
        }
    }

}
