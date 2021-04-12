package io.fuwafuwa.banjo.extension;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;

public class ActivityRequest {

    public static final int REQUEST_LOCAL_MEDIA = 0x0011;
    public static final int REQUEST_LOCAL_VIDEO = 0x0013;
    public static final int REQUEST_READ_LOCAL_PERMISSIONS = 0x0500;
    public static final int REQUEST_WRITE_LOCAL_PERMISSIONS = 0x5000;

    public static void toast(@NonNull Context activity, String content) {
        Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
    }

    public static void requestMediaCommon(@NonNull Activity activity, String type, int reqCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            toast(activity, "播放本地视频需要读取SD卡，请允许操作SD卡的权限。");
            return;
        }
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(type);
            activity.startActivityForResult(intent, reqCode);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_LOCAL_PERMISSIONS);
        }
    }

    public static void requestMediaCommon(@NonNull Fragment fragment, String type, int reqCode) {
        Activity context = fragment.getActivity();
        if (context == null) return;
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            toast(context, "播放本地视频需要读取SD卡，请允许操作SD卡的权限。");
            return;
        }
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(type);
            fragment.startActivityForResult(intent, reqCode);
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_LOCAL_PERMISSIONS);
        }
    }

    public static boolean hasReadPermission(Activity activity) {
        boolean isDenied = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        return !isDenied && ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasWritePermission(Activity activity) {
        boolean isDenied = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return !isDenied && ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestMedia(Activity activity) {
        requestMediaCommon(activity, "*/*", REQUEST_LOCAL_MEDIA);
    }

    public static void requestMediaVideo(Activity activity) {
        requestMediaCommon(activity, "video/*", REQUEST_LOCAL_VIDEO);
    }

    public static void requestMediaVideo(Fragment fragment) {
        requestMediaCommon(fragment, "video/*", REQUEST_LOCAL_VIDEO);
    }


    public static void requestExternalStorageWrite(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_WRITE_LOCAL_PERMISSIONS);
    }

    public static void requestExternalStorageRead(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_LOCAL_PERMISSIONS);
    }

    public static boolean grantRequestDenied(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }


    public static void openFileDir(@NonNull Context context, String authority, @NonNull File file) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = FileProvider.getUriForFile(context, authority, file);
        intent.setDataAndType(uri, "video/*");
        try {
            context.startActivity(intent);
            //Intent.createChooser(intent, "浏览文件")
        } catch (ActivityNotFoundException e) {
            toast(context, "手机上未找到打开该目录的应用");
        }
    }

}

