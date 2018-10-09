package tv.dfyc.yckt;

import android.app.Activity;
import android.app.DevInfoManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chinamobile.authclient.AuthClient;
import com.chinamobile.authclient.Constants;
import com.open.androidtvwidget.bridge.EffectNoDrawBridge;
import com.open.androidtvwidget.utils.Utils;
import com.open.androidtvwidget.view.MainUpView;
import com.open.androidtvwidget.view.ReflectItemView;
import com.open.androidtvwidget.view.RelativeMainLayout;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import tv.dfyc.yckt.beans.JsonListPage;
import tv.dfyc.yckt.beans.JsonMainPage;
import tv.dfyc.yckt.beans.JsonUserLogin;
import tv.dfyc.yckt.beans.QuitBean;
import tv.dfyc.yckt.custom.RoundImageView;
import tv.dfyc.yckt.detail.DetailActivity;
import tv.dfyc.yckt.detail.ProductListActivity;
import tv.dfyc.yckt.info.personal.PersonalPageNewActivity;
import tv.dfyc.yckt.info.search.searchPageActivity;
import tv.dfyc.yckt.network.CallBackUtil;
import tv.dfyc.yckt.network.GsonUtil;
import tv.dfyc.yckt.network.OkhttpUtil;
import tv.dfyc.yckt.util.BlurBitmapUtil;
import tv.dfyc.yckt.util.GlideCacheUtil;
import tv.dfyc.yckt.util.GlobleData;
import tv.dfyc.yckt.util.LogUtils;
import tv.dfyc.yckt.util.PreferencesUtil;
import tv.dfyc.yckt.util.ToastUtils;
import tv.dfyc.yckt.util.XESUtil;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity";
    private Context mContext;

    // 数据
    private JsonMainPage mMainPageJson = null;
    private JsonListPage mListPageJson = null;
    private AuthClient mAuthClient;
    private MsgReceiver mMsgReceiver;

    // 控件
    private TextView mWeekTextView;
    private TextView mTimeTextView;
    private TextView mDateTextView;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Dialog mBuilderReLogin;
    private RelativeLayout mReLoginDialog;
    private Button mRetryButton;
    private MainUpView mainUpView1;
    private View mOldFocus; // 4.3以下版本需要自己保存.
    private Button mButtonPrimary;
    private Button mButtonJunior;
    private Button mButtonSenior;
    private int mButtonClickedFlag = GlobleData.GRADE_TYPE_PRIMARY;
    private ImageButton mButtonSearch;
    private ImageButton mButtonProduct;
    private TextView mTextViewSearch;
    private TextView mTextViewProduct;
    private RelativeLayout mFirst_page_personal;
    private RelativeLayout first_page_aq;
    private RelativeLayout mLayoutFeedBack;
    private TextView mMessageText;

    // 推荐位
    private RelativeLayout mRecommendLayout00;
    private RoundImageView mRecommendImage00;
    private TextView mRecommendName00;
    private RelativeLayout mRecommendLayout01;
    private RoundImageView mRecommendImage01;
    private TextView mRecommendName01;
    private RelativeLayout mRecommendLayout02;
    private RoundImageView mRecommendImage02;
    private TextView mRecommendName02;
    private RelativeLayout mRecommendLayout12;
    private RoundImageView mRecommendImage12;
    private TextView mRecommendName12;
    private RelativeLayout mRecommendLayout03;
    private RoundImageView mRecommendImage03;
    private TextView mRecommendName03;
    private ReflectItemView mRecommendLayoutBottom1;
    private ImageView mRecommendImageBottom1;
    private RoundImageView mRecommendImageBottom2;
    private ReflectItemView mRecommendLayoutBottom2;
    private RoundImageView mRecommendImageBottom3;
    private ReflectItemView mRecommendLayoutBottom3;
    private RoundImageView mRecommendImageBottom4;
    private ReflectItemView mRecommendLayoutBottom4;
    private RoundImageView mRecommendImageBottom5;
    private ReflectItemView mRecommendLayoutBottom5;
    private RoundImageView mRecommendImageBottom6;
    private ReflectItemView mRecommendLayoutBottom6;
    private RoundImageView mRecommendImageBottom7;
    private ReflectItemView mRecommendLayoutBottom7;
    private String mQuitImageUrl;
    private int presstime = 1;
    private QuitBean quitBean;
    private Map<String, Integer> quitMap = new HashMap<>();
    private Dialog builder;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String sTimeData = msg.getData().getString("time");
                    mDateTextView.setText(sTimeData.split(" ")[0]);
                    mTimeTextView.setText(sTimeData.split(" ")[1]);
                    mWeekTextView.setText(sTimeData.split(" ")[2]);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        String cache = GlideCacheUtil.getInstance().getCacheSize(mContext);
//        Log.e("MSG_E","cache====="+cache);
        GlideCacheUtil.getInstance().clearImageMemoryCache(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                GlideCacheUtil.getInstance().clearImageDiskCache(mContext);
//                String cache1 = GlideCacheUtil.getInstance().getCacheSize(MainActivity.this);
//                Log.e("MSG_E","cache1====="+cache1);
            }
        }).start();
        quitMap.put("小学", GlobleData.GRADE_TYPE_PRIMARY);
        quitMap.put("初中", GlobleData.GRADE_TYPE_JUNIOR);
        quitMap.put("高中", GlobleData.GRADE_TYPE_SENIOR);

        // 初始化控件
        initView();

        // 消息接收
        initMessageRecviver();

        // 获取系统时间
        setTimeData();

        // 从后台获取主页数据
        getMainPageData();

        // 从后台获取列表数据
        getListPageData();

        // 从后台获取退出数据
        getQuitData();

        // 获取第三方参数
        initIntent();

        //登录
//        initUserAuth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (builder != null && builder.isShowing()) {
            builder.dismiss();
        }
//        MobclickAgent.onPageStart(TAG);//统计页面，TAG是Activity的类名
        MobclickAgent.onResume(this);//统计时长
        MobclickAgent.onEvent(mContext, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presstime = 1;
//        MobclickAgent.onPageEnd(TAG);// 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        unregisterReceiver(mMsgReceiver);

    }

    //  控件初始化控件
    private void initView() {
        mWeekTextView = (TextView) findViewById(R.id.week);
        mTimeTextView = (TextView) findViewById(R.id.time);
        mDateTextView = (TextView) findViewById(R.id.date);
        mainUpView1 = (MainUpView) findViewById(R.id.mainUpView1);
        mButtonPrimary = (Button) findViewById(R.id.first_page_button_primary);
        mButtonJunior = (Button) findViewById(R.id.first_page_button_junior);
        mButtonSenior = (Button) findViewById(R.id.first_page_button_senior);
        mButtonSearch = (ImageButton) findViewById(R.id.first_page_button_search);
        mButtonProduct = (ImageButton) findViewById(R.id.first_page_button_product);
        mTextViewSearch = (TextView) findViewById(R.id.first_page_text_search);
        mTextViewProduct = (TextView) findViewById(R.id.first_page_text_product);
        mFirst_page_personal = (RelativeLayout) findViewById(R.id.first_page_recommend_10);
        first_page_aq = (RelativeLayout) findViewById(R.id.first_page_aq);
        mLayoutFeedBack = (RelativeLayout) findViewById(R.id.first_page_recommend_11);
        mRecommendLayout00 = (RelativeLayout) findViewById(R.id.first_page_recommend_00);
        mRecommendImage00 = (RoundImageView) findViewById(R.id.first_page_recommend_00_image);
        mRecommendName00 = (TextView) findViewById(R.id.first_page_recommend_00_name);
        mRecommendLayout01 = (RelativeLayout) findViewById(R.id.first_page_recommend_01);
        mRecommendImage01 = (RoundImageView) findViewById(R.id.first_page_recommend_01_image);
        mRecommendName01 = (TextView) findViewById(R.id.first_page_recommend_01_name);
        mRecommendLayout02 = (RelativeLayout) findViewById(R.id.first_page_recommend_02);
        mRecommendImage02 = (RoundImageView) findViewById(R.id.first_page_recommend_02_image);
        mRecommendName02 = (TextView) findViewById(R.id.first_page_recommend_02_name);
        mRecommendLayout12 = (RelativeLayout) findViewById(R.id.first_page_recommend_12);
        mRecommendImage12 = (RoundImageView) findViewById(R.id.first_page_recommend_12_image);
        mRecommendName12 = (TextView) findViewById(R.id.first_page_recommend_12_name);
        mRecommendLayout03 = (RelativeLayout) findViewById(R.id.first_page_recommend_03);

        mRecommendImage03 = (RoundImageView) findViewById(R.id.first_page_recommend_03_image);
        mRecommendName03 = (TextView) findViewById(R.id.first_page_recommend_03_name);
        mRecommendLayoutBottom1 = (ReflectItemView) findViewById(R.id.first_page_recommend_reflect1);
        mRecommendImageBottom1 = (ImageView) findViewById(R.id.first_page_recommend_reflect1_image);
        mRecommendLayoutBottom2 = (ReflectItemView) findViewById(R.id.first_page_recommend_reflect2);
        mRecommendImageBottom2 = (RoundImageView) findViewById(R.id.first_page_recommend_reflect2_image);
        mRecommendLayoutBottom3 = (ReflectItemView) findViewById(R.id.first_page_recommend_reflect3);
        mRecommendImageBottom3 = (RoundImageView) findViewById(R.id.first_page_recommend_reflect3_image);
        mRecommendLayoutBottom4 = (ReflectItemView) findViewById(R.id.first_page_recommend_reflect4);
        mRecommendImageBottom4 = (RoundImageView) findViewById(R.id.first_page_recommend_reflect4_image);
        mRecommendLayoutBottom5 = (ReflectItemView) findViewById(R.id.first_page_recommend_reflect5);
        mRecommendImageBottom5 = (RoundImageView) findViewById(R.id.first_page_recommend_reflect5_image);
        mRecommendLayoutBottom6 = (ReflectItemView) findViewById(R.id.first_page_recommend_reflect6);
        mRecommendImageBottom6 = (RoundImageView) findViewById(R.id.first_page_recommend_reflect6_image);
        mRecommendLayoutBottom7 = (ReflectItemView) findViewById(R.id.first_page_recommend_reflect7);
        mRecommendImageBottom7 = (RoundImageView) findViewById(R.id.first_page_recommend_reflect7_image);

        mMessageText = (TextView) findViewById(R.id.first_page_message_text);

        // 控件监听及控制
        initViewControl();

        // 重新登录窗口
        initReLoginDialog();
    }

    private void initIntent() {
        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String sWhichPage = bundle.getString("which_page");
                Log.d(TAG, "which_page=: " + sWhichPage);
                final int iWhichPage;
                if (sWhichPage == null) {
                    iWhichPage = bundle.getInt("which_page");
                } else {
                    String regEx = "[^0-9]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(sWhichPage);
                    String sOut = m.replaceAll("").trim();
                    if (sOut.isEmpty())
                        return;
                    iWhichPage = Integer.valueOf(sOut).intValue();
                }
                if (iWhichPage >= 2 && iWhichPage <= 4) { // 第三方调用的逻辑
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            switch (iWhichPage - 2) {
                                case GlobleData.GRADE_TYPE_PRIMARY:
                                    break;
                                case GlobleData.GRADE_TYPE_JUNIOR:
                                    mButtonClickedFlag = GlobleData.GRADE_TYPE_JUNIOR;
                                    navSecondPositonSelected();
                                    resetGradeButtonBackground(mButtonPrimary, false);
                                    resetGradeButtonBackground(mButtonSenior, false);
                                    initMainPageData(GlobleData.GRADE_TYPE_JUNIOR);
                                    break;
                                case GlobleData.GRADE_TYPE_SENIOR:
                                    mButtonClickedFlag = GlobleData.GRADE_TYPE_SENIOR;
                                    navThirdPositonSelected();
                                    resetGradeButtonBackground(mButtonPrimary, false);
                                    resetGradeButtonBackground(mButtonJunior, false);
                                    initMainPageData(GlobleData.GRADE_TYPE_SENIOR);
                                    break;
                            }
                        }
                    }, 500);
                }
            }
        }
    }

    private void setAllTextMaxLines() {
        mRecommendName00.setMaxLines(1);
        mRecommendName01.setMaxLines(1);
        mRecommendName02.setMaxLines(1);
        mRecommendName12.setMaxLines(1);
        mRecommendName03.setMaxLines(1);
    }

    private void setRecommendSelectState(float left, float top, float right, float bottom) {
        // 设置移动边框的图片
        mainUpView1.setUpRectResource(R.drawable.yellow_white_border);
        float density = getResources().getDisplayMetrics().density;
        RectF rectf = new RectF(left * density, top * density, right * density, bottom * density);
        mainUpView1.setDrawUpRectPadding(rectf);

        mTextViewSearch.setVisibility(View.INVISIBLE);
        mTextViewProduct.setVisibility(View.INVISIBLE);
        nofocusResetGradeButtonClicked();
    }

    private void initViewControl() {
        if (Utils.getSDKVersion() == 17) { // 测试 android 4.2版本.
            switchNoDrawBridgeVersion();
        } else {
            switchDrawBridgeVersion();
        }

        RelativeMainLayout ll = (RelativeMainLayout) findViewById(R.id.first_page_recommend_layout);
        ll.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {
                setAllTextMaxLines();

                if (newFocus != null)
                    newFocus.bringToFront(); // 防止放大的view被压在下面. (建议使用MainLayout)

                float scale = 1.1f;
                if (newFocus == mButtonSearch) {
                    scale = 1.5f;
                    mainUpView1.setUpRectResource(R.drawable.test_round); // 设置移动边框的图片.
                    mainUpView1.setDrawUpRectPadding(0);
                    mTextViewSearch.setVisibility(View.VISIBLE);
                    mTextViewProduct.setVisibility(View.INVISIBLE);
                    nofocusResetGradeButtonClicked();
                } else if (newFocus == mButtonProduct) {
                    scale = 1.5f;
                    mainUpView1.setUpRectResource(R.drawable.test_round); // 设置移动边框的图片.
                    mainUpView1.setDrawUpRectPadding(0);
                    mTextViewProduct.setVisibility(View.VISIBLE);
                    mTextViewSearch.setVisibility(View.INVISIBLE);
                    nofocusResetGradeButtonClicked();
                } else if (newFocus == mButtonPrimary || newFocus == mButtonJunior || newFocus == mButtonSenior) {
                    focusResetGradeButtonClicked(newFocus);
                } else if (newFocus == mRecommendLayout00) {
                    mRecommendName00.setMaxLines(2);
                    setRecommendSelectState(getDimension(R.dimen.w_13), getDimension(R.dimen.w_12),
                            getDimension(R.dimen.w_13), getDimension(R.dimen.w_12));
                } else if (newFocus == mRecommendLayout01) {
                    mRecommendName01.setMaxLines(2);
                    setRecommendSelectState(getDimension(R.dimen.w_13), getDimension(R.dimen.w_6),
                            getDimension(R.dimen.w_13), getDimension(R.dimen.w_12));
                } else if (newFocus == mRecommendLayout02) {
                    mRecommendName02.setMaxLines(2);
                    setRecommendSelectState(getDimension(R.dimen.w_13), getDimension(R.dimen.w_12),
                            getDimension(R.dimen.w_13), getDimension(R.dimen.w_11));
                } else if (newFocus == mRecommendLayout12) {
                    mRecommendName12.setMaxLines(2);
                    setRecommendSelectState(getDimension(R.dimen.w_13), getDimension(R.dimen.w_12),
                            getDimension(R.dimen.w_13), getDimension(R.dimen.w_11));
                } else if (newFocus == mRecommendLayout03) {
                    mRecommendName03.setMaxLines(2);
                    setRecommendSelectState(getDimension(R.dimen.w_13), getDimension(R.dimen.w_6),
                            getDimension(R.dimen.w_13), getDimension(R.dimen.w_12));
                } else {
                    setRecommendSelectState(getDimension(R.dimen.w_13), getDimension(R.dimen.w_12),
                            getDimension(R.dimen.w_13), getDimension(R.dimen.w_12));
                }
                mainUpView1.setFocusView(newFocus, mOldFocus, scale);
                mOldFocus = newFocus;
            }
        });


        new Handler().postDelayed(new Runnable() {
            public void run() {
                navFirstPositionSelected();
            }
        }, 200);

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "searchbutton");
                startActivity(new Intent(MainActivity.this, searchPageActivity.class));
            }
        });

        mFirst_page_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "personalbutton");
                Intent personalIntent = new Intent(MainActivity.this, PersonalPageNewActivity.class);
                startActivityForResult(personalIntent, 1);
            }
        });

        first_page_aq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "FAQbutton");
                Intent aqIntent = new Intent(MainActivity.this, AQActivity.class);
                startActivity(aqIntent);
            }
        });

        mButtonProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "ProductListbutton");
                Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
                startActivity(intent);
            }
        });

        mButtonPrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "Primarybutton");
                mButtonClickedFlag = GlobleData.GRADE_TYPE_PRIMARY;
                resetGradeButtonBackground(mButtonJunior, false);
                resetGradeButtonBackground(mButtonSenior, false);
                initMainPageData(GlobleData.GRADE_TYPE_PRIMARY);
            }
        });

        mButtonJunior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "Juniorbutton");
                mButtonClickedFlag = GlobleData.GRADE_TYPE_JUNIOR;
                resetGradeButtonBackground(mButtonPrimary, false);
                resetGradeButtonBackground(mButtonSenior, false);
                initMainPageData(GlobleData.GRADE_TYPE_JUNIOR);
            }
        });

        mButtonSenior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "Seniorbutton");
                mButtonClickedFlag = GlobleData.GRADE_TYPE_SENIOR;
                resetGradeButtonBackground(mButtonPrimary, false);
                resetGradeButtonBackground(mButtonJunior, false);
                initMainPageData(GlobleData.GRADE_TYPE_SENIOR);
            }
        });

        mRecommendLayout00.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(1);
            }
        });

        mRecommendLayout01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(2);
            }
        });

        mRecommendLayout02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(3);
            }
        });

        mRecommendLayout03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendOpenWhichPage(4);
            }
        });

        mRecommendLayout12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(5);
            }
        });

        mRecommendLayoutBottom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(6);
            }
        });

        mRecommendLayoutBottom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(7);
            }
        });

        mRecommendLayoutBottom3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(8);
            }
        });

        mRecommendLayoutBottom4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(9);
            }
        });

        mRecommendLayoutBottom5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(10);
            }
        });

        mRecommendLayoutBottom6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(11);
            }
        });

        mRecommendLayoutBottom7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendOpenWhichPage(12);
            }
        });

        mLayoutFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "FeedBackbutton");
                startActivity(new Intent(MainActivity.this, FeedBackActivity.class));
            }
        });
    }

    private void recommendOpenWhichPage(int recommend_index) {
        List<JsonMainPage.MainData.RecommendData> recommendDataList = null;
        String recommend_type = "";
        int recommend_class = -1;
        int recommend_id = 0;
        if (mMainPageJson.getData() == null)
            return;
        if (mButtonClickedFlag == GlobleData.GRADE_TYPE_PRIMARY) {
            recommendDataList = mMainPageJson.getData().getXiaoxue();
            if (mMainPageJson.getData().getXiaoxue() == null)
                return;
            if (mMainPageJson.getData().getXiaoxue().size() < recommend_index)
                return;
            recommend_type = mMainPageJson.getData().getXiaoxue().get(recommend_index - 1).getTuijian_type();
//            if (recommend_index >= 7 && recommend_index <= 12){
//                recommend_class = recommend_index - 7;
//            }
            recommend_id = mMainPageJson.getData().getXiaoxue().get(recommend_index - 1).getId();
//            if (recommend_index >= 1 && recommend_index < 7){
            List<JsonListPage.pageData.pageDataDetail.grade> grades = mListPageJson.getData().getLists().get(mButtonClickedFlag).getLists();//获取到年纪列表
            for (int i = 0; i < grades.size(); i++) {
                for (int j = 0; j < grades.get(i).getLists().size(); j++) {
                    if (recommend_id == grades.get(i).getLists().get(j).getNode_id()) {
                        recommend_class = i;
                    }
                }
            }

//            }


        } else if (mButtonClickedFlag == GlobleData.GRADE_TYPE_JUNIOR) {
            recommendDataList = mMainPageJson.getData().getChuzhong();
            if (mMainPageJson.getData().getChuzhong() == null)
                return;
            if (mMainPageJson.getData().getChuzhong().size() < recommend_index)
                return;
            recommend_type = mMainPageJson.getData().getChuzhong().get(recommend_index - 1).getTuijian_type();
//            if (recommend_index >= 7 && recommend_index <= 9)
//                recommend_class = recommend_index - 7;
            recommend_id = mMainPageJson.getData().getChuzhong().get(recommend_index - 1).getId();

//            if (recommend_index >= 1 && recommend_index < 7){
            List<JsonListPage.pageData.pageDataDetail.grade> grades = mListPageJson.getData().getLists().get(mButtonClickedFlag).getLists();//获取到年纪列表
            for (int i = 0; i < grades.size(); i++) {
                for (int j = 0; j < grades.get(i).getLists().size(); j++) {
                    if (recommend_id == grades.get(i).getLists().get(j).getNode_id()) {
                        recommend_class = i;
                    }
                }
            }

//            }
        } else if (mButtonClickedFlag == GlobleData.GRADE_TYPE_SENIOR) {
            recommendDataList = mMainPageJson.getData().getGaozhong();
            if (mMainPageJson.getData().getGaozhong() == null)
                return;
            if (mMainPageJson.getData().getGaozhong().size() < recommend_index)
                return;
            recommend_type = mMainPageJson.getData().getGaozhong().get(recommend_index - 1).getTuijian_type();
//            if (recommend_index >= 7 && recommend_index <= 9)
//                recommend_class = recommend_index - 7;
            recommend_id = mMainPageJson.getData().getGaozhong().get(recommend_index - 1).getId();

//            if (recommend_index >= 1 && recommend_index < 7){
            List<JsonListPage.pageData.pageDataDetail.grade> grades = mListPageJson.getData().getLists().get(mButtonClickedFlag).getLists();//获取到年纪列表
            for (int i = 0; i < grades.size(); i++) {
                for (int j = 0; j < grades.get(i).getLists().size(); j++) {
                    if (recommend_id == grades.get(i).getLists().get(j).getNode_id()) {
                        recommend_class = i;
                    }
                }
            }

//            }
        }

        if (mListPageJson == null || mListPageJson.getData() == null || mListPageJson.getData().getLists() == null)
            Toast.makeText(mContext, "获取数据失败！", Toast.LENGTH_SHORT).show();

        recommend_class = (recommend_class < 0) ? 0 : recommend_class;

        //统计数据

        //recommend_id 被推荐的id
        //recommend_type 被推荐的类型
        //recommend_name被推荐的名字
        //recommend_location推荐位位置
        //

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("recommend_location", "" + recommend_index);
        if (recommendDataList != null) {
            map.put("recommend_name", recommendDataList.get(recommend_index - 1).getName());
        } else {
            map.put("recommend_name", "推荐位" + recommend_index);
        }
        map.put("recommend_type", recommend_type);
        MobclickAgent.onEvent(mContext, "recommend", map);

        switch (recommend_type) {
            case "1": // 栏目
                Intent intent_list_page = new Intent(MainActivity.this, ListPageActivity.class);
                intent_list_page.putExtra("class_list", (Serializable) mListPageJson);
                intent_list_page.putExtra("grade_index", mButtonClickedFlag);
                intent_list_page.putExtra("class_index", recommend_class);
                intent_list_page.putExtra("recommend_id", recommend_id);
                startActivity(intent_list_page);
                break;
            case "2": // 专题
                Intent intent_theme = new Intent(MainActivity.this, ThemeActivity.class);
                intent_theme.putExtra("specialid", recommend_id);
                startActivity(intent_theme);
                break;
            case "3": // 详情
                Intent intent_detail = new Intent(MainActivity.this, DetailActivity.class);
                intent_detail.putExtra("libraryId", recommend_id);
                startActivity(intent_detail);
                break;
            case "4": // 特色
                Intent intent_special = new Intent(MainActivity.this, SpecialActivity.class);
                intent_special.putExtra("class_list", (Serializable) mListPageJson);
                intent_special.putExtra("grade_index", mButtonClickedFlag);
                intent_special.putExtra("libraryId", recommend_id);
                startActivity(intent_special);
                break;
        }
    }

    private void navFirstPositionSelected() {
        mButtonPrimary.requestFocus();
        mButtonPrimary.setTextColor(getResources().getColor(R.color.white));
        mButtonPrimary.setBackground(getResources().getDrawable(R.drawable.yellow_white_border_shadow));
    }

    private void navSecondPositonSelected() {
        mButtonJunior.requestFocus();
        mButtonJunior.setTextColor(getResources().getColor(R.color.white));
        mButtonJunior.setBackground(getResources().getDrawable(R.drawable.yellow_white_border));
    }

    private void navThirdPositonSelected() {
        mButtonSenior.requestFocus();
        mButtonSenior.setTextColor(getResources().getColor(R.color.white));
        mButtonSenior.setBackground(getResources().getDrawable(R.drawable.yellow_white_border));
    }

    private void resetGradeButtonBackground(Button button, boolean clicked) {
        if (clicked) {
            button.setTextColor(getResources().getColor(R.color.black));
            button.setBackground(getResources().getDrawable(R.drawable.first_page_yellow_round_bg));
        } else {
            button.setTextColor(getResources().getColor(R.color.white));
            button.setBackground(getResources().getDrawable(R.drawable.first_page_white_round_border));
        }
    }

    private void nofocusResetGradeButtonClicked() {
        if (mButtonClickedFlag == GlobleData.GRADE_TYPE_PRIMARY) {
            resetGradeButtonBackground(mButtonPrimary, true);
            resetGradeButtonBackground(mButtonJunior, false);
            resetGradeButtonBackground(mButtonSenior, false);
        } else if (mButtonClickedFlag == GlobleData.GRADE_TYPE_JUNIOR) {
            resetGradeButtonBackground(mButtonJunior, true);
            resetGradeButtonBackground(mButtonPrimary, false);
            resetGradeButtonBackground(mButtonSenior, false);
        } else if (mButtonClickedFlag == GlobleData.GRADE_TYPE_SENIOR) {
            resetGradeButtonBackground(mButtonSenior, true);
            resetGradeButtonBackground(mButtonPrimary, false);
            resetGradeButtonBackground(mButtonJunior, false);
        }
    }

    private void focusResetGradeButtonClicked(View focus) {
        // 设置移动边框的图片
        mainUpView1.setUpRectResource(R.drawable.yellow_white_border_shadow);
        mainUpView1.setDrawUpRectPadding(0);

        if (focus == mButtonPrimary) {
            mTextViewSearch.setVisibility(View.INVISIBLE);
            mTextViewProduct.setVisibility(View.INVISIBLE);
            resetGradeButtonBackground(mButtonPrimary, false);
            if (mButtonClickedFlag == GlobleData.GRADE_TYPE_JUNIOR) {
                resetGradeButtonBackground(mButtonJunior, true);
                resetGradeButtonBackground(mButtonSenior, false);
            } else if (mButtonClickedFlag == GlobleData.GRADE_TYPE_SENIOR) {
                resetGradeButtonBackground(mButtonSenior, true);
                resetGradeButtonBackground(mButtonJunior, false);
            }
        } else if (focus == mButtonJunior) {
            mTextViewSearch.setVisibility(View.INVISIBLE);
            mTextViewProduct.setVisibility(View.INVISIBLE);
            resetGradeButtonBackground(mButtonJunior, false);
            if (mButtonClickedFlag == GlobleData.GRADE_TYPE_PRIMARY) {
                resetGradeButtonBackground(mButtonPrimary, true);
                resetGradeButtonBackground(mButtonSenior, false);
            } else if (mButtonClickedFlag == GlobleData.GRADE_TYPE_SENIOR) {
                resetGradeButtonBackground(mButtonSenior, true);
                resetGradeButtonBackground(mButtonPrimary, false);
            }
        } else if (focus == mButtonSenior) {
            mTextViewSearch.setVisibility(View.INVISIBLE);
            mTextViewProduct.setVisibility(View.INVISIBLE);
            resetGradeButtonBackground(mButtonSenior, false);
            if (mButtonClickedFlag == GlobleData.GRADE_TYPE_PRIMARY) {
                resetGradeButtonBackground(mButtonPrimary, true);
                resetGradeButtonBackground(mButtonJunior, false);
            } else if (mButtonClickedFlag == GlobleData.GRADE_TYPE_JUNIOR) {
                resetGradeButtonBackground(mButtonJunior, true);
                resetGradeButtonBackground(mButtonPrimary, false);
            }
        }
    }

    private void switchNoDrawBridgeVersion() {
        float density = getResources().getDisplayMetrics().density;
        RectF rectf = new RectF(getDimension(R.dimen.w_10) * density, getDimension(R.dimen.h_10) * density,
                getDimension(R.dimen.w_9) * density, getDimension(R.dimen.h_9) * density);
        EffectNoDrawBridge effectNoDrawBridge = new EffectNoDrawBridge();
        effectNoDrawBridge.setTranDurAnimTime(200);
        mainUpView1.setEffectBridge(effectNoDrawBridge);
        mainUpView1.setUpRectResource(R.drawable.white_light_10);
        mainUpView1.setDrawUpRectPadding(rectf);
    }

    private void switchDrawBridgeVersion() {
        EffectNoDrawBridge effectNoDrawBridge = new EffectNoDrawBridge();
        effectNoDrawBridge.setTranDurAnimTime(200);
        mainUpView1.setEffectBridge(effectNoDrawBridge);
        mainUpView1.setUpRectResource(R.drawable.yellow_white_border);
    }

    private void initUserAuth() {
        if (mAuthClient == null)
            mAuthClient = AuthClient.getIntance(MainActivity.this);
        mAuthClient.getToken(new AuthClient.CallBack() {
            @Override
            public void onResult(final JSONObject json) {
                try {
                    final int resultCode = json.getInt(Constants.VALUNE_KEY_RESULT_CODE);
                    if (resultCode == Constants.RESULT_OK) {

                        ///////////////////////////////////获取UserToken////////////////////////////////////

                        final String sUserToken = json.getString(Constants.VALUNE_KEY_TOKEN);
                        Log.d(TAG, "认证成功 UserToken=" + sUserToken);
                        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USERTOKEN, sUserToken);

                        ////////////////////////////////////获取DevInfo////////////////////////////////////

                        Map<String, String> paramsMap = new HashMap<String, String>();
                        try {
                            //noinspection ResourceType
                            DevInfoManager devManager = (DevInfoManager) getSystemService(DevInfoManager.DATA_SERVER);
                            if (devManager == null) {
                                Log.d(TAG, "获取设备信息失败: devManager == null");
                                return;
                            }
                            String sSTB_MAC = devManager.getValue(DevInfoManager.STB_MAC);
                            String sPhoneNumber = devManager.getValue(DevInfoManager.PHONE);
                            String sAccount = devManager.getValue(DevInfoManager.ACCOUNT);
                            String sSTB_SN = devManager.getValue(DevInfoManager.STB_SN);
                            String sEPGAddress = devManager.getValue(DevInfoManager.EPGAddress);
                            String sTV_ID = devManager.getValue(DevInfoManager.TVID);
                            String sDeviceType = devManager.getValue(DevInfoManager.DeviceType);
                            String sFirmwareVersion = devManager.getValue(DevInfoManager.FirmwareVersion);
                            String sCDNAddress = devManager.getValue(DevInfoManager.CDNAddress);
                            String sCDNAddressBackup = devManager.getValue(DevInfoManager.BackupCDNAddress);

                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_STBMAC, sSTB_MAC);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_PHONE, sPhoneNumber);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_ACCOUNT, sAccount);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_STBSN, sSTB_SN);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_EPGADDRESS, sEPGAddress);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_TVID, sTV_ID);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_DEVICETYPE, sDeviceType);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_FIRMWAREVERSION, sFirmwareVersion);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_CDNADDRESS, sCDNAddress);
                            PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_BACKUPCDNADDRESS, sCDNAddressBackup);


                            final String sResult = "STB_MAC: " + sSTB_MAC + " PHONE: " + sPhoneNumber + " ACCOUNT: " + sAccount + " STB_SN: " + sSTB_SN + " EPGAddress: " +
                                    sEPGAddress + " TVID: " + sTV_ID + " DeviceType: " + sDeviceType + " FirmwareVersion: " + sFirmwareVersion +
                                    " CDNAddress: " + sCDNAddress + " BackupCDNAddress: " + sCDNAddressBackup;

                            Log.d(TAG, "获取设备信息成功: resultCode=" + sResult);

                            paramsMap.put(GlobleData.HTTP_VERSION_KEY, GlobleData.HTTP_VERSION_VALUE);
                            paramsMap.put("STBSN", sSTB_SN);
                            paramsMap.put("FirmwareVersion", sFirmwareVersion);

                            paramsMap.put("EPGAddress", sEPGAddress);
                            paramsMap.put("TVID", sTV_ID);
                            paramsMap.put("DeviceType", sDeviceType);
                            paramsMap.put("UserToken", sUserToken);
                            paramsMap.put("MAC", sSTB_MAC);
                            if (sPhoneNumber.isEmpty()) {
                                paramsMap.put("UserID", sAccount);
                            } else {
                                paramsMap.put("UserID", sPhoneNumber);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "获取设备信息失败!");
                            return;
                        }

                        ////////////////////////////////////获取OTTToken////////////////////////////////////
                        OkhttpUtil.okHttpGet(GlobleData.HTTP_URL_LOGIN, paramsMap, new CallBackUtil.CallBackString() {
                            @Override
                            public void onFailure(Call call, Exception e) {
                                Log.d(TAG, "获取用户信息失败!");
                            }

                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, response);
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (obj == null) {
                                        Log.d(TAG, "获取用户信息失败!");
                                        return;
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, "获取用户信息失败!");
                                    return;
                                }
                                try {
                                    JsonUserLogin loginBean = GsonUtil.fromJson(response, JsonUserLogin.class);
                                    if (loginBean == null)
                                        return;
                                    if (loginBean.getResult().equals("1")) {
                                        Toast.makeText(mContext, loginBean.getMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_ID, loginBean.getData().getUser_id());
                                    PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_OTTTOKEN, loginBean.getData().getOTTUserToken());
                                    PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USERP_PONE, loginBean.getData().getPhoneNum());
                                    PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USERP_APPID, loginBean.getData().getApppId());
                                    PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_EXPIREDTIME, loginBean.getData().getExpiredTime());
                                    PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_TIMEOUT, loginBean.getData().getTimeOut());

                                    String sResult =
                                            "Result: " + loginBean.getResult() +
                                                    " Message: " + loginBean.getMessage() +
                                                    " phoneNum: " + loginBean.getData().getPhoneNum() +
                                                    " OTTUserToken: " + loginBean.getData().getOTTUserToken() +
                                                    " expiredTime: " + loginBean.getData().getExpiredTime() +
                                                    " UserID: " + loginBean.getData().getUser_id() +
                                                    " TimeOut: " + loginBean.getData().getTimeOut();

                                    Log.d(TAG, "获取用户信息成功: " + sResult);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "获取用户信息失败!");
                                }
                            }
                        });


                    } else {
                        Log.d(TAG, "认证失败: resultCode=" + resultCode);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
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
                Log.d(TAG, response);
                try {
                    mMainPageJson = GsonUtil.fromJson(response, JsonMainPage.class);
                    if (mMainPageJson.getResult() == 0) {
                        Log.d(TAG, "主页数据返回成功!");

                        // 跑马灯
                        if (mMainPageJson.getData() != null && mMainPageJson.getData().getNotice() != null)
                            mMessageText.setText(mMainPageJson.getData().getNotice());

                        // 刷新数据
                        if (mMainPageJson.getData() != null)
                            initMainPageData(GlobleData.GRADE_TYPE_PRIMARY);
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

    private void getListPageData() {
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
                    mListPageJson = GsonUtil.fromJson(response, JsonListPage.class);
                    if (mMainPageJson.getResult() == 0) {
                        Log.d(TAG, "列表数据返回成功!");
                    } else {
                        Log.d(TAG, "列表数据返回失败!");
                    }
                } catch (Exception e) {
                    Log.d(TAG, "列表数据返回失败!");
                }
            }
        });
    }

    private void getQuitData() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(GlobleData.HTTP_VERSION_KEY, GlobleData.HTTP_VERSION_VALUE);
        OkhttpUtil.okHttpGet(GlobleData.HTTP_URL_QUIT, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                LogUtils.log_e("连接失败");
            }

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (jsonObj == null) {
                        Toast.makeText(mContext, "获取退出页面失败！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "获取退出页面失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    quitBean = GsonUtil.fromJson(response, QuitBean.class);
                    if (quitBean == null) {
                        return;
                    }
                    if (quitBean.getResult().equals("0")) {
                        mQuitImageUrl = quitBean.getData().getNew_image_url();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initMainPageData(int type) {
        switch (type) {
            case GlobleData.GRADE_TYPE_PRIMARY:
                if (mMainPageJson == null)
                    break;
                if (mMainPageJson.getData() == null)
                    break;
                if (mMainPageJson.getData().getXiaoxue() == null)
                    break;
                if (mMainPageJson.getData().getXiaoxue().size() > 0) {
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(0).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage00);
                    mRecommendName00.setText(mMainPageJson.getData().getXiaoxue().get(0).getName());
                }
                if (mMainPageJson.getData().getXiaoxue().size() > 1) {
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(1).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage01);
                    mRecommendName01.setText(mMainPageJson.getData().getXiaoxue().get(1).getName());
                }
                if (mMainPageJson.getData().getXiaoxue().size() > 2) {
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(2).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage02);
                    mRecommendName02.setText(mMainPageJson.getData().getXiaoxue().get(2).getName());
                }
                if (mMainPageJson.getData().getXiaoxue().size() > 3) {
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(3).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage03);
                    mRecommendName03.setText(mMainPageJson.getData().getXiaoxue().get(3).getName());
                }
                if (mMainPageJson.getData().getXiaoxue().size() > 4) {
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(4).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage12);
                    mRecommendName12.setText(mMainPageJson.getData().getXiaoxue().get(4).getName());
                }
                if (mMainPageJson.getData().getXiaoxue().size() > 5)
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(5).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom1);
                if (mMainPageJson.getData().getXiaoxue().size() > 6)
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(6).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom2);
                if (mMainPageJson.getData().getXiaoxue().size() > 7)
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(7).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom3);
                if (mMainPageJson.getData().getXiaoxue().size() > 8)
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(8).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom4);
                if (mMainPageJson.getData().getXiaoxue().size() > 9)
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(9).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom5);
                if (mMainPageJson.getData().getXiaoxue().size() > 10)
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(10).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom6);
                if (mMainPageJson.getData().getXiaoxue().size() > 11)
                    Glide.with(mContext).load(mMainPageJson.getData().getXiaoxue().get(11).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom7);
                break;
            case GlobleData.GRADE_TYPE_JUNIOR:
                if (mMainPageJson == null)
                    break;
                if (mMainPageJson.getData() == null)
                    break;
                if (mMainPageJson.getData().getChuzhong() == null)
                    break;
                if (mMainPageJson.getData().getChuzhong().size() > 0) {
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(0).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage00);
                    mRecommendName00.setText(mMainPageJson.getData().getChuzhong().get(0).getName());
                }
                if (mMainPageJson.getData().getChuzhong().size() > 1) {
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(1).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage01);
                    mRecommendName01.setText(mMainPageJson.getData().getChuzhong().get(1).getName());
                }
                if (mMainPageJson.getData().getChuzhong().size() > 2) {
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(2).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage02);
                    mRecommendName02.setText(mMainPageJson.getData().getChuzhong().get(2).getName());
                }
                if (mMainPageJson.getData().getChuzhong().size() > 3) {
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(3).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage03);
                    mRecommendName03.setText(mMainPageJson.getData().getChuzhong().get(3).getName());
                }
                if (mMainPageJson.getData().getChuzhong().size() > 4) {
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(4).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage12);
                    mRecommendName12.setText(mMainPageJson.getData().getChuzhong().get(4).getName());
                }
                if (mMainPageJson.getData().getChuzhong().size() > 5)
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(5).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom1);
                if (mMainPageJson.getData().getChuzhong().size() > 6)
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(6).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom2);
                if (mMainPageJson.getData().getChuzhong().size() > 7)
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(7).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom3);
                if (mMainPageJson.getData().getChuzhong().size() > 8)
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(8).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom4);
                if (mMainPageJson.getData().getChuzhong().size() > 9)
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(9).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom5);
                if (mMainPageJson.getData().getChuzhong().size() > 10)
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(10).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom6);
                if (mMainPageJson.getData().getChuzhong().size() > 11)
                    Glide.with(mContext).load(mMainPageJson.getData().getChuzhong().get(11).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom7);
                break;
            case GlobleData.GRADE_TYPE_SENIOR:
                if (mMainPageJson == null)
                    break;
                if (mMainPageJson.getData() == null)
                    break;
                if (mMainPageJson.getData().getGaozhong() == null)
                    break;
                if (mMainPageJson.getData().getGaozhong().size() > 0) {
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(0).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage00);
                    mRecommendName00.setText(mMainPageJson.getData().getGaozhong().get(0).getName());
                }
                if (mMainPageJson.getData().getGaozhong().size() > 1) {
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(1).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage01);
                    mRecommendName01.setText(mMainPageJson.getData().getGaozhong().get(1).getName());
                }
                if (mMainPageJson.getData().getGaozhong().size() > 2) {
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(2).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage02);
                    mRecommendName02.setText(mMainPageJson.getData().getGaozhong().get(2).getName());
                }
                if (mMainPageJson.getData().getGaozhong().size() > 3) {
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(3).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage03);
                    mRecommendName03.setText(mMainPageJson.getData().getGaozhong().get(3).getName());
                }
                if (mMainPageJson.getData().getGaozhong().size() > 4) {
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(4).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImage12);
                    mRecommendName12.setText(mMainPageJson.getData().getGaozhong().get(4).getName());
                }
                if (mMainPageJson.getData().getGaozhong().size() > 5)
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(5).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom1);
                if (mMainPageJson.getData().getGaozhong().size() > 6)
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(6).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom2);
                if (mMainPageJson.getData().getGaozhong().size() > 7)
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(7).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom3);
                if (mMainPageJson.getData().getGaozhong().size() > 8)
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(8).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom4);
                if (mMainPageJson.getData().getGaozhong().size() > 9)
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(9).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom5);
                if (mMainPageJson.getData().getGaozhong().size() > 10)
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(10).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom6);
                if (mMainPageJson.getData().getGaozhong().size() > 11)
                    Glide.with(mContext).load(mMainPageJson.getData().getGaozhong().get(11).getTuijian_number_image()).placeholder(R.drawable.load_detail).into(mRecommendImageBottom7);
                break;
            default:
                break;
        }
    }

    private void initMessageRecviver() {
        mMsgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobleData.MESSAGE_HEART_FAILED);
        intentFilter.addAction(GlobleData.MESSAGE_HEART_SUCCESS);
        intentFilter.addAction(GlobleData.MESSAGE_GET_INFO);
        registerReceiver(mMsgReceiver, intentFilter);
    }

    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (GlobleData.MESSAGE_HEART_FAILED.equals(intent.getAction())) {
                    String sResult = intent.getStringExtra("Result");
                    //Toast.makeText(context, "heart beat failed: " + sResult, Toast.LENGTH_SHORT).show();
                    //showReLoginDialog();
                }
                if (GlobleData.MESSAGE_HEART_SUCCESS.equals(intent.getAction())) {
                    String sResult = intent.getStringExtra("Result");
                    //Toast.makeText(context, "heart beat success: " + sResult, Toast.LENGTH_SHORT).show();
                }
                if (GlobleData.MESSAGE_GET_INFO.equals(intent.getAction())) {
                    String sResult = intent.getStringExtra("Result");
                    boolean bSucc = intent.getBooleanExtra("Successed", false);
                    //Toast.makeText(context, sResult, Toast.LENGTH_SHORT).show();
                    if (!bSucc)
                        showReLoginDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTimeData() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                String sTimeData = XESUtil.getDateTime();
                Bundle bundle = new Bundle();
                bundle.putString("time", sTimeData);
                msg.setData(bundle);
                msg.what = 1;
                mHandler.sendMessage(msg);
                sendBroadcastTimeData(sTimeData);
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private void sendBroadcastTimeData(String timedata) {
        Intent intent = new Intent(GlobleData.MESSAGE_UPDATE_TIME);
        intent.putExtra("time", timedata);
        this.sendBroadcast(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (presstime == 2) {
                MobclickAgent.onEvent(mContext, "quit_sure");
                MobclickAgent.onProfileSignOff();//统计用户退出
                Intent intent = new Intent(GlobleData.RECEIVER_ACTION_FINISH);
                mContext.sendBroadcast(intent);
                finish();
                System.exit(0);

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        MobclickAgent.onEvent(mContext, "quit_sure");
//                        MobclickAgent.onProfileSignOff();//统计用户退出
//                        Intent intent = new Intent(GlobleData.RECEIVER_ACTION_FINISH);
//                        mContext.sendBroadcast(intent);
//                        finish();
//                        System.exit(0);
//                    }
//                }, 100);
            }
            presstime += 1;
            builder = new Dialog(MainActivity.this, R.style.dialogTransparent);
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        ToastUtils.showToast(mContext, "再按一次返回退出应用");
//                        ToastUtils.showMY(mContext, "");
                    }
                    return false;
                }
            });
            RelativeLayout quitDialog = (RelativeLayout) getLayoutInflater().inflate(R.layout.pop_dialog_quit, null);
            final Button mOk = (Button) quitDialog.findViewById(R.id.popOK);
            final Button mCancel = (Button) quitDialog.findViewById(R.id.popCancel);
            final ImageView mquit_image = (ImageView) quitDialog.findViewById(R.id.quit_image);
            final ImageView quit_image_back = (ImageView) quitDialog.findViewById(R.id.quit_image_back);

            List<LinearLayout> layouts = new ArrayList<>();
            layouts.add((LinearLayout) quitDialog.findViewById(R.id.ll_quit0));
            layouts.add((LinearLayout) quitDialog.findViewById(R.id.ll_quit1));
            layouts.add((LinearLayout) quitDialog.findViewById(R.id.ll_quit2));
            layouts.add((LinearLayout) quitDialog.findViewById(R.id.ll_quit3));
            layouts.add((LinearLayout) quitDialog.findViewById(R.id.ll_quit4));

            List<ImageView> imageViews = new ArrayList<>();
            imageViews.add((ImageView) quitDialog.findViewById(R.id.iv_quit0));
            imageViews.add((ImageView) quitDialog.findViewById(R.id.iv_quit1));
            imageViews.add((ImageView) quitDialog.findViewById(R.id.iv_quit2));
            imageViews.add((ImageView) quitDialog.findViewById(R.id.iv_quit3));
            imageViews.add((ImageView) quitDialog.findViewById(R.id.iv_quit4));

            List<TextView> textViews = new ArrayList<>();
            textViews.add((TextView) quitDialog.findViewById(R.id.tv_quit0));
            textViews.add((TextView) quitDialog.findViewById(R.id.tv_quit1));
            textViews.add((TextView) quitDialog.findViewById(R.id.tv_quit2));
            textViews.add((TextView) quitDialog.findViewById(R.id.tv_quit3));
            textViews.add((TextView) quitDialog.findViewById(R.id.tv_quit4));

            for (int i = 0; i < quitBean.getData().getTuijianwei().size(); i++) {
                Glide.with(mContext).load(quitBean.getData().getTuijianwei().get(i).getTuijianwei_image()).into(imageViews.get(i));
                textViews.get(i).setText(quitBean.getData().getTuijianwei().get(i).getTuijianwei_name());
                final int finalI = i;
                layouts.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id = quitBean.getData().getTuijianwei().get(finalI).getTuijianwei_id();
                        int recommend_id = 0;
                        if (!TextUtils.isEmpty(id)) {
                            recommend_id = Integer.valueOf(id);
                        }

                        int recommend_class = 0;
                        switch (quitBean.getData().getTuijianwei().get(finalI).getTuijianwei_type()) {
                            case 1: // 栏目

                                Intent intent_list_page = new Intent(MainActivity.this, ListPageActivity.class);
                                intent_list_page.putExtra("class_list", (Serializable) mListPageJson);
                                intent_list_page.putExtra("grade_index", quitMap.get(quitBean.getData().getTuijianwei().get(finalI).getTuijianwei_category()));
                                List<JsonListPage.pageData.pageDataDetail.grade> grades = mListPageJson.getData().getLists().get(mButtonClickedFlag).getLists();//获取到年纪列表
                                for (int i = 0; i < grades.size(); i++) {
                                    for (int j = 0; j < grades.get(i).getLists().size(); j++) {
                                        if (recommend_id == grades.get(i).getLists().get(j).getNode_id()) {
                                            recommend_class = i;
                                        }
                                    }
                                }
                                intent_list_page.putExtra("class_index", recommend_class);
                                intent_list_page.putExtra("recommend_id", recommend_id);
                                startActivity(intent_list_page);
                                presstime = 1;
//                                builder.dismiss();
                                break;
                            case 2: // 专题

                                Intent intent_theme = new Intent(MainActivity.this, ThemeActivity.class);
                                intent_theme.putExtra("specialid", recommend_id);
                                startActivity(intent_theme);
                                presstime = 1;
//                                builder.dismiss();
                                break;
                            case 3: // 详情

                                Intent intent_detail = new Intent(MainActivity.this, DetailActivity.class);
                                intent_detail.putExtra("libraryId", recommend_id);
                                startActivity(intent_detail);
                                presstime = 1;
//                                builder.dismiss();
                                break;
                            case 4: // 特色

                                Intent intent_special = new Intent(MainActivity.this, SpecialActivity.class);
                                intent_special.putExtra("class_list", (Serializable) mListPageJson);
                                intent_special.putExtra("grade_index", quitMap.get(quitBean.getData().getTuijianwei().get(finalI).getTuijianwei_category()));
                                intent_special.putExtra("libraryId", recommend_id);
                                startActivity(intent_special);
                                presstime = 1;
//                                builder.dismiss();
                                break;

                            case 5:

                                break;
                            default:
                                break;
                        }
                    }
                });
            }

            Window dialogWindow = builder.getWindow();
            dialogWindow.setGravity(Gravity.FILL);
            Glide.with(mContext).load(mQuitImageUrl).into(mquit_image);

            //获取当前屏幕的大小
            int width = getWindow().getDecorView().getRootView().getWidth();
            int height = getWindow().getDecorView().getRootView().getHeight();
            //找到当前页面的跟布局
            View view = getWindow().getDecorView().getRootView();
            //设置缓存
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            //从缓存中获取当前屏幕的图片
            Bitmap temBitmap = view.getDrawingCache();
            Bitmap blurBitmap = BlurBitmapUtil.blurBitmap(mContext, temBitmap, 10);//高斯模糊处理
            quit_image_back.setImageBitmap(blurBitmap);

            mOk.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    mOk.setSelected(b);
                }
            });

            mCancel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    mCancel.setSelected(b);
                }
            });

            mOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    Intent intent = new Intent(GlobleData.RECEIVER_ACTION_FINISH);
                    mContext.sendBroadcast(intent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MobclickAgent.onEvent(mContext, "quit_sure");
                            MobclickAgent.onProfileSignOff();//统计用户退出
                            Intent intent = new Intent(GlobleData.RECEIVER_ACTION_FINISH);
                            mContext.sendBroadcast(intent);
                            finish();
                            System.exit(0);
                        }
                    }, 100);

                }
            });

            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presstime = 1;
                    builder.dismiss();
                    MobclickAgent.onEvent(mContext, "quit_cancle");
                    return;
                }
            });

            builder.setContentView(quitDialog);

            builder.show();
            builder.dismiss();
            builder.setCancelable(true);
            builder.show();
            mCancel.requestFocus();
            mCancel.setSelected(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initReLoginDialog() {
        mBuilderReLogin = new Dialog(MainActivity.this, R.style.dialogTransparent);
        mReLoginDialog = (RelativeLayout) getLayoutInflater().inflate(R.layout.pop_dialog_relogin, null);
        mRetryButton = (Button) mReLoginDialog.findViewById(R.id.retry_login_button);

        mBuilderReLogin.setContentView(mReLoginDialog);
        mBuilderReLogin.setCancelable(true);
        mRetryButton.requestFocus();
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBuilderReLogin.dismiss();
            }
        });

        mBuilderReLogin.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                initUserAuth();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        switch (mButtonClickedFlag) {
                            case GlobleData.GRADE_TYPE_PRIMARY:
                                navFirstPositionSelected();
                                break;
                            case GlobleData.GRADE_TYPE_JUNIOR:
                                navSecondPositonSelected();
                                break;
                            case GlobleData.GRADE_TYPE_SENIOR:
                                navThirdPositonSelected();
                                break;
                        }
                    }
                }, 200);
            }
        });
    }

    private void showReLoginDialog() {
        if (!mBuilderReLogin.isShowing()) {
            mBuilderReLogin.show();
            mRetryButton.requestFocus();
        }
    }

    public float getDimension(int id) {
        return getResources().getDimension(id);
    }
}