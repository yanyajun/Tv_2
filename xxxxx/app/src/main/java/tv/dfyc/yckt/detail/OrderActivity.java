package tv.dfyc.yckt.detail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import tv.dfyc.yckt.R;
import tv.dfyc.yckt.beans.JsonBuyListData;
import tv.dfyc.yckt.beans.JsonGoodsListData;
import tv.dfyc.yckt.network.CallBackUtil;
import tv.dfyc.yckt.network.GsonUtil;
import tv.dfyc.yckt.network.OkhttpUtil;
import tv.dfyc.yckt.util.GlobleData;
import tv.dfyc.yckt.util.PreferencesUtil;

/**
 * Created by Administrator on 2017/8/16.
 */

public class OrderActivity extends Activity {
    private static final String TAG = "OrderActivity";
    private Context mContext;
    private WebView mCreateOrderWebview;
    private JsonGoodsListData.GoodDetail mGoodsDetail;
    private String mOrderUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        mContext = this;
        mCreateOrderWebview = (WebView) findViewById(R.id.create_order_webview);
        mCreateOrderWebview.getSettings().setSavePassword(false);
        mCreateOrderWebview.removeJavascriptInterface("searchBoxJavaBridge_");
        mCreateOrderWebview.removeJavascriptInterface("accessibility");
        mCreateOrderWebview.removeJavascriptInterface("accessibilityTraversal");
        mCreateOrderWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);   //在当前的webview中跳转到新的url
                return true;
            }
        });
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        mOrderUrl = bundle.getString("product_order_url");
        mGoodsDetail = (JsonGoodsListData.GoodDetail) bundle.getSerializable("product_good_info");

        mCreateOrderWebview.getSettings().setJavaScriptEnabled(true);
        // jscall:  xes_app.closeWindow(String msg);
        mCreateOrderWebview.addJavascriptInterface(OrderActivity.this, "xes_app");
        mCreateOrderWebview.loadUrl(mOrderUrl);

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCreateOrderWebview.loadUrl("http://api.nmop.jr3d.cn/api/app/backurl?version_id=16");
            }
        },10*1000);
        */
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
    }

    @JavascriptInterface
    public void closeWindow(String msg) {// 属于android的回调接口
        checkOrderResult();
    }

    private void checkOrderResult() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(GlobleData.HTTP_VERSION_KEY, GlobleData.HTTP_VERSION_VALUE);
        paramsMap.put("productType", mGoodsDetail.getGoods_id());
        String userID = PreferencesUtil.GetAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USER_ID, "");
        paramsMap.put("UserId", userID);

        OkhttpUtil.okHttpGet(GlobleData.HTTP_URL_PAYQUERT, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                Toast.makeText(mContext, "连接失败！", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onResponse(String response) {
//                Log.d(TAG, "app check order result: " + response);
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    if (jsonObj == null) {
                        Toast.makeText(mContext, "查询订单结果失败！", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "查询订单结果失败！", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                try {
                    JsonBuyListData jsonResult = GsonUtil.fromJson(response, JsonBuyListData.class);
                    if (jsonResult == null) {
                        finish();
                        return;
                    }
                    if (jsonResult.getResult().equals("0")) {
                        String sResult = jsonResult.getData();
                        String sToast;
                        if (sResult.equals("1")) {
                            sToast = "支付成功！";
                            sendBroadcastOrderFinish(1, mGoodsDetail.getName());
                        } else if (sResult.equals("2")) {
                            sToast = "支付失败！";
                            sendBroadcastOrderUnFinish();
                        } else if (sResult.equals("3")) {
                            sToast = "用户取消！";
                            sendBroadcastOrderUnFinish();
                        } else if (sResult.equals("4")) {
                            sToast = "超时取消！";
                            sendBroadcastOrderUnFinish();
                        } else if (sResult.equals("5")) {
                            sToast = "待支付！";
                            sendBroadcastOrderUnFinish();
                        } else {
                            sToast = "未知结果！";
                        }
//                        Toast.makeText(mContext, sResult+sToast, Toast.LENGTH_LONG).show();//这里原来是注注释掉的
                    } else {
                        Toast.makeText(mContext, jsonResult.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendBroadcastOrderFinish(int iResult, String good_name) {
        Intent intent = new Intent(GlobleData.MESSAGE_ORDER_FINISH);
        intent.putExtra("order_finish", iResult);
        intent.putExtra("good_name", good_name);
        this.sendBroadcast(intent);
    }

    private void sendBroadcastOrderUnFinish() {
        Intent intent = new Intent(GlobleData.MESSAGE_ORDER_UNFINISH);
        intent.putExtra("product_good_info", mGoodsDetail);
        this.sendBroadcast(intent);
    }
}
