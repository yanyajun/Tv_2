package tv.dfyc.yckt.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import tv.dfyc.yckt.R;

/**
 * Created by wenyu on 2017/10/10.
 * Toast工具类
 */

public class ToastUtils {
    private static Toast toast = null;

    /**
     * 显示普通Toast，多次点击只显示一次
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {

        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * 自定义位置Toast
     * @param context
     * @param msg
     */
    public static void showLocation(Context context, String msg) {

        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 显示带图片的Toast
     * @param context
     * @param msg
     */
    public static void showP(Context context, String msg) {

        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
//            LinearLayout toastView = (LinearLayout) toast.getView();
//            ImageView imageCodeProject = new ImageView(context);
//            imageCodeProject.setImageResource(R.mipmap.ic_launcher);
//            toastView.addView(imageCodeProject, 0);
            toast.show();
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            LinearLayout toastView = (LinearLayout) toast.getView();
            ImageView imageCodeProject = new ImageView(context);
            imageCodeProject.setImageResource(R.mipmap.ic_launcher);
            toastView.addView(imageCodeProject, 0);
            toast.show();
        }
    }

    /**
     * 显示完全自定义Toast
     * @param context
     * @param msg
     */
    public static void showMY(Context context, String msg) {
//        LayoutInflater inflater = getLayoutInflater();
//        View layout = inflater.inflate(R.layout.custom,
//                (ViewGroup) findViewById(R.id.llToast));
//        ImageView image = (ImageView) layout
//                .findViewById(R.id.tvImageToast);
//        image.setImageResource(R.drawable.icon);
//        TextView title = (TextView) layout.findViewById(R.id.tvTitleToast);
//        title.setText("Attention");
//        TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
//        text.setText("完全自定义Toast");
//        toast = new Toast(getApplicationContext());
//        toast.setGravity(Gravity.RIGHT | Gravity.TOP, 12, 40);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setView(layout);
//        toast.show();
        View toastView = LayoutInflater.from(context).inflate(R.layout.toast_click_quit, null);
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastView);
            toast.show();
        } else {

            //自定义Toast控件
            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastView);
            toast.show();

        }
    }

    /**
     * 显示其他线程Toast
     * @param context
     * @param msg
     */
    public static void showNewTHR(Context context, String msg) {

        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
