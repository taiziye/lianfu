package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.utils.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SelectPicActivity extends Activity implements OnClickListener {

    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;

    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    public static final String KEY_PHOTO_PATH = "photo_path";

    public static final String SMALL_KEY_PHOTO_PATH = "smalll_photo_path";

    private String flag = "";

    private LinearLayout dialogLayout;
    private TextView takePhotoBtn, pickPhotoBtn, cancelBtn;

    /** 获取到的图片路径 */
    private String picPath = "";

    private String SmallpicPath="";

    private Intent lastIntent;

    private Uri photoUri;
    private File FILE_DIR = new File(Environment.getExternalStorageDirectory() + "/data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.take_picture);
        Tools.gatherActivity(SelectPicActivity.this);
        init();
        flag = getIntent().getStringExtra("flag");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    private void init() {
        dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
        dialogLayout.setOnClickListener(this);
        takePhotoBtn = (TextView) findViewById(R.id.btn_take_photo);
        takePhotoBtn.setOnClickListener(this);
        pickPhotoBtn = (TextView) findViewById(R.id.btn_pick_photo);
        pickPhotoBtn.setOnClickListener(this);
        cancelBtn = (TextView) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        lastIntent = getIntent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_layout:
                finish();
                break;
            case R.id.btn_take_photo:
                takePhoto();
                break;
            case R.id.btn_pick_photo:
                pickPhoto();
                break;
            default:
                finish();
                break;
        }
    }

    private void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);

            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Tools.showToast(this, "内存卡不存在");
        }
    }

    private void pickPhoto() {
        if(getIntent().getStringExtra("name")==null){
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            //intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
        }else{
            Intent intent=new Intent(this,PhotoAlbumActivity.class);
            startActivity(intent);
            finish();
            //startActivityForResult(intent,);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            doPhoto(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doPhoto(int requestCode, Intent data) {

        if (requestCode == SELECT_PIC_BY_PICK_PHOTO) // 从相册取图片，有些手机有异常情况，请注意
        {
            if (data == null) {
                Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                return;
            }
            photoUri = data.getData();
            Bitmap bitmap1;
            if (photoUri == null) {
                Bundle bundle=data.getExtras();
                if(bundle!=null){
                    bitmap1= (Bitmap) bundle.get("data");
                    if (bitmap1 != null) {
                        picPath = FILE_DIR.toString() + "/" + System.currentTimeMillis() + ".jpg";
                        Tools.isHasFile(picPath);
                        Tools.saveImage(bitmap1, picPath);
                        SmallpicPath = save(picPath);
                    }
                }else{
                    Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
                }
            }else{
                ContentResolver cr = this.getContentResolver();
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds=false;
                    options.inPreferredConfig=Bitmap.Config.RGB_565;
                    options.inPurgeable=true;
                    options.inInputShareable=true;
                    options.inDither=false;
                    options.inSampleSize =calculateInSampleSize(options,128,128);
                    bitmap1 = BitmapFactory.decodeStream(cr.openInputStream(photoUri),null,options);

                    if(bitmap1 == null) {
                    }

                    if (bitmap1 != null) {
                        picPath = FILE_DIR.toString() + "/" + System.currentTimeMillis() + ".jpg";
                        Tools.isHasFile(picPath);
                        Tools.saveImage(bitmap1, picPath);
                        SmallpicPath = save(picPath);
                    }
                    // bitmap = ImageUtil.toRoundBitmap(bitmap1);
                } catch (FileNotFoundException e) {

                }
            }
        } else {
            String[] pojo = { MediaStore.Images.Media.DATA };
            Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                cursor.moveToFirst();
                picPath = cursor.getString(columnIndex);
                cursor.close();
            }
            SmallpicPath = save(picPath);
        }
//        // 返回图片的地址
//        if (flag != null && flag.length() != 0) {
//            //
//            photoUri = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//            Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
//            if (cursor != null) {
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                String picturePath = cursor.getString(columnIndex);
//                cursor.close();
//                cursor = null;
//
//                if (picturePath == null || picturePath.equals("null")) {
//                    Toast toast = Toast.makeText(this, "选择图片失败", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
//                    return;
//                }
//                lastIntent.putExtra(KEY_PHOTO_PATH, picturePath);
//            } else {
//                File file = new File(photoUri.getPath());
//                if (!file.exists()) {
//                    Toast toast = Toast.makeText(this, "选择图片失败", Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
//                    return;
//                }
//                lastIntent.putExtra(KEY_PHOTO_PATH, file.getAbsolutePath());
//            }
//        } else {
//            lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
//            lastIntent.putExtra(SMALL_KEY_PHOTO_PATH, SmallpicPath);
//        }
        lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
        lastIntent.putExtra(SMALL_KEY_PHOTO_PATH, SmallpicPath);
        setResult(Activity.RESULT_OK, lastIntent);
        finish();
    }
    public static String save(String picpath) {

        String path = "";

        if (picpath != null && picpath.length() > 0) {

            try {
                File f = new File(picpath);

                if (f.exists()) {
                    Bitmap bm = getSmallBitmap(picpath);
                    String tmppath = Environment.getExternalStorageDirectory()+"/data";

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
            }

        } else {
            // Toast.makeText(this, "请先点击拍照按钮拍摄照片", Toast.LENGTH_SHORT).show();
        }

        return path;
    }
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 128, 128);
//        BitmapFactory.decodeFile(filePath, options);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

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

}
