package com.yolesdk.sdk;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.yolesdk.sdk.bigossp.BigosspMgr;
import com.yolesdk.sdk.callback.InitCallBackFunction;
import com.yolesdk.sdk.data.InitSdkData;
import com.yolesdk.sdk.data.UserInfo;
import com.yolesdk.sdk.data.YoleInitConfig;
import com.yolesdk.sdk.tool.NetworkRequest;

import java.util.Timer;

public class YoleSdkBase {
    private String TAG = "YoleSdkBase";
//    private static  YoleSdkMgr _instance = null;
    protected Context context =  null;
    protected boolean isDebugger = false;


    /**各种网络接口**/
    protected NetworkRequest request = null;
    /**用户信息(通过用户设置 和 请求的返回。组装成的数据)**/
    public UserInfo user =  null;
    /**广告sdk管理**/
    protected BigosspMgr bigosspMgr =  null;
    /**广告sdk初始化定时器*/
    protected Timer bigosspInitBackTimer = null;
    /**广告sdk初始化成功标志*/
    protected boolean bigosspInitSuccess = false;

    /**YoleSdk 初始化结果的回调*/
    public InitCallBackFunction initBack = null;
    /**bigosspSdk 初始化结果的回调*/
    protected InitCallBackFunction bigosspInitBack = null;

    /**sdk初始化的主接口*/
    public void initSdk(Context _var1, YoleInitConfig _config, InitCallBackFunction _initBack)
    {

        context = _var1;
        initBack = _initBack;

        this.init(_var1,_config.getAppId(),_config.getAppKey(),_config.getCpCode(),_config.isDebug());
        this.initBigossp(_var1,_config,new InitCallBackFunction(){
            @Override
            public void success(InitSdkData info) {
                bigosspInitSuccess = true;
                if(bigosspInitBack != null)
                {
                    if(bigosspInitBackTimer != null) {
                        bigosspInitBackTimer.cancel();
                    }
                    bigosspInitBack.success(null);
                }
                initAppBySdk(_config.getCpCode(),_config.getUserAgent(),_config.getMobile());
            }

            @Override
            public void fail(String info) {
                if(bigosspInitBack != null)
                {
                    if(bigosspInitBackTimer != null) {
                        bigosspInitBackTimer.cancel();
                    }
                    bigosspInitBack.fail("");
                }
                initBack.fail(info);
            }
        });
    }
    /**创建sdk内的各个功能模块*/
    protected void init(Context var1,String appId,String appkey,String cpCode,boolean _isDebugger)
    {
        request = new NetworkRequest();
        isDebugger = _isDebugger;
        user = new UserInfo(var1,appkey,cpCode,_isDebugger);
        if(appId.length() > 0)
            bigosspMgr = new BigosspMgr(var1,appId);
    }
    /**初始化sdk内 BCD支付模块*/
    protected void initAppBySdk(String cpCode,String userAgent,String mobile) {
        userAgent = userAgent.length() <=0 ? user.getPhoneModel() : userAgent;
        mobile = mobile.length() <=0 ? user.getPhoneNumber() : mobile;
        this.initAppBySdk(cpCode,userAgent,mobile,user.getGaid(),user.getImei(),user.getMac(),user.getCountryCode(),user.getMcc(),user.getMnc());
    }
    /**初始化sdk内 Bigossp广告模块*/
    protected void initBigossp(Context var1,YoleInitConfig config,InitCallBackFunction _initBack)
    {
        if(bigosspMgr != null)
            bigosspMgr.initAd(var1,config,_initBack);
        else {
            _initBack.success(null);
        }
    }
    protected void initAppBySdk(String cpCode,String userAgent,String mobile,String gaid,String imei,String mac,String countryCode,String mcc,String mnc) {

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



    /**支付的可行性*/
    protected boolean getFeasibility(Activity act)
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
