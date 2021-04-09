package io.fuwafuwa.banjo.extension;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TMUtils {

    public static String getTimeAtString(int timeSeconds) {
        int raw = timeSeconds;
        List<String> result = new ArrayList<>();
        while (raw >= 60) {
            result.add(0, String.format(Locale.getDefault(), "%02d", raw % 60));
            raw = raw / 60;
        }
        result.add(0, String.format(Locale.getDefault(), "%02d", raw));
        if (result.size() < 2) {
            result.add(0, "00");
        }
        return StringUtils.join(result, ":");
    }


    public static String $timeString(long timeMilSecs) {
        if (timeMilSecs <= 0) return "00:00";
        float ms = (timeMilSecs % 1000);
        //会产生进位
//        String msString = String.format(Locale.getDefault(),
//                "%.2f", ms);
        DecimalFormat df = new DecimalFormat("#.00");
        df.setRoundingMode(RoundingMode.FLOOR);
        String msString = df.format(ms / 1000f);
        if (msString.startsWith("0.")) {
            msString = msString.replace("0.", ".");
        } else if (msString.startsWith("1.")) {
            msString = "";
            timeMilSecs = timeMilSecs + 1000;
        }
        return getTimeAtString((int) (timeMilSecs / 1000)) + msString;
    }
}
