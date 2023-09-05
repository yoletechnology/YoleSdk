package com.yolesdk.sdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yolesdk.sdk.dcb.PaymentView;
import com.yolesdk.sdk.bigossp.YoleBannerListener;
import com.yolesdk.sdk.bigossp.YoleInterstitialListener;
import com.yolesdk.sdk.bigossp.YoleRewardVideoListener;
import com.yolesdk.sdk.bigossp.YoleSplashAdListener;
import com.yolesdk.sdk.callback.CallBackFunction;
import com.yolesdk.sdk.callback.InitCallBackFunction;
import com.yolesdk.sdk.data.InitSdkData;

import java.util.Timer;
import java.util.TimerTask;

public class YoleSdkMgr extends YoleSdkBase{

    private String TAG = "Yole_YoleSdkMgr";
    private static  YoleSdkMgr _instance = null;
    public String ruPayOrderNum = "";

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

    /** bcd支付*/
    public void bcdStartPay(Activity act, String amount, String orderNumber, CallBackFunction backFunction)
    {
        if(user.getConfig().isDcb() == false)
        {
            if(isDebugger)
                Toast.makeText(act, "sdk初始化时，未接入Dcb模块", Toast.LENGTH_SHORT).show();
            backFunction.onCallBack(false,"sdk初始化时，未接入Dcb模块","");
            return;
        }

        user.setAmount(amount);
        user.setPayOrderNum(orderNumber);
        user.setPayCallBack(new CallBackFunction(){
            @Override
            public void onCallBack(boolean data,String info,String billingNumber) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingDialog.getInstance(act).hideDialog();
                        if(data == true){
                        }else{
                        }
                        if(isDebugger)
                            Toast.makeText(act, act.getString(R.string.results)+":"+info, Toast.LENGTH_SHORT).show();
                        backFunction.onCallBack(data,info,billingNumber);
                    }
                });
            }
        });

        if(this.getBCDFeasibility(act) == false)
        {
            YoleSdkMgr.getsInstance().user.getPayCallBack().onCallBack(false,act.getString(R.string.parameter_error),"");
            return;
        }


        Intent i =new Intent(act, PaymentView.class);
        act.startActivity(i);
    }
    public void createDCBInvoiceBySdk() {

        LoadingDialog.getInstance(context).show();//显示

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
    /************************SMS 支付*********************************/
    /*****************************************************************/


    /**sms模块的权限注册*/
    public void  smsRequest(Activity var1) {

        sms.smsRequest(var1);
    }
    /**sms支付开始 设置回调，显示loading界面***/
    public void  smsStartPay(Activity var1,String _payOrderNum,CallBackFunction callBack) {
        ruPayOrderNum = _payOrderNum;
        LoadingDialog.getInstance(var1).show();//显示
        user.setPayCallBack(new CallBackFunction(){
            @Override
            public void onCallBack(boolean data, String info, String billingNumber) {
                LoadingDialog.getInstance(var1).hide();//显示
                callBack.onCallBack(data,info,billingNumber);
                smsPaymentNotify(data);
            }
        });
        this.paySdkStartPay();
    }
    /**sms支付开始 发送短信功能***/
    private void paySdkStartPay()
    {
        sms.sendSMSS(user.getSmsCode(),user.getSmsNumber());
    }
    /**sms支付完成 同步服务器结果***/
    public void smsPaymentNotify(boolean  paymentStatus)
    {
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    request.smsPaymentNotify(ruPayOrderNum,paymentStatus == true ? "SUCCESSFUL" : "FAILED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*****************************************************************/
    /************************bigossp     广告**************************/
    /*****************************************************************/

    /**************开屏*************/
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
    /**设置开屏延时检测回调*/
    public void setSplashDelayBack(Activity _var, int delayInitOverTime,InitCallBackFunction listener)
    {
        if(bigosspMgr != null && bigosspMgr.bigosspInitSuccess == true)
        {
            listener.success(null);
        }
        else
        {
            bigosspInitBack = listener;
            bigosspInitBackTimer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    bigosspInitBack = null;
                    listener.fail("");
                }
            };
            bigosspInitBackTimer.schedule(task, delayInitOverTime);
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
    /**************插 屏*************/
    public void showInterstitial(Activity _var, YoleInterstitialListener listener)
    {
        if(bigosspMgr == null || bigosspMgr.bigosspInitSuccess == false)
        {
            listener.onAdError(-1,"sdk init faile");
            return;
        }
        showInterstitial(_var,"",listener);
    }
    public void showInterstitial(Activity _var,String slotId, YoleInterstitialListener listener)
    {
        if(bigosspMgr == null || bigosspMgr.bigosspInitSuccess == false)
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
    /**************视 屏*************/
    public void showRewardVideo(Activity _var,YoleRewardVideoListener listener)
    {
        if(bigosspMgr == null || bigosspMgr.bigosspInitSuccess == false)
        {
            listener.onAdError(-1,"sdk init faile");
            return;
        }
        showRewardVideo(_var,"",listener);
    }
    public void showRewardVideo(Activity _var,String slotId, YoleRewardVideoListener listener)
    {
        if(bigosspMgr == null || bigosspMgr.bigosspInitSuccess == false)
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
    /**************横 幅*************/
    public void showBanner(Activity _var,ViewGroup containerView, YoleBannerListener listener)
    {
        if(bigosspMgr == null || bigosspMgr.bigosspInitSuccess == false)
        {
            listener.onAdError(-1,"sdk init faile");
            return;
        }
        showBanner(_var,"",containerView,listener);
    }
    public void showBanner(Activity _var,String slotId, ViewGroup containerView, YoleBannerListener listener)
    {
        if(bigosspMgr == null || bigosspMgr.bigosspInitSuccess == false)
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
