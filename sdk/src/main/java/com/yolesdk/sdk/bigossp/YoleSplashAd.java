package com.yolesdk.sdk.bigossp;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.SplashAd;
import sg.bigo.ads.api.SplashAdInteractionListener;
import sg.bigo.ads.api.SplashAdLoader;
import sg.bigo.ads.api.SplashAdRequest;

public class YoleSplashAd {
    private String TAG = "Yole_YoleSplashAd";
    SplashAd mSplashAd = null;
    SplashAdRequest splashAdRequest = null;
    public void showAd(String slotId, int app_icon, String app_name, ViewGroup containerView, YoleSplashAdListener listener)
    {

        Log.i(TAG,"showAd");
        if(splashAdRequest == null)
        {
            splashAdRequest = new SplashAdRequest.Builder()
                    .withSlotId(slotId)
                    .withAppLogo(app_icon)
                    .withAppName(app_name)
                    .build();
        }

        if(mSplashAd != null && mSplashAd.isExpired() == true)
        {
            mSplashAd.destroy();
        }

        SplashAdLoader splashAdLoader = new SplashAdLoader.Builder().
                withAdLoadListener(new AdLoadListener<SplashAd>() {
                    @Override
                    public void onError(@NonNull AdError error) {
                        // There's something wrong during ad loading
                        listener.onAdError(error.getCode(),error.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull SplashAd ad) {

                        mSplashAd = ad;
                        ad.setAdInteractionListener(new SplashAdInteractionListener() {
                            @Override
                            public void onAdSkipped() {
//                                jumpToMainPage();
                                listener.onAdSkipped();
                            }

                            @Override
                            public void onAdFinished() {
//                                jumpToMainPage();
                                listener.onAdFinished();
                            }

                            @Override
                            public void onAdError(@NonNull AdError adError) {
                                listener.onAdError(adError.getCode(),adError.getMessage());
                            }

                            @Override
                            public void onAdImpression() {
                                listener.onAdImpression();
                            }

                            @Override
                            public void onAdClicked() {
                                listener.onAdClicked();
                            }

                            @Override
                            public void onAdOpened() {
                                listener.onAdOpened();
                            }

                            @Override
                            public void onAdClosed() {
                                listener.onAdClosed();
                            }
                        });
                        ad.showInAdContainer(containerView);
                    }
                }).build();

        splashAdLoader.loadAd(splashAdRequest);

    }
    public boolean isSkippable()
    {
        if(mSplashAd != null)
            return mSplashAd.isSkippable();
        else
            return false;
    }

    public void destroy()
    {
        if(mSplashAd != null)
            mSplashAd.destroy();
        mSplashAd = null;
    }

}