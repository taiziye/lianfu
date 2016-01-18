package com.tangpo.lianfu.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by shengshoubo on 2015/11/18.
 */
public class UploadImage {

    public static String imgToBase64(String imgPath) {

//        Bitmap bitmap = null;
//        if (imgPath != null && imgPath.length() > 0) {
//            bitmap = readBitmap(imgPath);
//        }
//        if (bitmap == null) {
//            //bitmap not found!!
//            return null;
//        }

//        String res ="";
//        File f = new File(imgPath);
//        Log.e("tag","size:"+f.length());
//        try {
//            FileInputStream fis = new FileInputStream(f);
//            byte[] b = new byte[1024 * 500];
//            while (fis.read(b, 0, b.length) > 0) {
//                res += String.valueOf(Base64.encode(b, Base64.NO_WRAP));
//            }
//            fis.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return URLEncoder.encode(res);
//    }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imgPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[512];

        int count = 0;

        try {
            while((count = fis.read(buffer)) > 0){

                baos.write(buffer, 0, count);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //path = new String(new BASE64Encoder().encodeBuffer(baos.toByteArray()));
        String res=URLEncoder.encode(Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP));
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
//        ByteArrayOutputStream out = null;
//        try {
//            out = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//
//            out.flush();
//            out.close();
////            byte[] imgBytes=new byte[1024*500];
//
//            byte[] imgBytes = out.toByteArray();
//            Log.e("tag","size:"+imgBytes.length);
//            return URLEncoder.encode(Base64.encodeToString(imgBytes, Base64.NO_WRAP));
////            return URLEncoder.encode(String.valueOf(Base64.encode(imgBytes, Base64.NO_WRAP)));
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            return null;
//        } finally {
//            try {
//                out.flush();
//                out.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
    }

    private static Bitmap readBitmap(String imgPath) {
        try {
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }

    }
}
