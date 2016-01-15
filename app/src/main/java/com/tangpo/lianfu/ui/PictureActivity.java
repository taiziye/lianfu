package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.ImageCache;
import com.tangpo.lianfu.utils.Tools;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 果冻 on 2016/1/1.
 */
public class PictureActivity extends Activity implements View.OnClickListener {
    private ImageView pic;
    private Bitmap bitmap;
    private String localFilePath;
    private boolean isDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.picture);

        pic = (ImageView) findViewById(R.id.pic);
        pic.setOnClickListener(this);

        String flag = getIntent().getStringExtra("flag");
        if ("url".equals(flag)) {
            String url = getIntent().getStringExtra("url");
            if (isExist(url)) {
                bitmap = BitmapFactory.decodeFile(url);
                pic.setImageBitmap(bitmap);
            } else {
                Tools.setPhoto(PictureActivity.this, url, pic);
            }
        } else if ("uri".equals(flag)) {
            Uri uri = getIntent().getParcelableExtra("uri");
            bitmap = ImageCache.getInstance().get(uri.getPath());
            if (bitmap != null) {
                pic.setImageBitmap(bitmap);
            } else {
                pic.setImageResource(R.drawable.camera);
            }
        } else {
            String remotePath = getIntent().getStringExtra("remotepath");
            String secret = getIntent().getStringExtra("secret");
            if (remotePath != null) {
                Map<String, String> maps = new HashMap<String, String>();
                if (!TextUtils.isEmpty(secret)) {
                    maps.put("share-secret", secret);
                }
                downloadImage(remotePath, maps);
            }
        }
    }

    private boolean isExist(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pic:
            default:
                finish();
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }
    private ProgressDialog pd = null;
    /**
     * 下载图片
     *
     * @param remoteFilePath
     */
    private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        localFilePath = getLocalFilePath(remoteFilePath);
        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenWidth = metrics.widthPixels;
                        int screenHeight = metrics.heightPixels;

                        bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
                        if (bitmap == null) {
                            pic.setImageResource(R.drawable.camera);
                        } else {
                            pic.setImageBitmap(bitmap);
                            ImageCache.getInstance().put(localFilePath, bitmap);
                            isDownloaded = true;
                        }
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                });
            }

            public void onError(int error, String msg) {
                File file = new File(localFilePath);
                if (file.exists()&&file.isFile()) {
                    file.delete();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        pic.setImageResource(R.drawable.camera);
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.setMessage(str2 + progress + "%");
                    }
                });
            }
        };
        EMChatManager.getInstance().downloadFile(remoteFilePath, localFilePath, headers, callback);
    }

    /**
     * 通过远程URL，确定下本地下载后的localurl
     * @param remoteUrl
     * @return
     */
    public String getLocalFilePath(String remoteUrl){
        String localPath;
        if (remoteUrl.contains("/")){
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
                    + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
        }else{
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
        }
        return localPath;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isDownloaded) {
            setResult(RESULT_OK);
        }
        finish();
    }
}
