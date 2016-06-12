package com.showjoy.tashow.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件操作类
 */
public class FileUtils {

    public static File createTmpFile(Context context){

        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            // 已挂载
            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_"+timeStamp+"";
            File tmpFile = new File(pic, fileName+".jpg");
            return tmpFile;
        }else{
            File cacheDir = context.getCacheDir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "multi_image_"+timeStamp+"";
            File tmpFile = new File(cacheDir, fileName+".jpg");
            return tmpFile;
        }
    }

    public static String getPath(final Context context, final Uri uri) {
        ContentResolver cr = context.getContentResolver();
        String uploadPhotPath="";
        try {
            // 先得到bitmap图片
            Bitmap bm = MediaStore.Images.Media.getBitmap(cr, uri);
            String [] proj={MediaStore.Images.Media.DATA};
            Cursor cursor = cr.query(uri, proj,null,null,null);
            if(cursor!=null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//找到图片的索引
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    uploadPhotPath = cursor.getString(column_index);//获取路径
                }
            }
            bm.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uploadPhotPath;
    }
}
