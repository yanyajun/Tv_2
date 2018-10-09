package tv.dfyc.yckt;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

public class AQActivity extends Activity implements View.OnFocusChangeListener, View.OnKeyListener {
    private final static String TAG = "AQActivity";
    private ImageView iv_aqcontent_about, iv_aqcontent_unsubscribe, iv_aqcontent_function;//三张图片
    private ImageView oldImage;//上次展现的图片
    private Button bt_about, bt_unsubscribe, bt_function;//三个按钮
    private Button scroll_productlist_bar;//滚动条
    private LinearLayout scroll_productlist_bg;//滚动条背景
    private float mScrollBarDistance;//滚动条比例
    private Button oldView;//上一次被选中的按钮
    private int itemCount;//滚动条滚动次数
    private int location;//滚动条当前位置
    private int topPosition, bottomPosition, position;//图片控件顶部位置，底部位置，当前位置
    private int aboutHeight, unsubscribeHeight, functionHeight, viewWidth, viewHeight;//三个控件的高度，宽度
    private BitmapFactory.Options options, unsubscribeOptions, functionOptions;
    private int height, width;//图片高度，宽度
    private float distances, length, imageLength;//缩放比例，隐藏单边长度,图片长度
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aq);
        options = new BitmapFactory.Options();
        unsubscribeOptions = new BitmapFactory.Options();
        functionOptions = new BitmapFactory.Options();

        iv_aqcontent_about = (ImageView) findViewById(R.id.iv_aqcontent_about);
        iv_aqcontent_unsubscribe = (ImageView) findViewById(R.id.iv_aqcontent_unsubscribe);
        iv_aqcontent_function = (ImageView) findViewById(R.id.iv_aqcontent_function);

        scroll_productlist_bar = (Button) findViewById(R.id.scroll_productlist_bar);
        bt_about = (Button) findViewById(R.id.bt_about);
        bt_unsubscribe = (Button) findViewById(R.id.bt_unsubscribe);
        bt_function = (Button) findViewById(R.id.bt_function);
        scroll_productlist_bg = (LinearLayout) findViewById(R.id.scroll_productlist_bg);

        bt_about.requestFocus();
        bt_about.setBackgroundResource(R.drawable.bt_about_focuse);


//        isFirst = false;
//        iv_aqcontent_about.setImageResource(R.drawable.content_about);
//        iv_aqcontent_unsubscribe.setImageResource(R.drawable.content_unsubscribe);
//        iv_aqcontent_function.setImageResource(R.drawable.content_function);
        oldImage = iv_aqcontent_about;
        oldView = bt_about;

        bt_about.setOnFocusChangeListener(this);
        bt_unsubscribe.setOnFocusChangeListener(this);
        bt_function.setOnFocusChangeListener(this);
        scroll_productlist_bar.setOnFocusChangeListener(this);
        scroll_productlist_bar.setOnKeyListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(TAG);//统计页面，TAG是Activity的类名
        MobclickAgent.onResume(this);//统计时长
        MobclickAgent.onEvent(mContext, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(TAG);// 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        functionOptions = null;
        unsubscribeOptions = null;
        options = null;
        iv_aqcontent_about.setImageBitmap(null);
        iv_aqcontent_unsubscribe.setImageBitmap(null);
        iv_aqcontent_function.setImageBitmap(null);
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        aboutHeight = iv_aqcontent_about.getHeight();
        unsubscribeHeight = iv_aqcontent_unsubscribe.getHeight();
        functionHeight = iv_aqcontent_function.getHeight();
        viewWidth = iv_aqcontent_about.getWidth();
        viewHeight = iv_aqcontent_about.getHeight();

//        BitmapFactory.decodeResource(getResources(), R.drawable.content_about, options);
//        setScroll(iv_aqcontent_about, aboutHeight, options);
        iv_aqcontent_about.setImageResource(R.drawable.content_about);
        BitmapFactory.decodeResource(getResources(), R.drawable.content_about, options);
        setScroll(iv_aqcontent_about, aboutHeight, options);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        //更新UI

                        iv_aqcontent_unsubscribe.setImageResource(R.drawable.content_unsubscribe);
                        iv_aqcontent_function.setImageResource(R.drawable.content_function);

                        BitmapFactory.decodeResource(getResources(), R.drawable.content_function, functionOptions);
                        BitmapFactory.decodeResource(getResources(), R.drawable.content_unsubscribe, unsubscribeOptions);
                    }

                });
            }
        }).start();

        //获取图片的宽高
//        height = options.outHeight;
//        width = options.outWidth;
//        position = (int)(((height/distances)-unsubscribeHeight)/2);
//        iv_aqcontent_unsubscribe.scrollTo(0, -position);
//        iv_aqcontent_unsubscribe.setImageResource(R.drawable.content_unsubscribe);
//        iv_aqcontent_function.setImageResource(R.drawable.content_function);
//        BitmapFactory.decodeResource(getResources(), R.drawable.content_function, functionOptions);
//        BitmapFactory.decodeResource(getResources(), R.drawable.content_unsubscribe, unsubscribeOptions);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {

            switch (v.getId()) {
                case R.id.bt_about:
                    MobclickAgent.onEvent(mContext, "about");
                    clear();
                    oldImage.setVisibility(View.INVISIBLE);
                    oldView = (Button) v;
                    bt_about.setBackgroundResource(R.drawable.bt_about_focuse);
                    iv_aqcontent_about.setVisibility(View.VISIBLE);
                    oldImage = iv_aqcontent_about;
                    setScroll(iv_aqcontent_about, aboutHeight, options);
                    break;

                case R.id.bt_unsubscribe:
                    MobclickAgent.onEvent(mContext, "unsubscribe");
                    clear();
                    oldImage.setVisibility(View.INVISIBLE);
                    oldView = (Button) v;

//                    iv_aqcontent_unsubscribe.setImageResource(R.drawable.content_unsubscribe);
//                    BitmapFactory.decodeResource(getResources(), R.drawable.content_unsubscribe, unsubscribeOptions);

                    bt_unsubscribe.setBackgroundResource(R.drawable.bt_unsubscribe_focuse);
                    iv_aqcontent_unsubscribe.setVisibility(View.VISIBLE);
                    oldImage = iv_aqcontent_unsubscribe;
                    setScroll(iv_aqcontent_unsubscribe, unsubscribeHeight, unsubscribeOptions);
                    break;

                case R.id.bt_function:
                    MobclickAgent.onEvent(mContext, "function");
                    clear();
                    oldImage.setVisibility(View.INVISIBLE);
                    oldView = (Button) v;

//                    iv_aqcontent_function.setImageResource(R.drawable.content_function);
//                    BitmapFactory.decodeResource(getResources(), R.drawable.content_function, functionOptions);

                    bt_function.setBackgroundResource(R.drawable.bt_function_focuse);
                    iv_aqcontent_function.setVisibility(View.VISIBLE);
                    oldImage = iv_aqcontent_function;
                    setScroll(iv_aqcontent_function, functionHeight, functionOptions);
                    break;

                case R.id.scroll_productlist_bar:
                    scroll_productlist_bar.setBackgroundResource(R.drawable.list_page_scroll_bar_focuse);
                    if (oldView.getId() == R.id.bt_about) {
                        oldView.setBackgroundResource(R.drawable.bt_about_choose);
                    } else if (oldView.getId() == R.id.bt_unsubscribe) {
                        oldView.setBackgroundResource(R.drawable.bt_unsubscribe_choose);
                    } else if (oldView.getId() == R.id.bt_function) {
                        oldView.setBackgroundResource(R.drawable.bt_function_choose);
                    }
                    break;
            }
            scroll_productlist_bar.setNextFocusLeftId(R.id.scroll_productlist_bar);
        } else {
            if (v.getId() == R.id.scroll_productlist_bar) {
                scroll_productlist_bar.setBackgroundResource(R.drawable.list_page_scroll_bar);
            }
        }
    }

    /**
     * 还原
     */
    private void clear() {
        switch (oldView.getId()) {
            case R.id.bt_about:
                bt_about.setBackgroundResource(R.drawable.bt_about_normal);
                break;

            case R.id.bt_unsubscribe:
                bt_unsubscribe.setBackgroundResource(R.drawable.bt_unsubscribe_normal);
                break;

            case R.id.bt_function:
                bt_function.setBackgroundResource(R.drawable.bt_function_normal);
                break;

            case R.id.scroll_productlist_bar:
                scroll_productlist_bar.setBackgroundResource(R.drawable.list_page_scroll_bar);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//            LogUtils.log_e("向上");
            switch (v.getId()) {

                case R.id.scroll_productlist_bar:
                    scrollBarUp(oldImage);
//                    scroll_productlist_bar.setNextFocusUpId(R.id.scroll_productlist_bar);
                    if (location == 0) {
//                        LogUtils.log_e("----location-----" + location);
                        scroll_productlist_bar.setNextFocusUpId(oldView.getId());
//                        scroll_productlist_bar.setBackgroundResource(R.drawable.list_page_scroll_bar);
                        oldView.requestFocus();
                    } else {
                        scroll_productlist_bar.setNextFocusUpId(R.id.scroll_productlist_bar);
                    }
                    break;
            }

        } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//            LogUtils.log_e("向下");
            switch (v.getId()) {

                case R.id.scroll_productlist_bar:
                    scrollBarDown(oldImage);
                    break;
            }
        }
        return false;
    }

    /**
     * 设置滚动条
     *
     * @param iv
     * @param privateHeight
     * @param options
     */
    private void setScroll(ImageView iv, int privateHeight, BitmapFactory.Options options) {
        //获取图片的宽高
        height = options.outHeight;
        width = options.outWidth;

        distances = width * 1.0f / viewWidth;
        imageLength = height / distances;
        length = imageLength - privateHeight;
        position = topPosition = -(int) (length / 2);
        bottomPosition = (int) (length / 2);
        iv.scrollTo(0, position);
        location = 0;
//        LogUtils.log_e("distances"+distances);
//        LogUtils.log_e("distances"+distances);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                ViewGroup.LayoutParams para1;
                para1 = scroll_productlist_bar.getLayoutParams();
//                itemCount = (int) distances*2;
                itemCount = (int) imageLength * 2 / viewHeight;
//                LogUtils.log_e("itemCount"+itemCount);
                mScrollBarDistance = scroll_productlist_bg.getHeight() / itemCount;
                para1.height = scroll_productlist_bg.getHeight() / itemCount;

                scroll_productlist_bar.setLayoutParams(para1);
                scroll_productlist_bar.setTranslationY(0);

            }
        }, 200);
    }

    /**
     * 向下滚动
     *
     * @param oldImage
     */
    private void scrollBarDown(ImageView oldImage) {
        position = position + viewHeight / 2;
        if (position >= bottomPosition) {
            position = bottomPosition;
        }
        oldImage.scrollTo(0, position);
        location++;

        int iTranslation = (int) (scroll_productlist_bar.getTranslationY() + mScrollBarDistance);
        if (iTranslation > scroll_productlist_bg.getHeight() - scroll_productlist_bar.getHeight()) {
            iTranslation = (int) scroll_productlist_bar.getTranslationY();
        }
        if (location > itemCount) {
            location--;
            iTranslation = scroll_productlist_bg.getHeight() - (int) mScrollBarDistance;
        }
        scroll_productlist_bar.setTranslationY(iTranslation);
//        LogUtils.log_e("向下location" + location);
//        LogUtils.log_e("向下itemCount" + itemCount);
    }

    /**
     * 向上滚动
     *
     * @param oldImage
     */
    private void scrollBarUp(ImageView oldImage) {
        position = position - viewHeight / 2;
        if (position <= topPosition) {
            position = topPosition;
        }
        oldImage.scrollTo(0, position);

        location--;

        int iTranslation = (int) (scroll_productlist_bar.getTranslationY() - mScrollBarDistance);
        if (iTranslation < 0) {
            iTranslation = 0;
        }
        if (location < 0) {
            iTranslation = 0;
            location++;
        }
        scroll_productlist_bar.setTranslationY(iTranslation);
//        LogUtils.log_e("向上location" + location);
//        LogUtils.log_e("向上itemCount" + itemCount);
    }
}
