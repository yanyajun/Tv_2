package tv.dfyc.yckt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import tv.dfyc.yckt.authority.HeartBeatService;
import tv.dfyc.yckt.beans.JsonListPage;
import tv.dfyc.yckt.beans.JsonMainPage;
import tv.dfyc.yckt.detail.DetailActivity;
import tv.dfyc.yckt.network.CallBackUtil;
import tv.dfyc.yckt.network.GsonUtil;
import tv.dfyc.yckt.network.OkhttpUtil;
import tv.dfyc.yckt.util.GlobleData;
import tv.dfyc.yckt.util.LogUtils;
import tv.dfyc.yckt.util.PreferencesUtil;
import tv.dfyc.yckt.util.XESUtil;

/**
 * 欢迎页面
 */
public class SplashActivity extends Activity {

    private final static String TAG = "SplashActivity";
    private Context mContext;
    private FinishActivityRecevier mRecevier;

    private ImageView mSplashImage;
    private TextView mSplashText;
    private String mSplashUrl;
    private int mSplashTime = 5;

    private JsonMainPage mMainPageJson = null;
    private int iWhichPage;
    private int isFirst = 1, jump = 1;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Intent mServiceIntent;


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // splash
                    mSplashText.setText(mSplashTime + "s");
                    if (--mSplashTime == 0)
                        stopSplash();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.log_e("onCreate");
        setContentView(R.layout.activity_splash);
        MobclickAgent.setDebugMode(true);
        mContext = this;
        // 清理缓存数据
        clearPrefernceValue();
        // 心跳包
        mServiceIntent = new Intent(mContext, HeartBeatService.class);
        startService(mServiceIntent);

        mRecevier = new FinishActivityRecevier();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobleData.RECEIVER_ACTION_FINISH);
        registerReceiver(mRecevier, intentFilter);

        mSplashImage = (ImageView) findViewById(R.id.splash_image);
        mSplashText = (TextView) findViewById(R.id.splash_text);
        getMainPageData();

        Intent intents = this.getIntent();
        Intent jumpIntent = new Intent();
        Bundle bundle = new Bundle();
        String activity = intents.getStringExtra("Activity");//详情页参数
        if (intents != null && !TextUtils.isEmpty(activity)) {

            Bundle bundles = intents.getExtras();
            if (bundles != null) {
                String contentID = bundles.getString("ContentID");
                String libraryI = bundles.getString("libraryId");
                String sWhichPage = bundles.getString("which_page");
                String grade_index = bundles.getString("grade_index");
                boolean backtolauncher = bundles.getBoolean("backtolauncher");
                if (sWhichPage == null) {
                    iWhichPage = bundles.getInt("which_page");
                } else {
                    String regEx = "[^0-9]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(sWhichPage);
                    String sOut = m.replaceAll("").trim();
                    if (sOut.isEmpty())
                        return;
                    iWhichPage = Integer.valueOf(sOut).intValue();
                }
                switch (activity) {
                    case "SpecialActivity":
                        startCountDownTime(false);
                        jumpIntent.setClass(mContext, SpecialActivity.class);
                        bundle.putString("ContentID", contentID);
                        bundle.putInt("libraryId", Integer.valueOf(libraryI));
                        jumpIntent.putExtras(bundle);
                        startActivity(jumpIntent);
                        break;

                    case "DetailActivity":
                        startCountDownTime(false);
                        jumpIntent.setClass(mContext, DetailActivity.class);
                        bundle.putString("ContentID", contentID);
//                        bundle.putString("play_video_url", bundles.getString("play_video_url"));
//                        bundle.putBoolean("toPlay",bundles.getBoolean("toPlay"));
//                        bundle.putInt("detail_id", bundles.getInt("detail_id"));
                        jumpIntent.putExtras(bundle);
                        startActivity(jumpIntent);
                        break;

                    case "ListPageActivity":
                        startCountDownTime(false);
                        getClassList(jumpIntent, bundle, contentID, grade_index, libraryI);
                        break;

                    case "ThemeActivity":
                        startCountDownTime(false);
                        jumpIntent.setClass(mContext, ThemeActivity.class);
                        bundle.putInt("specialid", Integer.valueOf(contentID));
                        jumpIntent.putExtras(bundle);
                        startActivity(jumpIntent);
                        break;

                    case "MainActivity":
                        jump = 2;
                        startSplash();
                        startCountDownTime(true);
                        break;

                    default:
                        startSplash();
                        startCountDownTime(true);
                        break;
                }
                if (backtolauncher && jump != 2) {
                    finish();
                }
            }
        } else {
            startSplash();
            startCountDownTime(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.log_e("onResume");
//        MobclickAgent.onPageStart(TAG);//统计页面，TAG是Activity的类名
        MobclickAgent.onResume(this);//统计时长
        MobclickAgent.onEvent(mContext, TAG);
        if (isFirst != 1) {
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
        }
        isFirst += 1;
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
        stopService(mServiceIntent);
        if (mTimer != null) {
            mTimer.cancel();
        }
        unregisterReceiver(mRecevier);
    }

    private void sendBroadcastTimeData(String timedata) {
        Intent intent = new Intent(GlobleData.MESSAGE_UPDATE_TIME);
        intent.putExtra("time", timedata);
        this.sendBroadcast(intent);
    }

    /**
     * 获取列表数据
     *
     * @param jumpIntent
     * @param bundle
     * @param contentID
     * @param grade_index
     * @param libraryI
     */
    private void getClassList(final Intent jumpIntent, final Bundle bundle, final String contentID, final String grade_index, final String libraryI) {
        String url = GlobleData.HTTP_URL_CHANNEL + "?" + GlobleData.HTTP_VERSION_KEY + "=" + GlobleData.HTTP_VERSION_VALUE;
        OkhttpUtil.okHttpGet(url, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Log.d(TAG, "连接失败！");
            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try {
                    JsonListPage mListPageJson = GsonUtil.fromJson(response, JsonListPage.class);
                    if (mListPageJson.getResult() == 0) {
                        jumpIntent.setClass(mContext, ListPageActivity.class);
                        bundle.putString("class_index", contentID);
                        bundle.putString("grade_index", grade_index);
                        bundle.putString("recommend_id", libraryI);
                        bundle.putSerializable("class_list", mListPageJson);
                        jumpIntent.putExtras(bundle);
                        startActivity(jumpIntent);
                    } else {
                        Toast.makeText(mContext, "列表数据返回失败！无法打开栏目页面！", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "列表数据返回失败！无法打开栏目页面！", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startSplash() {
        mSplashImage.setImageResource(R.mipmap.splash);
        try {
            if (XESUtil.isPictureExist(GlobleData.PNG_SPLASH_PICTURE_NAME)) {
                Log.d(TAG, "splash.png exist");
                Uri uri = Uri.fromFile(new File(XESUtil.getPicturePath(GlobleData.PNG_SPLASH_PICTURE_NAME)));
                mSplashImage.setImageURI(uri);
            }
        } catch (Exception e) {
            Log.d(TAG, "startSplash exception");
            e.printStackTrace();
        }
    }

    private void resetSplash(String splash_url) {
        mSplashUrl = splash_url;
        try {
            if (!XESUtil.isPictureExist(GlobleData.PNG_SPLASH_PICTURE_NAME)) {
                Log.d(TAG, "network splash.png");
                Glide.with(mContext).load(splash_url).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.mipmap.splash).into(mSplashImage);
            }
        } catch (Exception e) {
            Log.d(TAG, "resetSplash Exception");
            e.printStackTrace();
        }
    }

    private void stopSplash() {
        Intent intent = new Intent(mContext, MainActivity.class);
        if (jump == 2) {
            Bundle bundle = new Bundle();
            bundle.putInt("which_page", iWhichPage);
            intent.putExtras(bundle);
        } else {
            XESUtil.savePicture(mContext, GlobleData.PNG_SPLASH_PICTURE_NAME, mSplashUrl);
        }
        startActivity(intent);
        finish();
    }

    private void getMainPageData() {
        String url = GlobleData.HTTP_URL_HOME + "?" + GlobleData.HTTP_VERSION_KEY + "=" + GlobleData.HTTP_VERSION_VALUE;
        OkhttpUtil.okHttpGet(url, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Log.d(TAG, "连接失败！");
            }

            @Override
            public void onResponse(String response) {
                try {
                    mMainPageJson = GsonUtil.fromJson(response, JsonMainPage.class);
//                    Log.e(TAG, "主页数据返回成功!===response" + response);
//                    Log.e(TAG, "主页数据返回成功!===mMainPageJson" + mMainPageJson.toString());
                    if (mMainPageJson.getResult() == 0) {
                        Log.d(TAG, "主页数据返回成功!");

                        // 刷新splash
                        if (mMainPageJson.getData() != null && mMainPageJson.getData().getImage() != null)
                            resetSplash(mMainPageJson.getData().getImage());
                    } else {
                        Log.d(TAG, "主页数据返回失败!");
                        Toast.makeText(mContext, "获取主页数据失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "主页数据返回失败!");
                    Toast.makeText(mContext, "获取主页数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 开启倒计时和时间广播
     *
     * @param b
     */
    private void startCountDownTime(final boolean b) {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                String sTimeData = XESUtil.getDateTime();
                if (b) {
                    Bundle bundle = new Bundle();
                    bundle.putString("time", sTimeData);
                    Message msg = Message.obtain();
                    msg.setData(bundle);
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
                sendBroadcastTimeData(sTimeData);
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * 清除数据
     */
    private void clearPrefernceValue() {
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USERTOKEN, "");

        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_STBMAC, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_PHONE, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_ACCOUNT, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_STBSN, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_EPGADDRESS, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_TVID, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_DEVICETYPE, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_FIRMWAREVERSION, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_CDNADDRESS, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_BACKUPCDNADDRESS, "");

        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_ID, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_OTTTOKEN, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USERP_PONE, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USERP_APPID, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_EXPIREDTIME, "");
        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_TIMEOUT, "");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class FinishActivityRecevier extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据需求添加自己需要关闭页面的action
            if (GlobleData.RECEIVER_ACTION_FINISH.equals(intent.getAction())) {
                isFirst = 1;
                SplashActivity.this.finish();
            }
        }
    }
}
