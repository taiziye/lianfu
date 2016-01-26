package com.tangpo.lianfu.service;

import android.net.Uri;

import com.tangpo.lianfu.utils.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 果冻 on 2016/1/25.
 */
public class CacheService {
    public Uri getImageURI(String path, File cache) throws Exception {
        File file = new File(cache, path);
        if (file.exists()) {
            return Uri.fromFile(file);
        }
        String name = Tools.getMD5(path) + path.substring(path.lastIndexOf("."));
        file = new File(cache, name);

        if (file.exists()) {
            return Uri.fromFile(file);
        } else {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                return Uri.fromFile(file);
            }
        }
        return null;
    }
}
