package tv.dfyc.yckt;

import android.app.Application;
import android.app.DevInfoManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.chinamobile.authclient.AuthClient;
import com.chinamobile.authclient.Constants;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import tv.dfyc.yckt.beans.JsonUserLogin;
import tv.dfyc.yckt.network.CallBackUtil;
import tv.dfyc.yckt.network.GsonUtil;
import tv.dfyc.yckt.network.OkhttpUtil;
import tv.dfyc.yckt.util.GlobleData;
import tv.dfyc.yckt.util.LogUtils;
import tv.dfyc.yckt.util.PreferencesUtil;

/**
 * Created by android on 2018/3/19.
 */

public class XESApplication extends Application {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

//        UMConfigure.s = true;
        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(true);
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(this, "5a979adbf43e4832a6000424", "jiangsu_yidong_fensheng", UMConfigure.DEVICE_TYPE_BOX, null);
//        UMConfigure.init(this, "5ab474df8f4a9d40d80001a5", "jiangsu_yidong_fensheng", UMConfigure.DEVICE_TYPE_BOX, null);
        //开启ShareSDK debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
//        MobclickAgent.setDebugMode(true);//开启调试模式（如果不开启debug运行不会上传umeng统计）
        UMConfigure.setEncryptEnabled(true);
//        MobclickAgent.setCheckDevice(false);//不采集手机mac地址

        getLocalInfor();

    }

    private void getLocalInfor() {
        LogUtils.log_e("getLocalInfor");
        AuthClient mAuthClient = AuthClient.getIntance(mContext);
        mAuthClient.getToken(new AuthClient.CallBack() {
            @Override
            public void onResult(final JSONObject json) {
                try {
                    final int resultCode = json.getInt(Constants.VALUNE_KEY_RESULT_CODE);
                    if (resultCode == Constants.RESULT_OK) {

                        ///////////////////////////////////获取UserToken////////////////////////////////////

                        final String sUserToken = json.getString(Constants.VALUNE_KEY_TOKEN);
                        Log.d("MSG", "认证成功 UserToken=" + sUserToken);
                        PreferencesUtil.PutAuthorValue(mContext, GlobleData.PREFERENCE_AUTHOR_USERTOKEN, sUserToken);

                        ////////////////////////////////////获取DevInfo////////////////////////////////////

                        Map<String, String> paramsMap = new HashMap<String, String>();
                        try {
                            //noinspection ResourceType
                            DevInfoManager devManager = (DevInfoManager) mContext.getSystemService(DevInfoManager.DATA_SERVER);
                            if (devManager == null) {
                                Log.d("MSG", "获取设备信息失败: devManager == null");
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

                            Log.d("MSG", "获取设备信息成功: resultCode=" + sResult);

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
                            Log.d("MSG", "获取设备信息失败!");
                            return;
                        }

                        ////////////////////////////////////获取OTTToken////////////////////////////////////
                        OkhttpUtil.okHttpGet(GlobleData.HTTP_URL_LOGIN, paramsMap, new CallBackUtil.CallBackString() {
                            @Override
                            public void onFailure(Call call, Exception e) {
                                Log.d("MSG", "获取用户信息失败!");
                            }

                            @Override
                            public void onResponse(String response) {
                                Log.d("MSG", response);
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (obj == null) {
                                        Log.d("MSG", "获取用户信息失败!");
                                        return;
                                    }
                                } catch (Exception e) {
                                    Log.d("MSG", "获取用户信息失败!");
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
                                    MobclickAgent.onProfileSignIn(loginBean.getData().getUser_id());//统计用户登录
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

                                    Log.d("MSG", "获取用户信息成功: " + sResult);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("MSG", "获取用户信息失败!");
                                }
                            }
                        });


                    } else {
                        Log.d("MSG", "认证失败: resultCode=" + resultCode);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
