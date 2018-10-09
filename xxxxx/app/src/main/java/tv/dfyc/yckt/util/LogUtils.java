package tv.dfyc.yckt.util;

import android.util.Log;

/**
 * Created by pan on 2017/8/29.
 */

public class LogUtils {
    private static boolean showLog = true;
    private static String tag = "MSG";

    /**
     * i级别的log
     * */
    public static void log_i(String msg){
        if (showLog){
            Log.i(tag,msg);
        }
    }

    /**
     * i级别的log
     * */
    public static void log_i(String tag, String msg){
        if (showLog){
            Log.i(tag,msg);
        }
    }

    /**
     * e级别的log
     * */
    public static void log_e(String msg){
        if (showLog){
            Log.e(tag,msg);
        }
    }

    /**
     * e级别的log
     * */
    public static void log_e(String tag, String msg){
        if (showLog){
            Log.e(tag,msg);
        }
    }

    /**
     * w级别的log
     * */
    public static void log_w(String msg){
        if (showLog){
            Log.w(tag,msg);
        }
    }

    /**
     * w级别的log
     * */
    public static void log_w(String tag, String msg){
        if (showLog){
            Log.w(tag,msg);
        }
    }

    /**
     * d级别的log
     * */
    public static void log_d(String msg){
        if (showLog){
            Log.d(tag,msg);
        }
    }

    /**
     * d级别的log
     * */
    public static void log_d(String tag, String msg){
        if (showLog){
            Log.d(tag,msg);
        }
    }
}
