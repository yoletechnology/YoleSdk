package com.yolesdk.sdk.bigossp;

import android.util.Log;

import androidx.annotation.NonNull;

import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdInteractionListener;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.AdSize;
import sg.bigo.ads.api.BannerAd;
import sg.bigo.ads.api.BannerAdLoader;
import sg.bigo.ads.api.BannerAdRequest;
import sg.bigo.ads.api.InterstitialAd;
import sg.bigo.ads.api.InterstitialAdLoader;
import sg.bigo.ads.api.InterstitialAdRequest;

public class YoleInterstitialAd {
    private String TAG = "Yole_YoleInterstitialAd";
    InterstitialAd mInterstitialAd = null;
    InterstitialAdRequest interstitialAdRequest = null;
    String defaultSlotId = "";
    YoleInterstitialListener listener = null;
    public YoleInterstitialAd(String _defaultSlotId)
    {
        defaultSlotId = _defaultSlotId;
        if(defaultSlotId.length() > 0)
        {
            interstitialAdRequest = new InterstitialAdRequest.Builder()
                    .withSlotId(_defaultSlotId)
                    .build();

            this.loadAd();
        }
    }
    public void showAd(String slotId,YoleInterstitialListener _listener)
    {
        Log.i(TAG,"showAd");
        listener = _listener;

        if(interstitialAdRequest == null || slotId.length() >0)
        {
            interstitialAdRequest = new InterstitialAdRequest.Builder()
                    .withSlotId(slotId)
                    .build();
        }

        if(mInterstitialAd == null ||  mInterstitialAd != null && mInterstitialAd.isExpired() == true) {
            if( mInterstitialAd != null && mInterstitialAd.isExpired() == true)
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
        InterstitialAdLoader interstitialAdLoader = new InterstitialAdLoader.Builder().
                withAdLoadListener(new AdLoadListener<InterstitialAd>() {
                    @Override
                    public void onError(@NonNull AdError error) {
                        Log.i(TAG, "onAdLoaded onError");
                        // There's something wrong during ad loading
                        if(listener != null)
                            listener.onAdError(error.getCode(), error.getMessage());
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        Log.i(TAG, "onAdLoaded success");
                        mInterstitialAd = ad;
                        if(listener != null)
                        {
                            showAd();
                        }
                    }
                }).build();

        interstitialAdLoader.loadAd(interstitialAdRequest);
    }
    public void showAd()
    {
        mInterstitialAd.setAdInteractionListener(new AdInteractionListener() {
            @Override
            public void onAdError(@NonNull AdError error) {
                // There's something wrong when using this ad.
                if(listener != null)
                    listener.onAdError(error.getCode(),error.getMessage());
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
                // When the fullsceen ad covers the screen.
                if(listener != null)
                    listener.onAdOpened();
            }

            @Override
            public void onAdClosed() {
                // When the fullsceen ad closes.
                if(listener != null)
                {
                    listener.onAdClosed();
                }
                destroyAll();

            }
        });
        mInterstitialAd.show();
    }

    public void destroyAd()
    {
        if(mInterstitialAd != null)
            mInterstitialAd.destroy();
        mInterstitialAd = null;
    }
    public void destroyAll()
    {
        destroyAd();
        listener = null;
        loadAd();
    }

}
