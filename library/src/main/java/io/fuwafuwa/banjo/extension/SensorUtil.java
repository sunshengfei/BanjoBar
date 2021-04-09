package io.fuwafuwa.banjo.extension;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class SensorUtil {

    public static void vibrate(Context context) {
        if (context == null) return;
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        //震动70毫秒
        if (vib != null) {
            vib.vibrate(70);
        }
    }
}
