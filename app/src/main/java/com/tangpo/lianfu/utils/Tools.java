package com.tangpo.lianfu.utils;

import android.app.Activity;
<<<<<<< HEAD
import android.content.ContentValues;
=======
import android.content.ContentResolver;
>>>>>>> d319bc09f1bef04e56d32912f5dae5b862d1f9db
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.easemob.util.PathUtil;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.tangpo.lianfu.MyApplication;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.config.Configs;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.ChatUser;
import com.tangpo.lianfu.ui.MainActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 果冻 on 2015/11/7.
 * 该类包含一些功能函数：页面跳转
 */
public class Tools {

    public static final int DATABASE_VERSION = 1;

    public static final Context context = MyApplication.getContext();

    public static final String URL = "http:\\/\\/182.92.191.236:10000\\/";

    private static List<Activity> activityList = new LinkedList<Activity>();

    private static Toast toast;

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
//        View view = ((LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
//                R.layout.toast_layout, null);
//        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
//        WindowManager wm = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        int width = wm.getDefaultDisplay().getWidth();
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv_message
//                .getLayoutParams();
//        lp.width = width - Tools.dip2px(context, 50);
//        tv_message.setLayoutParams(lp);
//        tv_message.setText(msg);
//        if(toast==null){
//            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
//            toast.setView(view);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//        }else{
//            toast.setText(msg);
//        }
        if(toast==null){
            toast=Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else{
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
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
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern
                .compile("^[1][3,4,5,8][0-9]{9}$");
        m = p.matcher(mobiles);
        b = m.matches();
        return b;
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

    public static void setPhoto(Context context,ImageView img,String path){
//        File file=new File(path);
//        Bitmap bitmap=null;
//        if(file.exists()){
//            bitmap=BitmapFactory.decodeFile(path);
//        }
        //img.setImageBitmap(bitmap);
//        img.setImageURI(Uri.parse(path));
        ContentResolver cr = context.getContentResolver();
        try {
            Bitmap bitmap= BitmapFactory.decodeStream(cr.openInputStream(Uri.parse(path)));
            img.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public static boolean checkLAN(){
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 时间排序
     */
    public static int CompareDate(String s1, String s2) {
        boolean flag = false;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss",
                Locale.CHINA);
        try {
            Date d1 = dateFormat.parse(s1);
            Date d2 = dateFormat.parse(s2);
            if(d1.getTime() > d2.getTime()) {
                flag = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(flag) {
            return 1;
        } else {
            return -1;
        }
    }

    public static String long2DateString(Long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String str = new SimpleDateFormat("dd号 HH:mm").format(calendar.getTime());
        return str;
    }

    public static void saveAccount(ChatAccount account) {
        DataHelper helper = new DataHelper(MyApplication.context);
        helper.saveChatAccount(account);
        helper.close();
    }

    public static void saveConversation(ChatUser user) {
        DataHelper helper = new DataHelper(MyApplication.context);
        helper.saveChatUser(user);
        helper.close();
    }

    public static List<ChatUser> getChatUserList() {
        DataHelper helper = new DataHelper(MyApplication.context);
        return helper.getChatUser();
    }

    public static void setHeight(BaseAdapter adapter, PullToRefreshListView list) {
        int height = 0;
        for (int i = 0; i<adapter.getCount(); i++) {
            View item = adapter.getView(i, null, list);
            item.measure(0, 0);
            height += item.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = list.getLayoutParams();
        params.height = height + (list.getDividerPadding() * (adapter.getCount() - 1));
        list.setLayoutParams(params);
    }

    public static void downLoadImg(String localurl, final String remotepath, String filename) {
        final String name = localurl + filename.substring(filename.lastIndexOf(".") + 1);
        new Thread() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL(remotepath);
                    InputStream is = url.openStream();
                    OutputStream os = context.openFileOutput(name, context.MODE_APPEND);
                    byte[] buff = new byte[1024];
                    int hasRead = 0;
                    while ((hasRead = is.read(buff)) > 0) {
                        os.write(buff, 0, hasRead);
                    }
                    is.close();
                    os.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static String getImagePath(String remoteurl) {
        String imageName = remoteurl.substring(remoteurl.lastIndexOf("/") +1, remoteurl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + imageName;
        return path;
    }

    public static String getThumbnailImagePath(String thumbRemoteUrl) {
        String thumbImageName = thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
        String path = PathUtil.getInstance().getImagePath() + "/" + "th" + thumbImageName;
        return path;
    }

    /*public static String getPath() {
        String file = Environment.getDownloadCacheDirectory().toString();
        return file;
    }*/

    /**
     * 保存最近会话人
     * @param account
     *//*
    synchronized public static void saveContact(ChatAccount account){
        SQLiteDatabase db = SqliteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ChatAccount.COLUMN_USER_ID, account.getUser_id());
        if (account.getUsername() != null) values.put(ChatAccount.COLUMN_USERNAME, account.getUsername());
        if (account.getName() != null) values.put(ChatAccount.COLUMN_NAME, account.getName());
        if (account.getPhone() != null) values.put(ChatAccount.COLUMN_PHONE, account.getPhone());
        values.put(ChatAccount.COLUMN_EASEMOD_ID, account.getEasemod_id());
        values.put(ChatAccount.COLUMN_UUID, account.getUuid());
        values.put(ChatAccount.COLUMN_PWD, account.getPwd());
        values.put(ChatAccount.COLUMN_MSG, account.getMsg());
        values.put(ChatAccount.COLUMN_TIME, account.getTime());
        if (account.getPhoto() != null) values.put(ChatAccount.COLUMN_PHOTO, account.getPhoto());
    }*/

    /**
     * 获取最近会话人列表
     * @return
     *//*
    synchronized public static List<ChatAccount> getContactList() {
        SQLiteDatabase db = SqliteHelper.getInstance(context).getReadableDatabase();
        List<ChatAccount> users = new ArrayList<ChatAccount>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + ChatAccount.TABLE_NAME *//* + " desc" *//*, null);
            while (cursor.moveToNext()) {
                String user_id = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_USER_ID));
                String username = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_USERNAME));
                String name = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_PHONE));
                String easemod_id = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_EASEMOD_ID));
                String uuid = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_UUID));
                String pwd = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_PWD));
                String photo = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_PHOTO));
                String msg = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_MSG));
                String time = cursor.getString(cursor.getColumnIndex(ChatAccount.COLUMN_TIME));
                ChatAccount account = new ChatAccount();
                account.setUser_id(user_id);
                account.setUsername(username);
                account.setName(name);
                account.setPhone(phone);
                account.setEasemod_id(easemod_id);
                account.setUuid(uuid);
                account.setPwd(pwd);
                account.setPhoto(photo);
                account.setMsg(msg);
                account.setTime(time);

                users.add(account);
            }
            cursor.close();
        }
        return users;
    }

    public static void closeDB() {
        SqliteHelper.getInstance(context).closeDB();
    }*/

}
