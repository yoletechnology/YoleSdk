package com.yolesdk.sdk.bigossp;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdInteractionListener;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.AdSize;
import sg.bigo.ads.api.BannerAd;
import sg.bigo.ads.api.BannerAdLoader;
import sg.bigo.ads.api.BannerAdRequest;
import sg.bigo.ads.api.SplashAd;
import sg.bigo.ads.api.SplashAdInteractionListener;
import sg.bigo.ads.api.SplashAdLoader;
import sg.bigo.ads.api.SplashAdRequest;

public class YoleBannerAd {
    private String TAG = "Yole_YoleBannerAd";
    BannerAd mBannerAd = null;
    BannerAdRequest  bannerAdRequest = null;
    ViewGroup adViewContainer = null;
     String defaultSlotId = "";
    YoleBannerListener listener = null;
    public YoleBannerAd(String _defaultSlotId)
    {
        defaultSlotId = _defaultSlotId;
        if(defaultSlotId.length() > 0)
        {
            bannerAdRequest = new BannerAdRequest.Builder()
                    .withSlotId(_defaultSlotId)
                    .withAdSizes(AdSize.BANNER)
                    .build();

            this.loadAd();
        }

    }


    public void showAd(String slotId,  ViewGroup containerView, YoleBannerListener _listener)
    {
        Log.i(TAG,"showAd");

        adViewContainer = containerView;
        listener = _listener;

        if(bannerAdRequest == null || slotId.length() >0)
        {
            bannerAdRequest = new BannerAdRequest.Builder()
                    .withSlotId(slotId)
                    .withAdSizes(AdSize.BANNER)
                    .build();
        }

        if(mBannerAd == null ||  mBannerAd != null && mBannerAd.isExpired() == true) {
            if( mBannerAd != null && mBannerAd.isExpired() == true)
            {
                this.destroyAd();
            }

            this.loadAd();
        }
        else
        {

            this.showAd();
        }

    }
    public void loadAd()
    {
        BannerAdLoader bannerAdLoader = new BannerAdLoader.Builder().
                withAdLoadListener(new AdLoadListener<BannerAd>() {
                    @Override
                    public void onError(@NonNull AdError error) {
                        Log.i(TAG, "onAdLoaded onError");
                        // There's something wrong during ad loading
                        if(listener != null)
                            listener.onAdError(error.getCode(), error.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull BannerAd ad) {
                        Log.i(TAG, "onAdLoaded success");
                        mBannerAd = ad;
                        mBannerAd.setAdInteractionListener(new AdInteractionListener() {
                            @Override
                            public void onAdError(@NonNull AdError error) {
                                // There's something wrong when using this ad.
                                if(listener != null)
                                    listener.onAdError(error.getCode(), error.getMessage());
                            }

                            @Override
                            public void onAdImpression() {
                                // When the ad appears on the screen.
                                if(listener != null)
                                    listener.onAdImpression();
                            }

                            @Override
                            public void onAdClicked() {
                                // When the user clicks on the ad.
                                if(listener != null)
                                    listener.onAdClicked();
                            }

                            @Override
                            public void onAdOpened() {
                                if(listener != null)
                                    listener.onAdOpened();

                            }

                            @Override
                            public void onAdClosed() {
                                listener.onAdClosed();
                                destroyAll();
                            }
                        });
                        if(listener != null)
                        {
                            showAd();
                        }
                    }
                }).build();

        bannerAdLoader.loadAd(bannerAdRequest);
    }
    public void showAd()
    {
        if(adViewContainer != null)
            adViewContainer.removeAllViews();

        adViewContainer.addView(mBannerAd.adView());
    }

    public void destroyAd()
    {
        if(adViewContainer != null)
            adViewContainer.removeAllViews();
        if(mBannerAd != null)
            mBannerAd.destroy();
        mBannerAd = null;
    }
    public void destroyAll()
    {
        destroyAd();
        listener = null;
        loadAd();
    }

}
