package com.yolesdk.sdk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.yolesdk.sdk.callback.CallBackFunction;
import com.yolesdk.sdk.callback.InitCallBackFunction;
import com.yolesdk.sdk.data.UserInfo;
import com.yolesdk.sdk.data.init.YoleInitConfig;
//import com.yolesdk.sdk.ru_sms.SendSms;
import com.yolesdk.sdk.tool.NetworkRequest;

import java.io.IOException;
import java.net.URL;
import java.util.Timer;

public class YoleSdkBase {
    private String TAG = "Yole_YoleSdkBase";
//    private static  YoleSdkMgr _instance = null;
    protected Context context =  null;
    protected boolean isDebugger = false;


    /**各种网络接口**/
    protected NetworkRequest request = null;
    /**用户信息(通过用户设置 和 请求的返回。组装成的数据)**/
    public UserInfo user =  null;
//    protected SendSms sms =  null;
    /**广告sdk初始化定时器*/
    protected Timer bigosspInitBackTimer = null;


    /**YoleSdk 初始化结果的回调*/
    public CallBackFunction initBasicSdkBack = null;
    /**bigosspSdk 初始化结果的回调*/
    protected InitCallBackFunction bigosspInitBack = null;

    /**sdk初始化的主接口*/
    public void initSdk(Context _var1, YoleInitConfig _config, InitCallBackFunction _initBack)
    {
        context = _var1;
        this.init(_var1,_config);


//        //初始化 ruSms的回调
//        if(_config.isRuSms() == true) {
//            YoleSdkMgr.getsInstance().initRuSms(next1);
//        }
        CallBackFunction next1 = new CallBackFunction(){
            @Override
            public void onCallBack(boolean result, String info1, String info2) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = null;
                        try {
                            String  icon = YoleSdkMgr.getsInstance().user.initSdkData.productIcon;
                            URL url = new URL(icon);
                            YoleSdkMgr.getsInstance().user.initSdkData.productIconBitmap = BitmapFactory.decodeStream(url.openStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };

        //初始化 基本信息的回调
        CallBackFunction next2 = new CallBackFunction(){
            @Override
            public void onCallBack(boolean result, String info1, String info2) {
                Log.i(TAG,"initBasicSdk:"+result);
                if(result == true){
                    _initBack.success(user.initSdkData);
                    next1.onCallBack(false,null,null);
                }else{
                    _initBack.fail(info1);
                }
            }
        };
        initBasicSdk(_config.getCpCode(),_config.getUserAgent(),_config.getMobile(),next2);
    }
    /**创建sdk内的各个功能模块*/
    protected void init(Context var1,YoleInitConfig _config)
    {
        request = new NetworkRequest();
        isDebugger = _config.isDebug();
        user = new UserInfo(var1,_config);
//        if(_config.isRuSms() == true)
//            sms = new SendSms(var1);

    }

    /**初始化sdk*/
    protected void initBasicSdk(String cpCode,String userAgent,String mobile,CallBackFunction callBack) {
        initBasicSdkBack = callBack;
        userAgent = userAgent.length() <=0 ? user.getPhoneModel() : userAgent;
        mobile = mobile.length() <=0 ? user.getPhoneNumber() : mobile;
        this.initBasicSdk(cpCode,userAgent,mobile,user.getGaid(),user.getImei(),user.getMac(),user.getCountryCode(),user.getMcc(),user.getMnc());
    }
    protected void initBasicSdk(String cpCode,String userAgent,String mobile,String gaid,String imei,String mac,String countryCode,String mcc,String mnc) {

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
    public void initBasicSdkResult(boolean result,String info) {
        if(result == true && initBasicSdkBack != null)
        {
            initBasicSdkBack.onCallBack(true,"","");
        }
        else if(initBasicSdkBack != null)
        {
            initBasicSdkBack.onCallBack(false,info,"");
        }
        initBasicSdkBack = null;
    }
    /*****************************************************************/
    /************************SMS 支付*********************************/
    /*****************************************************************/
    CallBackFunction initRuSmsBack = null;
    public void initRuSms(CallBackFunction callBack) {
        initRuSmsBack = callBack;
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    request.getPaymentSms(user.getCountryCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void initRuSmsResult(boolean result,String info) {
        if(result == true && initRuSmsBack != null)
        {
            initRuSmsBack.onCallBack(true,"","");
        }
        else if(initRuSmsBack != null)
        {
            initRuSmsBack.onCallBack(false,info,"");
        }
    }
    /**支付的可行性*/
    protected boolean getBCDFeasibility(Activity act)
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
}
