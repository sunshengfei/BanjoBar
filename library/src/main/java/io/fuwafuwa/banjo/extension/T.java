package io.fuwafuwa.banjo.extension;

/**
 * Created by fred on 16/8/6.
 */

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

public class T {

    /**
     * 标明当前环境是Release还是Debug
     */
    public static boolean loggable = false;

    /**
     * 时间调试信息，以yourName为tag，日志输出<br>
     * 线下版本才会输入调试信息，线上版本会自动关闭
     *
     * @throws RuntimeException
     */
    public static void e(String tag, String msg) {
        e(tag, null, msg, null);
    }

    public static void e(String msg) {
        e("TT_TT", null, msg, null);
    }

    public static void e(String tag, String msg, Object... args) {
        msg = args == null ? msg : String.format(msg, args);
        e(tag, null, msg, null);
    }

    public static void e(String tag, String className, String msg) {
        e(tag, className, msg, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        e(tag, null, msg, tr);
    }

    public static void e(String tag, Throwable tr) {
        e(tag, null, null, tr);
    }

    public static void e(String tag, String className, String msg, Throwable tr) {
        if (loggable) {
            if (className != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("[ClassName: ").append(className).append("] msg: ").append(msg);
                msg = sb.toString();
            }
            if (tr == null)
                Log.e(tag, msg);
            else
                Log.e(tag, msg, tr);
        }
    }

    /**
     * 时间调试信息，以yourName为tag，日志输出<br>
     * 线下版本才会输入调试信息，线上版本会自动关闭
     *
     * @param yourName 你的名字，避免与其他人统计区分
     * @param message
     * @throws RuntimeException
     */
    public static void d(String yourName, String message) {
        d(yourName, null, message, null);
    }

    public static void d(String tag, String msg, Object... args) {
        msg = args == null ? msg : String.format(msg, args);
        d(tag, null, msg, null);
    }

    public static void d(String yourName, String className, String message) {
        d(yourName, className, message, null);
    }

    public static void d(String yourName, String message, Throwable tr) {
        d(yourName, null, message, tr);
    }

    public static void d(String yourName, String className, String message, Throwable tr) {
        if (loggable) {
            if (className != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("[ClassName: ").append(className).append("] msg: ").append(message);
                message = sb.toString();
            }
            if (tr == null)
                android.util.Log.d(yourName, message);
            else
                android.util.Log.d(yourName, message, tr);
        }
    }

    /**
     * 时间调试信息，以yourName为tag，日志输出<br>
     * 线下版本才会输入调试信息，线上版本会自动关闭
     *
     * @throws RuntimeException
     */

    public static void w(String tag, String msg) {
        w(tag, null, msg, null);
    }

    public static void w(String tag, String msg, Object... args) {
        msg = args == null ? msg : String.format(msg, args);
        w(tag, null, msg, null);
    }

    public static void w(String tag, String className, String msg) {
        w(tag, className, msg, null);
    }

    public static void w(String tag, String msg, Throwable tr) {
        w(tag, null, msg, tr);
    }

    public static void w(String tag, String className, String msg, Throwable tr) {
        if (loggable) {
            if (className != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("[ClassName: ").append(className).append("] msg: ").append(msg);
                msg = sb.toString();
            }
            if (tr == null)
                android.util.Log.w(tag, msg);
            else
                android.util.Log.w(tag, msg, tr);
        }
    }

    /**
     * 时间调试信息，以yourName为tag，日志输出<br>
     * 线下版本才会输入调试信息，线上版本会自动关闭
     *
     * @param yourName 你的名字，避免与其他人统计区分
     * @param message
     * @throws RuntimeException
     */
    public static void v(String yourName, String message) {
        v(yourName, null, message, null);
    }

    public static void v(String tag, String msg, Object... args) {
        msg = args == null ? msg : String.format(msg, args);
        v(tag, null, msg, null);
    }

    public static void v(String yourName, String className, String message) {
        v(yourName, className, message, null);
    }

    public static void v(String yourName, String message, Throwable tr) {
        v(yourName, null, message, tr);
    }

    public static void v(String yourName, String className, String message, Throwable tr) {
        if (loggable) {
            if (className != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("[ClassName: ").append(className).append("] msg: ").append(message);
                message = sb.toString();
            }
            if (tr == null)
                android.util.Log.v(yourName, message);
            else
                android.util.Log.v(yourName, message, tr);
        }
    }

    /**
     * 时间调试信息，以yourName为tag，日志输出<br>
     * 线下版本才会输入调试信息，线上版本会自动关闭
     *
     * @throws RuntimeException
     */

    public static void i(String tag, String msg) {
        i(tag, null, msg, null);
    }

    public static void i(String tag, String msg, Object... args) {
        msg = args == null ? msg : String.format(msg, args);
        i(tag, null, msg, null);
    }

    public static void i(String tag, String className, String msg) {
        i(tag, className, msg, null);
    }

    public static void i(String tag, String msg, Throwable tr) {
        i(tag, null, msg, tr);
    }

    public static void i(String tag, String className, String msg, Throwable tr) {
        if (loggable) {
            if (className != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("[ClassName: ").append(className).append("] msg: ").append(msg);
                msg = sb.toString();
            }
            if (tr == null)
                android.util.Log.i(tag, msg);
            else
                android.util.Log.i(tag, msg, tr);
        }
    }

    /**
     * 手机操作系统是否>=ECLAIR level5 2.0
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel5() {
        return Build.VERSION.SDK_INT >= 5;
    }

    /**
     * 手机操作系统是否>=Froyo level8 2.2
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel8() {
        return Build.VERSION.SDK_INT >= 8;
    }

    /**
     * 手机操作系统是否>=Gingerbread level9 2.3.1
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel9() {
        return Build.VERSION.SDK_INT >= 9;
    }

    /**
     * 手机操作系统是否>=Honeycomb level11 3.0
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel11() {
        return Build.VERSION.SDK_INT >= 11;
    }

    /**
     * 手机操作系统是否>=HoneycombMR1 level12 3.1
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel12() {
        return Build.VERSION.SDK_INT >= 12;
    }

    /**
     * 手机操作系统是否>=HoneycombMR2 level13 3.2
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel13() {
        return Build.VERSION.SDK_INT >= 13;
    }

    /**
     * 手机操作系统是否>=ICE_CREAM_SANDWICH level14 4.0
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel14() {
        return Build.VERSION.SDK_INT >= 14;
    }

    /**
     * 手机操作系统是否>=JELLY_BEAN level16 4.1
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel16() {
        return Build.VERSION.SDK_INT >= 16;
    }

    /**
     * 手机操作系统是否>=JELLY_BEAN_MR1 level17 4.2
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel17() {
        return Build.VERSION.SDK_INT >= 17;
    }

    /**
     * 手机操作系统是否>=JELLY_BEAN_MR2 level18 4.3
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel18() {
        return Build.VERSION.SDK_INT >= 18;
    }

    /**
     * 手机操作系统是否>=KITKAT level19 4.4
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel19() {
        return Build.VERSION.SDK_INT >= 19;
    }

    /**
     * 手机操作系统是否>=KITKAT_WATCH level20 4.4
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel20() {
        return Build.VERSION.SDK_INT >= 20;
    }

    /**
     * 手机操作系统是否>=LOLLIPOP level21 5.0
     *
     * @return
     * @see <a href=http://androiddoc.qiniudn.com/reference/android/os/Build.
     * VERSION_CODES.html>版本信息说明</a>
     * @see <a href=http://developer.android.com/reference/android/os/Build.
     * VERSION_CODES.html>官方版本信息说明</a>
     */
    public static boolean hasAPILevel21() {
        return Build.VERSION.SDK_INT >= 21;
    }

    /**
     * 开启StrickMode
     */
    @TargetApi(9)
    public static void enableStrictMode() {

        if (hasAPILevel9()) {

            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    /**
     * 关闭StrickMode
     */
    @TargetApi(9)
    public static void disableStrictMode() {

        if (hasAPILevel9()) {

            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .permitAll()
                            .penaltyLog();

            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
        }
    }

}
