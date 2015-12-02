package com.tangpo.lianfu;

import android.app.Application;
import android.content.Context;

import com.tangpo.lianfu.config.WeiXin.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by shengshoubo on 2015/11/2.
 */
public class MyApplication extends Application {

    public static Context context;
    private boolean isDownload;



    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        /*File cacheDir = StorageUtils.getCacheDirectory(context);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(2 * 1024 * 1024) //缓存到内存的最大数据
                .discCache(new UnlimitedDiscCache(cacheDir))
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCacheSize(100 * 1024 * 1024)
                .discCacheFileCount(1000)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);*/
    }

    public static Context getContext() {
        return context;
    }
}
