package com.yolesdk.sdk.bigossp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.yolesdk.sdk.callback.CallBackFunction;
import com.yolesdk.sdk.callback.InitCallBackFunction;
import com.yolesdk.sdk.data.init.YoleInitConfig;

import sg.bigo.ads.BigoAdSdk;
import sg.bigo.ads.api.AdConfig;

public class BigosspMgr {
    private String TAG = "Yole_BigosspMgr";
    private static BigosspMgr _instance = null;
    private Context var =  null;
    private YoleInterstitialAd  interstitialAd = null;
    private YoleRewardVideoAd rewardVideoAd = null;
    private YoleSplashAd splashAd  = null;
    private YoleBannerAd bannerAd  = null;
    /**广告sdk初始化成功标志*/
    public boolean bigosspInitSuccess = false;

    public BigosspMgr(Context var1,String appId) {
        Log.i(TAG,TAG);

    }
    public void initAd(Context var1, YoleInitConfig _config, CallBackFunction _initBack)
    {
        var = var1;

        AdConfig config = new AdConfig.Builder()
                .setAppId(_config.getAppId())
                .build();
        BigoAdSdk.initialize(var1, config, new BigoAdSdk.InitListener() {
            @Override
            public void onInitialized() {
                Log.i(TAG,"BigoAdSdk init success");
                bigosspInitSuccess = true;

                interstitialAd = new YoleInterstitialAd(_config.getInterstitialAdId());
                rewardVideoAd = new YoleRewardVideoAd(_config.getRewardAdId());
                bannerAd = new YoleBannerAd(_config.getBannerAdId());
                splashAd = new YoleSplashAd();

                _initBack.onCallBack(true,"","");
            }
        });

    }
    public void showInterstitial(Activity _var,String slotId,YoleInterstitialListener listener)
    {
        interstitialAd.showAd(slotId,listener);
    }
    public void showRewardVideo(Activity _var,String slotId,YoleRewardVideoListener listener)
    {
        rewardVideoAd.showAd(slotId,listener);
    }
    public void showSplash(Activity _var,String slotId, int app_icon, String app_name, ViewGroup containerView, YoleSplashAdListener listener)
    {
        if(splashAd != null)
            splashAd.showAd(slotId, app_icon, app_name, containerView, listener);
    }
    public boolean splashIsSkippable()
    {
        return splashAd.isSkippable();
    }
    public void splashDestroy()
    {
        splashAd.destroy();
    }

    public void showBanner(Activity _var,String slotId, ViewGroup containerView,YoleBannerListener listener)
    {
        bannerAd.showAd(slotId,containerView,listener);
    }
    public void bannerDestroy()
    {
        bannerAd.destroyAll();
    }
}
