package com.tangpo.lianfu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
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
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.ui.MainActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static void isHasFile(String path) {
        if (path != null && path.length() != 0) {
            return;
        }
        File file = new File(path);
        if (file != null && file.exists()) {
            file.delete();
        } else {
            File fileParent = file.getParentFile();
            fileParent.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存图片到本地
     * @param image
     * @param file
     * @return
     */
    public static Bitmap saveImage(Bitmap image, String file){
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        image.compress(Bitmap.CompressFormat.JPEG, 35, bos);
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return image;
    }

    public static String save(String picpath) {

        String path = "";

        if (picpath != null && picpath.length() > 0) {

            try {
                File f = new File(picpath);

                if (f.exists()) {
                    Bitmap bm = getSmallBitmap(picpath);
                    String tmppath = Environment.getDataDirectory().getAbsolutePath()+"/data";

                    File ftmp = new File(tmppath, "small_" + f.getName());

                    FileOutputStream fos = new FileOutputStream(ftmp);

                    bm.compress(Bitmap.CompressFormat.JPEG, 35, fos);

                    fos.flush();
                    fos.close();

                    path = tmppath + "/small_" + f.getName();
                } else {
                    return null;
                }

                // PictureUtil.deleteTempFile(picpath);

            } catch (Exception e) {
                Log.e("TAG", "error", e);
            }

        } else {
            // Toast.makeText(this, "请先点击拍照按钮拍摄照片", Toast.LENGTH_SHORT).show();
        }
        Log.e("TAG", "PICPATH2    " + path);

        return path;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }
    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee 保存图片并返回图片存放地址
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static void handleResult(Context context, String resultCode){
        if("1".equals(resultCode)) {
            Tools.showToast(context, context.getString(R.string.add_failed));
        } else if("2".equals(resultCode)) {
            Tools.showToast(context, context.getString(R.string.format_error));
        } else if("9".equals(resultCode)) {
            Tools.showToast(context, context.getString(R.string.login_timeout));
            SharedPreferences preferences = context.getSharedPreferences(Configs.APP_ID, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(Configs.KEY_TOKEN);
            editor.commit();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } else if("10".equals(resultCode)) {
            Tools.showToast(context, context.getString(R.string.server_exception));
        }
    }
}
