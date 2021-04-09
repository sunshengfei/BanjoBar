package io.fuwafuwa.banjo.extension;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * Created by fred on 16/8/7.
 */
public class Frame {

    private static int SCREEN_HEIGHT;
    private static int SCREEN_WIDTH;
    private static float SCREEN_DENSITY;

    public static DisplayMetrics getDisplay(@NonNull WindowManager wm) {
        DisplayMetrics dm;
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }


    public static void init(WindowManager wm) {
        if (wm == null) return;
        DisplayMetrics dm;
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_DENSITY = dm.density;
    }


    public static int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public static int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    public static int px2dp(float px) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (px / density + 0.5F);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 判断屏幕是否活跃 【亮、暗】
     *
     * @param context
     * @return true 亮 false 暗
     */
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= 20) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    /**
     * 判断屏幕是否锁屏
     *
     * @param context
     * @return
     */
    public static boolean isScreenLock(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= 16) {
            return mKeyguardManager.isKeyguardLocked();
        } else {
            return mKeyguardManager.inKeyguardRestrictedInputMode();
        }
    }


    /**
     * 获取相对屏幕坐标
     *
     * @param view
     * @return
     */
    public static int[] getLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }
}
