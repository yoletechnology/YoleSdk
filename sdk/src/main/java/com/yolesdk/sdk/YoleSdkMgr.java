package com.yolesdk.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yolesdk.sdk.bcd.PaymentView;
import com.yolesdk.sdk.bigossp.YoleBannerListener;
import com.yolesdk.sdk.bigossp.YoleInterstitialListener;
import com.yolesdk.sdk.bigossp.YoleRewardVideoListener;
import com.yolesdk.sdk.bigossp.YoleSplashAdListener;
import com.yolesdk.sdk.callback.CallBackFunction;
import com.yolesdk.sdk.callback.InitCallBackFunction;
import com.yolesdk.sdk.data.YoleInitConfig;
import com.yolesdk.sdk.bigossp.BigosspMgr;
import com.yolesdk.sdk.tool.UserInfo;
import com.yolesdk.sdk.R;

import java.util.Timer;
import java.util.TimerTask;

public class YoleSdkMgr {

    private String TAG = "YoleSdkMgr";
    private static  YoleSdkMgr _instance = null;
    private YoleInitConfig config = null;
    private NetUtil request = null;
    private Context context =  null;
//    private Activity act =  null;
    public UserInfo user =  null;
    public BigosspMgr bigosspMgr =  null;
    public InitCallBackFunction initBack = null;
    public boolean isDebugger = false;
    public boolean initSuccess = false;
    public InitCallBackFunction sdkInitBack = null;
    public Timer sdkInitBackTimer = null;
//    public Handler handler = null;

    public static YoleSdkMgr getsInstance() {
        if(YoleSdkMgr._instance == null)
        {
            YoleSdkMgr._instance = new YoleSdkMgr();
        }
        return YoleSdkMgr._instance;
    }
    private YoleSdkMgr() {
        Log.e(TAG,"YoleSdkMgr");
    }
    public void initSdk( Context _var1,YoleInitConfig _config, InitCallBackFunction _initBack)
    {

        context = _var1;
        config = _config;
        initBack = _initBack;
//        handler = new Handler(Looper.getMainLooper());

        this.init(_var1,config.getAppId(),config.getAppKey(),config.getCpCode(),config.isDebug());
        this.initBigossp(_var1,config,new InitCallBackFunction(){
            @Override
            public void success(InitSdkData info) {
                initSuccess = true;
                if(sdkInitBack != null)
                {
                    if(sdkInitBackTimer != null) {
                        sdkInitBackTimer.cancel();
                    }
                    sdkInitBack.success(null);
                }
                initAppBySdk(config.getCpCode(),config.getUserAgent(),config.getMobile());
            }

            @Override
            public void fail(String info) {
                if(sdkInitBack != null)
                {
                    if(sdkInitBackTimer != null) {
                        sdkInitBackTimer.cancel();
                    }
                    sdkInitBack.fail("");
                }
                initBack.fail(info);
            }
        });
    }
    private void init(Context var1,String appId,String appkey,String cpCode,boolean _isDebugger)
    {
        request = new NetUtil();
        isDebugger = _isDebugger;
        user = new UserInfo(var1,appkey,cpCode,_isDebugger);
        if(appId.length() > 0)
            bigosspMgr = new BigosspMgr(var1,config.getAppId());
    }
    private void initAppBySdk(String cpCode,String userAgent,String mobile) {
        userAgent = userAgent.length() <=0 ? user.getPhoneModel() : userAgent;
        mobile = mobile.length() <=0 ? user.getPhoneNumber() : mobile;
        this.initAppBySdk(cpCode,userAgent,mobile,user.getGaid(),user.getImei(),user.getMac(),user.getCountryCode(),user.getMcc(),user.getMnc());
    }
    private void initBigossp(Context var1,YoleInitConfig config,InitCallBackFunction _initBack)
    {
        if(bigosspMgr != null)
            bigosspMgr.initAd(var1,config,_initBack);
        else {
            _initBack.success(null);
        }
    }
    public void startPay(Activity act, String amount, String orderNumber, CallBackFunction backFunction)
    {
        user.setAmount(amount);
        user.setPayOrderNum(orderNumber);
        user.setPayCallBack(new CallBackFunction(){
            @Override
            public void onCallBack(boolean data,String info,String billingNumber) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.getInstance(act).hideDialog();
                        if(data == true)
                        {

                        }
                        else
                        {

                        }
                        if(isDebugger)
                            Toast.makeText(act, act.getString(R.string.results)+":"+info, Toast.LENGTH_SHORT).show();
                        backFunction.onCallBack(data,info,billingNumber);
                    }
                });
            }
        });

        if(this.getFeasibility(act) == false)
        {
            YoleSdkMgr.getsInstance().user.getPayCallBack().onCallBack(false,act.getString(R.string.parameter_error),"");
            return;
        }


        Intent i =new Intent(act, PaymentView.class);
        act.startActivity(i);
    }


    //支付的可行性
    private boolean getFeasibility(Activity act)
    {
        if(user.getCpCode().length() <= 0)
        {
            Toast.makeText(act, act.getString(R.string.cpcode_error), Toast.LENGTH_SHORT).show();
             return false;
        }
        if(user.getAmount().length() <= 0)
        {
            Toast.makeText(act, act.getString(R.string.amount_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(user.getCountryCode().length() <= 0)
        {
            Toast.makeText(act, act.getString(R.string.countrycode_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(user.getMcc().length() <= 0)
        {
            Toast.makeText(act, act.getString(R.string.mcc_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(user.getMnc().length() <= 0)
        {
            Toast.makeText(act, act.getString(R.string.mnc_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(user.getPayOrderNum().length() <= 0)
        {
            Toast.makeText(act, act.getString(R.string.payordernum_error), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void initAppBySdk(String cpCode,String userAgent,String mobile,String gaid,String imei,String mac,String countryCode,String mcc,String mnc) {

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    request.initAppBySdk(
                            mobile,
                            gaid,
                            userAgent,
                            imei,
                            mac,
                            countryCode,
                            mcc,
                            mnc,
                            cpCode
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void createDCBInvoiceBySdk() {

        LoadingDialog.getInstance(context).showDialog();//显示

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    request.createDCBInvoiceBySdk(
                            user.getCpCode(),
                            user.getPhoneNumber(),
                            user.getAmount(),
                            user.getCountryCode(),
                            user.getMcc(),
                            user.getMnc(),
                            user.getPayOrderNum());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /*****************************************************************/
    /************************开     屏*********************************/
    /*****************************************************************/
    public void showSplash(Activity _var,int delayInitOverTime,String slotId, int app_icon, String app_name, ViewGroup containerView, YoleSplashAdListener listener)
    {

        if(bigosspMgr == null)
        {
            listener.onAdError(-1,"The cpid is not configured correctly, causing the advertising module to fail to initialize");
            return;
        }

        YoleSdkMgr.getsInstance().setSplashDelayBack(_var,delayInitOverTime,new InitCallBackFunction() {
            @Override
            public void success(InitSdkData info) {

                bigosspMgr.showSplash(_var,slotId, app_icon, app_name, containerView, listener);
            }

            @Override
            public void fail(String info) {
                listener.onAdError(-1,info);
            }
        });

    }
    //设置开屏延时检测回调
    public void setSplashDelayBack(Activity _var, int delayInitOverTime,InitCallBackFunction listener)
    {
        if(initSuccess == true)
        {
            listener.success(null);
        }
        else
        {
            sdkInitBack = listener;
            sdkInitBackTimer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    sdkInitBack = null;
                    listener.fail("");
                }
            };
            sdkInitBackTimer.schedule(task, delayInitOverTime);
        }
    }
    public boolean splashIsSkippable()
    {
        if(bigosspMgr != null)
            return bigosspMgr.splashIsSkippable();
        else
            return false;
    }
    public void splashDestroy()
    {
        if(bigosspMgr != null)
            bigosspMgr.splashDestroy();
    }
    /*****************************************************************/
    /************************插     屏*********************************/
    /*****************************************************************/
    public void showInterstitial(Activity _var, YoleInterstitialListener listener)
    {
        showInterstitial(_var,"",listener);
    }
    public void showInterstitial(Activity _var,String slotId, YoleInterstitialListener listener)
    {
        if(initSuccess == false)
        {
            listener.onAdError(-1,"sdk init faile");
            return;
        }
        if(bigosspMgr == null)
        {
            listener.onAdError(-1,"The cpid is not configured correctly, causing the advertising module to fail to initialize");
            return;
        }

        bigosspMgr.showInterstitial(_var,slotId,listener);

    }
    /*****************************************************************/
    /************************视     屏*********************************/
    /*****************************************************************/
    public void showRewardVideo(Activity _var,YoleRewardVideoListener listener)
    {
        showRewardVideo(_var,"",listener);
    }
    public void showRewardVideo(Activity _var,String slotId, YoleRewardVideoListener listener)
    {
        if(initSuccess == false)
        {
            listener.onAdError(-1,"sdk init faile");
            return;
        }
        if(bigosspMgr == null)
        {
            listener.onAdError(-1,"The cpid is not configured correctly, causing the advertising module to fail to initialize");
            return;
        }

        bigosspMgr.showRewardVideo(_var,slotId,listener);
    }

    /*****************************************************************/
    /************************横     幅*********************************/
    /*****************************************************************/
    public void showBanner(Activity _var,ViewGroup containerView, YoleBannerListener listener)
    {
        showBanner(_var,"",containerView,listener);
    }
    public void showBanner(Activity _var,String slotId, ViewGroup containerView, YoleBannerListener listener)
    {
        if(initSuccess == false)
        {
            listener.onAdError(-1,"sdk init faile");
            return;
        }
        if(bigosspMgr == null)
        {
            listener.onAdError(-1,"The cpid is not configured correctly, causing the advertising module to fail to initialize");
            return;
        }

        bigosspMgr.showBanner(_var,slotId, containerView, listener);

    }
    public void bannerDestroy()
    {
        if(bigosspMgr != null)
            bigosspMgr.bannerDestroy();
    }



}
